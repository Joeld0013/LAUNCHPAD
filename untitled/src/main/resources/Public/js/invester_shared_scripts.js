/**
 * Toggles the notification popup and the bell icon's active state.
 * @param {Event} event - The click event.
 */
function toggleNotifications(event) {
    // This stops the click from bubbling up to the 'document' listener,
    // which would otherwise immediately close the popup.
    if (event) event.stopPropagation();

    const popup = document.getElementById('notification-popup');
    const bellIcon = document.getElementById('bell-icon'); // Get the icon

    if (popup && bellIcon) {
        if (popup.style.display === 'block') {
            popup.style.display = 'none';
            bellIcon.classList.remove('active'); // Remove active class
        } else {
            popup.style.display = 'block';
            bellIcon.classList.add('active'); // Add active class
        }
    }
}

// This function is for the post filtering on the posts page
/**
 * Filters posts on the post feed page based on category.
 * @param {Event} event - The click event from the filter button.
 * @param {string} type - The category to filter by ('all', 'investor', 'startup').
 */
function filterPosts(event, type) {
    // Check if filter buttons exist on this page
    const filterButtons = document.querySelectorAll('.feed-filter-bar .filter-btn, .filter-buttons .filter-btn');
    if (!filterButtons.length) return;

    filterButtons.forEach(btn => btn.classList.remove('active'));
    if (event && event.currentTarget) {
        event.currentTarget.classList.add('active');
    }

    const allPosts = document.querySelectorAll('.post-feed .post-card, .post-list .post-card');

    allPosts.forEach(post => {
        // This logic uses style.display to show/hide posts based on filter
        post.style.display = 'block'; // Show all by default

        if (type === 'all') {
            // do nothing, already visible
        } else if (type === 'investor' && !post.classList.contains('investor-post')) {
            post.style.display = 'none';
        } else if (type === 'startup' && !post.classList.contains('startup-post')) {
            post.style.display = 'none';
        }
    });

    // Handle old post-list-title if it exists
    const postListTitle = document.getElementById('post-list-title');
    if (postListTitle) {
        if (type === 'all') {
            postListTitle.textContent = 'All Posts';
        } else if (type === 'investor') {
            postListTitle.textContent = 'Investor Posts';
        } else if (type === 'startup') {
            postListTitle.textContent = 'Startup Posts';
        }
    }
}

// This function is for the edit profile modal on the profile page
function toggleEditProfileModal(open) {
    const modal = document.getElementById('edit-profile-modal');
    if (!modal) return;

    if (open) {
        // --- LOAD DATA FROM PAGE INTO MODAL ---
        try {
            document.getElementById('edit-name').value = document.getElementById('page-name')?.textContent || '';
            document.getElementById('edit-title').value = document.getElementById('page-title')?.textContent || '';
            document.getElementById('edit-location').value = document.getElementById('page-location')?.textContent || '';
            document.getElementById('edit-about').value = document.getElementById('page-about')?.textContent || '';

            document.getElementById('edit-contact-website').value = document.querySelector('#page-website .link')?.textContent || '';
            document.getElementById('edit-contact-email').value = document.querySelector('#page-email span:last-child')?.textContent || '';
            document.getElementById('edit-contact-phone').value = document.querySelector('#page-phone span:last-child')?.textContent || '';

            // Load dynamic lists
            loadThesisToModal();
            loadTeamMembersToModal();
            loadMilestonesToModal();
            loadSkillsToModal();
        } catch (e) {
            console.error("Error loading profile data into modal:", e);
        }

        // Reset to the first tab
        switchEditModalTab(null, 'edit-tab-overview');
        modal.style.display = 'flex';
    } else {
        modal.style.display = 'none';
    }
}

document.addEventListener('DOMContentLoaded', function() {

    // --- Global Click Listener (for closing popups) ---
    document.addEventListener('click', function(event) {
        const popup = document.getElementById('notification-popup');
        const bellIcon = document.getElementById('bell-icon');

        // Check if notification popup is open AND the click was NOT on the popup or the bell
        if (popup && bellIcon && popup.style.display === 'block' &&
            !popup.contains(event.target) && !bellIcon.contains(event.target)) {
            popup.style.display = 'none';
            bellIcon.classList.remove('active');
        }
    });

    // --- Navigation Highlighting ---
    const currentPage = window.location.pathname.split('/').pop();
    const navLinks = document.querySelectorAll('.nav-links a');
    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPage) {
            link.classList.add('active');
        }
    });

    // --- Post Feed Listeners (Likes, Comments, Share, See More) ---
    document.querySelectorAll('.like-btn').forEach(button => {
        button.addEventListener('click', function() {
            this.classList.toggle('liked');
            const countElement = this.querySelector('.like-count');
            if (!countElement) return; // Guard clause

            let likes = parseInt(this.getAttribute('data-likes'));
            likes += this.classList.contains('liked') ? 1 : -1; // Simplified logic
            this.setAttribute('data-likes', likes);
            countElement.textContent = likes;
        });
    });

    document.querySelectorAll('.comment-btn').forEach(button => {
        button.addEventListener('click', function() {
            // Find the parent .post-card
            const postCard = this.closest('.post-card');
            if (postCard) {
                // Find the .comment-section within that card
                const commentSection = postCard.querySelector('.comment-section');
                if (commentSection) {
                    // Toggle the class
                    commentSection.classList.toggle('comments-open');
                }
            }
        });
    });

    document.querySelectorAll('.share-btn').forEach(button => {
        button.addEventListener('click', toggleShareModal);
    });

    document.querySelectorAll('.see-more-toggle').forEach(toggle => {
        toggle.addEventListener('click', function() {
            const postBodyDiv = this.parentElement;
            if (postBodyDiv) {
                postBodyDiv.classList.add('expanded');
            }
        });
    });

    // --- Chat Page Listeners ---
    document.querySelectorAll('.chat-inbox-item').forEach(item => {
        item.addEventListener('click', selectChat);
    });

    const sendButton = document.querySelector('.send-btn');
    if (sendButton) {
        const chatInput = document.querySelector('.chat-input');
        const messagesArea = document.querySelector('.chat-messages-area');

        // Helper function to send the chat message
        const sendChatMessage = () => {
            if (!chatInput || !messagesArea) return;
            const messageText = chatInput.value.trim();

            if (messageText) {
                const newBubble = document.createElement('div');
                newBubble.className = 'message-bubble outgoing';
                const now = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

                // Set inner HTML safely
                const textDiv = document.createElement('div');
                textDiv.className = 'message-text';
                textDiv.textContent = messageText; // Use .textContent to prevent XSS

                const timeDiv = document.createElement('div');
                timeDiv.className = 'message-timestamp';
                timeDiv.textContent = now;

                newBubble.appendChild(textDiv);
                newBubble.appendChild(timeDiv);

                messagesArea.appendChild(newBubble);
                chatInput.value = '';
                messagesArea.scrollTop = messagesArea.scrollHeight;
            }
        };

        // Click listener
        sendButton.addEventListener('click', sendChatMessage);

        // Keypress listener
        if (chatInput) {
            chatInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    e.preventDefault(); // Stop form submission (if any)
                    sendChatMessage(); // Trigger the send
                }
            });
        }
    }
});

/**
 * Toggles the visibility of the "Logout" confirmation modal.
 */
function toggleLogoutModal() {
    const modal = document.getElementById('logout-confirm-modal');
    if (modal) {
        modal.style.display = (modal.style.display === 'flex') ? 'none' : 'flex';
    }
}

/**
 * Confirms the logout action, closes the modal, and shows a message.
 */
function confirmLogout() {
    toggleLogoutModal(); // Close the modal
    console.log("User clicked log out.");
    alert("You have been logged out.");
    // In a real app, you'd redirect:
    // window.location.href = "login.html";
}

/**
 * Switches the active tab on the Bids Dashboard page.
 * @param {Event} event - The click event from the tab button.
 * @param {string} tabId - The ID of the tab panel to show.
 */
function switchBidTab(event, tabId) {
    // Get all tab content panels
    const tabPanels = document.querySelectorAll('.bids-tab-panel');
    tabPanels.forEach(panel => {
        panel.classList.remove('active-tab');
    });

    // Get all tab buttons and remove the 'active' class
    const tabButtons = document.querySelectorAll('.profile-tab-btn');
    tabButtons.forEach(button => {
        button.classList.remove('active');
    });

    // Show the target tab panel by adding the 'active-tab' class
    const targetPanel = document.getElementById(tabId);
    if (targetPanel) {
        targetPanel.classList.add('active-tab');
    }

    // Set the clicked button to active
    if (event && event.currentTarget) {
        event.currentTarget.classList.add('active');
    }
}

/**
 * Simulates handling a bid action on the Bids Dashboard (Investor side).
 * @param {Event} event - The click event.
 * @param {string} action - The action taken ('withdraw').
 */
function handleBidAction(event, action) {
    const bidCard = event.currentTarget.closest('.bid-card');
    const startupName = bidCard.querySelector('.bid-investor-name').textContent;

    if (action === 'withdraw') {
        if (confirm(`Are you sure you want to WITHDRAW your bid from ${startupName}?`)) {
            alert('Bid Withdrawn.');
            bidCard.style.opacity = '0.6';
            bidCard.querySelector('.bid-card-actions').innerHTML = '<p style="color: #e74c3c; font-weight: 600; margin: 0 10px 0 auto;">BID WITHDRAWN</p>';
        }
    }
}

/**
 * Handles selecting a chat in the chat inbox.
 * @param {Event} event - The click event from the chat item.
 */
function selectChat(event) {
    // Ensure we're on the chat page
    const chatList = document.querySelector('.chat-inbox-list');
    if (!chatList) return;

    // Remove 'active' class from all chat items
    document.querySelectorAll('.chat-inbox-item').forEach(item => {
        item.classList.remove('active');
    });

    const currentItem = event.currentTarget;
    currentItem.classList.add('active');

    const chatName = currentItem.querySelector('.chat-item-name').textContent;
    const chatAvatar = currentItem.querySelector('.chat-avatar');

    const chatHeaderName = document.querySelector('.chat-header-name');
    const chatAvatarLarge = document.querySelector('.chat-avatar-large');
    const messagesArea = document.querySelector('.chat-messages-area');

    if (chatHeaderName) chatHeaderName.textContent = chatName;
    if (chatAvatarLarge) {
        chatAvatarLarge.textContent = chatAvatar.textContent;
        // Copy style (for colors)
        chatAvatarLarge.style.backgroundColor = chatAvatar.style.backgroundColor;
        chatAvatarLarge.style.color = chatAvatar.style.color;
    }

    if (messagesArea) {
        messagesArea.innerHTML =
            `<div class="message-bubble incoming">
                <div class="message-text">Loading messages for ${chatName}...</div>
            </div>`;
    }
}

/**
 * Toggles the visibility of the "Create Post" modal.
 */
function toggleCreatePostModal() {
    const modal = document.getElementById('create-post-modal');
    if (modal) {
        modal.style.display = (modal.style.display === 'flex') ? 'none' : 'flex';
    }
}

/**
 * Toggles the visibility of the "Share Post" modal.
 */
function toggleShareModal() {
    const modal = document.getElementById('share-post-modal');
    if (modal) {
        modal.style.display = (modal.style.display === 'flex') ? 'none' : 'flex';
    }
}

/**
 * Copies the link from the share modal's input field.
 */
function copyShareLink() {
    const input = document.getElementById('share-link-input');
    if (!input) return;

    input.select();
    input.setSelectionRange(0, 99999); // For mobile devices

    try {
        // Use modern clipboard API if available
        navigator.clipboard.writeText(input.value).then(() => {
            alert('Link copied to clipboard!');
        }).catch(err => {
            // Fallback for older browsers
            document.execCommand('copy');
            alert('Link copied to clipboard!');
        });
    } catch (err) {
        // Fallback for very old browsers
        document.execCommand('copy');
        alert('Link copied to clipboard!');
    }
}

/**
 * Toggles the "Follow" button state on the profile page.
 * @param {Event} event - The click event from the follow button.
 */
function toggleFollow(event) {
    const button = event.currentTarget;
    if (!button) return;

    const buttonIcon = button.querySelector('i');

    if (button.classList.contains('following')) {
        // --- Logic to Unfollow ---
        button.classList.remove('following');
        if (buttonIcon) {
            buttonIcon.classList.remove('fa-check');
            buttonIcon.classList.add('fa-plus');
        }
        button.innerHTML = '<i class="fas fa-plus"></i> Follow';
    } else {
        // --- Logic to Follow ---
        button.classList.add('following');
        if (buttonIcon) {
            buttonIcon.classList.remove('fa-plus');
            buttonIcon.classList.add('fa-check');
        }
         button.innerHTML = '<i class="fas fa-check"></i> Following';
    }
}

/**
 * Switches the active tab on the profile page.
 * @param {Event} event - The click event from the tab button.
 * @param {string} tabId - The ID of the tab panel to show.
 */
function switchProfileTab(event, tabId) {
    // Get all tab content panels
    const tabPanels = document.querySelectorAll('.profile-tab-panel');
    tabPanels.forEach(panel => {
        panel.style.display = '';
        panel.classList.remove('active-tab');
    });

    // Get all tab buttons and remove the 'active' class
    const tabButtons = document.querySelectorAll('.profile-tab-btn');
    tabButtons.forEach(button => {
        button.classList.remove('active');
    });

    // Show the target tab panel by adding the 'active-tab' class
    const targetPanel = document.getElementById(tabId);
    if (targetPanel) {
        targetPanel.classList.add('active-tab');
    }

    // Set the clicked button to active
    if (event && event.currentTarget) {
        event.currentTarget.classList.add('active');
    }
}

/**
 * Loads existing team members from the sidebar into the edit modal.
 */
function loadTeamMembersToModal() {
    const list = document.getElementById('team-member-list');
    if (!list) return;
    list.innerHTML = ''; // Clear existing
    document.querySelectorAll('#page-team-list-sidebar .follow-item').forEach(member => {
        const initials = member.querySelector('.post-avatar')?.textContent || '';
        const name = member.querySelector('.company-name')?.textContent || '';
        const title = member.querySelector('.follow-title')?.textContent || '';
        // Pass 'false' to prevent animation
        addTeamMember(initials, name, title, false);
    });
}

/**
 * Loads existing milestones from the profile page into the edit modal.
 */
function loadMilestonesToModal() {
    const list = document.getElementById('milestone-list');
    if (!list) return;
    list.innerHTML = ''; // Clear existing
    // Note: We are creating this list, so it might be empty on first load.
    // This is just a placeholder, as the old profile had milestones.
    // We'll load from the "funding-timeline" of the old profile if it exists.
    document.querySelectorAll('.funding-timeline .timeline-content').forEach(item => {
        const title = item.querySelector('h3')?.textContent || '';
        const date = item.querySelector('time')?.textContent || '';
        addMilestone('fas fa-star', title, date, false);
    });
}

/**
 * Loads skills from the page into the modal's textarea.
 */
function loadSkillsToModal() {
    const skills = Array.from(document.querySelectorAll('#page-skills-list .skill-tag')).map(tag => tag.textContent);
    document.getElementById('edit-skills').value = skills.join(', ');
}

/**
 * Loads thesis data from the page into the modal.
 */
function loadThesisToModal() {
    const list = document.getElementById('page-thesis-list');
    if (!list) return;

    let stage = 'Pre-Seed, Seed';
    let amount = '$250,000 - $1,000,000';
    let horizon = '3-5 Years';

    list.querySelectorAll('li').forEach(li => {
        const text = li.textContent;
        if (text.startsWith('Stage:')) {
            stage = text.replace('Stage:', '').trim();
        } else if (text.startsWith('Check Size:')) {
            amount = text.replace('Check Size:', '').trim();
        } else if (text.startsWith('Horizon:')) {
            horizon = text.replace('Horizon:', '').trim();
        }
    });

    document.getElementById('edit-investment-stage').value = stage;
    document.getElementById('edit-investment-amount').value = amount;
    document.getElementById('edit-investment-horizon').value = horizon;
}


/**
 * Adds a new (empty or pre-filled) team member row to the modal.
 * @param {string} [initials=''] - The member's initials.
 * @param {string} [name=''] - The member's name.
 * @param {string} [title=''] - The member's title.
 * @param {boolean} [animate=true] - Whether to animate the new row.
 */
function addTeamMember(initials = '', name = '', title = '', animate = true) {
    const list = document.getElementById('team-member-list');
    if (!list) return;
    const item = document.createElement('div');
    item.className = 'dynamic-list-item';
    item.innerHTML = `
        <input type="text" placeholder="Initials (e.g., AG)" value="${initials}" class="dynamic-input-small form-input">
        <input type="text" placeholder="Name (e.g., Anand Gupta)" value="${name}" class="dynamic-input-main form-input">
        <input type="text" placeholder="Title (e.g., Managing Partner)" value="${title}" class="dynamic-input-main form-input">
        <button class="dynamic-remove-btn" onclick="removeDynamicItem(this)">&times;</button>
    `;
    if (animate) {
        item.style.animation = 'fadeIn 0.3s ease';
    }
    list.appendChild(item);
}

/**
 * Adds a new (empty or pre-filled) milestone row to the modal.
 * @param {string} [icon='fas fa-star'] - The Font Awesome icon class.
 * @param {string} [title=''] - The milestone title.
 * @param {string} [date=''] - The milestone date.
 * @param {boolean} [animate=true] - Whether to animate the new row.
 */
function addMilestone(icon = 'fas fa-star', title = '', date = '', animate = true) {
    const list = document.getElementById('milestone-list');
    if (!list) return;
    const item = document.createElement('div');
    item.className = 'dynamic-list-item';
    item.innerHTML = `
        <input type="text" placeholder="Icon (e.g., fas fa-rocket)" value="${icon}" class="dynamic-input-small form-input">
        <input type="text" placeholder="Title (e.g., Fund II Closed)" value="${title}" class="dynamic-input-main form-input">
        <input type="text" placeholder="Date (e.g., January 2024)" value="${date}" class="dynamic-input-main form-input">
        <button class="dynamic-remove-btn" onclick="removeDynamicItem(this)">&times;</button>
    `;
    if (animate) {
        item.style.animation = 'fadeIn 0.3s ease';
    }
    list.appendChild(item);
}

/**
 * Removes a dynamic item (team or milestone) from the modal list.
 * @param {HTMLElement} button - The remove button that was clicked.
 */
function removeDynamicItem(button) {
    const item = button.parentElement;
    item.style.animation = 'fadeOut 0.3s ease';
    setTimeout(() => item.remove(), 300);
}

/**
 * Switches tabs *within* the edit profile modal.
 * @param {Event} event - The click event from the tab button.
 * @param {string} tabId - The ID of the modal tab panel to show.
 */
function switchEditModalTab(event, tabId) {
    const tabPanels = document.querySelectorAll('.modal-tab-panel');
    tabPanels.forEach(panel => panel.classList.remove('active-tab'));

    const tabButtons = document.querySelectorAll('.modal-tab-btn');
    tabButtons.forEach(button => button.classList.remove('active'));

    const targetPanel = document.getElementById(tabId);
    if (targetPanel) {
        targetPanel.classList.add('active-tab');
    }
    if (event && event.currentTarget) {
        event.currentTarget.classList.add('active');
    }
}

/**
 * Simulates saving the profile changes from the modal back to the page.
 */
function saveProfileChanges() {
    // --- SAVE OVERVIEW ---
    document.getElementById('page-name').textContent = document.getElementById('edit-name').value;
    document.getElementById('page-title').textContent = document.getElementById('edit-title').value;
    document.getElementById('page-location').textContent = document.getElementById('edit-location').value;
    document.getElementById('page-about').textContent = document.getElementById('edit-about').value;

    // Save profile picture (simulated)
    const fileInput = document.getElementById('edit-profile-pic');
    if (fileInput.files && fileInput.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const pageAvatar = document.getElementById('page-avatar');
            pageAvatar.textContent = ''; // Clear initials
            pageAvatar.style.backgroundImage = `url(${e.target.result})`;
            pageAvatar.style.backgroundSize = 'cover';
        }
        reader.readAsDataURL(fileInput.files[0]);
    }

    // --- SAVE CONTACT ---
    document.querySelector('#page-website .link').textContent = document.getElementById('edit-contact-website').value;
    document.querySelector('#page-email span:last-child').textContent = document.getElementById('edit-contact-email').value;
    document.querySelector('#page-phone span:last-child').textContent = document.getElementById('edit-contact-phone').value;

    // --- SAVE THESIS & SKILLS ---
    const skillsList = document.getElementById('page-skills-list');
    skillsList.innerHTML = ''; // Clear old list
    const skillsText = document.getElementById('edit-skills').value;
    skillsText.split(',').forEach(skill => {
        const skillName = skill.trim();
        if (skillName) {
            skillsList.innerHTML += `<span class="skill-tag">${skillName}</span>`;
        }
    });

    const thesisList = document.getElementById('page-thesis-list');
    thesisList.innerHTML = `
        <li><strong>Stage:</strong> ${document.getElementById('edit-investment-stage').value}</li>
        <li><strong>Check Size:</strong> ${document.getElementById('edit-investment-amount').value}</li>
        <li><strong>Horizon:</strong> ${document.getElementById('edit-investment-horizon').value}</li>
    `;


    // --- SAVE TEAM ---
    const teamList = document.getElementById('page-team-list-sidebar');
    teamList.innerHTML = ''; // Clear old list
    document.querySelectorAll('#team-member-list .dynamic-list-item').forEach(item => {
        const inputs = item.querySelectorAll('input');
        const initials = inputs[0].value;
        const name = inputs[1].value;
        const title = inputs[2].value;
        if (name && title) { // Only add if valid
            teamList.innerHTML += `
                <div class="follow-item">
                    <div class="post-avatar">${initials}</div>
                    <div class="follow-info">
                        <div class="company-name">${name}</div>
                        <div class="follow-title">${title}</div>
                    </div>
                </div>`;
        }
    });

    // --- SAVE MILESTONES (Note: The page doesn't have a milestone list, this is for the modal logic) ---
    // In a real app, you'd save this to a database. We'll just log it.
    console.log("Milestones saved:");
    document.querySelectorAll('#milestone-list .dynamic-list-item').forEach(item => {
        console.log(item.querySelectorAll('input')[1].value);
    });


    // Close modal and show success
    toggleEditProfileModal(false);
    alert('Profile Updated!');
}

/**
 * Previews the selected profile picture in the modal.
 * @param {Event} event - The change event from the file input.
 */
function previewProfilePic(event) {
    const input = event.currentTarget;
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('profile-pic-preview').src = e.target.result;
        }
        reader.readAsDataURL(input.files[0]);
    }
}