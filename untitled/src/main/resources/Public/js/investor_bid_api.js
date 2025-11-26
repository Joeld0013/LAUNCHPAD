// investor_bid_api.js - Handle bid dashboard for investors
window.API_BASE_URL = window.API_BASE_URL || '/api';


/**
 * Initialize investor bid dashboard
 */
async function initializeInvestorBidDashboard() {
    console.log('=== Initializing Investor Bid Dashboard ===');
    checkAuth();
    await loadBidsForInvestor();
    setupBidEventListeners();
}

/**
 * Check authentication
 */
function checkAuth() {
    const token = localStorage.getItem('token');
    const userType = localStorage.getItem('userType');

    if (!token || userType !== 'INVESTOR') {
        window.location.href = 'investor_login.html';
        return;
    }
}

/**
 * Load all bids for the current investor
 */
async function loadBidsForInvestor() {
    const token = localStorage.getItem('token');

    try {
        const response = await fetch(`${API_BASE_URL}/bids/investor`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const allBids = await response.json();
            console.log('✓ Loaded bids:', allBids);
            displayBidsByStatus(allBids);
        } else {
            console.error('Failed to load bids');
            showErrorInTabs();
        }
    } catch (error) {
        console.error('Error loading bids:', error);
        showErrorInTabs();
    }
}

/**
 * Organize and display bids by status
 */
function displayBidsByStatus(allBids) {
    const pending = allBids.filter(bid => bid.status === 'PENDING');
    const negotiating = allBids.filter(bid => bid.status === 'NEGOTIATING');
    const accepted = allBids.filter(bid => bid.status === 'ACCEPTED');
    const rejected = allBids.filter(bid => bid.status === 'REJECTED' || bid.status === 'WITHDRAWN');

    // Update badge counts
    updateBadgeCounts(pending.length, negotiating.length);

    // Display bids in respective tabs
    displayBidsInTab('bids-pending', pending, 'PENDING');
    displayBidsInTab('bids-negotiating', negotiating, 'NEGOTIATING');
    displayBidsInTab('bids-accepted', accepted, 'ACCEPTED');
    displayBidsInTab('bids-rejected', rejected, 'REJECTED');
}

/**
 * Update badge counts in tab buttons
 */
function updateBadgeCounts(pendingCount, negotiatingCount) {
    const badges = document.querySelectorAll('.bid-count-badge');
    if (badges.length >= 2) {
        badges[0].textContent = pendingCount;
        badges[1].textContent = negotiatingCount;
    }
}

/**
 * Display bids in a specific tab
 */
function displayBidsInTab(tabId, bids, status) {
    const tabElement = document.getElementById(tabId);

    if (!tabElement) {
        console.warn(`Tab ${tabId} not found`);
        return;
    }

    if (bids.length === 0) {
        let emptyMessage = 'No bids in this category';
        if (status === 'PENDING') emptyMessage = 'No pending bids.';
        else if (status === 'NEGOTIATING') emptyMessage = 'No bids in negotiation.';
        else if (status === 'ACCEPTED') emptyMessage = 'No bids have been accepted yet.';
        else if (status === 'REJECTED') emptyMessage = 'No bids have been rejected or withdrawn.';

        tabElement.innerHTML = `<p class="empty-tab-message">${emptyMessage}</p>`;
        return;
    }

    tabElement.innerHTML = bids.map(bid => createBidCard(bid, status)).join('');
}

/**
 * Create a bid card HTML
 */
function createBidCard(bid, status) {
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

    if (status === 'PENDING') {
        actionButtons = `
            <button class="bid-action-btn bid-negotiate-btn" onclick="handleBidAction('${bid.id}', 'negotiate')">
                <i class="fas fa-comments"></i> Negotiate
            </button>
            <button class="bid-action-btn bid-withdraw-btn" onclick="handleBidAction('${bid.id}', 'withdraw')">
                <i class="fas fa-times"></i> Withdraw
            </button>
        `;
    } else if (status === 'NEGOTIATING') {
        actionButtons = `
            <button class="bid-action-btn bid-chat-btn" onclick="openBidChat('${bid.investorId}', '${bid.startupName}')">
                <i class="fas fa-comment-dots"></i> Chat
            </button>
            <button class="bid-action-btn bid-withdraw-btn" onclick="handleBidAction('${bid.id}', 'withdraw')">
                <i class="fas fa-times"></i> Withdraw
            </button>
        `;
    }

    return `
        <div class="bid-card" data-bid-id="${bid.id}">
            <div class="bid-card-header">
                <div class="bid-company-info">
                    <h3 class="bid-startup-name">${escapeHtml(bid.startupName)}</h3>
                    <span class="bid-investor-name" style="display:none;">${escapeHtml(bid.investorName)}</span>
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
                    <span class="bid-detail-value">${bid.bidType}</span>
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
 * Handle bid actions
 */
async function handleBidAction(bidId, action) {
    const token = localStorage.getItem('token');

    let endpoint = '';
    let confirmMessage = '';

    if (action === 'withdraw') {
        endpoint = `${API_BASE_URL}/bids/${bidId}/withdraw`;
        confirmMessage = 'Are you sure you want to withdraw this bid?';
    } else if (action === 'negotiate') {
        endpoint = `${API_BASE_URL}/bids/${bidId}/negotiate`;
        confirmMessage = 'Start negotiation with this startup?';
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
            showToast(`Bid ${action} successful!`, 'success');
            setTimeout(() => {
                location.reload();
            }, 1000);
        } else {
            showToast(`Failed to ${action} bid`, 'error');
        }
    } catch (error) {
        console.error(`Error handling bid action:`, error);
        showToast(`Error: ${error.message}`, 'error');
    }
}

/**
 * Show error message in all tabs
 */
function showErrorInTabs() {
    const tabs = ['bids-pending', 'bids-negotiating', 'bids-accepted', 'bids-rejected'];
    tabs.forEach(tabId => {
        const tab = document.getElementById(tabId);
        if (tab) {
            tab.innerHTML = '<p class="empty-tab-message">Error loading bids. Please try again later.</p>';
        }
    });
}

/**
 * Setup event listeners
 */
function setupBidEventListeners() {
    // Add any additional event listeners if needed
}

/**
 * Open chat with startup
 */
function openBidChat(startupId, startupName) {
    console.log(`Opening chat with startup: ${startupName}`);
    showToast(`Opening chat with ${startupName}...`, 'info');
    // In a real app, this would redirect to the chat page with the startup
    // window.location.href = `investors_chat.html?startupId=${startupId}`;
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
 * Show toast notification
 */
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    let icon = 'fa-info-circle';
    if (type === 'success') icon = 'fa-check-circle';
    if (type === 'error') icon = 'fa-exclamation-circle';

    toast.innerHTML = `<i class="fas ${icon}"></i> ${message}`;
    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add('show'), 100);
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Toast styles (add to CSS)
const toastStyles = `
<style>
.bid-card {
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 16px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.08);
    border-left: 4px solid var(--gold);
    transition: all 0.3s ease;
}
.bid-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0,0,0,0.12);
}
.bid-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 2px solid rgba(237, 185, 111, 0.2);
}
.bid-startup-name {
    margin: 0;
    font-size: 18px;
    font-weight: 700;
    color: var(--navy);
}
.bid-status-badge {
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
}
.bid-status-pending { background: rgba(241, 196, 15, 0.2); color: #f39c12; }
.bid-status-negotiating { background: rgba(52, 152, 219, 0.2); color: #3498db; }
.bid-status-accepted { background: rgba(46, 204, 113, 0.2); color: #27ae60; }
.bid-status-rejected { background: rgba(231, 76, 60, 0.2); color: #e74c3c; }
.bid-detail-row {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
    border-bottom: 1px solid rgba(0,0,0,0.05);
}
.bid-detail-label {
    font-weight: 600;
    color: var(--light-text);
}
.bid-detail-value {
    color: var(--navy);
    font-weight: 500;
}
.bid-card-actions {
    display: flex;
    gap: 10px;
    margin-top: 16px;
}
.bid-action-btn {
    flex: 1;
    padding: 10px 16px;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-size: 13px;
    font-weight: 600;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
}
.bid-negotiate-btn {
    background: linear-gradient(135deg, var(--gold) 0%, #e67e22 100%);
    color: white;
}
.bid-negotiate-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(237, 185, 111, 0.4);
}
.bid-withdraw-btn {
    background: rgba(231, 76, 60, 0.1);
    color: #e74c3c;
    border: 1px solid #e74c3c;
}
.bid-withdraw-btn:hover {
    background: #e74c3c;
    color: white;
}
.bid-chat-btn {
    background: linear-gradient(135deg, var(--navy) 0%, #2c3e50 100%);
    color: white;
}
.bid-chat-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(43, 52, 70, 0.4);
}
</style>
`;