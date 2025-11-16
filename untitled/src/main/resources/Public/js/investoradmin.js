// Investor Admin JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // ========== NAVIGATION FUNCTIONALITY ==========
    const navItems = document.querySelectorAll('.nav-item');
    const BASE_URL = window.location.origin;

    // Frontend routes mapping
    const routes = {
        'dashboard': 'admindashboard.html',
        'startups': 'startupadmin.html',
        'investors': 'investoradmin.html',
        'approvals': 'approvals.html',
        'analytics': 'analytics.html',
        'settings': 'settings.html'
    };

    // Navigation handling
    navItems.forEach(item => {
        item.addEventListener('click', function() {
            const routeKey = this.getAttribute('data-route');
            const route = routes[routeKey];

            if (route && route !== window.location.pathname.split('/').pop()) {
                showNavigationFeedback(routeKey);
                setTimeout(() => {
                    window.location.href = route;
                }, 300);
            }
        });
    });

    function showNavigationFeedback(page) {
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
            font-family: 'Inter', sans-serif;
        `;

        const formattedPageName = page.charAt(0).toUpperCase() + page.slice(1);
        notification.textContent = `Navigating to ${formattedPageName}...`;

        document.body.appendChild(notification);

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

    // ========== INVESTOR DATA & FUNCTIONALITY ==========
    // Sample investor data (in real app, this would come from API)
    const investors = [
        {
            _id: "68f707aac4a25d4935437c1a",
            name: "AARON",
            email: "24mcab28@kristujayanti.com",
            phone: "8080694605",
            country: "India",
            organization: "firstcry",
            address: "Porvirim North Goa\nGoogle Play Store",
            investorType: "vc",
            preferences: "Technology, E-commerce, Consumer Products",
            registrationStatus: "PENDING",
            isVerified: false,
            createdAt: "2025-10-21T04:10:18.817+00:00",
            investmentRange: "$100K - $500K",
            pastInvestments: [
                { name: "TechNova", amount: "$250,000", year: "2024", description: "AI Analytics Platform" },
                { name: "GreenSolutions", amount: "$150,000", year: "2023", description: "Recycling Technology" }
            ],
            documents: [
                { name: "Investment Portfolio.pdf", type: "pdf" },
                { name: "Verification Documents.zip", type: "zip" },
                { name: "Investment Strategy.docx", type: "doc" }
            ]
        },
        {
            _id: "68f707aac4a25d4935437c1b",
            name: "Sarah Bennett",
            email: "sarah@capitalventures.com",
            phone: "+1 (415) 555-7890",
            country: "United States",
            organization: "Capital Ventures",
            address: "123 Market St, San Francisco, CA",
            investorType: "angel",
            preferences: "Healthcare, Biotech, AI",
            registrationStatus: "PENDING",
            isVerified: false,
            createdAt: "2025-10-20T10:30:00.000+00:00",
            investmentRange: "$50K - $250K",
            pastInvestments: [
                { name: "HealthTech Pro", amount: "$100,000", year: "2024", description: "Medical Devices" },
                { name: "BioGen Labs", amount: "$75,000", year: "2023", description: "Biotechnology Research" }
            ],
            documents: [
                { name: "Portfolio Overview.pdf", type: "pdf" },
                { name: "Accreditation Proof.pdf", type: "pdf" }
            ]
        },
        {
            _id: "68f707aac4a25d4935437c1c",
            name: "Rajesh Jain",
            email: "rajesh@growthpartners.in",
            phone: "+91 9876543210",
            country: "India",
            organization: "Growth Partners",
            address: "456 Business Park, Mumbai",
            investorType: "private-equity",
            preferences: "Fintech, Edtech, SaaS",
            registrationStatus: "APPROVED",
            isVerified: true,
            createdAt: "2025-10-15T08:15:00.000+00:00",
            investmentRange: "$1M - $5M",
            pastInvestments: [
                { name: "Finova Digital", amount: "$2,500,000", year: "2024", description: "Digital Banking" },
                { name: "EduTech Pro", amount: "$1,200,000", year: "2023", description: "Learning Platform" }
            ],
            documents: [
                { name: "Fund Details.pdf", type: "pdf" },
                { name: "Investment Thesis.docx", type: "doc" }
            ]
        }
    ];

    // Render investor cards
    function renderInvestorCards() {
        const pendingGrid = document.querySelector('.investors-section:first-child .investors-grid');
        const approvedGrid = document.querySelector('.investors-section:last-child .investors-grid');

        // Clear existing cards
        pendingGrid.innerHTML = '';
        approvedGrid.innerHTML = '';

        investors.forEach(investor => {
            const card = createInvestorCard(investor);
            if (investor.registrationStatus === 'PENDING') {
                pendingGrid.appendChild(card);
            } else {
                approvedGrid.appendChild(card);
            }
        });
    }

    // Create investor card HTML
    function createInvestorCard(investor) {
        const card = document.createElement('div');
        card.className = 'investor-card';
        card.innerHTML = `
            <div class="investor-header">
                <div class="investor-avatar">${investor.name.charAt(0)}</div>
                <div class="investor-info">
                    <h3 class="investor-name">${investor.name}</h3>
                    <p class="investor-company">${investor.organization}</p>
                    <span class="investor-type">${getInvestorTypeLabel(investor.investorType)}</span>
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
                        <span class="detail-value">${investor.investmentRange}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Preferences</span>
                        <div class="preferences-tags">
                            ${investor.preferences.split(', ').map(pref =>
                                `<span class="preference-tag">${pref}</span>`
                            ).join('')}
                        </div>
                    </div>
                </div>
            </div>
            <div class="investor-footer">
                <span class="status-badge status-${investor.registrationStatus.toLowerCase()}">
                    ${investor.registrationStatus === 'PENDING' ? 'Pending Review' : 'Approved'}
                </span>
                <div class="action-buttons">
                    <div class="btn-icon btn-view" data-id="${investor._id}">
                        <i class="fas fa-eye"></i>
                    </div>
                    ${investor.registrationStatus === 'PENDING' ? `
                        <div class="btn-icon btn-approve" data-id="${investor._id}">
                            <i class="fas fa-check"></i>
                        </div>
                        <div class="btn-icon btn-reject" data-id="${investor._id}">
                            <i class="fas fa-times"></i>
                        </div>
                    ` : ''}
                </div>
            </div>
        `;

        return card;
    }

    // Get investor type label
    function getInvestorTypeLabel(type) {
        const types = {
            'vc': 'Venture Capital',
            'angel': 'Angel Investor',
            'corporate': 'Corporate Investor',
            'private-equity': 'Private Equity'
        };
        return types[type] || type;
    }

    // ========== MODAL FUNCTIONALITY ==========
    function openInvestorModal(investorId) {
        const investor = investors.find(inv => inv._id === investorId);
        if (!investor) return;

        const modalBody = document.querySelector('#investorModal .modal-body');
        modalBody.innerHTML = createModalContent(investor);

        document.getElementById('investorModal').style.display = 'flex';
    }

    function createModalContent(investor) {
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
                        <div class="detail-value">${investor.address.replace('\n', ', ')}</div>
                    </div>
                    <div class="detail-field">
                        <div class="detail-label">Investor Type</div>
                        <div class="detail-value">${getInvestorTypeLabel(investor.investorType)}</div>
                    </div>
                    <div class="detail-field">
                        <div class="detail-label">Registration Date</div>
                        <div class="detail-value">${new Date(investor.createdAt).toLocaleDateString()}</div>
                    </div>
                </div>
            </div>

            <div class="investor-detail-section">
                <h3 class="detail-section-title">Investment Preferences</h3>
                <div class="detail-grid">
                    <div class="detail-field">
                        <div class="detail-label">Preferred Industries</div>
                        <div class="detail-value">${investor.preferences}</div>
                    </div>
                    <div class="detail-field">
                        <div class="detail-label">Investment Range</div>
                        <div class="detail-value">${investor.investmentRange}</div>
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

            <div class="investor-detail-section">
                <h3 class="detail-section-title">Past Investments</h3>
                <div class="detail-field">
                    <div class="detail-value" style="min-height: 120px; flex-direction: column; align-items: flex-start;">
                        ${investor.pastInvestments.map(inv =>
                            `<div style="margin-bottom: 10px; padding: 10px; background: white; border-radius: 8px; width: 100%; border-left: 4px solid #edb96f;">
                                <strong>${inv.name}</strong> - ${inv.amount} (${inv.year})<br>
                                <small style="color: #666;">${inv.description}</small>
                            </div>`
                        ).join('')}
                    </div>
                </div>
            </div>

            <div class="investor-detail-section">
                <h3 class="detail-section-title">Documents</h3>
                <div class="document-list">
                    ${investor.documents.map(doc => `
                        <div class="document-item">
                            <div class="document-name">
                                <i class="fas fa-file-${doc.type}"></i>
                                <span>${doc.name}</span>
                            </div>
                            <div class="document-actions">
                                <button class="btn-icon btn-view">
                                    <i class="fas fa-eye"></i>
                                </button>
                                <button class="btn-icon btn-view">
                                    <i class="fas fa-download"></i>
                                </button>
                            </div>
                        </div>
                    `).join('')}
                </div>
            </div>

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
        `;
    }

    function closeInvestorModal() {
        document.getElementById('investorModal').style.display = 'none';
    }

    // ========== EVENT LISTENERS ==========
    function setupEventListeners() {
        // View buttons
        document.addEventListener('click', function(e) {
            if (e.target.closest('.btn-view')) {
                const investorId = e.target.closest('.btn-view').getAttribute('data-id');
                if (investorId) {
                    openInvestorModal(investorId);
                }
            }
        });

        // Approve buttons
        document.addEventListener('click', function(e) {
            if (e.target.closest('.btn-approve')) {
                const investorId = e.target.closest('.btn-approve').getAttribute('data-id');
                if (confirm('Are you sure you want to approve this investor?')) {
                    approveInvestor(investorId);
                }
            }
        });

        // Reject buttons
        document.addEventListener('click', function(e) {
            if (e.target.closest('.btn-reject')) {
                const investorId = e.target.closest('.btn-reject').getAttribute('data-id');
                if (confirm('Are you sure you want to reject this investor?')) {
                    rejectInvestor(investorId);
                }
            }
        });

        // Modal close
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
                filterInvestors(searchTerm);
            });
        }

        // Filter functionality
        const filterSelects = document.querySelectorAll('.filter-select');
        filterSelects.forEach(select => {
            select.addEventListener('change', function() {
                applyFilters();
            });
        });
    }

    // ========== BUSINESS LOGIC ==========
    function approveInvestor(investorId) {
        // In real app, this would be an API call
        const investor = investors.find(inv => inv._id === investorId);
        if (investor) {
            investor.registrationStatus = 'APPROVED';
            investor.isVerified = true;
            renderInvestorCards();
            alert('Investor approved! Email notification sent.');
        }
    }

    function rejectInvestor(investorId) {
        // In real app, this would be an API call
        const investor = investors.find(inv => inv._id === investorId);
        if (investor) {
            investor.registrationStatus = 'REJECTED';
            renderInvestorCards();
            alert('Investor rejected! Email notification sent.');
        }
    }

    function filterInvestors(searchTerm) {
        const cards = document.querySelectorAll('.investor-card');
        cards.forEach(card => {
            const text = card.textContent.toLowerCase();
            card.style.display = text.includes(searchTerm) ? 'block' : 'none';
        });
    }

    function applyFilters() {
        // Implementation for applying multiple filters
        console.log('Filters applied');
    }

    // ========== INITIALIZATION ==========
    function init() {
        renderInvestorCards();
        setupEventListeners();

        // Update stats based on actual data
        updateStats();
    }

    function updateStats() {
        const totalInvestors = investors.length;
        const pendingInvestors = investors.filter(inv => inv.registrationStatus === 'PENDING').length;
        const verifiedInvestors = investors.filter(inv => inv.isVerified).length;

        // Update DOM elements if they exist
        const statValues = document.querySelectorAll('.stat-value');
        if (statValues.length >= 3) {
            statValues[0].textContent = totalInvestors;
            statValues[1].textContent = pendingInvestors;
            statValues[2].textContent = verifiedInvestors;
        }
    }

    // Start the application
    init();
});