import {FiX} from "react-icons/fi";
import {useState} from "react";
import {useTranslation} from "react-i18next";
import Button from "../Design/Button.jsx";

const Modal = ({onClickCross,title,description,button,children}) => {    const { t } = useTranslation();
    const [error, setError] = useState("");
    return (
        <dialog className="modal modal-open">
            <div className="modal-box w-full max-w-3xl bg-[radial-gradient(circle_at_top_left,#00CCCB33,transparent_70%)]">
                <div className="relative mb-5 flex min-h-10 items-center justify-center px-12">
                    <h3 className="font-bold text-lg text-center">{title}</h3>
                    <button
                        type="button"
                        className="close-button absolute right-0 top-0"
                        onClick={onClickCross}
                        aria-label={t("drawer.close")}
                    >
                        <FiX className="size-5" aria-hidden="true" />
                    </button>
                </div>
                <p className="text-center">{description}</p>
                {children}

                <div className="modal-action">
                    {error && (
                            <p role="alert" className="text-sm text-red-700">
                                {t(error)}
                            </p>
                        )}
                    {button && (
                        <div className="flex justify-around  w-full mt-5">
                            {button}
                        </div>
                    )}
                </div>
            </div>
        </dialog>
    )
};export default Modal