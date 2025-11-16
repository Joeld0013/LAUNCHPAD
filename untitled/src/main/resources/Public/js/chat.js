// ==== Chat System JavaScript for LaunchPad ====
// This file handles all chat-related functionality for both startup and investor views

// Configuration
const API_BASE_URL = '/api';
const CHAT_REFRESH_INTERVAL = 5000; // Refresh messages every 5 seconds
const UNREAD_CHECK_INTERVAL = 30000; // Check unread count every 30 seconds

// Global variables
let currentConversationId = null;
let currentUserId = null;
let currentUserType = null; // 'STARTUP' or 'INVESTOR'
let messageRefreshTimer = null;
let unreadCheckTimer = null;

// ==== Utility Functions ====

/**
 * Get JWT token from localStorage
 */
function getAuthToken() {
    // Try both keys for compatibility
    return localStorage.getItem('authToken') || localStorage.getItem('token');
}

/**
 * Get current user info from localStorage
 */
function getCurrentUser() {
    // First try the currentUser object
    const userJson = localStorage.getItem('currentUser');
    if (userJson) {
        try {
            return JSON.parse(userJson);
        } catch (e) {
            console.error('Error parsing currentUser:', e);
        }
    }

    // Fallback to individual items
    const userId = localStorage.getItem('userId');
    const userType = localStorage.getItem('userType');
    const userName = localStorage.getItem('userName');
    const userEmail = localStorage.getItem('userEmail');

    if (userId && userType) {
        return {
            id: userId,
            userType: userType,
            name: userName,
            companyName: userName,
            email: userEmail
        };
    }

    return null;
}

/**
 * Make authenticated API request
 */
async function apiRequest(endpoint, options = {}) {
    const token = getAuthToken();

    if (!token) {
        console.error('No auth token found');
        window.location.href = 'startup_login.html';
        return null;
    }

    const defaultHeaders = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };

    const config = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers
        }
    };

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

        if (response.status === 401 || response.status === 403) {
            // Token expired or invalid, redirect to login
            console.error('Authentication failed, redirecting to login');
            localStorage.clear();
            window.location.href = 'startup_login.html';
            return null;
        }

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Request failed');
        }

        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }

        return await response.text();
    } catch (error) {
        console.error('API request error:', error);
        throw error;
    }
}

/**
 * Format timestamp for display
 */
function formatMessageTime(timestamp) {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now - date;
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    if (diffDays === 1) return 'Yesterday';
    if (diffDays < 7) return date.toLocaleDateString([], { weekday: 'short' });
    return date.toLocaleDateString([], { month: 'short', day: 'numeric' });
}

/**
 * Show toast notification
 */
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === 'error' ? '#e74c3c' : type === 'success' ? '#2ecc71' : '#3498db'};
        color: white;
        padding: 15px 20px;
        border-radius: 10px;
        box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        z-index: 10000;
        animation: slideIn 0.3s ease;
    `;
    toast.textContent = message;

    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// ==== Chat Initialization ====

/**
 * Initialize chat page
 */
async function initializeChatPage() {
    console.log('Initializing chat page...');

    // Get current user info
    const user = getCurrentUser();
    const token = getAuthToken();

    console.log('Auth check - User:', user, 'Token exists:', !!token);

    if (!user || !token) {
        console.error('No user or token found, redirecting to login');
        const loginPage = window.location.pathname.includes('investor') ? 'investor_login.html' : 'startup_login.html';
        window.location.href = loginPage;
        return;
    }

    currentUserId = user.id;
    currentUserType = user.userType; // 'STARTUP' or 'INVESTOR'

    console.log('Current user:', currentUserId, currentUserType);

    // Load conversations
    await loadConversations();

    // Check for pending conversations (startup only)
    if (currentUserType === 'STARTUP') {
        await checkPendingConversations();
    }

    // Update unread count
    await updateUnreadCount();

    // Start periodic refresh
    startPeriodicRefresh();

    // Set up event listeners
    setupChatEventListeners();
}

/**
 * Set up event listeners for chat
 */
function setupChatEventListeners() {
    // Send button
    const sendBtn = document.querySelector('.send-btn');
    const chatInput = document.querySelector('.chat-input');

    if (sendBtn && chatInput) {
        sendBtn.addEventListener('click', sendCurrentMessage);

        chatInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendCurrentMessage();
            }
        });
    }

    // Chat search
    const searchInput = document.querySelector('.chat-search-container input');
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            filterConversations(e.target.value);
        });
    }
}

// ==== Conversation Loading ====

/**
 * Load all conversations for current user
 */
async function loadConversations() {
    try {
        console.log('Loading conversations for', currentUserType);

        const conversations = await apiRequest(
            `/chat/conversations?userType=${currentUserType}`,
            { method: 'GET' }
        );

        console.log('Loaded conversations:', conversations);

        renderConversationList(conversations);

        // Auto-select first conversation if none selected
        if (conversations.length > 0 && !currentConversationId) {
            selectConversationById(conversations[0].id);
        }
    } catch (error) {
        console.error('Error loading conversations:', error);
        showToast('Failed to load conversations', 'error');
    }
}

/**
 * Render conversation list in sidebar
 */
function renderConversationList(conversations) {
    const listContainer = document.querySelector('.chat-inbox-list');
    if (!listContainer) return;

    listContainer.innerHTML = '';

    if (conversations.length === 0) {
        listContainer.innerHTML = `
            <div style="padding: 20px; text-align: center; color: #888;">
                <i class="fas fa-comments" style="font-size: 48px; margin-bottom: 10px;"></i>
                <p>No conversations yet</p>
            </div>
        `;
        return;
    }

    conversations.forEach(conv => {
        const isActive = conv.id === currentConversationId;
        const isPending = conv.status === 'PENDING';

        // Determine display name and avatar based on user type
        const displayName = currentUserType === 'STARTUP'
            ? conv.investorName
            : conv.startupName;
        const displayAvatar = currentUserType === 'STARTUP'
            ? conv.investorAvatar
            : conv.startupAvatar;

        const item = document.createElement('div');
        item.className = `chat-inbox-item ${isActive ? 'active' : ''} ${isPending ? 'pending' : ''}`;
        item.dataset.conversationId = conv.id;
        item.onclick = () => selectConversationById(conv.id);

        item.innerHTML = `
            <div class="chat-avatar">${displayAvatar}</div>
            <div class="chat-item-details">
                <div class="chat-item-name">
                    ${displayName}
                    ${isPending ? '<span class="pending-badge">Pending</span>' : ''}
                    ${conv.type === 'BID_NEGOTIATION' ? '<i class="fas fa-handshake" title="Bid Discussion"></i>' : ''}
                </div>
                <div class="chat-item-preview">${conv.lastMessageText || 'No messages yet'}</div>
            </div>
            <div class="chat-item-meta">
                <div class="chat-item-time">${formatMessageTime(conv.lastMessageTime)}</div>
                ${conv.unreadCount > 0 ? `<div class="unread-badge">${conv.unreadCount}</div>` : ''}
            </div>
        `;

        listContainer.appendChild(item);
    });
}

/**
 * Filter conversations by search query
 */
function filterConversations(query) {
    const items = document.querySelectorAll('.chat-inbox-item');
    const searchTerm = query.toLowerCase();

    items.forEach(item => {
        const name = item.querySelector('.chat-item-name').textContent.toLowerCase();
        const preview = item.querySelector('.chat-item-preview').textContent.toLowerCase();

        if (name.includes(searchTerm) || preview.includes(searchTerm)) {
            item.style.display = 'flex';
        } else {
            item.style.display = 'none';
        }
    });
}

// ==== Conversation Selection ====

/**
 * Select a conversation by ID
 */
async function selectConversationById(conversationId) {
    try {
        console.log('Selecting conversation:', conversationId);

        // Update UI
        document.querySelectorAll('.chat-inbox-item').forEach(item => {
            item.classList.remove('active');
        });

        const selectedItem = document.querySelector(`[data-conversation-id="${conversationId}"]`);
        if (selectedItem) {
            selectedItem.classList.add('active');
        }

        // Load conversation details
        currentConversationId = conversationId;
        await loadConversationDetails(conversationId);

        // Reload conversation list to update unread counts
        await loadConversations();
        await updateUnreadCount();

    } catch (error) {
        console.error('Error selecting conversation:', error);
        showToast('Failed to load conversation', 'error');
    }
}

/**
 * Load conversation details with messages
 */
async function loadConversationDetails(conversationId) {
    try {
        const conversation = await apiRequest(
            `/chat/conversations/${conversationId}`,
            { method: 'GET' }
        );

        console.log('Loaded conversation details:', conversation);

        renderConversationHeader(conversation);
        renderMessages(conversation.messages);

    } catch (error) {
        console.error('Error loading conversation details:', error);
        showToast('Failed to load messages', 'error');
    }
}

/**
 * Render conversation header
 */
function renderConversationHeader(conversation) {
    const headerName = document.querySelector('.chat-header-name');
    const headerAvatar = document.querySelector('.chat-avatar-large');
    const headerStatus = document.querySelector('.chat-header-status');

    if (!headerName || !headerAvatar) return;

    // Determine display based on user type
    const displayName = currentUserType === 'STARTUP'
        ? conversation.investorName
        : conversation.startupName;
    const displayAvatar = currentUserType === 'STARTUP'
        ? conversation.investorAvatar
        : conversation.startupAvatar;

    headerName.textContent = displayName;
    headerAvatar.textContent = displayAvatar;

    if (headerStatus) {
        headerStatus.textContent = conversation.status === 'BLOCKED' ? 'Blocked' : 'Online';
    }

    // Add bid info if it's a bid conversation
    if (conversation.type === 'BID_NEGOTIATION' && conversation.bidId) {
        const bidBadge = document.createElement('span');
        bidBadge.className = 'bid-badge';
        bidBadge.innerHTML = '<i class="fas fa-handshake"></i> Bid Discussion';
        bidBadge.style.cssText = 'margin-left: 10px; font-size: 12px; color: #3498db;';
        headerName.appendChild(bidBadge);
    }
}

/**
 * Render messages in chat area
 */
function renderMessages(messages) {
    const messagesArea = document.querySelector('.chat-messages-area');
    if (!messagesArea) return;

    messagesArea.innerHTML = '';

    if (messages.length === 0) {
        messagesArea.innerHTML = `
            <div style="text-align: center; padding: 40px; color: #888;">
                <i class="fas fa-comments" style="font-size: 48px; margin-bottom: 10px;"></i>
                <p>No messages yet. Start the conversation!</p>
            </div>
        `;
        return;
    }

    messages.forEach(msg => {
        const bubble = createMessageBubble(msg);
        messagesArea.appendChild(bubble);
    });