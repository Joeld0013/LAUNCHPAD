// -------------------- Toggle Password Visibility --------------------
function togglePassword(inputId) {
    const passwordInput = document.getElementById(inputId);
    const toggleIcon = passwordInput.nextElementSibling;

    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleIcon.textContent = '🔒';
    } else {
        passwordInput.type = 'password';
        toggleIcon.textContent = '👁️';
    }
}

// -------------------- Admin Login --------------------
document.getElementById('admin-login').addEventListener('submit', async function(e) {
    e.preventDefault();

    const email = document.getElementById('admin-email').value.trim();
    const password = document.getElementById('admin-password').value;

    if (!email || !password) {
        alert('Please enter both email and password.');
        return;
    }

    // Button animation
    const btn = this.querySelector('.login-btn');
    const originalText = btn.textContent;
    btn.textContent = 'AUTHENTICATING...';
    btn.style.opacity = '0.8';
    btn.disabled = true;

    try {
        // Call backend API
        const response = await fetch('http://localhost:8080/api/admin/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            const result = await response.json();
            console.log('Login success:', result);

            // Example: store token in localStorage
            if (result.token) {
                localStorage.setItem('adminToken', result.token);
            }

            btn.textContent = 'ACCESS GRANTED ✅';

            setTimeout(() => {
                window.location.href = 'admindashboard.html'; // Redirect after login
            }, 1200);
        } else {
            const error = await response.text();
            alert(`Login failed: ${error}`);
            console.error('Error:', error);
            btn.textContent = 'ACCESS DENIED ❌';
        }
    } catch (err) {
        console.error('Network error:', err);
        alert('Login failed. Please check your connection.');
        btn.textContent = 'NETWORK ERROR ❌';
    } finally {
        setTimeout(() => {
            btn.textContent = originalText;
            btn.style.opacity = '1';
            btn.disabled = false;
        }, 2000);
    }
});

// -------------------- Particles Animation --------------------
document.addEventListener('DOMContentLoaded', function() {
    const container = document.querySelector('.particles-container');
    if (!container) return;

    for (let i = 0; i < 30; i++) {
        const particle = document.createElement('div');
        particle.className = 'particle';
        particle.style.width = Math.random() * 8 + 2 + 'px';
        particle.style.height = particle.style.width;
        particle.style.left = Math.random() * 100 + '%';
        particle.style.animationDelay = Math.random() * 8 + 's';
        particle.style.animationDuration = (Math.random() * 10 + 10) + 's';
        container.appendChild(particle);
    }
});
