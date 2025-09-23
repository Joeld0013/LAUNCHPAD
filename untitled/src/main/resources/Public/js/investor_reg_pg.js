// -------------------- Section Navigation --------------------
function showSection(sectionNumber) {
    // Validate before moving forward
    if (sectionNumber === 2) {
        if (!validateSection1()) return;
    } else if (sectionNumber === 3) {
        if (!validateSection2()) return;
    }

    // Hide all sections
    document.querySelectorAll('.form-section').forEach(section => {
        section.classList.remove('active');
    });

    // Show the selected section
    document.getElementById(`section-${sectionNumber}`).classList.add('active');

    // Update progress bar
    document.querySelectorAll('.step').forEach((step, index) => {
        if (index < sectionNumber) {
            step.classList.add('active');
        } else {
            step.classList.remove('active');
        }
    });

    // Update progress line (using CSS variable)
    document.documentElement.style.setProperty('--progress-width', `${(sectionNumber - 1) * 33}%`);
}

// -------------------- File Upload --------------------
const dropZone = document.getElementById('doc-dropzone');
const fileInput = document.getElementById('file-input');
const fileList = document.getElementById('file-list');
let uploadedFiles = [];

dropZone.addEventListener('click', () => fileInput.click());
fileInput.addEventListener('change', handleFileSelect);

dropZone.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropZone.style.borderColor = '#edb96f';
    dropZone.style.backgroundColor = '#f8f7f2';
});

dropZone.addEventListener('dragleave', () => {
    dropZone.style.borderColor = '#ddd';
    dropZone.style.backgroundColor = 'transparent';
});

dropZone.addEventListener('drop', (e) => {
    e.preventDefault();
    dropZone.style.borderColor = '#ddd';
    dropZone.style.backgroundColor = 'transparent';
    handleFiles(e.dataTransfer.files);
});

function handleFileSelect(e) {
    handleFiles(e.target.files);
}

function handleFiles(files) {
    for (const file of files) {
        uploadedFiles.push(file);
        addFileToList(file);
    }
}

function addFileToList(file) {
    const fileItem = document.createElement('div');
    fileItem.className = 'file-item';

    fileItem.innerHTML = `
        <div class="file-name">
            <i class="fas fa-file"></i>
            <span>${file.name}</span>
        </div>
        <div class="file-remove" onclick="removeFile(this, '${file.name}')">
            <i class="fas fa-times"></i>
        </div>
    `;

    fileList.appendChild(fileItem);
}

function removeFile(element, fileName) {
    const fileItem = element.closest('.file-item');
    fileList.removeChild(fileItem);
    uploadedFiles = uploadedFiles.filter(file => file.name !== fileName);
}

// -------------------- Form Validation --------------------
function validateSection1() {
    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    if (!name || !email || !phone || !password || !confirmPassword) {
        alert('Please fill all required fields in Personal Info');
        return false;
    }

    if (password !== confirmPassword) {
        alert('Passwords do not match!');
        return false;
    }

    if (password.length < 6) {
        alert('Password must be at least 6 characters long');
        return false;
    }

    return true;
}

function validateSection2() {
    const country = document.getElementById('country').value;
    const investorType = document.querySelector('input[name="investor_type"]:checked');

    if (!country || !investorType) {
        alert('Please select Country and Investor Type');
        return false;
    }

    return true;
}

function validateSection3() {
    const docType = document.getElementById('doc-type').value;

    if (!docType) {
        alert('Please select a document type');
        return false;
    }

    if (uploadedFiles.length === 0) {
        alert('Please upload at least one document');
        return false;
    }

    return true;
}

// -------------------- Form Submission --------------------
document.getElementById('registration-form').addEventListener('submit', async function(e) {
    e.preventDefault();

    // Final validation
    if (!validateSection1() || !validateSection2() || !validateSection3()) {
        return;
    }

    // Collect form data
    const formData = new FormData();
    formData.append('name', document.getElementById('name').value.trim());
    formData.append('email', document.getElementById('email').value.trim());
    formData.append('phone', document.getElementById('phone').value.trim());
    formData.append('password', document.getElementById('password').value);

    formData.append('country', document.getElementById('country').value);
    formData.append('organization', document.getElementById('organization').value.trim());
    formData.append('address', document.getElementById('address').value.trim());
    formData.append('investorType', document.querySelector('input[name="investor_type"]:checked').value);
    formData.append('preferences', document.getElementById('preferences').value.trim());

    formData.append('docType', document.getElementById('doc-type').value);

    uploadedFiles.forEach(file => {
        formData.append('documents', file);
    });

    // Submit button state
    const submitBtn = document.querySelector('.btn-submit');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Submitting...';
    submitBtn.disabled = true;

    try {
        const response = await fetch('http://localhost:8080/api/investor/register', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            const result = await response.text();
            alert('Registration submitted! Your documents will be verified. You will receive login credentials once approved.');
            console.log('Success:', result);
            // window.location.href = 'investor_login.html';
        } else {
            const error = await response.text();
            alert(`Registration failed: ${error}`);
            console.error('Error:', error);
        }
    } catch (error) {
        console.error('Network error:', error);
        alert('Registration failed. Please check your connection and try again.');
    } finally {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
});
