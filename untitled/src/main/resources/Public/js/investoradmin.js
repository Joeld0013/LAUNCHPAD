// ========== INVESTOR ADMIN - COMPLETE FINAL JAVASCRIPT ==========
const API_BASE_URL = 'http://localhost:8080/api/admin/investors';

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 ========== INVESTOR ADMIN INITIALIZED ==========');
    initializeApp();
});

// ========== INITIALIZATION ==========
function initializeApp() {
    console.log('⚙️ Initializing application...');
    loadInvestors();
    setupEventListeners();
    setupNavigation();
    console.log('✓ Application initialized');
}

// ========== API CALLS ==========

/**
 * Fetch all investors from backend
 */
async function fetchInvestors() {
    try {
        console.log('📥 Fetching all investors from:', API_BASE_URL);

        const response = await fetch(API_BASE_URL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'omit'
        });

        console.log('✓ Response status:', response.status);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        const result = await response.json();
        console.log('✓ Fetched investors count:', result.data?.length || 0);
        return result.data || result || [];
    } catch (error) {
        console.error('❌ Error fetching investors:', error);
        showNotification('Error loading investors: ' + error.message, 'error');
        return [];
    }
}

/**
 * Fetch detailed investor information including documents
 */
async function fetchInvestorDetails(investorId) {
    try {
        console.log('📥 Fetching investor details for ID:', investorId);
        console.log('🔗 URL:', `${API_BASE_URL}/${investorId}/details`);

        const response = await fetch(`${API_BASE_URL}/${investorId}/details`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'omit'
        });

        console.log('✓ Response status:', response.status);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        const result = await response.json();
        console.log('✓ Investor details fetched successfully');
        console.log('📋 Full response:', result);
        console.log('📄 Documents in response:', result.documents);
        console.log('📄 Documents type:', typeof result.documents);
        console.log('📄 Documents is array:', Array.isArray(result.documents));
        console.log('📄 Documents count:', result.documents?.length || 0);

        return result || null;
    } catch (error) {
        console.error('❌ Error fetching investor details:', error);
        showNotification('Error loading investor details: ' + error.message, 'error');
        return null;
    }
}

/**
 * Approve investor
 */
async function approveInvestor(investorId) {
    try {
        console.log('✅ APPROVING INVESTOR');
        console.log('🔗 Endpoint: POST', `${API_BASE_URL}/${investorId}/approve`);

        const response = await fetch(`${API_BASE_URL}/${investorId}/approve`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'omit'
        });

        console.log('✓ Response status:', response.status);

        if (!response.ok) {
            const errorText = await response.text();
            console.error('❌ HTTP Error response:', response.status, errorText);
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        const result = await response.json();
        console.log('✓ Approval response:', result);
        showNotification(result.message || 'Investor approved successfully', 'success');
        loadInvestors();
        closeInvestorModal();
        return true;
    } catch (error) {
        console.error('❌ Error approving investor:', error);
        showNotification('Error approving investor: ' + error.message, 'error');
        return false;
    }
}

/**
 * Reject investor
 */
async function rejectInvestor(investorId, reason) {
    try {
        console.log('❌ REJECTING INVESTOR');
        console.log('🔗 Endpoint: POST', `${API_BASE_URL}/${investorId}/reject`);
        console.log('📝 Reason:', reason);

        const response = await fetch(`${API_BASE_URL}/${investorId}/reject?reason=${encodeURIComponent(reason)}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'omit'
        });

        console.log('✓ Response status:', response.status);

        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const result = await response.json();
        console.log('✓ Rejection response:', result);
        showNotification(result.message || 'Investor rejected successfully', 'success');
        loadInvestors();
        closeInvestorModal();
        return true;
    } catch (error) {
        console.error('❌ Error rejecting investor:', error);
        showNotification('Error rejecting investor: ' + error.message, 'error');
        return false;
    }
}

/**
 * Download document
 */
async function downloadDocument(documentId, fileName) {
    try {
        console.log('⬇️ DOWNLOADING DOCUMENT');
        console.log('📄 Document ID:', documentId);
        console.log('📝 File name:', fileName);
        console.log('🔗 URL:', `${API_BASE_URL}/document/${documentId}/download`);

        const response = await fetch(`${API_BASE_URL}/document/${documentId}/download`, {
            method: 'GET',
            credentials: 'omit'
        });

        console.log('✓ Response status:', response.status);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: Failed to download`);
        }

        const blob = await response.blob();
        console.log('✓ Blob received, size:', blob.size, 'bytes');

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName || 'document';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        console.log('✓ Download initiated for:', fileName);
        showNotification('Document downloaded successfully', 'success');
    } catch (error) {
        console.error('❌ Error downloading document:', error);
        showNotification('Error downloading document: ' + error.message, 'error');
    }
}

/**
 * View document in browser
 */
async function viewDocument(documentId) {
    try {
        console.log('👁️ VIEWING DOCUMENT');
        console.log('📄 Document ID:', documentId);
        console.log('🔗 URL:', `${API_BASE_URL}/document/${documentId}/view`);

        const response = await fetch(`${API_BASE_URL}/document/${documentId}/view`, {
            method: 'GET',
            credentials: 'omit'
        });

        console.log('✓ Response status:', response.status);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: Failed to view`);
        }

        const blob = await response.blob();
        console.log('✓ Document blob received, size:', blob.size);

        const url = window.URL.createObjectURL(blob);
        console.log('✓ Opening document in new window');

        const newWindow = window.open(url, '_blank');
        if (!newWindow) {
            showNotification('Please allow pop-ups to view documents', 'error');
        }
    } catch (error) {
        console.error('❌ Error viewing document:', error);
        showNotification('Error viewing document: ' + error.message, 'error');
    }
}

// ========== LOAD AND RENDER FUNCTIONS ==========

/**
 * Load all investors and display them
 */
async function loadInvestors() {
    console.log('📂 ========== LOADING INVESTORS ==========');
    const investors = await fetchInvestors();

    const pendingGrid = document.querySelector('.investors-section:nth-child(4) .investors-grid');
    const approvedGrid = document.querySelector('.investors-section:nth-child(5) .investors-grid');

    if (pendingGrid) pendingGrid.innerHTML = '';
    if (approvedGrid) approvedGrid.innerHTML = '';

    let pendingCount = 0;
    let approvedCount = 0;

    investors.forEach(investor => {
        const card = createInvestorCard(investor);
        if (investor.registrationStatus === 'PENDING') {
            if (pendingGrid) pendingGrid.appendChild(card);
            pendingCount++;
        } else if (investor.registrationStatus === 'APPROVED') {
            if (approvedGrid) approvedGrid.appendChild(card);
            approvedCount++;
        }
    });

    // Update badges
    const badges = document.querySelectorAll('.section-title .badge');
    if (badges.length >= 2) {
        badges[0].textContent = pendingCount;
        badges[1].textContent = approvedCount;
    }

    updateStats(investors);
    console.log(`✓ Loaded ${investors.length} investors (${pendingCount} pending, ${approvedCount} approved)`);
    console.log('========== LOADING COMPLETE ==========\n');
}

/**
 * Create investor card HTML element
 */
function createInvestorCard(investor) {
    const card = document.createElement('div');
    card.className = 'investor-card';

    const investorTypeLabel = getInvestorTypeLabel(investor.investorType);
    const statusClass = investor.registrationStatus.toLowerCase();
    const statusText = investor.registrationStatus === 'PENDING' ? 'Pending Review' : 'Approved';
    const investorId = investor._id || investor.id;

    const preferences = investor.preferences ? investor.preferences.split(', ').map(p =>
        `<span class="preference-tag">${p.trim()}</span>`
    ).join('') : '';

    const actionButtons = investor.registrationStatus === 'PENDING' ? `
        <div class="btn-icon btn-approve" data-id="${investorId}" title="Approve">
            <i class="fas fa-check"></i>
        </div>
        <div class="btn-icon btn-reject" data-id="${investorId}" title="Reject">
            <i class="fas fa-times"></i>
        </div>
    ` : '';

    card.innerHTML = `
        <div class="investor-header">
            <div class="investor-avatar">${investor.name.charAt(0).toUpperCase()}</div>
            <div class="investor-info">
                <h3 class="investor-name">${investor.name}</h3>
                <p class="investor-company">${investor.organization}</p>
                <span class="investor-type">${investorTypeLabel}</span>
            </div>
        </div>
        <div class="investor-body">
            <div class="investor-details">
                <div class="detail-item">
                    <span class="detail-label">Email</span>
                    <span class="detail-value">${investor.email}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Phone</span>
                    <span class="detail-value">${investor.phone}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Country</span>
                    <span class="detail-value">${investor.country}</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Investment Range</span>
                    <span class="detail-value">${investor.investmentRange || 'N/A'}</span>
                </div>
                ${preferences ? `
                <div class="detail-item">
                    <span class="detail-label">Preferences</span>
                    <div class="preferences-tags">${preferences}</div>
                </div>
                ` : ''}
            </div>
        </div>
        <div class="investor-footer">
            <span class="status-badge status-${statusClass}">
                ${statusText}
            </span>
            <div class="action-buttons">
                <div class="btn-icon btn-view" data-id="${investorId}" title="View Details">
                    <i class="fas fa-eye"></i>
                </div>
                ${actionButtons}
            </div>
        </div>
    `;

    return card;
}

/**
 * Convert investor type code to label
 */
function getInvestorTypeLabel(type) {
    const types = {
        'vc': 'Venture Capital',
        'angel': 'Angel Investor',
        'corporate': 'Corporate Investor',
        'private-equity': 'Private Equity'
    };
    return types[type] || (type ? type.charAt(0).toUpperCase() + type.slice(1) : 'Investor');
}

// ========== MODAL FUNCTIONS ==========

/**
 * Open investor details modal
 */
async function openInvestorModal(investorId) {
    console.log('\n🔓 ========== OPENING MODAL ==========');
    console.log('👤 Investor ID:', investorId);

    const details = await fetchInvestorDetails(investorId);

    if (!details) {
        console.error('❌ Failed to get investor details');
        console.log('========== MODAL OPEN FAILED ==========\n');
        return;
    }

    console.log('✓ Details retrieved successfully');
    console.log('📋 Investor Name:', details.name);
    console.log('📄 Has documents field:', 'documents' in details);
    console.log('📄 Documents count:', details.documents?.length || 0);

    const modalBody = document.querySelector('#investorModal .modal-body');
    if (!modalBody) {
        console.error('❌ Modal body not found');
        return;
    }

    modalBody.innerHTML = createModalContent(details);
    document.getElementById('investorModal').style.display = 'flex';

    // Attach event listeners AFTER content is rendered
    setTimeout(() => {
        attachModalEventListeners(details._id || investorId);
        console.log('========== MODAL OPEN COMPLETE ==========\n');
    }, 100);
}

/**
 * Create modal content HTML
 */
function createModalContent(investor) {
    console.log('📝 Creating modal content...');

    const createdDate = investor.createdAt ? new Date(investor.createdAt).toLocaleDateString() : 'N/A';
    let documentsHTML = '';

    // Build documents section
    if (investor.documents && Array.isArray(investor.documents) && investor.documents.length > 0) {
        console.log('✓ Found', investor.documents.length, 'documents');
        documentsHTML = `
            <div class="investor-detail-section">
                <h3 class="detail-section-title">Documents (${investor.documents.length})</h3>
                <div class="document-list">
                    ${investor.documents.map((doc, index) => {
                        console.log(`  📄 Document ${index + 1}:`, doc.fileName, '-', doc.fileType);
                        return `
                            <div class="document-item" data-doc-id="${doc._id}">
                                <div class="document-name">
                                    <i class="fas fa-file-${getFileIcon(doc.fileType)}"></i>
                                    <div>
                                        <span>${doc.fileName || 'Document'}</span>
                                        <small style="color: #999; display: block; margin-top: 4px;">
                                            ${doc.fileSize ? formatFileSize(doc.fileSize) : 'Unknown size'} - ${doc.uploadedAt ? new Date(doc.uploadedAt).toLocaleDateString() : 'Unknown date'}
                                        </small>
                                    </div>
                                </div>
                                <div class="document-actions">
                                    <button class="btn-icon btn-view-doc" data-doc-id="${doc._id}" data-doc-name="${doc.fileName}" type="button" style="cursor: pointer; background: none; border: none; padding: 8px; color: #666;">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                    <button class="btn-icon btn-download-doc" data-doc-id="${doc._id}" data-doc-name="${doc.fileName}" type="button" style="cursor: pointer; background: none; border: none; padding: 8px; color: #666;">
                                        <i class="fas fa-download"></i>
                                    </button>
                                </div>
                            </div>
                        `;
                    }).join('')}
                </div>
            </div>
        `;
    } else {
        console.log('⚠️ No documents available');
        documentsHTML = `
            <div class="investor-detail-section">
                <h3 class="detail-section-title">Documents</h3>
                <p style="color: #999; text-align: center; padding: 20px; background: #f5f5f5; border-radius: 6px;">
                    <i class="fas fa-file-slash"></i> No documents uploaded
                </p>
            </div>
        `;
    }

    return `
        <div class="investor-detail-section">
            <h3 class="detail-section-title">Basic Information</h3>
            <div class="detail-grid">
                <div class="detail-field">
                    <div class="detail-label">Investor Name</div>
                    <div class="detail-value">${investor.name}</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Email</div>
                    <div class="detail-value">${investor.email}</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Phone</div>
                    <div class="detail-value">${investor.phone}</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Organization</div>
                    <div class="detail-value">${investor.organization}</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Country</div>
                    <div class="detail-value">${investor.country}</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Address</div>
                    <div class="detail-value">${(investor.address || '').replace(/\n/g, ', ')}</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Investor Type</div>
                    <div class="detail-value">${getInvestorTypeLabel(investor.investorType)}</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Registration Date</div>
                    <div class="detail-value">${createdDate}</div>
                </div>
            </div>
        </div>

        <div class="investor-detail-section">
            <h3 class="detail-section-title">Investment Preferences</h3>
            <div class="detail-grid">
                <div class="detail-field">
                    <div class="detail-label">Preferred Industries</div>
                    <div class="detail-value">${investor.preferences || 'N/A'}</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Investment Range</div>
                    <div class="detail-value">${investor.investmentRange || 'N/A'}</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Geographic Focus</div>
                    <div class="detail-value">${investor.country}, Global</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Stage Preference</div>
                    <div class="detail-value">Seed, Series A, Series B</div>
                </div>
            </div>
        </div>

        ${investor.pastInvestments && investor.pastInvestments.length > 0 ? `
        <div class="investor-detail-section">
            <h3 class="detail-section-title">Past Investments</h3>
            <div class="detail-field">
                <div class="detail-value" style="min-height: auto; flex-direction: column; align-items: flex-start;">
                    ${investor.pastInvestments.map(inv =>
                        `<div style="margin-bottom: 10px; padding: 10px; background: #f5f5f5; border-radius: 8px; width: 100%; border-left: 4px solid #edb96f;">
                            <strong>${inv.name}</strong> - ${inv.amount} (${inv.year})<br>
                            <small style="color: #666;">${inv.description}</small>
                        </div>`
                    ).join('')}
                </div>
            </div>
        </div>
        ` : ''}

        ${documentsHTML}

        <div class="investor-detail-section">
            <h3 class="detail-section-title">Contact Information</h3>
            <div class="detail-grid">
                <div class="detail-field">
                    <div class="detail-label">Direct Contact</div>
                    <div class="detail-value">${investor.name}</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Email</div>
                    <div class="detail-value">${investor.email}</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Phone</div>
                    <div class="detail-value">${investor.phone}</div>
                </div>
                <div class="detail-field">
                    <div class="detail-label">Preferred Contact Method</div>
                    <div class="detail-value">Email</div>
                </div>
            </div>
        </div>

        ${investor.rejectionReason ? `
        <div class="investor-detail-section" style="background-color: #fff3cd; border-left: 4px solid #ff6b6b;">
            <h3 class="detail-section-title">Rejection Reason</h3>
            <div class="detail-field">
                <div class="detail-value" style="color: #856404;">
                    ${investor.rejectionReason}
                </div>
            </div>
        </div>
        ` : ''}
    `;
}

/**
 * Get file type icon
 */
function getFileIcon(fileType) {
    if (!fileType) return 'solid';
    const icons = {
        'pdf': 'pdf',
        'doc': 'word',
        'docx': 'word',
        'xls': 'excel',
        'xlsx': 'excel',
        'zip': 'archive',
        'jpg': 'image',
        'jpeg': 'image',
        'png': 'image'
    };
    return icons[fileType.toLowerCase()] || 'solid';
}

/**
 * Format file size to readable format
 */
function formatFileSize(bytes) {
    if (!bytes || bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

/**
 * Attach event listeners to modal buttons
 */
function attachModalEventListeners(investorId) {
    console.log('🔗 Attaching modal event listeners for investor:', investorId);
    const modal = document.getElementById('investorModal');

    if (!modal) {
        console.error('❌ Modal element not found');
        return;
    }

    // View document buttons
    const viewButtons = modal.querySelectorAll('.btn-view-doc');
    console.log('👁️ Found', viewButtons.length, 'view buttons');

    viewButtons.forEach((btn, idx) => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            const docId = this.getAttribute('data-doc-id');
            const docName = this.getAttribute('data-doc-name');
            console.log(`👁️ View button ${idx + 1} clicked - ID: ${docId}, Name: ${docName}`);
            viewDocument(docId);
        });
    });

    // Download document buttons
    const downloadButtons = modal.querySelectorAll('.btn-download-doc');
    console.log('⬇️ Found', downloadButtons.length, 'download buttons');

    downloadButtons.forEach((btn, idx) => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            const docId = this.getAttribute('data-doc-id');
            const docName = this.getAttribute('data-doc-name');
            console.log(`⬇️ Download button ${idx + 1} clicked - ID: ${docId}, Name: ${docName}`);
            downloadDocument(docId, docName);
        });
    });

    // Approve button
    const approveBtn = modal.querySelector('.btn-approve');
    if (approveBtn) {
        approveBtn.onclick = function() {
            console.log('✅ Approve button clicked');
            if (confirm('Are you sure you want to APPROVE this investor?')) {
                approveInvestor(investorId);
            }
        };
    }

    // Reject button
    const rejectBtn = modal.querySelector('.btn-reject');
    if (rejectBtn) {
        rejectBtn.onclick = function() {
            console.log('❌ Reject button clicked');
            const reason = prompt('Please enter the rejection reason:');
            if (reason !== null && reason.trim() !== '') {
                rejectInvestor(investorId, reason);
            }
        };
    }

    console.log('✓ Event listeners attached successfully\n');
}

/**
 * Close investor modal
 */
function closeInvestorModal() {
    const modal = document.getElementById('investorModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// ========== EVENT LISTENERS ==========

/**
 * Setup all event listeners
 */
function setupEventListeners() {
    console.log('🔌 Setting up event listeners...');

    document.addEventListener('click', function(e) {
        // View investor details
        if (e.target.closest('.btn-view')) {
            const investorId = e.target.closest('.btn-view').getAttribute('data-id');
            if (investorId) {
                console.log('👁️ View clicked for investor:', investorId);
                openInvestorModal(investorId);
            }
        }

        // Approve investor from card
        if (e.target.closest('.investor-card .btn-approve')) {
            const investorId = e.target.closest('.btn-approve').getAttribute('data-id');
            if (investorId && confirm('Are you sure you want to APPROVE this investor?')) {
                approveInvestor(investorId);
            }
        }

        // Reject investor from card
        if (e.target.closest('.investor-card .btn-reject')) {
            const investorId = e.target.closest('.btn-reject').getAttribute('data-id');
            if (investorId) {
                const reason = prompt('Please enter the rejection reason:');
                if (reason !== null && reason.trim() !== '') {
                    rejectInvestor(investorId, reason);
                }
            }
        }
    });

    // Modal close on background click
    window.onclick = function(event) {
        const modal = document.getElementById('investorModal');
        if (event.target === modal) {
            closeInvestorModal();
        }
    };

    // Search functionality
    const searchInput = document.querySelector('.search-bar input');
    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase();
            document.querySelectorAll('.investor-card').forEach(card => {
                const visible = card.textContent.toLowerCase().includes(searchTerm);
                card.style.display = visible ? 'block' : 'none';
            });
        });
    }

    console.log('✓ Event listeners setup complete');
}

// ========== NAVIGATION ==========

/**
 * Setup navigation between pages
 */
function setupNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    const routes = {
        'dashboard': 'admindashboard.html',
        'startups': 'startupadmin.html',
        'investors': 'investoradmin.html',
        'approvals': 'approvals.html',
        'analytics': 'analytics.html',
        'settings': 'settings.html'
    };

    navItems.forEach(item => {
        item.addEventListener('click', function() {
            const routeKey = this.getAttribute('data-route');
            const route = routes[routeKey];

            if (route && !route.includes(window.location.pathname.split('/').pop())) {
                showNotification(`Navigating to ${routeKey.charAt(0).toUpperCase() + routeKey.slice(1)}...`, 'info');
                setTimeout(() => {
                    window.location.href = route;
                }, 300);
            }
        });
    });
}

// ========== UTILITY FUNCTIONS ==========

/**
 * Update statistics display
 */
function updateStats(investors) {
    const pending = investors.filter(i => i.registrationStatus === 'PENDING').length;
    const approved = investors.filter(i => i.registrationStatus === 'APPROVED').length;
    const total = investors.length;

    const statValues = document.querySelectorAll('.stat-value');
    if (statValues.length >= 3) {
        statValues[0].textContent = total;
        statValues[1].textContent = pending;
        statValues[2].textContent = approved;
    }
}

/**
 * Show notification message
 */
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            z-index: 1000;
            font-weight: 500;
            animation: slideIn 0.3s ease-out;
            font-family: 'Inter', sans-serif;
        `;

        const colors = {
            'success': { bg: '#d4edda', color: '#155724', border: '#c3e6cb' },
            'error': { bg: '#f8d7da', color: '#721c24', border: '#f5c6cb' },
            'info': { bg: '#d1ecf1', color: '#0c5460', border: '#bee5eb' }
        };

        const colorScheme = colors[type] || colors['info'];
        notification.style.backgroundColor = colorScheme.bg;
        notification.style.color = colorScheme.color;
        notification.style.borderLeft = `4px solid ${colorScheme.border}`;
        notification.textContent = message;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease-in';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 3000);
    }

    // Add CSS animations
    const style = document.createElement('style');
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

    console.log('✓ ✓ ✓ INVESTOR ADMIN JS LOADED SUCCESSFULLY ✓ ✓ ✓');