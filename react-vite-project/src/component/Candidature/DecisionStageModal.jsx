import { useTranslation } from "react-i18next";
import { FiLock } from "react-icons/fi";
import Button from "../Design/Button.jsx";
import Modal from "../Modal/Modal.jsx";
import StatutEtudiant from "./StatutEtudiant.jsx";

export default function DecisionStageModal({ candidature, accepter, submitting, error, onClose, onConfirm }) {
    const { t } = useTranslation();

    return (
        <Modal
            boxClassName="bg-white text-[#043462]"
            title={t(accepter ? "stage.accept_title" : "stage.refuse_title")}
            description={t(accepter ? "stage.accept_description" : "stage.refuse_description")}
            onClickCross={onClose}
            submitting={submitting}
            error={error}
            button={<>
                <Button role="annulation" disabled={submitting} onClick={onClose} className="w-full sm:w-1/3">{t("actions.cancel")}</Button>
                <Button variant={accepter ? "success" : "danger"} disabled={submitting} loading={submitting} onClick={onConfirm} className="w-full sm:w-1/2">
                    {t("actions.confirm")}
                </Button>
            </>}
        >
            <p className="mt-5 text-center text-sm font-semibold">{candidature.offreTitle} — {candidature.employeurBusinessName}</p>
            {accepter && <>
                <div className="mt-4">
                    <StatutEtudiant value="STAGE_TROUVE" />
                </div>
                <div className="mt-4 flex items-start gap-3 rounded-xl border border-[#00CCCB]/25 bg-[#00CCCB]/10 p-4 text-sm leading-relaxed">
                    <FiLock className="mt-0.5 size-4 shrink-0" aria-hidden="true" />
                    <p>{t("stage.lock_warning")}</p>
                </div>
            </>}
        </Modal>
    );
}