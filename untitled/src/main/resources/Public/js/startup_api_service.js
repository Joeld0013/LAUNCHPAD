/**
 * Loads profile data into edit modal
 */
function loadProfileDataIntoModal() {
    const data = window.currentProfileData || {};

    // Overview fields
    const editName = document.getElementById('edit-name');
    if (editName) editName.value = data.name || '';

    const editIndustry = document.getElementById('edit-industry');
    if (editIndustry) editIndustry.value = data.industry || '';

    const editStage = document.getElementById('edit-stage');
    if (editStage) editStage.value = data.stage || '';

    const editLocation = document.getElementById('edit-location');
    if (editLocation) editLocation.value = data.location || data.address || '';

    const editAbout = document.getElementById('edit-about');
    if (editAbout) editAbout.value = data.about || data.description || '';

    // Profile picture preview
    const profilePicPreview = document.getElementById('profile-pic-preview');
    if (profilePicPreview) {
        if (data.profilePicture) {
            profilePicPreview.src = data.profilePicture;
        } else {
            profilePicPreview.src = 'https://via.placeholder.com/100x100/eee/666?text=No+Image';
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

    // Contact fields
    const editWebsite = document.getElementById('edit-contact-website');
    if (editWebsite) editWebsite.value = data.website || '';

    const editEmail = document.getElementById('edit-contact-email');
    if (editEmail) editEmail.value = data.email || '';

    const editPhone = document.getElementById('edit-contact-phone');
    if (editPhone) editPhone.value = data.phone || '';

    // Media fields
    const editGrowthMetrics = document.getElementById('edit-growth-metrics');
    if (editGrowthMetrics) editGrowthMetrics.value = data.growthMetrics || '';

    const editPitchVideoUrl = document.getElementById('edit-pitch-video-url');
    if (editPitchVideoUrl) editPitchVideoUrl.value = data.pitchVideoUrl || '';

    // Show current video if exists
    if (data.pitchVideoUrl) {
        const previewContainer = document.getElementById('current-video-preview');
        const previewVideo = document.getElementById('preview-video');
        if (previewVideo && previewContainer) {
            const videoUrl = data.pitchVideoUrl.startsWith('http')
                ? data.pitchVideoUrl
                : `${API_BASE_URL.replace('/api', '')}${data.pitchVideoUrl}`;
            previewVideo.src = videoUrl;
            previewContainer.style.display = 'block';
        }
    } else {
        const previewContainer = document.getElementById('current-video-preview');
        if (previewContainer) {
            previewContainer.style.display = 'none';
        }
    }

    // Skills
    const editSkills = document.getElementById('edit-skills');
    if (editSkills && data.skills) {
        editSkills.value = data.skills.join(', ');
    }

    // Load team and milestones dynamically
    loadTeamMembersForEdit(data.team || []);
    loadMilestonesForEdit(data.milestones || []);
}

function loadTeamMembersForEdit(team) {
    const list = document.getElementById('team-member-list');
    if (!list) return;
    list.innerHTML = '';

    if (team.length === 0) {
        // Add one empty row
        addTeamMemberRow();
    } else {
        team.forEach(member => {
            addTeamMemberRow(member.initials, member.name, member.title, member.description);
        });
    }
}

function loadMilestonesForEdit(milestones) {
    const list = document.getElementById('milestone-list');
    if (!list) return;
    list.innerHTML = '';

    if (milestones.length === 0) {
        addMilestoneRow();
    } else {
        milestones.forEach(m => {
            addMilestoneRow(m.icon, m.title, m.date);
        });
    }
}

function addTeamMemberRow(initials = '', name = '', title = '', description = '') {
    const list = document.getElementById('team-member-list');
    if (!list) return;

    const item = document.createElement('div');
    item.className = 'dynamic-list-item';
    item.innerHTML = `
        <input type="text" placeholder="Initials (e.g., JK)" value="${initials}" class="dynamic-input-small form-input team-initials">
        <input type="text" placeholder="Name (e.g., John Doe)" value="${name}" class="dynamic-input-main form-input team-name">
        <input type="text" placeholder="Title (e.g., CEO)" value="${title}" class="dynamic-input-main form-input team-title">
        <input type="text" placeholder="Description" value="${description}" class="dynamic-input-main form-input team-description" style="grid-column: span 3; margin-top: 5px;">
        <button class="dynamic-remove-btn" onclick="this.parentElement.remove()">&times;</button>
    `;
    list.appendChild(item);
}

function addMilestoneRow(icon = 'fas fa-star', title = '', date = '') {
    const list = document.getElementById('milestone-list');
    if (!list) return;

    const item = document.createElement('div');
    item.className = 'dynamic-list-item';
    item.innerHTML = `
        <input type="text" placeholder="Icon (e.g., fas fa-rocket)" value="${icon}" class="dynamic-input-small form-input milestone-icon">
        <input type="text" placeholder="Title (e.g., Seed Funding)" value="${title}" class="dynamic-input-main form-input milestone-title">
        <input type="text" placeholder="Date (e.g., January 2024)" value="${date}" class="dynamic-input-main form-input milestone-date">
        <button class="dynamic-remove-btn" onclick="this.parentElement.remove()">&times;</button>
    `;
    list.appendChild(item);
}

// Export for use in HTML onclick
window.addTeamMember = addTeamMemberRow;
window.addMilestone = addMilestoneRow;
window.loadProfileDataIntoModal = loadProfileDataIntoModal;

/**
 * Saves profile changes from the edit modal
 */
async function saveProfileChanges() {
    try {
        const startupId = getCurrentStartupId();

        // Show loading state
        const saveBtn = document.querySelector('.save-btn');
        const originalText = saveBtn.innerHTML;
        saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
        saveBtn.disabled = true;

        // Gather all form data
        const profileData = {
            // Overview
            name: document.getElementById('edit-name')?.value || '',
            industry: document.getElementById('edit-industry')?.value || '',
            stage: document.getElementById('edit-stage')?.value || '',
            location: document.getElementById('edit-location')?.value || '',
            about: document.getElementById('edit-about')?.value || '',

            // Contact
            website: document.getElementById('edit-contact-website')?.value || '',
            email: document.getElementById('edit-contact-email')?.value || '',
            phone: document.getElementById('edit-contact-phone')?.value || '',

            // Media
            growthMetrics: document.getElementById('edit-growth-metrics')?.value || '',
            pitchVideoUrl: document.getElementById('edit-pitch-video-url')?.value || '',

            // Skills
            skills: [],

            // Team
            team: [],

            // Milestones
            milestones: []
        };

        // Parse skills from comma-separated string
        const skillsText = document.getElementById('edit-skills')?.value || '';
        if (skillsText.trim()) {
            profileData.skills = skillsText.split(',').map(s => s.trim()).filter(s => s.length > 0);
        }

        // Gather team members
        const teamItems = document.querySelectorAll('#team-member-list .dynamic-list-item');
        console.log('Found team items:', teamItems.length);

        teamItems.forEach((item, index) => {
            const initialsInput = item.querySelector('.team-initials');
            const nameInput = item.querySelector('.team-name');
            const titleInput = item.querySelector('.team-title');
            const descInput = item.querySelector('.team-description');

            const initials = initialsInput ? initialsInput.value.trim() : '';
            const name = nameInput ? nameInput.value.trim() : '';
            const title = titleInput ? titleInput.value.trim() : '';
            const description = descInput ? descInput.value.trim() : '';

            console.log(`Team member ${index}:`, { initials, name, title, description });

            // Only add if name is provided
            if (name) {
                const memberInitials = initials || name.split(' ').map(w => w[0]).join('').toUpperCase().substring(0, 2);
                profileData.team.push({
                    initials: memberInitials,
                    name: name,
                    title: title || 'Team Member',
                    description: description || title || 'Team Member'
                });
            }
        });

        console.log('Total team members collected:', profileData.team.length);

        // Gather milestones
        const milestoneItems = document.querySelectorAll('#milestone-list .dynamic-list-item');
        console.log('Found milestone items:', milestoneItems.length);

        milestoneItems.forEach((item, index) => {
            const iconInput = item.querySelector('.milestone-icon');
            const titleInput = item.querySelector('.milestone-title');
            const dateInput = item.querySelector('.milestone-date');

            const icon = iconInput ? iconInput.value.trim() : 'fas fa-star';
            const title = titleInput ? titleInput.value.trim() : '';
            const date = dateInput ? dateInput.value.trim() : '';

            console.log(`Milestone ${index}:`, { icon, title, date });

            // Only add if title is provided
            if (title) {
                profileData.milestones.push({
                    icon: icon || 'fas fa-star',
                    title: title,
                    date: date || 'Date not specified'
                });
            }
        });

        console.log('Total milestones collected:', profileData.milestones.length);

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

        console.log('Saving profile data:', profileData);

        // Send update request
        const updatedProfile = await updateStartupProfile(startupId, profileData);

        // Update global data
        window.currentProfileData = updatedProfile;

        // Refresh the page display
        populateProfilePage(updatedProfile);

        // Close modal
        toggleEditProfileModal(false);

        // Show success message
        showSuccessMessage('Profile updated successfully!');

        // Restore button
        saveBtn.innerHTML = originalText;
        saveBtn.disabled = false;

    } catch (error) {
        console.error('Error saving profile:', error);

        // Restore button
        const saveBtn = document.querySelector('.save-btn');
        saveBtn.innerHTML = '<i class="fas fa-save"></i> Save Changes';
        saveBtn.disabled = false;

        // Show error
        alert('Failed to save profile. Please try again.\n\nError: ' + error.message);
    }
}

/**
 * Converts a file to base64 string
 */
function fileToBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result);
        reader.onerror = reject;
        reader.readAsDataURL(file);
    });
}

/**
 * Shows a success message
 */
function showSuccessMessage(message) {
    const msg = document.createElement('div');
    msg.textContent = message;
    msg.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #2ecc71;
        color: white;
        padding: 15px 25px;
        border-radius: 8px;
        z-index: 10000;
        box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        font-weight: 600;
    `;
    document.body.appendChild(msg);

    setTimeout(() => {
        msg.style.transition = 'opacity 0.3s ease';
        msg.style.opacity = '0';
        setTimeout(() => msg.remove(), 300);
    }, 3000);
}

// Make saveProfileChanges available globally
window.saveProfileChanges = saveProfileChanges;

/**
 * Debug function to test data collection
 */
function testDataCollection() {
    console.log('=== TESTING DATA COLLECTION ===');

    // Test team members
    const teamItems = document.querySelectorAll('#team-member-list .dynamic-list-item');
    console.log('Team items found:', teamItems.length);
    teamItems.forEach((item, i) => {
        const inputs = {
            initials: item.querySelector('.team-initials')?.value,
            name: item.querySelector('.team-name')?.value,
            title: item.querySelector('.team-title')?.value,
            description: item.querySelector('.team-description')?.value
        };
        console.log(`Team ${i}:`, inputs);
    });

    // Test milestones
    const milestoneItems = document.querySelectorAll('#milestone-list .dynamic-list-item');
    console.log('Milestone items found:', milestoneItems.length);
    milestoneItems.forEach((item, i) => {
        const inputs = {
            icon: item.querySelector('.milestone-icon')?.value,
            title: item.querySelector('.milestone-title')?.value,
            date: item.querySelector('.milestone-date')?.value
        };
        console.log(`Milestone ${i}:`, inputs);
    });

    console.log('=== END TEST ===');
}

window.testDataCollection = testDataCollection;

/**
 * Handles video file selection
 */
function handleVideoFileSelect(event) {
    const file = event.target.files[0];
    const uploadBtn = document.getElementById('upload-video-btn');

    if (file) {
        // Show upload button
        uploadBtn.style.display = 'inline-block';

        // Validate file size (100MB = 100 * 1024 * 1024 bytes)
        const maxSize = 100 * 1024 * 1024;
        if (file.size > maxSize) {
            alert('File size must be less than 100MB');
            event.target.value = '';
            uploadBtn.style.display = 'none';
            return;
        }

        // Validate file type
        if (!file.type.startsWith('video/')) {
            alert('Please select a video file');
            event.target.value = '';
            uploadBtn.style.display = 'none';
            return;
        }

        console.log('Video file selected:', file.name, 'Size:', (file.size / (1024 * 1024)).toFixed(2) + 'MB');
    } else {
        uploadBtn.style.display = 'none';
    }
}

/**
 * Uploads the pitch video to the server
 */
async function uploadPitchVideo() {
    const fileInput = document.getElementById('edit-pitch-video-file');
    const file = fileInput.files[0];

    if (!file) {
        alert('Please select a video file first');
        return;
    }

    try {
        // Show progress bar
        const progressContainer = document.getElementById('upload-progress');
        const progressBar = document.getElementById('progress-bar');
        const progressText = document.getElementById('progress-text');
        const uploadBtn = document.getElementById('upload-video-btn');

        progressContainer.style.display = 'block';
        uploadBtn.disabled = true;
        uploadBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Uploading...';

        // Create FormData
        const formData = new FormData();
        formData.append('file', file);

        // Upload with progress tracking
        const xhr = new XMLHttpRequest();

        // Track upload progress
        xhr.upload.addEventListener('progress', (e) => {
            if (e.lengthComputable) {
                const percentComplete = (e.loaded / e.total) * 100;
                progressBar.style.width = percentComplete + '%';
                progressText.textContent = Math.round(percentComplete) + '%';
            }
        });

        // Handle completion
        xhr.addEventListener('load', () => {
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                console.log('Video uploaded successfully:', response);

                // Store the video URL
                document.getElementById('edit-pitch-video-url').value = response.url;

                // Show preview
                const previewContainer = document.getElementById('current-video-preview');
                const previewVideo = document.getElementById('preview-video');
                previewVideo.src = `${API_BASE_URL.replace('/api', '')}${response.url}`;
                previewContainer.style.display = 'block';

                // Hide progress, reset button
                setTimeout(() => {
                    progressContainer.style.display = 'none';
                    progressBar.style.width = '0%';
                    uploadBtn.innerHTML = '<i class="fas fa-check"></i> Uploaded!';
                    uploadBtn.style.background = '#2ecc71';

                    setTimeout(() => {
                        uploadBtn.style.display = 'none';
                        uploadBtn.disabled = false;
                        uploadBtn.innerHTML = '<i class="fas fa-upload"></i> Upload';
                        uploadBtn.style.background = '#3498db';
                    }, 2000);
                }, 500);

                // Clear file input
                fileInput.value = '';

            } else {
                throw new Error('Upload failed with status: ' + xhr.status);
            }
        });

        // Handle errors
        xhr.addEventListener('error', () => {
            console.error('Upload error');
            alert('Failed to upload video. Please try again.');
            progressContainer.style.display = 'none';
            uploadBtn.disabled = false;
            uploadBtn.innerHTML = '<i class="fas fa-upload"></i> Upload';
        });

        // Send request
        xhr.open('POST', `${API_BASE_URL}/files/upload/video`);
        xhr.send(formData);

    } catch (error) {
        console.error('Error uploading video:', error);
        alert('Failed to upload video: ' + error.message);
    }
}

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

// Make functions globally available
window.handleVideoFileSelect = handleVideoFileSelect;
window.uploadPitchVideo = uploadPitchVideo;// API Service for Startup Profile
// Place this in: js/startup_api_service.js

// Only declare API_BASE_URL once
const API_BASE_URL = window.API_BASE_URL || 'http://localhost:8080/api';

/**
 * Gets the current startup ID from localStorage or URL
 */
function getCurrentStartupId() {
    // First try URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    let startupId = urlParams.get('id');

    // If no URL param, try localStorage (from login session)
    if (!startupId) {
        startupId = localStorage.getItem('currentStartupId');
    }

    // Default fallback
    if (!startupId) {
        startupId = '68db98e07d40e835d7c8a778';
    }

    return startupId;
}

/**
 * Fetches startup profile data from the backend
 * @param {string} startupId - The ID of the startup to fetch
 * @returns {Promise<Object>} The startup profile data
 */
async function fetchStartupProfile(startupId) {
    try {
        console.log('Fetching startup profile for ID:', startupId);
        console.log('API URL:', `${API_BASE_URL}/startups/${startupId}`);

        const response = await fetch(`${API_BASE_URL}/startups/${startupId}`, {
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
        console.error('Error fetching startup profile:', error);
        throw error;
    }
}

/**
 * Fetches startup profile by email
 * @param {string} email - The email of the startup
 * @returns {Promise<Object>} The startup profile data
 */
async function fetchStartupProfileByEmail(email) {
    try {
        const response = await fetch(`${API_BASE_URL}/startups/email/${encodeURIComponent(email)}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching startup profile by email:', error);
        throw error;
    }
}

/**
 * Updates startup profile data
 * @param {string} startupId - The ID of the startup
 * @param {Object} profileData - The updated profile data
 * @returns {Promise<Object>} The updated startup profile data
 */
async function updateStartupProfile(startupId, profileData) {
    try {
        console.log('Updating startup profile:', startupId, profileData);

        const response = await fetch(`${API_BASE_URL}/startups/${startupId}`, {
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
        console.error('Error updating startup profile:', error);
        throw error;
    }
}

/**
 * Populates the profile page with data from the backend
 * @param {Object} profileData - The startup profile data
 */
function populateProfilePage(profileData) {
    console.log('Populating page with data:', profileData);

    // Basic Info
    const nameEl = document.getElementById('page-name');
    if (nameEl && profileData.name) {
        nameEl.textContent = profileData.name;
    }

    const titleEl = document.getElementById('page-title');
    if (titleEl) {
        titleEl.textContent = profileData.title || `${profileData.industry || ''} | ${profileData.stage || ''} Stage`;
    }

    const locationEl = document.getElementById('page-location');
    if (locationEl) {
        locationEl.textContent = profileData.location || `📍 ${profileData.address || profileData.country || 'Location not specified'}`;
    }

    const aboutEl = document.getElementById('page-about');
    if (aboutEl) {
        aboutEl.textContent = profileData.about || profileData.description || 'No description available.';
    }

    // Profile Avatar
    const pageAvatar = document.getElementById('page-avatar');
    if (profileData.profilePicture) {
        pageAvatar.textContent = '';
        pageAvatar.style.backgroundImage = `url(${profileData.profilePicture})`;
        pageAvatar.style.backgroundSize = 'cover';
        pageAvatar.style.backgroundPosition = 'center';
    } else if (profileData.name) {
        // Show initials if no profile picture
        pageAvatar.style.backgroundImage = 'none';
        const initials = profileData.name.split(' ')
            .map(word => word[0])
            .join('')
            .toUpperCase()
            .substring(0, 2);
        pageAvatar.textContent = initials;
    }

    // Contact Info - Fix null checks
    const websiteElement = document.getElementById('page-website');
    if (websiteElement && profileData.website) {
        websiteElement.textContent = profileData.website;
        websiteElement.href = profileData.website.startsWith('http') ? profileData.website : `https://${profileData.website}`;
    } else if (websiteElement) {
        websiteElement.textContent = 'Not provided';
        websiteElement.href = '#';
    }

    const emailElement = document.getElementById('page-email');
    if (emailElement) {
        emailElement.textContent = profileData.email || 'Not provided';
    }

    const phoneElement = document.getElementById('page-phone');
    if (phoneElement) {
        phoneElement.textContent = profileData.phone || 'Not provided';
    }

    // Media - Fix video display
    const pitchVideoContainer = document.getElementById('pitch-video-container');
    const noVideoMsg = document.getElementById('no-video-msg');

    if (profileData.pitchVideoUrl) {
        if (pitchVideoContainer) {
            // Clear container
            pitchVideoContainer.innerHTML = '';

            // Create video player with custom overlay
            const playerWrapper = document.createElement('div');
            playerWrapper.className = 'video-player-wrapper';

            const video = document.createElement('video');
            video.id = 'pitch-video-player';
            video.controls = true;
            video.preload = 'metadata';

            // Check if it's a full URL or relative path
            const videoUrl = profileData.pitchVideoUrl.startsWith('http')
                ? profileData.pitchVideoUrl
                : `${API_BASE_URL.replace('/api', '')}${profileData.pitchVideoUrl}`;

            video.src = videoUrl;

            // Create overlay
            const overlay = document.createElement('div');
            overlay.className = 'video-overlay';
            overlay.innerHTML = `
                <div class="play-button">
                    <i class="fas fa-play"></i>
                </div>
                <div class="video-title">Watch Our Pitch Deck</div>
            `;

            // Add click handler to overlay
            overlay.addEventListener('click', function() {
                video.play();
                overlay.classList.add('hidden');
            });

            // Hide overlay when video plays
            video.addEventListener('play', function() {
                overlay.classList.add('hidden');
            });

            // Show overlay when video pauses
            video.addEventListener('pause', function() {
                if (!video.ended) {
                    overlay.classList.remove('hidden');
                }
            });

            // Show overlay when video ends
            video.addEventListener('ended', function() {
                overlay.classList.remove('hidden');
            });

            playerWrapper.appendChild(video);
            playerWrapper.appendChild(overlay);
            pitchVideoContainer.appendChild(playerWrapper);
        }

        if (noVideoMsg) {
            noVideoMsg.style.display = 'none';
        }
    } else {
        if (pitchVideoContainer) {
            pitchVideoContainer.innerHTML = '<div id="no-video-msg" class="text-muted" style="text-align: center; padding: 20px; font-size: 0.9em;">No pitch video uploaded yet.</div>';
        }
    }

    // Skills
    const skillsList = document.getElementById('page-skills-list');
    if (skillsList) {
        if (profileData.skills && profileData.skills.length > 0) {
            skillsList.innerHTML = '';
            profileData.skills.forEach(skill => {
                const skillTag = document.createElement('span');
                skillTag.className = 'skill-tag';
                skillTag.textContent = skill;
                skillsList.appendChild(skillTag);
            });
        } else {
            skillsList.innerHTML = '<span class="text-muted" style="font-size: 0.9em;">No skills added.</span>';
        }
    }

    // Team Members
    const teamList = document.getElementById('page-team-list');
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
                        <p>${member.description || ''}</p>
                    </div>
                `;
                teamList.appendChild(memberDiv);
            });
        } else {
            teamList.innerHTML = '<p class="text-muted" style="padding: 10px;">No team members listed.</p>';
        }
    }

    // Milestones
    const milestoneList = document.getElementById('page-milestone-list');
    if (milestoneList) {
        if (profileData.milestones && profileData.milestones.length > 0) {
            milestoneList.innerHTML = '';
            profileData.milestones.forEach(milestone => {
                const milestoneDiv = document.createElement('div');
                milestoneDiv.className = 'experience-item';
                milestoneDiv.dataset.icon = milestone.icon || 'fas fa-star';

                milestoneDiv.innerHTML = `
                    <div class="experience-icon"><i class="${milestone.icon || 'fas fa-star'}"></i></div>
                    <div class="experience-details">
                        <div class="experience-title">${milestone.title || ''}</div>
                        <div class="experience-duration">${milestone.date || ''}</div>
                    </div>
                `;
                milestoneList.appendChild(milestoneDiv);
            });
        } else {
            milestoneList.innerHTML = '<p class="text-muted" style="padding: 10px;">No milestones added yet.</p>';
        }
    }
}

/**
 * Shows a loading state on the profile page
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
 * Hides the loading state
 */
function hideLoadingState() {
    const loader = document.getElementById('profile-loader');
    if (loader) {
        loader.remove();
    }
}

/**
 * Shows an error message on the profile page
 * @param {string} message - The error message to display
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
 * Initializes the profile page by loading data from backend
 */
async function initializeProfilePage() {
    const startupId = getCurrentStartupId();
    console.log('Initializing profile page with ID:', startupId);

    showLoadingState();

    try {
        const profileData = await fetchStartupProfile(startupId);
        hideLoadingState();
        populateProfilePage(profileData);

        // Store data globally for edit modal
        window.currentProfileData = profileData;

    } catch (error) {
        hideLoadingState();
        console.error('Failed to initialize profile:', error);

        // Show more detailed error message
        let errorMsg = 'Could not load profile data. ';
        if (error.message.includes('403')) {
            errorMsg += 'Access forbidden. Please check your Spring Security configuration or try logging in again.';
        } else if (error.message.includes('404')) {
            errorMsg += 'Profile not found. Please check the startup ID.';
        } else if (error.message.includes('Failed to fetch')) {
            errorMsg += 'Backend server is not running. Please start your Spring Boot application.';
        } else {
            errorMsg += 'Please check your connection and try again.';
        }

        showErrorMessage(errorMsg);
    }
}