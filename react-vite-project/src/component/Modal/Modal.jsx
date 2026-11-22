import {FiX} from "react-icons/fi";
import {useId} from "react";
import {useTranslation} from "react-i18next";
import Button from "../Design/Button.jsx";

const Modal = ({onClickCross, title, description, button, children, submitting = false, error, boxClassName = ""}) => {
    const { t } = useTranslation();
    const titleId = useId();
    const descriptionId = useId();
    return (
        <dialog open aria-modal="true" aria-labelledby={titleId} aria-describedby={description ? descriptionId : undefined}
                className="modal modal-open"
                onCancel={event => { event.preventDefault(); if (!submitting) onClickCross(); }}>
            <div className={`modal-box w-full max-w-3xl bg-[radial-gradient(circle_at_top_left,#00CCCB33,transparent_70%)] ${boxClassName}`}>
                <div className="relative mb-5 flex min-h-10 items-center justify-center px-12">
                    <h3 id={titleId} className="font-bold text-lg text-center">{title}</h3>
                    <Button
                        variant="custom"
                        size="icon"
                        disabled={submitting}
                        icon={<FiX className="size-5" aria-hidden="true" />}
                        className="close-button absolute right-0 top-0"
                        onClick={onClickCross}
                        aria-label={t("drawer.close")}
                    />
                </div>
                {description && <p id={descriptionId} className="text-center">{description}</p>}
                {children}
                {error && <p role="alert" className="mt-4 text-sm text-red-700">{t(error, { defaultValue: t("error.generic") })}</p>}

                {button && (
                    <div className="modal-action">
                        <div className="flex justify-around w-full mt-5">
                            {button}
                        </div>
                    </div>
                )}
            </div>
        </dialog>
    )
};
export default Modal