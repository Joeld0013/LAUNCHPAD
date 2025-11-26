// investor_bidding_system.js - Bidding functionality for investors
//const API_BASE_URL = '/api';

/**
 * Toggles the "Make a Bid" modal
 * @param {boolean} open - True to open, false to close
 * @param {string} startupId - The startup ID to bid on
 * @param {string} startupName - The startup name
 */
function toggleBidModal(open, startupId = null, startupName = null) {
    const modal = document.getElementById('make-bid-modal');
    if (!modal) return;

    if (open) {
        // Store startup info for submission
        modal.dataset.startupId = startupId;
        modal.dataset.startupName = startupName;

        // Update modal header
        const bidFor = modal.querySelector('.bid-for-startup');
        if (bidFor) {
            bidFor.textContent = startupName;
        }

        modal.style.display = 'flex';
    } else {
        modal.style.display = 'none';
        // Clear form
        document.getElementById('bid-amount').value = '';
        document.getElementById('bid-equity').value = '';
        document.getElementById('bid-type').value = 'seed';
        document.getElementById('bid-message').value = '';
    }
}

/**
 * Submits a bid from the modal
 */
async function submitBid() {
    const modal = document.getElementById('make-bid-modal');
    const startupId = modal.dataset.startupId;
    const startupName = modal.dataset.startupName;

    const amount = document.getElementById('bid-amount').value.trim();
    const equity = document.getElementById('bid-equity').value.trim();
    const bidType = document.getElementById('bid-type').value;
    const message = document.getElementById('bid-message').value.trim();

    // Validation
    if (!amount || !equity) {
        showToast('Please fill in both Amount and Equity fields', 'error');
        return;
    }

    if (isNaN(amount) || isNaN(equity)) {
        showToast('Amount and Equity must be numbers', 'error');
        return;
    }

    if (parseFloat(amount) <= 0 || parseFloat(equity) <= 0) {
        showToast('Amount and Equity must be greater than 0', 'error');
        return;
    }

    const token = localStorage.getItem('token');
    const investorId = localStorage.getItem('userId');

    const bidData = {
        startupId: startupId,
        investorId: investorId,
        amount: parseFloat(amount),
        equity: parseFloat(equity),
        bidType: bidType,
        message: message || null,
        status: 'PENDING'
    };

    try {
        const response = await fetch(`${API_BASE_URL}/bids/create`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(bidData)
        });

        if (response.ok) {
            const result = await response.json();
            showToast(`Your bid for ${startupName} has been successfully submitted!`, 'success');
            toggleBidModal(false);

            // Optional: redirect to bids page
            setTimeout(() => {
                window.location.href = 'investors_bids.html';
            }, 1500);
        } else {
            let errorMessage = 'Failed to submit bid';

            try {
                // Try to parse JSON error if backend sends one
                const contentType = response.headers.get('Content-Type') || '';
                if (contentType.includes('application/json')) {
                    const error = await response.json();
                    if (error && (error.message || error.error)) {
                        errorMessage = error.message || error.error;
                    }
                } else {
                    // Fallback: read as text
                    const text = await response.text();
                    if (text) {
                        errorMessage = text;
                    }
                }
            } catch (e) {
                console.warn('Error parsing error response:', e);
            }

            showToast(errorMessage, 'error');
        }
    } catch (error) {
        console.error('Error submitting bid:', error);
        showToast('Network error. Please try again.', 'error');
    }
}

/**
 * Enhanced toast function with error support
 */
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    let icon = 'fa-info-circle';
    if (type === 'success') icon = 'fa-check-circle';
    if (type === 'error') icon = 'fa-exclamation-circle';

    toast.innerHTML = `<i class="fas ${icon}"></i> ${message}`;
    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add('show'), 100);
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}