document.getElementById('investor-login').addEventListener('submit', function(e) {
            e.preventDefault();
            const email = document.getElementById('investor-email').value;
            const password = document.getElementById('investor-password').value;
            
            // In a real application, you would send this data to your backend
            console.log('Investor login attempt:', { email, password });
            alert('Investor login submitted. In a real application, this would be processed.');
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