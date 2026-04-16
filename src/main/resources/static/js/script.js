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
                    <td><button onclick="deleteFile('${file}')" class="button">Delete</input></td>
                `;
        body.appendChild(tr);
    });
}

async function downloadFile(fileName) {
    const res = await apiReq(`/api/files/download-url?fileName=${encodeURIComponent(fileName)}`);
    if (!res) return;

    const {url} = await res.json();
    window.open(url, '_blank');

}

async function uploadNewFile(){
    const input = document.getElementById('fileInput');
    const status = document.getElementById('status');

    if (input.files.length === 0) return;

    const filesToUpload = Array.from(input.files);
    input.value = null;

    for (let i = 0; i < filesToUpload.length; i++) {
        const file = filesToUpload[i];

        try {
            status.innerText = `Processing file ${i + 1} of ${filesToUpload.length}: ${file.name}`;
            const res = await apiReq(`/api/files/upload-url?fileName=${encodeURIComponent(file.name)}&contentType=${encodeURIComponent(file.type)}`);
            if (!res) continue; // Move to next file if request fails

            const { url } = await res.json();
            status.innerText = `Uploading ${file.name}...`;
            const putRes = await fetch(url, {
                method: 'PUT',
                body: file,
                headers: { 'Content-Type': file.type }
            });
            if (putRes.ok) {
                await apiReq(`/api/files/callback?fileName=${encodeURIComponent(file.name)}`, {
                    method: 'POST'
                });

                status.innerText = `Successfully uploaded ${file.name}`;
                fetchAFile(); // Refresh list/view
            } else {
                status.innerText = `Failed to upload ${file.name}. Status: ${putRes.status}`;
            }
        } catch (error) {
            console.error(error);
            status.innerText = `Error uploading ${file.name}: ${error.message}`;
        }
    }
    status.innerText = "All uploads completed.";
}
async function deleteFile(fileName){
    const confirmation = window.confirm(fileName + " will be deleted! Are you sure?")
    if (confirmation) {
        const res = await apiReq(`/upload/api/files/delete-url?fileName=${encodeURIComponent(fileName)}`);
        window.location.reload();
    } else {
        return;
    }
}
async function apiReq(url, options = {}){
    // const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    // const csrfHeader = document.querySelector('meta[name="_crsf_header"]').getAttribute('content');
    options.credentials = options.credentials || "same-origin";
    options.headers = options.headers || {};
    options.redirect = 'manual';
    const method = options.method ? options.method.toUpperCase() : 'GET';
    //  if (method !== 'GET' && method !== 'HEAD'){
    //    options.headers[csrfHeader] = csrfToken;
    //}
    const res = await fetch(url, options);
    if (res.status === 401 || res.status === 403) {
        console.warn("auth issue, reloading....");
        window.location.reload();
        return null;
    }
    return res;
}