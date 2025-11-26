// startup_bid_api.js – Bids dashboard for STARTUP side
const API_BASE_URL = '/api';

/**
 * Initialize startup bid dashboard.
 * Called from startup_bids.html on DOMContentLoaded.
 */
async function initializeStartupBidDashboard() {
    console.log('=== Initializing Startup Bid Dashboard ===');
    checkStartupAuth();
    await loadBidsForStartup();
}

/**
 * Check auth for STARTUP user
 */
function checkStartupAuth() {
    const token = localStorage.getItem('token');
    const userType = localStorage.getItem('userType');

    if (!token || userType !== 'STARTUP') {
        window.location.href = 'startup_login.html';
        return;
    }
}

/**
 * Load all bids for the current startup
 */
async function loadBidsForStartup() {
    const token = localStorage.getItem('token');

    const incomingTab = document.getElementById('bids-incoming');
    const negotiatingTab = document.getElementById('bids-negotiating');
    const acceptedTab = document.getElementById('bids-accepted');
    const rejectedTab = document.getElementById('bids-rejected');

    if (incomingTab) incomingTab.innerHTML = '<p class="empty-tab-message">Loading bids...</p>';
    if (negotiatingTab) negotiatingTab.innerHTML = '<p class="empty-tab-message">Loading bids...</p>';

    try {
        const response = await fetch(`${API_BASE_URL}/bids/startup`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            console.error('Failed to load startup bids. Status:', response.status);
            showStartupErrorInTabs('Error loading bids. Please try again later.');
            return;
        }

        const allBids = await response.json();
        console.log('✓ Startup bids:', allBids);

        displayStartupBidsByStatus(allBids);
    } catch (err) {
        console.error('Error loading startup bids:', err);
        showStartupErrorInTabs('Error loading bids. Please try again later.');
    }
}

/**
 * Organize and display startup bids by status
 */
function displayStartupBidsByStatus(allBids) {
    const pending = allBids.filter(bid => bid.status === 'PENDING');
    const negotiating = allBids.filter(bid => bid.status === 'NEGOTIATING');
    const accepted = allBids.filter(bid => bid.status === 'ACCEPTED');
    const rejected = allBids.filter(bid => bid.status === 'REJECTED' || bid.status === 'WITHDRAWN');

    updateStartupBadgeCounts(pending.length, negotiating.length);

    displayStartupBidsInTab('bids-incoming', pending, 'PENDING');
    displayStartupBidsInTab('bids-negotiating', negotiating, 'NEGOTIATING');
    displayStartupBidsInTab('bids-accepted', accepted, 'ACCEPTED');
    displayStartupBidsInTab('bids-rejected', rejected, 'REJECTED');
}

/**
 * Update badge counts for Incoming & Negotiating tabs
 */
function updateStartupBadgeCounts(incomingCount, negotiatingCount) {
    const badges = document.querySelectorAll('.bid-count-badge');
    if (badges.length >= 2) {
        badges[0].textContent = incomingCount;     // Incoming
        badges[1].textContent = negotiatingCount;  // Negotiating
    }
}

/**
 * Render bids in a specific tab panel
 */
function displayStartupBidsInTab(tabId, bids, status) {
    const tabElement = document.getElementById(tabId);

    if (!tabElement) {
        console.warn(`Tab ${tabId} not found`);
        return;
    }

    if (!bids || bids.length === 0) {
        let msg = 'No bids in this category.';
        if (status === 'PENDING') msg = 'No incoming bids yet.';
        else if (status === 'NEGOTIATING') msg = 'No bids currently in negotiation.';
        else if (status === 'ACCEPTED') msg = 'You have not accepted any bids yet.';
        else if (status === 'REJECTED') msg = 'You have not rejected any bids yet.';

        tabElement.innerHTML = `<p class="empty-tab-message">${msg}</p>`;
        return;
    }

    tabElement.innerHTML = bids.map(bid => createStartupBidCard(bid, status)).join('');
}

/**
 * Create a bid card HTML for the STARTUP (incoming bids)
 */
function createStartupBidCard(bid, status) {
    const formattedAmount = new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(bid.amount);

    const createdDate = new Date(bid.createdAt).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });

    let actionButtons = '';

    // For startup side:
    // - PENDING: Accept, Reject, Negotiate
    // - NEGOTIATING: Accept, Reject, Chat
    if (status === 'PENDING') {
        actionButtons = `
            <button class="bid-action-btn bid-accept-btn"
                    onclick="startupHandleBidAction('${bid.id}', 'accept')">
                <i class="fas fa-check"></i> Accept
            </button>
            <button class="bid-action-btn bid-reject-btn"
                    onclick="startupHandleBidAction('${bid.id}', 'reject')">
                <i class="fas fa-times"></i> Reject
            </button>
            <button class="bid-action-btn bid-negotiate-btn"
                    onclick="startupHandleBidAction('${bid.id}', 'negotiate')">
                <i class="fas fa-comments-dollar"></i> Negotiate
            </button>
        `;
    } else if (status === 'NEGOTIATING') {
        actionButtons = `
            <button class="bid-action-btn bid-accept-btn"
                    onclick="startupHandleBidAction('${bid.id}', 'accept')">
                <i class="fas fa-check"></i> Accept
            </button>
            <button class="bid-action-btn bid-reject-btn"
                    onclick="startupHandleBidAction('${bid.id}', 'reject')">
                <i class="fas fa-times"></i> Reject
            </button>
            <button class="bid-action-btn bid-chat-btn"
                    onclick="openStartupBidChat('${bid.investorId}', '${escapeHtml(bid.investorName)}')">
                <i class="fas fa-comment-dots"></i> Chat
            </button>
        `;
    }

    return `
        <div class="bid-card" data-bid-id="${bid.id}">
            <div class="bid-card-header">
                <div class="bid-company-info">
                    <h3 class="bid-investor-name">${escapeHtml(bid.investorName)}</h3>
                </div>
                <span class="bid-status-badge bid-status-${status.toLowerCase()}">${status}</span>
            </div>

            <div class="bid-card-details">
                <div class="bid-detail-row">
                    <span class="bid-detail-label">Investment Amount:</span>
                    <span class="bid-detail-value">${formattedAmount}</span>
                </div>
                <div class="bid-detail-row">
                    <span class="bid-detail-label">Equity:</span>
                    <span class="bid-detail-value">${bid.equity}%</span>
                </div>
                <div class="bid-detail-row">
                    <span class="bid-detail-label">Type:</span>
                    <span class="bid-detail-value">${escapeHtml(bid.bidType)}</span>
                </div>
                ${bid.message ? `
                <div class="bid-detail-row">
                    <span class="bid-detail-label">Terms:</span>
                    <span class="bid-detail-value">${escapeHtml(bid.message)}</span>
                </div>
                ` : ''}
                <div class="bid-detail-row">
                    <span class="bid-detail-label">Submitted:</span>
                    <span class="bid-detail-value">${createdDate}</span>
                </div>
            </div>

            <div class="bid-card-actions">
                ${actionButtons}
            </div>
        </div>
    `;
}

/**
 * Handle bid actions from STARTUP side: accept / reject / negotiate
 */
async function startupHandleBidAction(bidId, action) {
    const token = localStorage.getItem('token');

    let endpoint = '';
    let confirmMessage = '';

    if (action === 'accept') {
        endpoint = `${API_BASE_URL}/bids/${bidId}/accept`;
        confirmMessage = 'Accept this bid?';
    } else if (action === 'reject') {
        endpoint = `${API_BASE_URL}/bids/${bidId}/reject`;
        confirmMessage = 'Reject this bid?';
    } else if (action === 'negotiate') {
        endpoint = `${API_BASE_URL}/bids/${bidId}/negotiate`;
        confirmMessage = 'Move this bid into negotiation?';
    } else {
        console.warn('Unknown bid action:', action);
        return;
    }

    if (!confirm(confirmMessage)) return;

    try {
        const response = await fetch(endpoint, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            showStartupToast(`Bid ${action} successful!`, 'success');
            await loadBidsForStartup();
        } else {
            let msg = `Failed to ${action} bid`;
            try {
                const text = await response.text();
                if (text) msg = text;
            } catch (e) {}
            showStartupToast(msg, 'error');
        }
    } catch (err) {
        console.error(`Error performing bid action (${action}):`, err);
        showStartupToast(`Error: ${err.message}`, 'error');
    }
}

/**
 * Open chat with investor (placeholder)
 */
function openStartupBidChat(investorId, investorName) {
    console.log(`Opening chat with investor: ${investorName} (${investorId})`);
    showStartupToast(`Opening chat with ${investorName}...`, 'info');
    // Later: redirect to chat page with query param
    // window.location.href = `startup_chat.html?investorId=${investorId}`;
}

/**
 * Escape HTML to prevent XSS
 */
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Simple toast for startup pages
 */
function showStartupToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    let icon = 'fa-info-circle';
    if (type === 'success') icon = 'fa-check-circle';
    if (type === 'error') icon = 'fa-exclamation-circle';

    toast.innerHTML = `<i class="fas ${icon}"></i> ${message}`;
    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add('show'), 50);
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

/**
 * Show the same error message in all bid tabs
 */
function showStartupErrorInTabs(message) {
    ['bids-incoming', 'bids-negotiating', 'bids-accepted', 'bids-rejected'].forEach(id => {
        const tab = document.getElementById(id);
        if (tab) {
            tab.innerHTML = `<p class="empty-tab-message">${message}</p>`;
        }
    });
}
