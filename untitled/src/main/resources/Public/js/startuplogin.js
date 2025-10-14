// API Base URL - Since frontend is served from backend, use relative path
const API_BASE_URL = '/api';

document.getElementById('startup-login').addEventListener('submit', async function(e) {
    e.preventDefault();

    const email = document.getElementById('startup-email').value;
    const password = document.getElementById('startup-password').value;
    const rememberMe = document.getElementById('startup-remember').checked;

    const loginBtn = document.querySelector('.login-btn');
    const originalText = loginBtn.textContent;

    try {
        // Disable button and show loading state
        loginBtn.disabled = true;
        loginBtn.textContent = 'Logging in...';

        console.log('Attempting login to:', `${API_BASE_URL}/startup/auth/login`);

        const response = await fetch(`${API_BASE_URL}/startup/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                password: password,
                rememberMe: rememberMe
            })
        });

        console.log('Response status:', response.status);
        console.log('Response headers:', response.headers);

        // Check if response has content before parsing JSON
        const contentType = response.headers.get("content-type");
        let data;

        if (contentType && contentType.includes("application/json")) {
            data = await response.json();
        } else {
            const text = await response.text();
            console.error('Non-JSON response:', text);
            throw new Error(`Server returned ${response.status}: ${text || 'No content'}`);
        }

        if (response.ok) {
            // Store token and user info
            localStorage.setItem('token', data.token);
            localStorage.setItem('userType', 'STARTUP');
            localStorage.setItem('userId', data.id);
            localStorage.setItem('userEmail', data.email);
            localStorage.setItem('userName', data.name);

            // Show success message
            alert('Login successful! Redirecting to dashboard...');

            // Redirect to startup dashboard
            window.location.href = 'startup_index.html';
        } else {
            // Show error message
            alert(data.message || 'Login failed. Please try again.');
            loginBtn.disabled = false;
            loginBtn.textContent = originalText;
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('An error occurred during login. Please check your connection and try again.');
        loginBtn.disabled = false;
        loginBtn.textContent = originalText;
    }
});

function togglePassword(inputId) {
    const passwordInput = document.getElementById(inputId);
    const toggleButton = passwordInput.nextElementSibling;
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleButton.textContent = '🔒';
    } else {
        passwordInput.type = 'password';
        toggleButton.textContent = '👁️';
    }
}

// Check if user is already logged in
window.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const userType = localStorage.getItem('userType');
    
    if (token && userType === 'STARTUP') {
        // Verify token is still valid
        fetch(`${API_BASE_URL}/startup/auth/verify`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => {
            if (response.ok) {
                window.location.href = 'startup_index.html';
            } else {
                // Token invalid, clear storage
                localStorage.clear();
            }
        })
        .catch(() => {
            // Error verifying, stay on login page
            localStorage.clear();
        });
    }
});