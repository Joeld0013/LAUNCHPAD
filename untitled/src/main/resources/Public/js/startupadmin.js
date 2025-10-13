// startup.js - Combined navigation and modal functionality
document.addEventListener('DOMContentLoaded', function() {
    // ========== NAVIGATION FUNCTIONALITY ==========
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

    // ========== MODAL FUNCTIONALITY ==========
    // Function to open the startup detail modal
    function openStartupModal(startupName) {
        document.getElementById('startupModal').style.display = 'flex';

        // In a real application, you would fetch the startup data based on the name
        // For this demo, we're just setting the name in the modal
        document.getElementById('modal-name').textContent = startupName;
    }

    // Function to close the startup detail modal
    function closeStartupModal() {
        document.getElementById('startupModal').style.display = 'none';
    }

    // Close modal when clicking outside of it
    window.onclick = function(event) {
        const modal = document.getElementById('startupModal');
        if (event.target === modal) {
            closeStartupModal();
        }
    }

    // Add event listeners for approve and reject buttons
    const approveButtons = document.querySelectorAll('.btn-approve');
    const rejectButtons = document.querySelectorAll('.btn-reject');

    approveButtons.forEach(button => {
        button.addEventListener('click', function() {
            // In a real application, you would send an API request to approve the startup
            alert('Startup approved! An email notification will be sent to the startup.');
        });
    });

    rejectButtons.forEach(button => {
        button.addEventListener('click', function() {
            // In a real application, you would send an API request to reject the startup
            alert('Startup rejected! An email notification will be sent to the startup.');
        });
    });

    // Add event listeners for view buttons to open modal
    const viewButtons = document.querySelectorAll('.btn-view');
    viewButtons.forEach(button => {
        button.addEventListener('click', function() {
            const startupCard = this.closest('.startup-card');
            const startupName = startupCard.querySelector('.startup-name').textContent;
            openStartupModal(startupName);
        });
    });

    // Modal approve/reject buttons
    const modalApproveBtn = document.querySelector('.modal-footer .btn-approve');
    const modalRejectBtn = document.querySelector('.modal-footer .btn-reject');

    if (modalApproveBtn) {
        modalApproveBtn.addEventListener('click', function() {
            alert('Startup approved from modal! An email notification will be sent to the startup.');
            closeStartupModal();
        });
    }

    if (modalRejectBtn) {
        modalRejectBtn.addEventListener('click', function() {
            alert('Startup rejected from modal! An email notification will be sent to the startup.');
            closeStartupModal();
        });
    }
});