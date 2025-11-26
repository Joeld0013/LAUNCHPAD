/**
 * Profile Popup Module
 * Handles displaying user profiles in a popup modal when clicking on their name/avatar/email in posts
 */

const ProfilePopup = (function() {
    const API_BASE_URL = window.API_BASE_URL || '/api';
    let popupOverlay = null;
    let currentPopup = null;

    /**
     * Initialize the popup overlay (call this once on page load)
     */
    function init() {
        // Create popup overlay if it doesn't exist
        if (!popupOverlay) {
            popupOverlay = document.createElement('div');
            popupOverlay.className = 'profile-popup-overlay';
            popupOverlay.innerHTML = `
                <div class="profile-popup-container">
                    <button class="profile-popup-close" onclick="ProfilePopup.close()">&times;</button>
                    <div class="profile-popup-content">
                        <div class="profile-popup-loading">
                            <i class="fas fa-spinner fa-spin"></i>
                            <p>Loading profile...</p>
                        </div>
                    </div>
                </div>
            `;
            document.body.appendChild(popupOverlay);

            // Close on overlay click
            popupOverlay.addEventListener('click', function(e) {
                if (e.target === popupOverlay) {
                    close();
                }
            });

            // Close on Escape key
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape' && popupOverlay.classList.contains('active')) {
                    close();
                }
            });
        }
    }

    /**
     * Opens the profile popup for a given user
     * @param {string} userId - The user's ID
     * @param {string} userType - Either 'STARTUP' or 'INVESTOR'
     */
    async function open(userId, userType) {
        console.log('Opening profile popup for:', userId, userType);

        if (!popupOverlay) {
            init();
        }

        // Show overlay with loading state
        const contentDiv = popupOverlay.querySelector('.profile-popup-content');
        contentDiv.innerHTML = `
            <div class="profile-popup-loading">
                <i class="fas fa-spinner fa-spin"></i>
                <p>Loading profile...</p>
            </div>
        `;
        popupOverlay.classList.add('active');
        document.body.style.overflow = 'hidden';

        try {
            // Fetch profile data
            const endpoint = userType === 'STARTUP'
                ? `${API_BASE_URL}/startups/${userId}`
                : `${API_BASE_URL}/investors/${userId}`;

            console.log('Fetching from:', endpoint);

            const response = await fetch(endpoint, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`Failed to fetch profile: ${response.status}`);
            }

            const profileData = await response.json();
            console.log('Profile data loaded:', profileData);

            // Render profile based on user type
            if (userType === 'STARTUP') {
                renderStartupProfile(profileData);
            } else {
                renderInvestorProfile(profileData);
            }

        } catch (error) {
            console.error('Error loading profile:', error);
            contentDiv.innerHTML = `
                <div class="profile-popup-error">
                    <i class="fas fa-exclamation-triangle"></i>
                    <h3>Failed to Load Profile</h3>
                    <p>${error.message}</p>
                </div>
            `;
        }
    }

    /**
     * Closes the profile popup
     */
    function close() {
        if (popupOverlay) {
            popupOverlay.classList.remove('active');
            document.body.style.overflow = '';
        }
    }

    /**
     * Renders a startup profile
     */
    function renderStartupProfile(data) {
        const contentDiv = popupOverlay.querySelector('.profile-popup-content');

        // Build team members HTML
        let teamHTML = '';
        if (data.team && data.team.length > 0) {
            teamHTML = data.team.map(member => `
                <div class="team-member">
                    <div class="member-avatar">${member.initials || ''}</div>
                    <div class="member-details">
                        <h4>${member.name || ''}</h4>
                        <p>${member.title || ''}</p>
                        <p>${member.description || ''}</p>
                    </div>
                </div>
            `).join('');
        } else {
            teamHTML = '<p class="text-muted" style="padding: 10px;">No team members listed.</p>';
        }

        // Build milestones HTML
        let milestonesHTML = '';
        if (data.milestones && data.milestones.length > 0) {
            milestonesHTML = data.milestones.map(milestone => `
                <div class="experience-item">
                    <div class="experience-icon"><i class="${milestone.icon || 'fas fa-star'}"></i></div>
                    <div class="experience-details">
                        <div class="experience-title">${milestone.title || ''}</div>
                        <div class="experience-duration">${milestone.date || ''}</div>
                    </div>
                </div>
            `).join('');
        } else {
            milestonesHTML = '<p class="text-muted" style="padding: 10px;">No milestones added yet.</p>';
        }

        // Build skills HTML
        let skillsHTML = '';
        if (data.skills && data.skills.length > 0) {
            skillsHTML = data.skills.map(skill =>
                `<span class="skill-tag">${skill}</span>`
            ).join('');
        } else {
            skillsHTML = '<span class="text-muted" style="font-size: 0.9em;">No skills added.</span>';
        }

        // Build video HTML
        let videoHTML = '';
        if (data.pitchVideoUrl) {
            const videoUrl = data.pitchVideoUrl.startsWith('http')
                ? data.pitchVideoUrl
                : `${API_BASE_URL.replace('/api', '')}${data.pitchVideoUrl}`;
            videoHTML = `<video controls style="width: 100%; border-radius: 8px;"><source src="${videoUrl}"></video>`;
        } else {
            videoHTML = '<div class="text-muted" style="text-align: center; padding: 20px;">No pitch video uploaded yet.</div>';
        }

        // Build avatar
        const avatarInitials = data.name ? data.name.split(' ').map(w => w[0]).join('').toUpperCase().substring(0, 2) : 'ST';
        const avatarStyle = data.profilePicture
            ? `background-image: url(${data.profilePicture}); background-size: cover; background-position: center;`
            : '';
        const avatarContent = data.profilePicture ? '' : avatarInitials;

        contentDiv.innerHTML = `
            <div class="profile-grid">
                <main class="profile-main-column">
                    <div class="card profile-header-card">
                        <div class="profile-header-top">
                            <div class="profile-avatar-large" style="${avatarStyle}">${avatarContent}</div>
                            <div class="profile-info-main">
                                <h1 class="profile-name">${data.name || 'Startup Name'}</h1>
                                <p class="profile-title">${data.industry || ''} | ${data.stage || ''} Stage</p>
                                <p class="profile-location">📍 ${data.location || data.address || 'Location not specified'}</p>
                            </div>
                        </div>
                    </div>

                    <div class="card profile-content-tabs">
                        <nav class="profile-tab-nav">
                            <button class="profile-tab-btn active" onclick="ProfilePopup.switchTab(event, 'popup-tab-about')">About</button>
                            <button class="profile-tab-btn" onclick="ProfilePopup.switchTab(event, 'popup-tab-milestones')">Milestones</button>
                            <button class="profile-tab-btn" onclick="ProfilePopup.switchTab(event, 'popup-tab-team')">Team</button>
                        </nav>

                        <div class="profile-tab-content">
                            <div id="popup-tab-about" class="profile-tab-panel active-tab">
                                <div class="section-title">About</div>
                                <p class="profile-summary-text">${data.about || data.description || 'No description available.'}</p>
                            </div>

                            <div id="popup-tab-milestones" class="profile-tab-panel">
                                <div class="section-title">Key Milestones</div>
                                <div class="experience-list">${milestonesHTML}</div>
                            </div>

                            <div id="popup-tab-team" class="profile-tab-panel">
                                <div class="section-title">Core Team</div>
                                <div class="team-list">${teamHTML}</div>
                            </div>
                        </div>
                    </div>
                </main>

                <aside class="sidebar-column">
                    <div class="card">
                        <div class="section-title">Contact Info</div>
                        <div class="contact-list">
                            <div class="contact-item">
                                <i class="fas fa-globe"></i>
                                <span><a href="${data.website || '#'}" class="link" target="_blank">${data.website || 'Not provided'}</a></span>
                            </div>
                            <div class="contact-item">
                                <i class="fas fa-envelope"></i>
                                <span>${data.email || 'Not provided'}</span>
                            </div>
                            <div class="contact-item">
                                <i class="fas fa-phone"></i>
                                <span>${data.phone || 'Not provided'}</span>
                            </div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="section-title">Areas of Focus</div>
                        <div class="skills-list">${skillsHTML}</div>
                    </div>

                    <div class="card">
                        <div class="section-title">Pitch Deck Video</div>
                        <div>${videoHTML}</div>
                    </div>
                </aside>
            </div>
        `;
    }

    /**
     * Renders an investor profile
     */
    function renderInvestorProfile(data) {
        const contentDiv = popupOverlay.querySelector('.profile-popup-content');

        // Build team members HTML
        let teamHTML = '';
        if (data.team && data.team.length > 0) {
            teamHTML = data.team.map(member => `
                <div class="team-member">
                    <div class="member-avatar">${member.initials || ''}</div>
                    <div class="member-details">
                        <h4>${member.name || ''}</h4>
                        <p>${member.title || ''}</p>
                        ${member.description ? `<p style="font-size: 0.85em; color: #666;">${member.description}</p>` : ''}
                    </div>
                </div>
            `).join('');
        } else {
            teamHTML = '<p class="text-muted" style="padding: 10px;">No team members listed.</p>';
        }

        // Build portfolio HTML
        let portfolioHTML = '';
        if (data.portfolio && data.portfolio.length > 0) {
            portfolioHTML = data.portfolio.map(company => `
                <div class="team-member">
                    <div class="member-avatar" style="background-color: #95a5a6;">${company.initials || ''}</div>
                    <div class="member-details">
                        <h4>${company.name || ''}</h4>
                        <p>${company.sector || ''}</p>
                        <p>${company.description || ''}</p>
                    </div>
                </div>
            `).join('');
        } else {
            portfolioHTML = '<p class="text-muted" style="padding: 10px;">No portfolio companies listed.</p>';
        }

        // Build investment focus HTML
        let skillsHTML = '';
        if (data.investmentFocus && data.investmentFocus.length > 0) {
            skillsHTML = data.investmentFocus.map(focus =>
                `<span class="skill-tag">${focus}</span>`
            ).join('');
        } else {
            skillsHTML = '<span class="text-muted">No investment focus specified</span>';
        }

        // Build avatar
        const avatarStyle = data.profilePicture
            ? `background-image: url(${data.profilePicture}); background-size: cover; background-position: center;`
            : '';

        contentDiv.innerHTML = `
            <div class="profile-grid">
                <main class="profile-main-column">
                    <div class="card profile-header-card">
                        <div class="profile-header-top">
                            <div class="profile-avatar-large" style="${avatarStyle}"><i class="fas fa-building"></i></div>
                            <div class="profile-info-main">
                                <h1 class="profile-name">${data.name || 'Firm Name'}</h1>
                                <p class="profile-title">${data.title || `${data.investorType || 'Investor'} | Investment Firm`}</p>
                                <p class="profile-location">📍 ${data.location || data.address || data.country || 'Location not specified'}</p>
                            </div>
                        </div>
                    </div>

                    <div class="card profile-content-tabs">
                        <nav class="profile-tab-nav">
                            <button class="profile-tab-btn active" onclick="ProfilePopup.switchTab(event, 'popup-tab-about')">About</button>
                            <button class="profile-tab-btn" onclick="ProfilePopup.switchTab(event, 'popup-tab-thesis')">Investment Thesis</button>
                            <button class="profile-tab-btn" onclick="ProfilePopup.switchTab(event, 'popup-tab-portfolio')">Portfolio</button>
                        </nav>

                        <div class="profile-tab-content">
                            <div id="popup-tab-about" class="profile-tab-panel active-tab">
                                <div class="section-title">About Us</div>
                                <p class="profile-summary-text">${data.about || 'No description available.'}</p>
                            </div>

                            <div id="popup-tab-thesis" class="profile-tab-panel">
                                <div class="section-title">Investment Thesis</div>
                                <div class="dashboard-grid">
                                    <div class="performance-graph-container">
                                        <h4>Typical Investment</h4>
                                        <ul class="thesis-list">
                                            <li><strong>Stage:</strong> ${data.investmentThesis?.stage || 'Not specified'}</li>
                                            <li><strong>Check Size:</strong> ${data.investmentThesis?.checkSize || 'Not specified'}</li>
                                            <li><strong>Horizon:</strong> ${data.investmentThesis?.horizon || 'Not specified'}</li>
                                        </ul>
                                    </div>
                                </div>
                            </div>

                            <div id="popup-tab-portfolio" class="profile-tab-panel">
                                <div class="section-title">Portfolio Companies</div>
                                <div class="team-list">${portfolioHTML}</div>
                            </div>
                        </div>
                    </div>
                </main>

                <aside class="sidebar-column">
                    <div class="card">
                        <div class="section-title">Contact Info</div>
                        <div class="contact-list">
                            <div class="contact-item">
                                <i class="fas fa-globe"></i>
                                <span><a href="${data.website || '#'}" class="link" target="_blank">${data.website || 'Not provided'}</a></span>
                            </div>
                            <div class="contact-item">
                                <i class="fas fa-envelope"></i>
                                <span>${data.email || 'Not provided'}</span>
                            </div>
                            <div class="contact-item">
                                <i class="fas fa-phone"></i>
                                <span>${data.phone || 'Not provided'}</span>
                            </div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="section-title">Investment Focus</div>
                        <div class="skills-list">${skillsHTML}</div>
                    </div>

                    <div class="card">
                        <div class="section-title">Core Team</div>
                        <div class="team-list">${teamHTML}</div>
                    </div>
                </aside>
            </div>
        `;
    }

    /**
     * Switches tabs within the popup
     */
    function switchTab(event, tabId) {
        // Remove active class from all tabs and panels
        const tabs = popupOverlay.querySelectorAll('.profile-tab-btn');
        const panels = popupOverlay.querySelectorAll('.profile-tab-panel');

        tabs.forEach(tab => tab.classList.remove('active'));
        panels.forEach(panel => panel.classList.remove('active-tab'));

        // Add active class to clicked tab and corresponding panel
        event.target.classList.add('active');
        const panel = popupOverlay.querySelector(`#${tabId}`);
        if (panel) {
            panel.classList.add('active-tab');
        }
    }

    // Public API
    return {
        init: init,
        open: open,
        close: close,
        switchTab: switchTab
    };
})();

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    ProfilePopup.init();
});

// Make it globally available
window.ProfilePopup = ProfilePopup;