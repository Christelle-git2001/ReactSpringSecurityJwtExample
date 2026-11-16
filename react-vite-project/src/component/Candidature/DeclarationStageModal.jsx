import { useId } from "react";
import { useTranslation } from "react-i18next";
import { FiX } from "react-icons/fi";
import Button from "../Design/Button.jsx";
import StatutEtudiant from "./StatutEtudiant.jsx";

export default function DeclarationStageModal({ candidature, statut, onStatutChange, submitting, error, onClose, onConfirm }) {
    const { t } = useTranslation();
    const titleId = useId();
    const descriptionId = useId();
    return (
        <dialog open aria-modal="true" aria-labelledby={titleId} aria-describedby={descriptionId}
                className="modal modal-open" onCancel={event => { event.preventDefault(); if (!submitting) onClose(); }}>
            <div className="modal-box w-full max-w-3xl bg-[radial-gradient(circle_at_top_left,#00CCCB33,transparent_70%)]">
                <div className="relative mb-5 flex min-h-10 items-center justify-center px-12">
                    <h3 id={titleId} className="text-center text-lg font-bold">{t("student_status.declaration_title")}</h3>
                    <button type="button" className="close-button absolute right-0 top-0" disabled={submitting}
                            onClick={onClose} aria-label={t("drawer.close")}>
                        <FiX className="size-5" aria-hidden="true" />
                    </button>
                </div>
                <p id={descriptionId} className="text-center">{t("student_status.declaration_description")}</p>
                <div className="mt-4 space-y-3">
                    <p className="text-center text-sm font-medium text-[#043462]">
                        {candidature.offreTitle} — {candidature.employeurBusinessName}
                    </p>
                    <StatutEtudiant value={statut} loading={submitting} onChange={onStatutChange} />
                    {error && <p role="alert" className="text-sm text-red-700">{t("error.generic")}</p>}
                </div>
                <div className="modal-action flex flex-wrap justify-around gap-3">
                    <Button role="annulation" disabled={submitting} onClick={onClose}>{t("actions.cancel")}</Button>
                    <Button role="approbation" disabled={statut !== "STAGE_TROUVE" || submitting} onClick={onConfirm}>
                        {t("student_status.confirm")}
                    </Button>
                </div>
            </div>
        </dialog>
    );
}