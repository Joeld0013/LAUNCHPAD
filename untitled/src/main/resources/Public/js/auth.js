// js/auth.js
const API_BASE_URL = 'http://localhost:8080/api';

// Function to handle login form submission
async function handleLogin(formData, userType) {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: formData.email,
                password: formData.password,
                userType: userType
            })
        });

        const data = await response.json();

        if (response.ok) {
            // Save the token to localStorage
            localStorage.setItem('authToken', data.token);
            localStorage.setItem('userType', userType);
            localStorage.setItem('userId', data.userId);
            
            // Redirect based on user type
            if (userType === 'STARTUP') {
                window.location.href = 'startup-dashboard.html';
            } else {
                window.location.href = 'investor-dashboard.html';
            }
        } else {
            alert(`Login failed: ${data.message}`);
        }
    } catch (error) {
        console.error('Error during login:', error);
        alert('Login failed. Please try again later.');
    }
}

// Function to handle registration
async function handleRegistration(formData, userType) {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: formData.email,
                password: formData.password,
                userType: userType
            })
        });

        if (response.ok) {
            alert('Registration successful! Please wait for admin approval.');
            // Redirect to login page
            if (userType === 'STARTUP') {
                window.location.href = 'startup_login.html';
            } else {
                window.location.href = 'investor_login.html';
            }
        } else {
            const error = await response.text();
            alert(`Registration failed: ${error}`);
        }
    } catch (error) {
        console.error('Error during registration:', error);
        alert('Registration failed. Please try again later.');
    }
}

// Add event listeners to your forms
document.addEventListener('DOMContentLoaded', function() {
    // Startup login form
    const startupLoginForm = document.getElementById('startup-login');
    if (startupLoginForm) {
        startupLoginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = {
                email: document.getElementById('startup-email').value,
                password: document.getElementById('startup-password').value
            };
            handleLogin(formData, 'STARTUP');
        });
    }

    // Investor login form
    const investorLoginForm = document.getElementById('investor-login');
    if (investorLoginForm) {
        investorLoginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = {
                email: document.getElementById('investor-email').value,
                password: document.getElementById('investor-password').value
            };
            handleLogin(formData, 'INVESTOR');
        });
    }

    // Startup registration form
    const startupRegisterForm = document.getElementById('startup-register');
    if (startupRegisterForm) {
        startupRegisterForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = {
                email: document.getElementById('startup-register-email').value,
                password: document.getElementById('startup-register-password').value,
                confirmPassword: document.getElementById('startup-register-confirm-password').value
            };
            
            if (formData.password !== formData.confirmPassword) {
                alert('Passwords do not match!');
                return;
            }
            
            handleRegistration(formData, 'STARTUP');
        });
    }

    // Investor registration form
    const investorRegisterForm = document.getElementById('investor-register');
    if (investorRegisterForm) {
        investorRegisterForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = {
                email: document.getElementById('investor-register-email').value,
                password: document.getElementById('investor-register-password').value,
                confirmPassword: document.getElementById('investor-register-confirm-password').value
            };
            
            if (formData.password !== formData.confirmPassword) {
                alert('Passwords do not match!');
                return;
            }
            
            handleRegistration(formData, 'INVESTOR');
        });
    }
});