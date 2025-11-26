// startup_posts.js - ABSOLUTELY FINAL VERSION
const API_BASE_URL = '/api';
let currentFilter = 'all';
let uploadedMediaFiles = [];

document.addEventListener('DOMContentLoaded', function() {
    console.log('═══ STARTUP POSTS INITIALIZED ═══');
    checkAuth();
    loadPosts();
    setupEventListeners();
    updateUserDisplayName();
});

function checkAuth() {
    const token = localStorage.getItem('token');
    const userType = localStorage.getItem('userType');

    if (!token || userType !== 'STARTUP') {
        window.location.href = 'startup_login.html';
        return;
    }
}

function updateUserDisplayName() {
    const userName = localStorage.getItem('userName') || 'Startup';
    const modalUserName = document.getElementById('modal-user-name');
    if (modalUserName) {
        modalUserName.textContent = userName;
    }

    const avatars = document.querySelectorAll('.my-avatar');
    avatars.forEach(avatar => {
        avatar.textContent = userName.substring(0, 2).toUpperCase();
    });
}

function setupEventListeners() {
    const fileInput = document.getElementById('post-attachment-modal');
    if (fileInput) {
        fileInput.addEventListener('change', handleFileUpload);
    }
}

async function handleFileUpload(event) {
    const files = event.target.files;
    if (!files.length) return;

    const formData = new FormData();
    for (let file of files) {
        formData.append('files', file);
    }

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}/files/upload`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` },
            body: formData
        });

        if (response.ok) {
            const data = await response.json();
            uploadedMediaFiles = data.urls || [];
            showToast('Files uploaded!', 'success');
        } else {
            alert('Failed to upload files');
        }
    } catch (error) {
        console.error('Error uploading:', error);
        alert('Error uploading files');
    }
}

async function loadPosts(filter = 'all') {
    const token = localStorage.getItem('token');
    currentFilter = filter;

    console.log(`═══ LOADING POSTS: ${filter} ═══`);

    const feedContainer = document.getElementById('post-feed');
    feedContainer.innerHTML = '<div class="loading-posts"><div class="loader"></div><p>Loading...</p></div>';

    try {
        const response = await fetch(`${API_BASE_URL}/posts?filter=${filter}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const posts = await response.json();
            console.log(`✓ Loaded ${posts.length} posts`);
            posts.forEach(post => {
                console.log(`Post: ${post.id} | Type: ${post.userType} | Name: ${post.userName} | Email: ${post.userEmail} | Owner: ${post.isOwner}`);
            });
            displayPosts(posts);
        } else {
            feedContainer.innerHTML = '<div class="error-state"><p>Failed to load posts</p></div>';
        }
    } catch (error) {
        console.error('Error:', error);
        feedContainer.innerHTML = '<div class="error-state"><p>Error loading posts</p></div>';
    }
}

function displayPosts(posts) {
    const feedContainer = document.getElementById('post-feed');

    if (!posts || posts.length === 0) {
        feedContainer.innerHTML = '<div class="empty-state"><p>No posts yet</p></div>';
        return;
    }

    feedContainer.innerHTML = posts.map(post => createPostCard(post)).join('');
}

function createPostCard(post) {
    const userInitials = post.userName ? post.userName.substring(0, 2).toUpperCase() : 'U';
    const timeAgo = getTimeAgo(post.createdAt);
    const isLiked = post.isLikedByCurrentUser;

    // Media Content Logic
    let mediaContent = '';
    if (post.mediaUrls && post.mediaUrls.length > 0) {
        mediaContent = '<div class="post-media-grid">';
        post.mediaUrls.forEach(url => {
            if (url.match(/\.(jpg|jpeg|png|gif|webp)$/i)) {
                mediaContent += `<img src="${url}" class="post-image">`;
            } else if (url.match(/\.(mp4|webm|ogg)$/i)) {
                mediaContent += `<video controls class="post-video"><source src="${url}"></video>`;
            }
        });
        mediaContent += '</div>';
    }

    // Link Logic
    let linkContent = '';
    if (post.linkUrl) {
        linkContent = `<a href="${post.linkUrl}" target="_blank" class="post-link-card">
            <i class="fas fa-link"></i><span>${post.linkUrl}</span>
        </a>`;
    }

    // Tags Logic
    let tagsContent = '';
    if (post.tags && post.tags.length > 0) {
        tagsContent = '<div class="post-tags">';
        post.tags.forEach(tag => {
            tagsContent += `<span class="post-tag">#${tag}</span>`;
        });
        tagsContent += '</div>';
    }

    // === NEW: CHAT BUTTON LOGIC ===
    // Show Chat button ONLY if I am NOT the owner
    const chatButton = !post.isOwner ?
        `<button class="post-header-chat-btn" onclick="openChat('${post.userId}', '${escapeHtml(post.userName)}')" title="Message User">
            <i class="fas fa-comment-dots"></i> Chat
        </button>` : '';

    // Delete Button (Only if owner)
    const deleteButton = post.isOwner ?
        `<button class="post-delete-btn" onclick="deletePost('${post.id}')" title="Delete post">
            <i class="fas fa-trash-alt"></i> Delete
        </button>` : '';

    // Avatar Logic
    const avatarContent = post.userProfilePic ?
        `<img src="${post.userProfilePic}" alt="${post.userName}" class="post-avatar-img">` :
        `<div class="post-avatar-initials">${userInitials}</div>`;

    return `
        <article class="post-card" data-post-id="${post.id}">
            <div class="post-header">
                <div class="post-avatar" onclick="ProfilePopup.open('${post.userId}', '${post.userType}')">${avatarContent}</div>
                <div class="post-header-info">
                    <div class="post-author" onclick="ProfilePopup.open('${post.userId}', '${post.userType}')">${escapeHtml(post.userName)}</div>
                    <div class="post-email" onclick="ProfilePopup.open('${post.userId}', '${post.userType}')">${escapeHtml(post.userEmail)}</div>
                    <div class="post-meta">
                        <span class="post-time"><i class="far fa-clock"></i> ${timeAgo}</span>
                    </div>
                </div>
                <div class="post-header-actions">
                    ${chatButton}
                    ${deleteButton}
                </div>
            </div>

            <div class="post-content">
                <p class="post-text">${escapeHtml(post.content)}</p>
                ${mediaContent}
                ${linkContent}
                ${tagsContent}
            </div>

            <div class="post-stats">
                <span class="post-stat"><i class="fas fa-heart"></i> ${post.likesCount || 0} likes</span>
                <span class="post-stat"><i class="fas fa-comment"></i> ${post.commentsCount || 0} comments</span>
            </div>

            <div class="post-actions">
                <button class="post-action-btn ${isLiked ? 'liked' : ''}" onclick="toggleLike('${post.id}', ${isLiked})">
                    <i class="fas fa-heart"></i><span>${isLiked ? 'Unlike' : 'Like'}</span>
                </button>
                <button class="post-action-btn" onclick="toggleComments('${post.id}')">
                    <i class="fas fa-comment"></i><span>Comment</span>
                </button>
                <button class="post-action-btn" onclick="sharePost('${post.id}')">
                    <i class="fas fa-share"></i><span>Share</span>
                </button>
            </div>

            <div class="comments-section" id="comments-${post.id}" style="display: none;">
                <div class="comment-input-area">
                    <input type="text" class="comment-input" id="comment-input-${post.id}" placeholder="Write a comment...">
                    <button class="comment-submit-btn" onclick="postComment('${post.id}')">
                        <i class="fas fa-paper-plane"></i>
                    </button>
                </div>
                <div class="comments-list" id="comments-list-${post.id}">
                    <div class="loading-comments">Loading...</div>
                </div>
            </div>
        </article>
    `;
}

async function createPost() {
    const content = document.getElementById('create-post-textarea').value.trim();

    if (!content) {
        alert('Please enter content');
        return;
    }

    const token = localStorage.getItem('token');
    const postData = {
        content: content,
        mediaUrls: uploadedMediaFiles,
        linkUrl: extractUrl(content),
        tags: extractHashtags(content)
    };

    try {
        const response = await fetch(`${API_BASE_URL}/posts`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(postData)
        });

        if (response.ok) {
            document.getElementById('create-post-textarea').value = '';
            uploadedMediaFiles = [];
            const fileInput = document.getElementById('post-attachment-modal');
            if (fileInput) fileInput.value = '';

            toggleCreatePostModal();
            await loadPosts(currentFilter);
            showToast('Post created!', 'success');
        } else {
            alert('Failed to create post');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error creating post');
    }
}

async function deletePost(postId) {
    if (!confirm('Delete this post?')) return;

    console.log(`═══ DELETING POST: ${postId} ═══`);

    const token = localStorage.getItem('token');

    try {
        const response = await fetch(`${API_BASE_URL}/posts/${postId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            console.log('✓ Post deleted');
            await loadPosts(currentFilter);
            showToast('Post deleted', 'success');
        } else {
            const error = await response.json();
            alert(error.message || 'Failed to delete');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error deleting post');
    }
}

async function toggleLike(postId, isCurrentlyLiked) {
    const token = localStorage.getItem('token');
    const endpoint = isCurrentlyLiked ? 'unlike' : 'like';

    console.log(`${isCurrentlyLiked ? 'UNLIKING' : 'LIKING'} post: ${postId}`);

    try {
        const response = await fetch(`${API_BASE_URL}/posts/${postId}/${endpoint}`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            console.log(`✓ Post ${isCurrentlyLiked ? 'unliked' : 'liked'}`);
            await loadPosts(currentFilter);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function toggleComments(postId) {
    const commentsSection = document.getElementById(`comments-${postId}`);

    if (commentsSection.style.display === 'none') {
        commentsSection.style.display = 'block';
        await loadComments(postId);
    } else {
        commentsSection.style.display = 'none';
    }
}

async function loadComments(postId) {
    const token = localStorage.getItem('token');
    const commentsList = document.getElementById(`comments-list-${postId}`);

    try {
        const response = await fetch(`${API_BASE_URL}/posts/${postId}/comments`, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const comments = await response.json();
            console.log(`✓ Loaded ${comments.length} comments`);
            displayComments(comments, postId);
        }
    } catch (error) {
        console.error('Error:', error);
        commentsList.innerHTML = '<p>Failed to load</p>';
    }
}

function displayComments(comments, postId) {
    const commentsList = document.getElementById(`comments-list-${postId}`);

    if (comments.length === 0) {
        commentsList.innerHTML = '<p class="no-comments">No comments yet</p>';
        return;
    }

    commentsList.innerHTML = comments.map(comment => {
        console.log(`Comment by: ${comment.userName} (${comment.userEmail})`);

        const initials = comment.userName ? comment.userName.substring(0, 2).toUpperCase() : 'U';
        const timeAgo = getTimeAgo(comment.createdAt);

        const avatarContent = comment.userProfilePic ?
            `<img src="${comment.userProfilePic}" alt="${comment.userName}">` :
            `<div class="comment-avatar-initials">${initials}</div>`;

        return `
            <div class="comment-item">
                <div class="comment-avatar">${avatarContent}</div>
                <div class="comment-content">
                    <div class="comment-header">
                        <span class="comment-author">${escapeHtml(comment.userName)}</span>
                        <span class="comment-email">${escapeHtml(comment.userEmail)}</span>
                    </div>
                    <p class="comment-text">${escapeHtml(comment.content)}</p>
                    <span class="comment-time">${timeAgo}</span>
                </div>
            </div>
        `;
    }).join('');
}

async function postComment(postId) {
    const input = document.getElementById(`comment-input-${postId}`);
    const content = input.value.trim();

    if (!content) return;

    const token = localStorage.getItem('token');

    try {
        const response = await fetch(`${API_BASE_URL}/posts/${postId}/comment`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ content })
        });

        if (response.ok) {
            input.value = '';
            await loadComments(postId);
            await loadPosts(currentFilter);
            showToast('Comment added!', 'success');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function filterPosts(event, filter) {
    console.log(`═══ FILTER CHANGED: ${filter} ═══`);
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    loadPosts(filter);
}

function getTimeAgo(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const seconds = Math.floor((now - date) / 1000);

    const intervals = {
        year: 31536000,
        month: 2592000,
        week: 604800,
        day: 86400,
        hour: 3600,
        minute: 60
    };

    for (let [unit, secondsInUnit] of Object.entries(intervals)) {
        const interval = Math.floor(seconds / secondsInUnit);
        if (interval >= 1) {
            return `${interval} ${unit}${interval > 1 ? 's' : ''} ago`;
        }
    }
    return 'Just now';
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function extractUrl(text) {
    const urlRegex = /(https?:\/\/[^\s]+)/g;
    const matches = text.match(urlRegex);
    return matches ? matches[0] : null;
}

function extractHashtags(text) {
    const hashtagRegex = /#(\w+)/g;
    const matches = text.match(hashtagRegex);
    return matches ? matches.map(tag => tag.substring(1)) : [];
}

function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<i class="fas fa-check-circle"></i> ${message}`;
    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add('show'), 100);
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function sharePost(postId) {
    const shareLink = `${window.location.origin}/post/${postId}`;
    navigator.clipboard.writeText(shareLink).then(() => {
        showToast('Link copied!', 'success');
    });
}

// Placeholder for future backend integration
function openChat(userId, userName) {
    console.log(`Initiating chat with User ID: ${userId}, Name: ${userName}`);
    // For now, just a visual confirmation
    showToast(`Opening chat with ${userName}...`, 'info');
}