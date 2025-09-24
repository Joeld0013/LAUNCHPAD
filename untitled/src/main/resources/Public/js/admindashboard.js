// Admin Dashboard Navigation Script with Localhost Support
document.addEventListener('DOMContentLoaded', function() {
    // Navigation elements
    const navItems = document.querySelectorAll('.nav-item');
    const actionButtons = document.querySelectorAll('.action-btn');

    // Backend/localhost base URL (adjust to your Spring Boot/Node/PHP server port)
    const BASE_URL = "http://localhost:8080/admin";

    // Navigation routes mapping
    const routes = {
        // Sidebar navigation
        'dashboard': '/dashboard',
        'startups': '/startups',
        'investors': '/investors',
        'approvals': '/approvals',
        'analytics': '/analytics',
        'settings': '/settings',

        // Quick action buttons
        'register-startup': '/startups?action=register',
        'browse-investors': '/investors',
        'review-approvals': '/approvals'
    };

    // Function to handle navigation
    function navigateTo(route) {
        const fullUrl = BASE_URL + route;

        // Try backend call first
        fetch(fullUrl, { method: "GET" })
            .then(res => {
                if (res.ok) {
                    // Redirect to backend page (real navigation)
                    window.location.href = fullUrl;
                } else {
                    // Fallback to frontend behavior if endpoint not found
                    handleFrontendRoute(route);
                }
            })
            .catch(() => {
                // If server not running, fallback
                handleFrontendRoute(route);
            });
    }

    // Frontend fallback navigation
    function handleFrontendRoute(route) {
        // Update URL hash
        window.location.hash = route;

        // Update active sidebar nav item
        updateActiveNavItem(route);

        // Show notification (demo only)
        showNavigationFeedback(route);
    }

    // Function to update active navigation item
    function updateActiveNavItem(route) {
        navItems.forEach(item => {
            item.classList.remove('active');
        });

        let activeNavId = '';

        if (route.includes('dashboard')) {
            activeNavId = 'dashboard';
        } else if (route.includes('startups')) {
            activeNavId = 'startups';
        } else if (route.includes('investors')) {
            activeNavId = 'investors';
        } else if (route.includes('approvals')) {
            activeNavId = 'approvals';
        } else if (route.includes('analytics')) {
            activeNavId = 'analytics';
        } else if (route.includes('settings')) {
            activeNavId = 'settings';
        }

        if (activeNavId) {
            const activeItem = document.querySelector(`.nav-item[data-route="${activeNavId}"]`);
            if (activeItem) {
                activeItem.classList.add('active');
            }
        }
    }

    // Function to show navigation feedback (demo only)
    function showNavigationFeedback(route) {
        const pageName = route.split('?')[0].replace('#', '') || 'Dashboard';
        const notification = document.createElement('div');
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: var(--accent-color);
            color: var(--dark-color);
            padding: 12px 20px;
            border-radius: 5px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            z-index: 1000;
            font-weight: 600;
            transition: all 0.3s ease;
        `;

        const formattedPageName = pageName.charAt(0).toUpperCase() + pageName.slice(1);
        notification.textContent = `Navigating to ${formattedPageName} page...`;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.opacity = '0';
            notification.style.transform = 'translateX(100px)';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 2000);
    }

    // Sidebar nav click handling
    navItems.forEach(item => {
        const routeKey = item.querySelector('.nav-text').textContent.toLowerCase().trim();
        item.setAttribute('data-route', routeKey);

        item.addEventListener('click', function() {
            const route = routes[routeKey];
            if (route) {
                navigateTo(route);
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
                    navigateTo(route);
                }
            });
        }
    });

    // Handle back/forward navigation
    window.addEventListener('hashchange', function() {
        const route = window.location.hash;
        updateActiveNavItem(route);
    });

    // Initialize
    if (window.location.hash) {
        updateActiveNavItem(window.location.hash);
    }

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
});
