// startupadmin.js - Complete functionality for startup admin page
const API_BASE_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Admin page loaded, initializing...');

    // Initialize page
    initializeNavigation();
    loadStartups();
    setupEventListeners();
    setupFilters();
    setupExportFunctionality();
});

// ========== NAVIGATION FUNCTIONALITY (ORIGINAL) ==========
function initializeNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    const actionButtons = document.querySelectorAll('.action-btn');

    // Frontend routes mapping - direct HTML file navigation
    const routes = {
        'dashboard': 'admindashboard.html',
        'startups': 'startupadmin.html',
        'investors': 'investoradmin.html',
        'approvals': 'approvals.html',
        'analytics': 'analytics.html',
        'settings': 'settings.html',
        'register-startup': 'startupadmin.html?action=register',
        'browse-investors': 'investors.html',
        'review-approvals': 'approvals.html'
    };

    function navigateTo(route) {
        if (route.startsWith('/')) {
            route = route.substring(1);
        }

        const currentPage = window.location.pathname.split('/').pop();
        if (currentPage === route) {
            updateActiveNavItem(route);
            return;
        }

        window.location.href = route;
    }

    function updateActiveNavItem(route) {
        navItems.forEach(item => {
            item.classList.remove('active');
        });

        let activeNavId = '';
        const currentPage = route.split('?')[0];

        if (currentPage.includes('admindashboard.html') || currentPage === '' || currentPage === 'dashboard') {
            activeNavId = 'dashboard';
        } else if (currentPage.includes('startupadmin.html') || currentPage.includes('startups')) {
            activeNavId = 'startups';
        } else if (currentPage.includes('investoradmin.html')) {
            activeNavId = 'investors';
        } else if (currentPage.includes('approvals.html')) {
            activeNavId = 'approvals';
        } else if (currentPage.includes('analytics.html')) {
            activeNavId = 'analytics';
        } else if (currentPage.includes('settings.html')) {
            activeNavId = 'settings';
        }

        if (activeNavId) {
            const activeItem = document.querySelector(`.nav-item[data-route="${activeNavId}"]`);
            if (activeItem) {
                activeItem.classList.add('active');
            }
        }
    }

    function showNavigationFeedback(route) {
        const pageName = route.split('?')[0].replace('.html', '') || 'Dashboard';
        const notification = document.createElement('div');
        notification.style.cssText = `
            position: fixed; top: 20px; right: 20px; background: #edb96f;
            color: #2b3446; padding: 12px 20px; border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15); z-index: 1000; font-weight: 600;
        `;

        const formattedPageName = pageName.charAt(0).toUpperCase() + pageName.slice(1).replace('admin', ' Admin');
        notification.textContent = `Navigating to ${formattedPageName}...`;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.opacity = '0';
            setTimeout(() => notification.remove(), 300);
        }, 1500);
    }

    navItems.forEach(item => {
        const routeKey = item.querySelector('.nav-text').textContent.toLowerCase().trim();
        item.setAttribute('data-route', routeKey);

        item.addEventListener('click', function() {
            const route = routes[routeKey];
            if (route) {
                showNavigationFeedback(route);
                setTimeout(() => navigateTo(route), 100);
            }
        });
    });

    // Add New Startup button
    const addNewStartupBtn = document.querySelector('.page-actions .btn-primary');
    if (addNewStartupBtn) {
        addNewStartupBtn.addEventListener('click', function() {
            // Redirect to startup registration page
            window.location.href = 'startup_reg_pg.html';
        });
    }

    function setInitialActiveState() {
        const currentPage = window.location.pathname.split('/').pop();
        updateActiveNavItem(currentPage);
    }

    setInitialActiveState();
}

// ========== EXPORT FUNCTIONALITY ==========
let selectedStartups = new Set();

function setupExportFunctionality() {
    const exportBtn = document.querySelector('.page-actions .btn-secondary');
    if (exportBtn) {
        exportBtn.addEventListener('click', showExportModal);
    }
}

function showExportModal() {
    const modal = document.createElement('div');
    modal.className = 'modal';
    modal.style.display = 'flex';
    modal.innerHTML = `
        <div class="modal-content" style="max-width: 500px;">
            <div class="modal-header">
                <h2 class="modal-title">Export Startup Data</h2>
                <button class="modal-close" onclick="this.closest('.modal').remove()">&times;</button>
            </div>
            <div class="modal-body">
                <div class="filter-group" style="margin-bottom: 20px;">
                    <label class="filter-label">Export Which Startups?</label>
                    <select class="filter-select" id="export-filter">
                        <option value="all">All Startups</option>
                        <option value="PENDING">Pending Only</option>
                        <option value="APPROVED">Approved Only</option>
                        <option value="REJECTED">Rejected Only</option>
                    </select>
                </div>
                <div class="filter-group">
                    <label class="filter-label">Export Format</label>
                    <select class="filter-select" id="export-format">
                        <option value="csv">CSV (Excel)</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-primary" onclick="executeExport()">
                    <i class="fas fa-download"></i> Download Export
                </button>
                <button class="btn btn-secondary" onclick="this.closest('.modal').remove()">
                    Cancel
                </button>
            </div>
        </div>
    `;
    document.body.appendChild(modal);
}

async function executeExport() {
    const filter = document.getElementById('export-filter').value;
    const format = document.getElementById('export-format').value;

    try {
        showLoading();

        const url = filter === 'all'
            ? `${API_BASE_URL}/admin/startups/export?format=${format}`
            : `${API_BASE_URL}/admin/startups/export?status=${filter}&format=${format}`;

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error('Export failed');
        }

        const blob = await response.blob();
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = downloadUrl;
        a.download = `startups-export-${filter}-${new Date().toISOString().split('T')[0]}.csv`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(downloadUrl);
        document.body.removeChild(a);

        document.querySelector('.modal').remove();
        showSuccess('Export completed successfully!');

    } catch (error) {
        console.error('Export error:', error);
        showError('Failed to export data. Please try again.');
    } finally {
        hideLoading();
    }
}

// ========== DATA LOADING ==========
async function loadStartups(filters = {}) {
    try {
        console.log('📡 Loading startups with filters:', filters);
        showLoading();

        const params = new URLSearchParams();
        if (filters.industry) params.append('industry', filters.industry);
        if (filters.stage) params.append('stage', filters.stage);
        if (filters.country) params.append('country', filters.country);
        if (filters.status) params.append('status', filters.status);
        if (filters.search) params.append('search', filters.search);

        const url = `${API_BASE_URL}/admin/startups?${params.toString()}`;
        console.log('🔗 Fetching from:', url);

        const response = await fetch(url);
        console.log('📥 Response status:', response.status);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('✅ Data received:', data);

        if (data.success && data.data) {
            console.log(`📊 Found ${data.data.length} startups`);
            displayStartups(data.data);
            updateStatistics();
        } else {
            console.error('❌ API returned success=false');
            showError('Failed to load startups');
        }

    } catch (error) {
        console.error('❌ Error loading startups:', error);
        showError('Failed to load startups: ' + error.message);
    } finally {
        hideLoading();
    }
}

function displayStartups(startups) {
    console.log('🎨 Displaying startups:', startups.length);

    const pendingStartups = startups.filter(s => s.registrationStatus === 'PENDING');
    const approvedStartups = startups.filter(s => s.registrationStatus === 'APPROVED');

    console.log(`📋 Pending: ${pendingStartups.length}, Approved: ${approvedStartups.length}`);

    const pendingGrid = document.getElementById('pending-startups');
    if (pendingGrid) {
        if (pendingStartups.length === 0) {
            pendingGrid.innerHTML = '<p style="text-align: center; color: #777; padding: 40px; grid-column: 1/-1;">No pending startup requests at the moment.</p>';
        } else {
            pendingGrid.innerHTML = pendingStartups.map(startup => createStartupCard(startup, true)).join('');
        }
    }

    const approvedGrid = document.getElementById('approved-startups');
    if (approvedGrid) {
        if (approvedStartups.length === 0) {
            approvedGrid.innerHTML = '<p style="text-align: center; color: #777; padding: 40px; grid-column: 1/-1;">No approved startups yet.</p>';
        } else {
            approvedGrid.innerHTML = approvedStartups.map(startup => createStartupCard(startup, false)).join('');
        }
    }
}

function createStartupCard(startup, showActions = true) {
    const statusClass = startup.registrationStatus === 'APPROVED' ? 'status-approved' : 'status-pending';
    const statusText = startup.registrationStatus === 'APPROVED' ? 'Approved' : 'Pending Review';

    const initials = startup.name.split(' ').map(word => word[0]).join('').substring(0, 2).toUpperCase();
    const stageFormatted = startup.stage ? startup.stage.charAt(0).toUpperCase() + startup.stage.slice(1) : 'N/A';
    const location = startup.address || startup.country || 'Location not specified';

    let founded = 'N/A';
    if (startup.createdAt) {
        try {
            const date = new Date(startup.createdAt);
            founded = date.getFullYear();
        } catch (e) {
            founded = 'N/A';
        }
    }

    const actionButtons = showActions ? `
        <div class="action-buttons">
            <div class="btn-icon btn-view" onclick="openStartupModal('${startup.id}')">
                <i class="fas fa-eye"></i>
            </div>
            <div class="btn-icon btn-approve" onclick="approveStartup('${startup.id}')">
                <i class="fas fa-check"></i>
            </div>
            <div class="btn-icon btn-reject" onclick="rejectStartup('${startup.id}')">
                <i class="fas fa-times"></i>
            </div>
        </div>
    ` : `
        <div class="action-buttons">
            <div class="btn-icon btn-view" onclick="openStartupModal('${startup.id}')">
                <i class="fas fa-eye"></i>
            </div>
        </div>
    `;

    return `
        <div class="startup-card" data-startup-id="${startup.id}">
            <div class="startup-header">
                <div class="startup-logo">${initials}</div>
                <div class="startup-info">
                    <h3 class="startup-name">${startup.name}</h3>
                    <span class="startup-category">${startup.industry || 'General'}</span>
                </div>
            </div>
            <div class="startup-body">
                <p class="startup-description">${startup.description || 'No description available.'}</p>
                <div class="startup-details">
                    <div class="detail-item">
                        <span class="detail-label">Location</span>
                        <span class="detail-value">${location}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Stage</span>
                        <span class="detail-value">${stageFormatted}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Founded</span>
                        <span class="detail-value">${founded}</span>
                    </div>
                </div>
            </div>
            <div class="startup-footer">
                <span class="status-badge ${statusClass}">${statusText}</span>
                ${actionButtons}
            </div>
        </div>
    `;
}

// ========== MODAL FUNCTIONALITY ==========
async function openStartupModal(startupId) {
    try {
        console.log('🔍 Opening modal for startup:', startupId);
        showLoading();

        const response = await fetch(`${API_BASE_URL}/admin/startups/${startupId}`);

        if (!response.ok) {
            throw new Error('Failed to fetch startup details');
        }

        const data = await response.json();
        console.log('📄 Startup details:', data);

        if (data.success) {
            populateModal(data.data);
            document.getElementById('startupModal').style.display = 'flex';
        } else {
            showError('Failed to load startup details');
        }

    } catch (error) {
        console.error('Error loading startup details:', error);
        showError('Failed to load startup details. Please try again.');
    } finally {
        hideLoading();
    }
}

function populateModal(startup) {
    console.log('📝 Populating modal with:', startup);

    document.getElementById('startupModal').dataset.startupId = startup.id;

    document.getElementById('modal-name').textContent = startup.name || 'N/A';
    document.getElementById('modal-category').textContent = startup.industry || 'N/A';
    document.getElementById('modal-industry').textContent = startup.industry || 'N/A';
    document.getElementById('modal-founded').textContent = startup.foundedDate || 'N/A';
    document.getElementById('modal-location').textContent = startup.address || startup.country || 'N/A';
    document.getElementById('modal-stage').textContent = startup.stage || 'N/A';
    document.getElementById('modal-team').textContent = startup.teamSize || 'N/A';
    document.getElementById('modal-website').textContent = startup.website || 'N/A';

    document.getElementById('modal-description').textContent = startup.description || 'No description provided.';
    document.getElementById('modal-plan').textContent = startup.businessPlan || 'No business plan provided.';

    document.getElementById('modal-contact').textContent = startup.contactPerson || startup.name || 'N/A';
    document.getElementById('modal-email').textContent = startup.email || 'N/A';
    document.getElementById('modal-phone').textContent = startup.phone || 'N/A';

    displayDocuments(startup.documents || []);

    const modalFooter = document.querySelector('.modal-footer');
    const approveBtn = modalFooter.querySelector('.btn-approve');
    const rejectBtn = modalFooter.querySelector('.btn-reject');

    if (startup.registrationStatus === 'APPROVED') {
        approveBtn.style.display = 'none';
        rejectBtn.style.display = 'none';
    } else {
        approveBtn.style.display = 'inline-block';
        rejectBtn.style.display = 'inline-block';
    }
}

function displayDocuments(documents) {
    const documentList = document.getElementById('modal-documents');

    if (!documentList) {
        console.error('❌ Document list element not found');
        return;
    }

    console.log('📎 Displaying documents:', documents);

    if (!documents || documents.length === 0) {
        documentList.innerHTML = '<p style="color: #777;">No documents uploaded.</p>';
        return;
    }

    documentList.innerHTML = documents.map(doc => {
        const icon = getFileIcon(doc.fileType);
        const fileName = doc.fileName || 'Unknown file';
        const fileSize = formatFileSize(doc.fileSize);

        return `
            <div class="document-item">
                <div class="document-name">
                    <i class="${icon}"></i>
                    <span>${fileName}</span>
                    <small style="color: #999; margin-left: 10px;">(${fileSize})</small>
                </div>
                <div class="document-actions">
                    <button class="btn-icon btn-view" onclick="downloadDocument('${doc.id}', '${fileName}')">
                        <i class="fas fa-download"></i>
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

function getFileIcon(fileType) {
    if (!fileType) return 'fas fa-file-alt';

    const type = fileType.toUpperCase();
    if (type.includes('PDF')) return 'fas fa-file-pdf';
    if (type.includes('IMAGE') || type.includes('PNG') || type.includes('JPG')) return 'fas fa-file-image';
    if (type.includes('EXCEL') || type.includes('SPREADSHEET')) return 'fas fa-file-excel';
    if (type.includes('WORD') || type.includes('DOCUMENT')) return 'fas fa-file-word';
    if (type.includes('POWERPOINT') || type.includes('PRESENTATION')) return 'fas fa-file-powerpoint';
    if (type.includes('OTHER') || type.includes('UNKNOWN')) return 'fas fa-file';
    return 'fas fa-file-alt';
}

function formatFileSize(bytes) {
    if (!bytes || bytes === 0) return 'Unknown size';

    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i];
}

function closeStartupModal() {
    document.getElementById('startupModal').style.display = 'none';
}

// ========== APPROVAL/REJECTION ACTIONS ==========
async function approveStartup(startupId) {
    if (!confirm('Are you sure you want to approve this startup? An email notification will be sent to the startup.')) {
        return;
    }

    try {
        console.log('✅ Approving startup:', startupId);
        showLoading();

        const response = await fetch(`${API_BASE_URL}/admin/startups/approve`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                startupId: startupId,
                adminEmail: 'admin@launchpad.com'
            })
        });

        const data = await response.json();
        console.log('📤 Approval response:', data);

        if (data.success) {
            showSuccess(data.message || 'Startup approved successfully! An email has been sent to the startup.');
            closeStartupModal();
            loadStartups();
        } else {
            showError(data.message || 'Failed to approve startup');
        }

    } catch (error) {
        console.error('Error approving startup:', error);
        showError('Failed to approve startup. Please try again.');
    } finally {
        hideLoading();
    }
}

async function rejectStartup(startupId) {
    const comments = prompt('Please provide a reason for rejection (optional):');

    if (comments === null) {
        return;
    }

    if (!confirm('Are you sure you want to reject this startup? An email notification will be sent.')) {
        return;
    }

    try {
        console.log('❌ Rejecting startup:', startupId);
        showLoading();

        const response = await fetch(`${API_BASE_URL}/admin/startups/reject`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                startupId: startupId,
                comments: comments || 'Your application did not meet our current criteria.',
                adminEmail: 'admin@launchpad.com'
            })
        });

        const data = await response.json();
        console.log('📤 Rejection response:', data);

        if (data.success) {
            showSuccess(data.message || 'Startup rejected. A notification email has been sent.');
            closeStartupModal();
            loadStartups();
        } else {
            showError(data.message || 'Failed to reject startup');
        }

    } catch (error) {
        console.error('Error rejecting startup:', error);
        showError('Failed to reject startup. Please try again.');
    } finally {
        hideLoading();
    }
}

function handleModalApprove() {
    const startupId = document.getElementById('startupModal').dataset.startupId;
    if (startupId) {
        approveStartup(startupId);
    }
}

function handleModalReject() {
    const startupId = document.getElementById('startupModal').dataset.startupId;
    if (startupId) {
        rejectStartup(startupId);
    }
}

// ========== FILTER FUNCTIONALITY ==========
function setupFilters() {
    const filterSelects = document.querySelectorAll('.filter-select');

    filterSelects.forEach(select => {
        select.addEventListener('change', applyFilters);
    });

    const resetBtn = document.querySelector('.filters-header .btn-secondary');
    if (resetBtn) {
        resetBtn.addEventListener('click', resetFilters);
    }
}

function applyFilters() {
    const filters = {
        industry: document.getElementById('filter-industry')?.value || '',
        stage: document.getElementById('filter-stage')?.value || '',
        country: document.getElementById('filter-country')?.value || ''
    };

    console.log('🔍 Applying filters:', filters);

    Object.keys(filters).forEach(key => {
        if (!filters[key]) {
            delete filters[key];
        }
    });

    loadStartups(filters);
}

function resetFilters() {
    console.log('🔄 Resetting filters');
    const filterSelects = document.querySelectorAll('.filter-select');
    filterSelects.forEach(select => {
        select.value = '';
    });

    const searchInput = document.querySelector('.search-bar input');
    if (searchInput) {
        searchInput.value = '';
    }

    loadStartups();
}

// ========== SEARCH FUNCTIONALITY ==========
function setupEventListeners() {
    const searchInput = document.querySelector('.search-bar input');
    if (searchInput) {
        let searchTimeout;
        searchInput.addEventListener('input', function(e) {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                const searchTerm = e.target.value.trim();
                console.log('🔎 Searching for:', searchTerm);
                if (searchTerm.length >= 2 || searchTerm.length === 0) {
                    loadStartups({ search: searchTerm });
                }
            }, 500);
        });
    }

    const modalCloseBtn = document.querySelector('.modal-close');
    if (modalCloseBtn) {
        modalCloseBtn.addEventListener('click', closeStartupModal);
    }

    const modalApproveBtn = document.querySelector('.modal-footer .btn-approve');
    const modalRejectBtn = document.querySelector('.modal-footer .btn-reject');
    const modalCloseFooterBtn = document.querySelector('.modal-footer .btn-secondary');

    if (modalApproveBtn) {
        modalApproveBtn.addEventListener('click', handleModalApprove);
    }

    if (modalRejectBtn) {
        modalRejectBtn.addEventListener('click', handleModalReject);
    }

    if (modalCloseFooterBtn) {
        modalCloseFooterBtn.addEventListener('click', closeStartupModal);
    }

    window.addEventListener('click', function(event) {
        const modal = document.getElementById('startupModal');
        if (event.target === modal) {
            closeStartupModal();
        }
    });
}

// ========== STATISTICS ==========
async function updateStatistics() {
    try {
        const response = await fetch(`${API_BASE_URL}/admin/startups/stats`);
        const data = await response.json();

        if (data.success) {
            console.log('📊 Statistics:', data.data);

            const pendingSection = document.querySelector('.startups-section:first-of-type .section-title');
            const approvedSection = document.querySelector('.startups-section:last-of-type .section-title');
            const notificationBadge = document.querySelector('.notification-badge');

            if (pendingSection) {
                pendingSection.textContent = `Startup Requests (${data.data.pendingApprovals} Pending Approval)`;
            }

            if (approvedSection) {
                approvedSection.textContent = `Approved Startups (${data.data.approvedStartups})`;
            }

            if (notificationBadge) {
                notificationBadge.textContent = data.data.pendingApprovals;
            }
        }

    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

// ========== DOCUMENT DOWNLOAD ==========
async function downloadDocument(documentId, fileName) {
    try {
        showLoading();

        const response = await fetch(`${API_BASE_URL}/admin/startups/documents/${documentId}/download`);

        if (!response.ok) {
            throw new Error('Failed to download document');
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        showSuccess('Document downloaded successfully!');

    } catch (error) {
        console.error('Error downloading document:', error);
        showError('Failed to download document. Please try again.');
    } finally {
        hideLoading();
    }
}

// ========== UI HELPERS ==========
function showLoading() {
    let loader = document.getElementById('loading-overlay');
    if (!loader) {
        loader = document.createElement('div');
        loader.id = 'loading-overlay';
        loader.innerHTML = `
            <div style="position: fixed; top: 0; left: 0; width: 100%; height: 100%;
                        background: rgba(0,0,0,0.5); display: flex; align-items: center;
                        justify-content: center; z-index: 9999;">
                <div style="background: white; padding: 30px; border-radius: 10px;
                            box-shadow: 0 4px 20px rgba(0,0,0,0.3);">
                    <div style="border: 4px solid #f3f3f3; border-top: 4px solid #667eea;
                                border-radius: 50%; width: 40px; height: 40px;
                                animation: spin 1s linear infinite; margin: 0 auto;"></div>
                    <p style="margin-top: 15px; color: #333; font-weight: 600;">Loading...</p>
                </div>
            </div>
        `;
        document.body.appendChild(loader);

        if (!document.getElementById('spinner-style')) {
            const style = document.createElement('style');
            style.id = 'spinner-style';
            style.textContent = `
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
            `;
            document.head.appendChild(style);
        }
    }
    loader.style.display = 'block';
}

function hideLoading() {
    const loader = document.getElementById('loading-overlay');
    if (loader) {
        loader.style.display = 'none';
    }
}

function showSuccess(message) {
    showNotification(message, 'success');
}

function showError(message) {
    showNotification(message, 'error');
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === 'success' ? '#28a745' : type === 'error' ? '#dc3545' : '#667eea'};
        color: white;
        padding: 15px 25px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 10000;
        font-weight: 600;
        max-width: 400px;
        animation: slideIn 0.3s ease-out;
    `;

    const icon = type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ';
    notification.innerHTML = `<span style="margin-right: 10px;">${icon}</span>${message}`;

    document.body.appendChild(notification);

    if (!document.getElementById('notification-style')) {
        const style = document.createElement('style');
        style.id = 'notification-style';
        style.textContent = `
            @keyframes slideIn {
                from {
                    transform: translateX(400px);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            @keyframes slideOut {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(400px);
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(style);
    }

    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 4000);
}