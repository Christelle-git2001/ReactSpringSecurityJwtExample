import { useTranslation } from "react-i18next";
import Button from "../Design/Button.jsx";
import Modal from "../Modal/Modal.jsx";

export default function ConfirmationRefusConvocationModal({ submitting, onClose, onConfirm }) {
    const { t } = useTranslation();

    return (
        <Modal
            boxClassName="bg-white text-[#043462]"
            title={t("convocation.confirm_refusal")}
            description={t("convocation.refusal_confirmation")}
            onClickCross={onClose}
            submitting={submitting}
            button={<>
                <Button role="annulation" disabled={submitting} onClick={onClose} className="w-full sm:w-1/3">{t("actions.cancel")}</Button>
                <Button role="refus" disabled={submitting} loading={submitting} onClick={onConfirm} className="w-full sm:w-1/2">{t("convocation.confirm_refusal")}</Button>
            </>}
        />
    );
}