import React from "react";
import { useTranslation } from "react-i18next";
import { FaFilePdf, FaBriefcase, FaCalendarAlt, FaUserCheck, FaSignature } from "react-icons/fa";

const TONE_CLASSES = {
    blue: "bg-blue-100 text-blue-600",
    cyan: "bg-cyan-100 text-cyan-600",
    green: "bg-green-100 text-green-600",
    indigo: "bg-indigo-100 text-indigo-600",
    purple: "bg-purple-100 text-purple-600",
    gray: "bg-gray-100 text-gray-400",
};

export const STEP_CONFIGS = {
    CV: {
        icon: <FaFilePdf className="size-5" />,
        tone: "blue",
    },
    CANDIDATURE: {
        icon: <FaBriefcase className="size-5" />,
        tone: "cyan",
    },
    ENTREVUE: {
        icon: <FaCalendarAlt className="size-5" />,
        tone: "indigo",
    },
    STATUT: {
        icon: <FaUserCheck className="size-5" />,
        tone: "green",
    },
    CONTRAT: {
        icon: <FaSignature className="size-5" />,
        tone: "purple",
    },
};
function StepProcess({ type, title, description, date, action, isStarted = true }) {
    const { t } = useTranslation();

    const config = STEP_CONFIGS[type] || STEP_CONFIGS.CANDIDATURE;
    const toneClass = isStarted ? (TONE_CLASSES[config.tone] || TONE_CLASSES.blue) : TONE_CLASSES.gray;

    return (
        <div className="flex items-start justify-between p-4 bg-white rounded-lg border border-gray-100 shadow-sm transition-all hover:shadow-md">
            <div className="flex items-start gap-4">
                <div className={`p-3 rounded-full flex items-center justify-center ${toneClass}`}>
                    {config.icon}
                </div>

                <div className="flex flex-col gap-1">
                    <p className="font-semibold text-gray-800 text-base">{title}</p>
                    <p className="text-sm text-gray-500">{description}</p>
                    {date && (
                        <p className="text-xs text-cyan-600 font-medium mt-1">
                            {t("process.completed_on")} : {date}
                        </p>
                    )}
                </div>
            </div>

            {action && (
                <div className="self-center">
                    {action}
                </div>
            )}
        </div>
    );
}

export default StepProcess;
