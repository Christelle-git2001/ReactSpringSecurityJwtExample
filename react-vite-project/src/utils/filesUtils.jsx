export const declencherTelechargement = (blobUrl, filename = "document.pdf") => {
    if (!blobUrl) return;

    const a = document.createElement("a");
    a.style.display = "none";
    a.href = blobUrl;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(blobUrl);
    a.remove();
};