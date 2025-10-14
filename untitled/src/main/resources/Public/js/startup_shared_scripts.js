// This function is for the notification popup
function toggleNotifications() {
    const popup = document.getElementById('notification-popup');
    if (popup.style.display === 'block') {
        popup.style.display = 'none';
    } else {
        popup.style.display = 'block';
    }
}

// This function is for the post filtering on the posts page
function filterPosts(type) {
    document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('active'));
    event.currentTarget.classList.add('active');

    const allPosts = document.querySelectorAll('.post-card');
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

    allPosts.forEach(post => {
        // This logic uses style.display to show/hide posts based on filter
        if (type === 'all') {
            post.style.display = 'block';
        } else if (type === 'investor' && post.classList.contains('investor-post')) {
            post.style.display = 'block';
        } else if (type === 'startup' && post.classList.contains('startup-post')) {
            post.style.display = 'block';
        } else {
            post.style.display = 'none';
        }
    });
}

// This function is for the edit profile modal on the profile page
function toggleEditProfileModal() {
    const modal = document.getElementById('edit-profile-modal');
    if (modal.style.display === 'flex') {
        modal.style.display = 'none';
    } else {
        modal.style.display = 'flex';
    }
}

// Add event listeners for interactive elements when the page loads
document.addEventListener('DOMContentLoaded', function() {
    // Highlight the current page's link in the navigation
    const currentPage = window.location.pathname.split('/').pop();
    const navLinks = document.querySelectorAll('.nav-links a');
    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPage) {
            link.classList.add('active');
        }
    });

    // Like button functionality
    document.querySelectorAll('.like-btn').forEach(button => {
        button.addEventListener('click', function() {
            this.classList.toggle('liked');
            const countElement = this.querySelector('.like-count');
            let likes = parseInt(this.getAttribute('data-likes'));
            if (this.classList.contains('liked')) {
                likes++;
            } else {
                likes--;
            }
            this.setAttribute('data-likes', likes);
            countElement.textContent = likes;
        });
    });

    // Comment button functionality
    document.querySelectorAll('.comment-btn').forEach(button => {
        button.addEventListener('click', function() {
            alert('A comment box would appear here.');
        });
    });

    // Share button functionality
    document.querySelectorAll('.share-btn').forEach(button => {
        button.addEventListener('click', function() {
            alert('This post would be shared.');
        });
    });
});