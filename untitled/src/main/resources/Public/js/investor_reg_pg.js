 // Show/hide form sections
    function showSection(sectionNumber) {
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

        // Update progress line
        const progressWidth = (sectionNumber - 1) * 33;
        document.querySelector('.progress-bar').style.setProperty('--progress-width', `${progressWidth}%`);
    }

    // File upload functionality
    const dropZone = document.getElementById('doc-dropzone');
    const fileInput = document.getElementById('file-input');
    const fileList = document.getElementById('file-list');

    dropZone.addEventListener('click', () => {
        fileInput.click();
    });

    fileInput.addEventListener('change', handleFileSelect);

    dropZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropZone.style.borderColor = '#2b3446';
        dropZone.style.backgroundColor = '#f5f5f7';
    });

    dropZone.addEventListener('dragleave', () => {
        dropZone.style.borderColor = '#ddd';
        dropZone.style.backgroundColor = '#fcfcfc';
    });

    dropZone.addEventListener('drop', (e) => {
        e.preventDefault();
        dropZone.style.borderColor = '#ddd';
        dropZone.style.backgroundColor = '#fcfcfc';

        const files = e.dataTransfer.files;
        handleFiles(files);
    });

    function handleFileSelect(e) {
        const files = e.target.files;
        handleFiles(files);
    }

    function handleFiles(files) {
        for (const file of files) {
            addFileToList(file);
        }
    }

    function addFileToList(file) {
        const fileItem = document.createElement('div');
        fileItem.className = 'file-item';

        fileItem.innerHTML = `
            <div class="file-name">
                <i class="fas fa-file-pdf"></i>
                <span>${file.name}</span>
            </div>
            <div class="file-remove" onclick="removeFile(this)">
                <i class="fas fa-times"></i>
            </div>
        `;

        fileList.appendChild(fileItem);
    }

    function removeFile(element) {
        const fileItem = element.closest('.file-item');
        fileList.removeChild(fileItem);
    }

    // Form submission
    document.getElementById('registration-form').addEventListener('submit', function(e) {
        e.preventDefault();
        alert('Registration submitted! Your accreditation will be verified by our team. You will receive an email with login credentials once approved.');
    });