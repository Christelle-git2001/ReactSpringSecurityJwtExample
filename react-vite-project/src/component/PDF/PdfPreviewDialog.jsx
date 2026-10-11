import { PDFVisioneuse } from "./PDFVisioneuse.jsx";

function PdfPreviewDialog({ dialogRef, pdfUrl, onClose }) {
    return (
        <dialog ref={dialogRef} className="modal">
            <PDFVisioneuse
                cvUrlAAffiche={pdfUrl}
                onClose={onClose}
            />
            <form method="dialog" className="modal-backdrop">
                <button type="submit">close</button>
            </form>
        </dialog>
    );
}

export default PdfPreviewDialog;