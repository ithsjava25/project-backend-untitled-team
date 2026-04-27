async function fetchAFile(){
    const response = await apiReq('/api/files');
    if (!response) return;
    const files = await response.json();
    const body = document.getElementById('fileTableBody')
    body.innerHTML = '';
    files.forEach(file => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
                    <td>${file}</td>
                    <td><button onclick="downloadFile('${file}')" class="button">Download</button></td>
                    <td><button onclick="deleteFile('${file}')" class="button">Delete</button></td>
                `;
        body.appendChild(tr);
    });
}

async function downloadFile(fileName) {
    const res = await apiReq(`/tickets/upload/api/files/download-url?fileName=${encodeURIComponent(fileName)}`);
    if (!res) return;
    const {url} = await res.json();
    window.open(url, '_blank');
}

async function uploadNewFile(){
    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    const input = document.getElementById('fileInput');
    const status = document.getElementById('status');
    if (input.files.length === 0){
        document.querySelector('form').submit();
        return;
    }
    const filesToUpload = Array.from(input.files);
    input.value = null;
    submitBtn.innerText = "Uploading...";
    for (let i = 0; i < filesToUpload.length; i++) {
        const file = filesToUpload[i];

        try {
            status.innerText = `Processing file ${i + 1} of ${filesToUpload.length}: ${file.name}`;
            const res = await apiReq(`/tickets/upload/api/files/upload-url?fileName=${encodeURIComponent(file.name)}&contentType=${encodeURIComponent(file.type)}`);
            if (!res) continue;
            const { url, fileName: uploadedFileName } = await res.json();
            status.innerText = `Uploading ${file.name}...`;
            const putRes = await fetch(url, {
                method: 'PUT',
                body: file,
                headers: { 'Content-Type': file.type }
            });
            if (putRes.ok) {
                await apiReq(`/tickets/upload/api/files/callback?fileName=${encodeURIComponent(uploadedFileName)}`, {
                    method: 'POST'
                });

                status.innerText = `Successfully uploaded ${file.name}`;

                const hiddenContainer = document.getElementById("hidden-file-inputs");
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'fileNames';
                input.value = uploadedFileName;
                hiddenContainer.appendChild(input);
            } else {
                status.innerText = `Failed to upload ${file.name}. Status: ${putRes.status}`;
                submitBtn.disabled = false;
                submitBtn.innerText = "Create Ticket";
            }
        } catch (error) {
            console.error(error);
            status.innerText = `Error uploading ${file.name}: ${error.message}`;
            submitBtn.disabled = false;
            submitBtn.innerText = "Create Ticket";
        }
    }
    submitBtn.disabled = false;
    submitBtn.innerText = "Create Ticket";
    status.innerText = "All uploads completed.";
    document.querySelector('form').submit();
}

async function deleteFile(fileName){
    const status = document.getElementById('status');
    if (window.confirm(fileName + " will be deleted! Are you sure?")) {
        const res = await apiReq(`/tickets/upload/api/files/delete-url?fileName=${encodeURIComponent(fileName)}`, {method: 'DELETE'});
        if (!res) return;
        if (res.ok) {
            await fetchAFile();
        } else {
            status.innerText= 'Error: ' + res.status;
        }
    }
}

async function apiReq(url, options = {}) {
    options.credentials = options.credentials || 'same-origin';
    options.headers = options.headers || {};
    options.redirect = 'manual';
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    if (csrfToken && csrfHeader && options.method && options.method.toUpperCase() !== 'GET') {
        options.headers[csrfHeader] = csrfToken;
    }
    const res = await fetch(url, options);
    if (res.status === 401 || res.status === 403) {
        window.location.reload();
        return null;
    }
    return res;
}