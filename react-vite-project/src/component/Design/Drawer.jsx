import React from "react";
import { FiX } from "react-icons/fi";
import {useTranslation} from "react-i18next";

function Drawer({ isOpen, onClose, content, title = "drawer.details" }) {
    const { t } = useTranslation();

    return (
        <div
            className={`transition-all duration-300 ease-in-out overflow-hidden flex flex-col h-full bg-[#D9B8FF] shadow-xl rounded-l-lg border-l border-purple-300 ${
                isOpen ? "w-80 opacity-100 ml-4" : "w-0 opacity-0 ml-0 border-none"
            }`}
        >
            <div className="w-80 flex flex-col h-full">
                <div className="p-3 flex justify-between items-center border-b border-purple-300/60">
                    <span className="font-bold text-[#043462] text-lg">{t(title)}</span>
                    <button
                        className="close-button"
                        type="button"
                        onClick={onClose}
                        aria-label={t("drawer.close")}
                    >
                        <FiX className="size-5" aria-hidden="true" />
                    </button>
                </div>
                <div className="p-4 overflow-y-auto flex-1">
                    {content}
                </div>
            </div>
        </div>
    );
}

export default Drawer;