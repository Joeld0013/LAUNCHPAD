// startupadmin.js - Complete functionality for startup admin page
const API_BASE_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Admin page loaded, initializing...');

    // Initialize page
    initializeNavigation();
    loadStartups();
    setupEventListeners();
    setupFilters();
});

// ========== NAVIGATION FUNCTIONALITY (ORIGINAL) ==========
function initializeNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    const actionButtons = document.querySelectorAll('.action-btn');

    // Frontend routes mapping - direct HTML file navigation
    const routes = {
        // Sidebar navigation - direct HTML files
        'dashboard': 'admindashboard.html',
        'startups': 'startupadmin.html',
        'investors': 'investors.html',
        'approvals': 'approvals.html',
        'analytics': 'analytics.html',
        'settings': 'settings.html',

        // Quick action buttons
        'register-startup': 'startupadmin.html?action=register',
        'browse-investors': 'investors.html',
        'review-approvals': 'approvals.html'
    };

    // Function to handle navigation
    function navigateTo(route) {
        // Direct frontend navigation to HTML files
        if (route.startsWith('/')) {
            route = route.substring(1); // Remove leading slash
        }

        // Check if we're already on the target page
        const currentPage = window.location.pathname.split('/').pop();
        if (currentPage === route) {
            // Already on the page, just update active state
            updateActiveNavItem(route);
            return;
        }

        // Navigate to the HTML file
        window.location.href = route;
    }

    // Function to update active navigation item
    function updateActiveNavItem(route) {
        // Remove active class from all items
        navItems.forEach(item => {
            item.classList.remove('active');
        });

        // Determine which nav item should be active
        let activeNavId = '';
        const currentPage = route.split('?')[0]; // Remove query parameters

        if (currentPage.includes('admindashboard.html') || currentPage === 'admindashboard.html' || currentPage === '' || currentPage === 'dashboard') {
            activeNavId = 'dashboard';
        } else if (currentPage.includes('startupadmin.html') || currentPage.includes('startups')) {
            activeNavId = 'startups';
        } else if (currentPage.includes('investors.html') || currentPage.includes('investors')) {
            activeNavId = 'investors';
        } else if (currentPage.includes('approvals.html') || currentPage.includes('approvals')) {
            activeNavId = 'approvals';
        } else if (currentPage.includes('analytics.html') || currentPage.includes('analytics')) {
            activeNavId = 'analytics';
        } else if (currentPage.includes('settings.html') || currentPage.includes('settings')) {
            activeNavId = 'settings';
        }

        // Add active class to the correct nav item
        if (activeNavId) {
            const activeItem = document.querySelector(`.nav-item[data-route="${activeNavId}"]`);
            if (activeItem) {
                activeItem.classList.add('active');
            }
        }
    }

    // Function to show navigation feedback
    function showNavigationFeedback(route) {
        const pageName = route.split('?')[0].replace('.html', '') || 'Dashboard';
        const notification = document.createElement('div');
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: #edb96f;
            color: #2b3446;
            padding: 12px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            z-index: 1000;
            font-weight: 600;
            transition: all 0.3s ease;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        `;

        const formattedPageName = pageName.charAt(0).toUpperCase() + pageName.slice(1).replace('admin', ' Admin');
        notification.textContent = `Navigating to ${formattedPageName}...`;

        document.body.appendChild(notification);

        // Remove notification after 2 seconds
        setTimeout(() => {
            notification.style.opacity = '0';
            notification.style.transform = 'translateX(100px)';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 1500);
    }

    // Sidebar nav click handling
    navItems.forEach(item => {
        const routeKey = item.querySelector('.nav-text').textContent.toLowerCase().trim();
        item.setAttribute('data-route', routeKey);

        item.addEventListener('click', function() {
            const route = routes[routeKey];
            if (route) {
                showNavigationFeedback(route);
                // Small delay to show the notification before navigation
                setTimeout(() => {
                    navigateTo(route);
                }, 100);
            }
        });
    });

    // Quick action button handling
    actionButtons.forEach(button => {
        let actionKey = '';
        const buttonText = button.textContent.toLowerCase().trim();

        if (buttonText.includes('register startup')) {
            actionKey = 'register-startup';
        } else if (buttonText.includes('browse investors')) {
            actionKey = 'browse-investors';
        } else if (buttonText.includes('review approvals')) {
            actionKey = 'review-approvals';
        }

        if (actionKey) {
            button.addEventListener('click', function() {
                const route = routes[actionKey];
                if (route) {
                    showNavigationFeedback(route);
                    setTimeout(() => {
                        navigateTo(route);
                    }, 100);
                }
            });
        }
    });

    // Set initial active state based on current page
    function setInitialActiveState() {
        const currentPage = window.location.pathname.split('/').pop();
        updateActiveNavItem(currentPage);
    }

    // Initialize navigation
    setInitialActiveState();

    // Search functionality
    const searchInput = document.querySelector('.search-bar input');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                const searchTerm = this.value.trim();
                if (searchTerm) {
                    alert(`Searching for: ${searchTerm}`);
                    this.value = '';
                }
            }
        });
    }

    // Notification dropdown
    const notificationIcon = document.querySelector('.notification');
    if (notificationIcon) {
        notificationIcon.addEventListener('click', function() {
            alert('Notifications dropdown would appear here');
        });
    }

    // User profile dropdown
    const userProfile = document.querySelector('.user-profile');
    if (userProfile) {
        userProfile.addEventListener('click', function() {
            alert('User profile menu would appear here');
        });
    }
}

// ========== DATA LOADING ==========

/**
 * Load all startups from backend
 */
async function loadStartups(filters = {}) {
    try {
        console.log('📡 Loading startups with filters:', filters);
        showLoading();

        // Build query parameters
        const params = new URLSearchParams();
        if (filters.industry) params.append('industry', filters.industry);
        if (filters.stage) params.append('stage', filters.stage);
        if (filters.country) params.append('country', filters.country);
        if (filters.status) params.append('status', filters.status);
        if (filters.search) params.append('search', filters.search);

        const url = `${API_BASE_URL}/admin/startups?${params.toString()}`;
        console.log('🔗 Fetching from:', url);

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

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

/**
 * Display startups in the grid
 */
function displayStartups(startups) {
    console.log('🎨 Displaying startups:', startups.length);

    // Separate pending and approved startups
    const pendingStartups = startups.filter(s => s.registrationStatus === 'PENDING');
    const approvedStartups = startups.filter(s => s.registrationStatus === 'APPROVED');

    console.log(`📋 Pending: ${pendingStartups.length}, Approved: ${approvedStartups.length}`);

    // Display pending startups
    const pendingGrid = document.getElementById('pending-startups');
    if (pendingGrid) {
        if (pendingStartups.length === 0) {
            pendingGrid.innerHTML = '<p style="text-align: center; color: #777; padding: 40px; grid-column: 1/-1;">No pending startup requests at the moment.</p>';
        } else {
            pendingGrid.innerHTML = pendingStartups.map(startup => createStartupCard(startup, true)).join('');
        }
    } else {
        console.error('❌ Pending grid element not found');
    }

    // Display approved startups
    const approvedGrid = document.getElementById('approved-startups');
    if (approvedGrid) {
        if (approvedStartups.length === 0) {
            approvedGrid.innerHTML = '<p style="text-align: center; color: #777; padding: 40px; grid-column: 1/-1;">No approved startups yet.</p>';
        } else {
            approvedGrid.innerHTML = approvedStartups.map(startup => createStartupCard(startup, false)).join('');
        }
    } else {
        console.error('❌ Approved grid element not found');
    }
}

/**
 * Create HTML for a startup card
 */
function createStartupCard(startup, showActions = true) {
    const statusClass = startup.registrationStatus === 'APPROVED' ? 'status-approved' : 'status-pending';
    const statusText = startup.registrationStatus === 'APPROVED' ? 'Approved' : 'Pending Review';

    // Get initials for logo
    const initials = startup.name.split(' ').map(word => word[0]).join('').substring(0, 2).toUpperCase();

    // Format stage
    const stageFormatted = startup.stage ? startup.stage.charAt(0).toUpperCase() + startup.stage.slice(1) : 'N/A';

    // Format location
    const location = startup.address || startup.country || 'Location not specified';

    // Founded date - handle both Date and string formats
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

/**
 * Open startup detail modal
 */
async function openStartupModal(startupId) {
    try {
        console.log('🔍 Opening modal for startup:', startupId);
        showLoading();

        const response = await fetch(`${API_BASE_URL}/admin/startups/${startupId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

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

/**
 * Populate modal with startup data
 */
function populateModal(startup) {
    console.log('📝 Populating modal with:', startup);

    // Store startup ID in modal for approve/reject actions
    document.getElementById('startupModal').dataset.startupId = startup.id;

    // Basic information
    document.getElementById('modal-name').textContent = startup.name || 'N/A';
    document.getElementById('modal-category').textContent = startup.industry || 'N/A';
    document.getElementById('modal-industry').textContent = startup.industry || 'N/A';
    document.getElementById('modal-founded').textContent = startup.foundedDate || 'N/A';
    document.getElementById('modal-location').textContent = startup.address || startup.country || 'N/A';
    document.getElementById('modal-stage').textContent = startup.stage || 'N/A';
    document.getElementById('modal-team').textContent = startup.teamSize || 'N/A';
    document.getElementById('modal-website').textContent = startup.website || 'N/A';

    // Description and business plan
    document.getElementById('modal-description').textContent = startup.description || 'No description provided.';
    document.getElementById('modal-plan').textContent = startup.businessPlan || 'No business plan provided.';

    // Contact information
    document.getElementById('modal-contact').textContent = startup.contactPerson || startup.name || 'N/A';
    document.getElementById('modal-email').textContent = startup.email || 'N/A';
    document.getElementById('modal-phone').textContent = startup.phone || 'N/A';

    // Documents
    displayDocuments(startup.documents || []);

    // Show/hide approve/reject buttons based on status
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

/**
 * Display documents in modal
 */
function displayDocuments(documents) {
    const documentList = document.getElementById('modal-documents');

    if (!documentList) {
        console.error('❌ Document list element not found');
        return;
    }

    if (!documents || documents.length === 0) {
        documentList.innerHTML = '<p style="color: #777;">No documents uploaded.</p>';
        return;
    }

    documentList.innerHTML = documents.map(doc => {
        const icon = getFileIcon(doc.fileType);
        const fileName = doc.fileName || 'Unknown file';

        return `
            <div class="document-item">
                <div class="document-name">
                    <i class="${icon}"></i>
                    <span>${fileName}</span>
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

/**
 * Get appropriate icon for file type
 */
function getFileIcon(fileType) {
    if (!fileType) return 'fas fa-file-alt';

    const type = fileType.toUpperCase();
    if (type.includes('PDF')) return 'fas fa-file-pdf';
    if (type.includes('IMAGE') || type.includes('PNG') || type.includes('JPG')) return 'fas fa-file-image';
    if (type.includes('EXCEL') || type.includes('SPREADSHEET')) return 'fas fa-file-excel';
    if (type.includes('WORD') || type.includes('DOCUMENT')) return 'fas fa-file-word';
    if (type.includes('POWERPOINT') || type.includes('PRESENTATION')) return 'fas fa-file-powerpoint';
    return 'fas fa-file-alt';
}

/**
 * Close startup modal
 */
function closeStartupModal() {
    document.getElementById('startupModal').style.display = 'none';
}

// ========== APPROVAL/REJECTION ACTIONS ==========

/**
 * Approve a startup
 */
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
            loadStartups(); // Reload the list
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

/**
 * Reject a startup
 */
async function rejectStartup(startupId) {
    const comments = prompt('Please provide a reason for rejection (optional):');

    if (comments === null) {
        return; // User cancelled
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
            loadStartups(); // Reload the list
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

/**
 * Modal approve button handler
 */
function handleModalApprove() {
    const startupId = document.getElementById('startupModal').dataset.startupId;
    if (startupId) {
        approveStartup(startupId);
    }
}

/**
 * Modal reject button handler
 */
function handleModalReject() {
    const startupId = document.getElementById('startupModal').dataset.startupId;
    if (startupId) {
        rejectStartup(startupId);
    }
}

// ========== FILTER FUNCTIONALITY ==========

/**
 * Setup filter controls
 */
function setupFilters() {
    const filterSelects = document.querySelectorAll('.filter-select');

    filterSelects.forEach(select => {
        select.addEventListener('change', applyFilters);
    });

    // Reset filters button
    const resetBtn = document.querySelector('.filters-header .btn-secondary');
    if (resetBtn) {
        resetBtn.addEventListener('click', resetFilters);
    }
}

/**
 * Apply filters
 */
function applyFilters() {
    const filters = {
        industry: document.getElementById('filter-industry')?.value || '',
        stage: document.getElementById('filter-stage')?.value || '',
        country: document.getElementById('filter-country')?.value || ''
    };

    console.log('🔍 Applying filters:', filters);

    // Remove empty filters
    Object.keys(filters).forEach(key => {
        if (!filters[key]) {
            delete filters[key];
        }
    });

    loadStartups(filters);
}

/**
 * Reset all filters
 */
function resetFilters() {
    console.log('🔄 Resetting filters');
    const filterSelects = document.querySelectorAll('.filter-select');
    filterSelects.forEach(select => {
        select.value = '';
    });

    loadStartups();
}

// ========== SEARCH FUNCTIONALITY ==========

/**
 * Setup search
 */
function setupEventListeners() {
    // Search functionality
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

    // Modal close button
    const modalCloseBtn = document.querySelector('.modal-close');
    if (modalCloseBtn) {
        modalCloseBtn.addEventListener('click', closeStartupModal);
    }

    // Modal footer buttons
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

    // Close modal when clicking outside
    window.addEventListener('click', function(event) {
        const modal = document.getElementById('startupModal');
        if (event.target === modal) {
            closeStartupModal();
        }
    });

    // Add New Startup button
    const addStartupBtn = document.querySelector('.btn-primary');
    if (addStartupBtn) {
        addStartupBtn.addEventListener('click', function() {
            alert('Add New Startup functionality would open here');
        });
    }

    // Export Data button
    const exportBtn = document.querySelector('.btn-secondary');
    if (exportBtn && exportBtn.textContent.includes('Export Data')) {
        exportBtn.addEventListener('click', function() {
            alert('Export Data functionality would trigger here');
        });
    }
}

// ========== STATISTICS ==========

/**
 * Update statistics display
 */
async function updateStatistics() {
    try {
        const response = await fetch(`${API_BASE_URL}/admin/startups/stats`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const data = await response.json();

        if (data.success) {
            console.log('📊 Statistics:', data.data);

            // Update section titles with counts
            const pendingSection = document.querySelector('.startups-section:first-of-type .section-title');
            const approvedSection = document.querySelector('.startups-section:last-of-type .section-title');

            // Update notification badge
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

/**
 * Download document
 */
async function downloadDocument(documentId, fileName) {
    try {
        showLoading();

        const response = await fetch(`${API_BASE_URL}/admin/startups/documents/${documentId}/download`, {
            method: 'GET'
        });

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

/**
 * Show loading indicator
 */
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

        // Add keyframes for spinner
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

/**
 * Hide loading indicator
 */
function hideLoading() {
    const loader = document.getElementById('loading-overlay');
    if (loader) {
        loader.style.display = 'none';
    }
}

/**
 * Show success message
 */
function showSuccess(message) {
    showNotification(message, 'success');
}

/**
 * Show error message
 */
function showError(message) {
    showNotification(message, 'error');
}

/**
 * Show notification
 */
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

    // Add slide-in animation
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

    // Remove after 4 seconds
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 4000);
}

// ========== GLOBAL FUNCTIONS ==========

// Make functions globally available for onclick handlers
window.openStartupModal = openStartupModal;
window.closeStartupModal = closeStartupModal;
window.approveStartup = approveStartup;
window.rejectStartup = rejectStartup;
window.downloadDocument = downloadDocument;
window.handleModalApprove = handleModalApprove;
window.handleModalReject = handleModalReject;

console.log('✅ startupadmin.js loaded successfully');