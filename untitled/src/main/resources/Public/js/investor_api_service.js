// Investor API Service
// Place this in: js/investor_api_service.js

const API_BASE_URL = window.API_BASE_URL || 'http://localhost:8080/api';

/**
 * Gets the current investor ID from localStorage or URL
 */
function getCurrentInvestorId() {
    const urlParams = new URLSearchParams(window.location.search);
    let investorId = urlParams.get('id');

    if (!investorId) {
        investorId = localStorage.getItem('currentInvestorId');
    }

    if (!investorId) {
        investorId = '68f771a0b3689221bc6007dc'; // Default from your MongoDB
    }

    return investorId;
}

/**
 * Fetches investor profile data from the backend
 */
async function fetchInvestorProfile(investorId) {
    try {
        console.log('Fetching investor profile for ID:', investorId);
        console.log('API URL:', `${API_BASE_URL}/investors/${investorId}`);

        const response = await fetch(`${API_BASE_URL}/investors/${investorId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            mode: 'cors'
        });

        console.log('Response status:', response.status);

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Error response:', errorText);
            throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
        }

        const data = await response.json();
        console.log('Profile data received:', data);
        return data;
    } catch (error) {
        console.error('Error fetching investor profile:', error);
        throw error;
    }
}

/**
 * Updates investor profile data
 */
async function updateInvestorProfile(investorId, profileData) {
    try {
        console.log('Updating investor profile:', investorId, profileData);

        const response = await fetch(`${API_BASE_URL}/investors/${investorId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            mode: 'cors',
            body: JSON.stringify(profileData)
        });

        console.log('Update response status:', response.status);

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Update error response:', errorText);
            throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
        }

        const data = await response.json();
        console.log('Profile updated successfully:', data);
        return data;
    } catch (error) {
        console.error('Error updating investor profile:', error);
        throw error;
    }
}

/**
 * Populates the profile page with data from the backend
 */
function populateInvestorProfilePage(profileData) {
    console.log('Populating page with data:', profileData);

    // Basic Info
    const nameEl = document.getElementById('page-name');
    if (nameEl && profileData.name) {
        nameEl.textContent = profileData.name;
    }

    const titleEl = document.getElementById('page-title');
    if (titleEl) {
        titleEl.textContent = profileData.title || `${profileData.investorType || 'Investor'} | Investment Firm`;
    }

    const locationEl = document.getElementById('page-location');
    if (locationEl) {
        locationEl.textContent = profileData.location || `📍 ${profileData.address || profileData.country || 'Location not specified'}`;
    }

    const aboutEl = document.getElementById('page-about');
    if (aboutEl) {
        aboutEl.textContent = profileData.about || 'No description available.';
    }

    // Profile Avatar
    const pageAvatar = document.getElementById('page-avatar');
    if (pageAvatar) {
        if (profileData.profilePicture) {
            pageAvatar.innerHTML = '';
            pageAvatar.style.backgroundImage = `url(${profileData.profilePicture})`;
            pageAvatar.style.backgroundSize = 'cover';
            pageAvatar.style.backgroundPosition = 'center';
        } else {
            pageAvatar.style.backgroundImage = 'none';
            pageAvatar.innerHTML = '<i class="fas fa-building"></i>';
        }
    }

    // Contact Info
    const websiteElement = document.querySelector('#page-website .link');
    if (websiteElement && profileData.website) {
        websiteElement.textContent = profileData.website;
        websiteElement.href = profileData.website.startsWith('http') ? profileData.website : `https://${profileData.website}`;
    } else if (websiteElement) {
        websiteElement.textContent = 'Not provided';
        websiteElement.href = '#';
    }

    const emailElement = document.querySelector('#page-email span');
    if (emailElement) {
        emailElement.textContent = profileData.email || 'Not provided';
    }

    const phoneElement = document.querySelector('#page-phone span');
    if (phoneElement) {
        phoneElement.textContent = profileData.phone || 'Not provided';
    }

    // Investment Focus (Skills)
    const skillsList = document.getElementById('page-skills-list');
    if (skillsList) {
        if (profileData.investmentFocus && profileData.investmentFocus.length > 0) {
            skillsList.innerHTML = '';
            profileData.investmentFocus.forEach(focus => {
                const skillTag = document.createElement('span');
                skillTag.className = 'skill-tag';
                skillTag.textContent = focus;
                skillsList.appendChild(skillTag);
            });
        } else {
            skillsList.innerHTML = '<span class="text-muted">No investment focus specified</span>';
        }
    }

    // Investment Thesis
    const thesisList = document.getElementById('page-thesis-list');
    if (thesisList && profileData.investmentThesis) {
        thesisList.innerHTML = `
            <li><strong>Stage:</strong> ${profileData.investmentThesis.stage || 'Not specified'}</li>
            <li><strong>Check Size:</strong> ${profileData.investmentThesis.checkSize || 'Not specified'}</li>
            <li><strong>Horizon:</strong> ${profileData.investmentThesis.horizon || 'Not specified'}</li>
        `;
    }

    // Team Members (Sidebar) - Show with description
    const teamList = document.getElementById('page-team-list-sidebar');
    if (teamList) {
        if (profileData.team && profileData.team.length > 0) {
            teamList.innerHTML = '';
            profileData.team.forEach(member => {
                const memberDiv = document.createElement('div');
                memberDiv.className = 'team-member';
                memberDiv.dataset.initials = member.initials || '';
                memberDiv.innerHTML = `
                    <div class="member-avatar">${member.initials || ''}</div>
                    <div class="member-details">
                        <h4>${member.name || ''}</h4>
                        <p>${member.title || ''}</p>
                        ${member.description ? `<p style="font-size: 0.85em; color: #666;">${member.description}</p>` : ''}
                    </div>
                `;
                teamList.appendChild(memberDiv);
            });
        } else {
            teamList.innerHTML = '<p class="text-muted" style="padding: 10px;">No team members listed.</p>';
        }
    }

    // Portfolio Companies
    const portfolioList = document.getElementById('page-portfolio-list');
    if (portfolioList) {
        if (profileData.portfolio && profileData.portfolio.length > 0) {
            portfolioList.innerHTML = '';
            profileData.portfolio.forEach(company => {
                const companyDiv = document.createElement('div');
                companyDiv.className = 'team-member';
                companyDiv.innerHTML = `
                    <div class="member-avatar" style="background-color: #95a5a6;">${company.initials || ''}</div>
                    <div class="member-details">
                        <h4>${company.name || ''}</h4>
                        <p>${company.sector || ''}</p>
                        <p>${company.description || ''}</p>
                    </div>
                `;
                portfolioList.appendChild(companyDiv);
            });
        } else {
            portfolioList.innerHTML = '<p class="text-muted" style="padding: 10px;">No portfolio companies listed.</p>';
        }
    }
}

/**
 * Shows loading state
 */
function showLoadingState() {
    const mainContent = document.querySelector('.profile-main-column');
    if (mainContent) {
        const loader = document.createElement('div');
        loader.id = 'profile-loader';
        loader.style.cssText = 'text-align: center; padding: 40px; font-size: 18px; color: #666;';
        loader.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Loading profile...';
        mainContent.prepend(loader);
    }
}

/**
 * Hides loading state
 */
function hideLoadingState() {
    const loader = document.getElementById('profile-loader');
    if (loader) {
        loader.remove();
    }
}

/**
 * Shows error message
 */
function showErrorMessage(message) {
    const mainContent = document.querySelector('.profile-main-column');
    if (mainContent) {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'card';
        errorDiv.style.cssText = 'background: #fee; border-left: 4px solid #e74c3c; padding: 20px; margin-bottom: 20px;';
        errorDiv.innerHTML = `
            <h3 style="margin: 0 0 10px 0; color: #e74c3c;">
                <i class="fas fa-exclamation-triangle"></i> Error Loading Profile
            </h3>
            <p style="margin: 0;">${message}</p>
        `;
        mainContent.prepend(errorDiv);
    }
}

/**
 * Initializes the investor profile page
 */
async function initializeInvestorProfilePage() {
    const investorId = getCurrentInvestorId();
    console.log('Initializing investor profile page with ID:', investorId);

    showLoadingState();

    try {
        const profileData = await fetchInvestorProfile(investorId);
        hideLoadingState();
        populateInvestorProfilePage(profileData);

        // Store data globally for edit modal
        window.currentInvestorData = profileData;

    } catch (error) {
        hideLoadingState();
        console.error('Failed to initialize profile:', error);

        let errorMsg = 'Could not load profile data. ';
        if (error.message.includes('403')) {
            errorMsg += 'Access forbidden. Please check your configuration.';
        } else if (error.message.includes('404')) {
            errorMsg += 'Profile not found.';
        } else if (error.message.includes('Failed to fetch')) {
            errorMsg += 'Backend server is not running.';
        } else {
            errorMsg += 'Please check your connection.';
        }

        showErrorMessage(errorMsg);
    }
}

/**
 * Loads profile data into edit modal
 */
function loadInvestorDataIntoModal() {
    const data = window.currentInvestorData || {};

    // Overview
    const editName = document.getElementById('edit-name');
    if (editName) editName.value = data.name || '';

    const editTitle = document.getElementById('edit-title');
    if (editTitle) editTitle.value = data.title || '';

    const editLocation = document.getElementById('edit-location');
    if (editLocation) editLocation.value = data.location || data.address || '';

    const editAbout = document.getElementById('edit-about');
    if (editAbout) editAbout.value = data.about || '';

    // Profile picture preview
    const profilePicPreview = document.getElementById('profile-pic-preview');
    if (profilePicPreview) {
        if (data.profilePicture) {
            const imageUrl = data.profilePicture.startsWith('http')
                ? data.profilePicture
                : `${API_BASE_URL.replace('/api', '')}${data.profilePicture}`;
            profilePicPreview.src = imageUrl;
        } else {
            profilePicPreview.src = 'https://placehold.co/100x100?text=Logo';
        }
    }

    // Add file input change listener for preview
    const fileInput = document.getElementById('edit-profile-pic');
    if (fileInput) {
        fileInput.onchange = function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(event) {
                    const preview = document.getElementById('profile-pic-preview');
                    if (preview) {
                        preview.src = event.target.result;
                    }
                };
                reader.readAsDataURL(file);
            }
        };
    }

    // Contact
    const editWebsite = document.getElementById('edit-contact-website');
    if (editWebsite) editWebsite.value = data.website || '';

    const editEmail = document.getElementById('edit-contact-email');
    if (editEmail) editEmail.value = data.email || '';

    const editPhone = document.getElementById('edit-contact-phone');
    if (editPhone) editPhone.value = data.phone || '';

    // Investment Thesis
    if (data.investmentThesis) {
        const editStage = document.getElementById('edit-investment-stage');
        if (editStage) editStage.value = data.investmentThesis.stage || '';

        const editAmount = document.getElementById('edit-investment-amount');
        if (editAmount) editAmount.value = data.investmentThesis.checkSize || '';

        const editHorizon = document.getElementById('edit-investment-horizon');
        if (editHorizon) editHorizon.value = data.investmentThesis.horizon || '';
    }

    // Investment Focus
    const editSkills = document.getElementById('edit-skills');
    if (editSkills && data.investmentFocus) {
        editSkills.value = data.investmentFocus.join(', ');
    }

    // Load dynamic lists
    loadInvestorTeamForEdit(data.team || []);
    loadInvestorPortfolioForEdit(data.portfolio || []);
}

/**
 * Load team members for editing
 */
function loadInvestorTeamForEdit(team) {
    const list = document.getElementById('team-member-list');
    if (!list) return;
    list.innerHTML = '';

    if (team.length === 0) {
        addInvestorTeamRow();
    } else {
        team.forEach(member => {
            addInvestorTeamRow(member.initials, member.name, member.title, member.description);
        });
    }
}

/**
 * Load portfolio for editing
 */
function loadInvestorPortfolioForEdit(portfolio) {
    const list = document.getElementById('portfolio-list');
    if (!list) return;
    list.innerHTML = '';

    if (portfolio.length === 0) {
        addPortfolioRow();
    } else {
        portfolio.forEach(company => {
            addPortfolioRow(company.initials, company.name, company.sector, company.description);
        });
    }
}

/**
 * Load milestones for editing - REMOVED (not needed for investors)
 */

/**
 * Add team member row
 */
function addInvestorTeamRow(initials = '', name = '', title = '', description = '') {
    const list = document.getElementById('team-member-list');
    if (!list) return;

    const item = document.createElement('div');
    item.className = 'dynamic-list-item'; // This triggers the CSS Grid

    // Notice we removed the inline styles because the CSS handles it now
    item.innerHTML = `
        <input type="text" placeholder="Initials" value="${initials}" class="dynamic-input-small form-input team-initials" maxlength="3">
        <input type="text" placeholder="Full Name" value="${name}" class="dynamic-input-main form-input team-name">
        <input type="text" placeholder="Title/Position" value="${title}" class="dynamic-input-main form-input team-title">

        <button class="dynamic-remove-btn" onclick="this.parentElement.remove()" title="Remove Member">
            <i class="fas fa-times"></i>
        </button>

        <input type="text" placeholder="Brief description (optional)" value="${description}" class="form-input team-description">
    `;
    list.appendChild(item);
}

/**
 * Add portfolio row
 */
function addPortfolioRow(initials = '', name = '', sector = '', description = '') {
    const list = document.getElementById('portfolio-list');
    if (!list) return;

    const item = document.createElement('div');
    item.className = 'dynamic-list-item'; // This triggers the CSS Grid

    item.innerHTML = `
        <input type="text" placeholder="Initials" value="${initials}" class="dynamic-input-small form-input portfolio-initials" maxlength="3">
        <input type="text" placeholder="Start-up Name" value="${name}" class="dynamic-input-main form-input portfolio-name">
        <input type="text" placeholder="Industry Sector" value="${sector}" class="dynamic-input-main form-input portfolio-sector">

        <button class="dynamic-remove-btn" onclick="this.parentElement.remove()" title="Remove Company">
            <i class="fas fa-times"></i>
        </button>

        <textarea placeholder="Short description of what the company does" class="form-input portfolio-description" rows="2">${description}</textarea>
    `;
    list.appendChild(item);
}

/**
 * Milestone functions removed - not needed for investors
 */

/**
 * Uploads profile picture
 */
async function uploadProfilePicture(file) {
    try {
        const formData = new FormData();
        formData.append('file', file);

        const response = await fetch(`${API_BASE_URL}/files/upload/image`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('Upload failed');
        }

        const data = await response.json();
        return data.url;

    } catch (error) {
        console.error('Error uploading image:', error);
        throw error;
    }
}

/**
 * Saves investor profile changes
 */
async function saveInvestorProfileChanges() {
    try {
        const investorId = getCurrentInvestorId();

        const saveBtn = document.querySelector('.save-btn');
        const originalText = saveBtn.innerHTML;
        saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
        saveBtn.disabled = true;

        // Gather all data
        const profileData = {
            name: document.getElementById('edit-name')?.value || '',
            title: document.getElementById('edit-title')?.value || '',
            location: document.getElementById('edit-location')?.value || '',
            about: document.getElementById('edit-about')?.value || '',
            website: document.getElementById('edit-contact-website')?.value || '',
            email: document.getElementById('edit-contact-email')?.value || '',
            phone: document.getElementById('edit-contact-phone')?.value || '',
            investmentThesis: {
                stage: document.getElementById('edit-investment-stage')?.value || '',
                checkSize: document.getElementById('edit-investment-amount')?.value || '',
                horizon: document.getElementById('edit-investment-horizon')?.value || ''
            },
            investmentFocus: [],
            team: [],
            portfolio: []
        };

        // Parse investment focus
        const skillsText = document.getElementById('edit-skills')?.value || '';
        if (skillsText.trim()) {
            profileData.investmentFocus = skillsText.split(',').map(s => s.trim()).filter(s => s.length > 0);
        }

        // Gather team
        document.querySelectorAll('#team-member-list .dynamic-list-item').forEach(item => {
            const initials = item.querySelector('.team-initials')?.value.trim() || '';
            const name = item.querySelector('.team-name')?.value.trim() || '';
            const title = item.querySelector('.team-title')?.value.trim() || '';
            const description = item.querySelector('.team-description')?.value.trim() || '';

            if (name) {
                profileData.team.push({
                    initials: initials || name.split(' ').map(w => w[0]).join('').toUpperCase().substring(0, 2),
                    name: name,
                    title: title,
                    description: description
                });
            }
        });

        // Gather portfolio
        document.querySelectorAll('#portfolio-list .dynamic-list-item').forEach(item => {
            const initials = item.querySelector('.portfolio-initials')?.value.trim() || '';
            const name = item.querySelector('.portfolio-name')?.value.trim() || '';
            const sector = item.querySelector('.portfolio-sector')?.value.trim() || '';
            const description = item.querySelector('.portfolio-description')?.value.trim() || '';

            if (name) {
                profileData.portfolio.push({
                    initials: initials || name.split(' ').map(w => w[0]).join('').toUpperCase().substring(0, 2),
                    name: name,
                    sector: sector,
                    description: description
                });
            }
        });

        // Handle profile picture upload
        const fileInput = document.getElementById('edit-profile-pic');
        if (fileInput && fileInput.files && fileInput.files[0]) {
            try {
                const uploadedUrl = await uploadProfilePicture(fileInput.files[0]);
                profileData.profilePicture = uploadedUrl;
            } catch (error) {
                console.error('Failed to upload profile picture:', error);
                // Continue without profile picture if upload fails
            }
        }

        console.log('Saving investor profile data:', profileData);

        const updatedProfile = await updateInvestorProfile(investorId, profileData);
        window.currentInvestorData = updatedProfile;
        populateInvestorProfilePage(updatedProfile);

        toggleEditProfileModal(false);
        showSuccessMessage('Profile updated successfully!');

        saveBtn.innerHTML = originalText;
        saveBtn.disabled = false;

    } catch (error) {
        console.error('Error saving profile:', error);
        const saveBtn = document.querySelector('.save-btn');
        saveBtn.innerHTML = '<i class="fas fa-save"></i> Save Changes';
        saveBtn.disabled = false;
        alert('Failed to save profile. Please try again.');
    }
}

function showSuccessMessage(message) {
    const msg = document.createElement('div');
    msg.textContent = message;
    msg.style.cssText = 'position: fixed; top: 20px; right: 20px; background: #2ecc71; color: white; padding: 15px 25px; border-radius: 8px; z-index: 10000; box-shadow: 0 4px 15px rgba(0,0,0,0.2); font-weight: 600;';
    document.body.appendChild(msg);
    setTimeout(() => {
        msg.style.transition = 'opacity 0.3s ease';
        msg.style.opacity = '0';
        setTimeout(() => msg.remove(), 300);
    }, 3000);
}

// Make functions globally available
window.loadInvestorDataIntoModal = loadInvestorDataIntoModal;
window.addTeamMember = addInvestorTeamRow;
window.addPortfolio = addPortfolioRow;
window.saveProfileChanges = saveInvestorProfileChanges;
window.initializeInvestorProfilePage = initializeInvestorProfilePage;