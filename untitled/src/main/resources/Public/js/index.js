
        // Add subtle animation to the buttons when page loads
        document.addEventListener('DOMContentLoaded', function() {
            const buttons = document.querySelectorAll('.user-btn');
            buttons.forEach((button, index) => {
                button.style.opacity = '0';
                button.style.transform = 'translateY(20px)';
                
                setTimeout(() => {
                    button.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
                    button.style.opacity = '1';
                    button.style.transform = 'translateY(0)';
                }, 300 + (index * 200));
            });
            
            // Add hover effect to feature cards
            const features = document.querySelectorAll('.feature-card');
            features.forEach(card => {
                card.addEventListener('mouseenter', () => {
                    card.querySelector('.feature-icon').style.background = 'var(--gold)';
                    card.querySelector('.feature-icon').style.color = 'white';
                });
                
                card.addEventListener('mouseleave', () => {
                    card.querySelector('.feature-icon').style.background = 'var(--cream)';
                    card.querySelector('.feature-icon').style.color = 'var(--gold)';
                });
            });
        });

        //MAGNETIC BUTTON EFFECT
/*document.querySelectorAll('.user-btn').forEach(btn => {
  btn.addEventListener('mousemove', function(e) {
    const x = e.offsetX;
    const y = e.offsetY;
    
    const btnWidth = btn.clientWidth;
    const btnHeight = btn.clientHeight;
    
    const transX = (x - btnWidth / 2) / 4;
    const transY = (y - btnHeight / 2) / 4;
    
    btn.style.transform = `translate(${transX}px, ${transY}px)`;
  });
  
  btn.addEventListener('mouseleave', function() {
    btn.style.transform = '';
  });
});*/