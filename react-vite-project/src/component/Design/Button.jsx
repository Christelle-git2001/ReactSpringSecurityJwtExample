import React from "react";
import {
    HiCheckCircle,
    HiXCircle,
    HiArrowSmLeft,
    HiUpload,
    HiEye,
    HiPaperAirplane,
    HiCalendar,
    HiPencil,
    HiArrowLeft,
    HiQuestionMarkCircle,
    HiCheck
} from "react-icons/hi";

const ROLE_CONFIG = {
    approbation: {
        style: "border-transparent bg-green-600 text-white enabled:hover:bg-green-700 focus-visible:outline-green-600",
        Icon: HiCheckCircle
    },
    refus: {
        style: "border-transparent bg-red-600 text-white enabled:hover:bg-red-700 focus-visible:outline-red-600",
        Icon: HiXCircle
    },
    annulation: {
        style: "border-gray-200 bg-gray-200 text-gray-700 enabled:hover:bg-gray-300 focus-visible:outline-gray-400",
        Icon: HiArrowSmLeft
    },
    televerser: {
        style: "border-transparent bg-[#66E5E4] text-[#043462] enabled:hover:bg-[#00CCCB] focus-visible:outline-[#66E5E4]",
        Icon: HiUpload
    },
    details: {
        style: "border-transparent bg-[#043462] text-white enabled:hover:bg-[#03284d] focus-visible:outline-[#043462]",
        Icon: HiEye
    },
    postuler: {
        style: "border-transparent bg-[#66E5E4] text-[#043462] font-semibold enabled:hover:bg-[#00CCCB] focus-visible:outline-[#66E5E4]",
        Icon: HiPaperAirplane
    },
    soumettre: {
        style: "border-transparent bg-[#043462] text-white font-medium enabled:hover:bg-[#03284d] focus-visible:outline-[#043462]",
        Icon: HiCheck
    },
    entrevue: {
        style: "border-transparent bg-[#043462] text-white enabled:hover:bg-[#03284d] focus-visible:outline-[#043462]",
        Icon: HiCalendar
    },
    signature: {
        style: "border-transparent bg-purple-600 text-white enabled:hover:bg-purple-700 focus-visible:outline-purple-600",
        Icon: HiPencil
    },
    retour: {
        style: "border-gray-300 bg-white text-gray-700 enabled:hover:bg-gray-50 focus-visible:outline-gray-400",
        Icon: HiArrowLeft
    },
    custom: {
        style: "border-transparent",
        Icon: null
    },
    neutre: {
        style: "border-transparent bg-[#043462] text-white enabled:hover:bg-[#03284d] focus-visible:outline-[#043462]",
        Icon: HiQuestionMarkCircle
    }
};

const VARIANT_TO_ROLE = {
    standard: "neutre",
    success: "approbation",
    danger: "refus",
    secondary: "retour",
    custom: "custom"
};

const sizeClasses = {
    normal: "px-4 py-2",
    small: "px-3 py-1.5 text-sm",
    icon: "size-10 shrink-0 p-2",
};

export default function Button({
                                   children,
                                   type = "button",
                                   role,
                                   variant,
                                   size = "normal",
                                   className = "",
                                   disabled = false,
                                   loading = false,
                                   icon = true,
                                   ...props
                               }) {
    const activeRoleKey = role || (variant ? (VARIANT_TO_ROLE[variant] || "neutre") : "neutre");
    const config = ROLE_CONFIG[activeRoleKey] || ROLE_CONFIG.neutre;

    const IconComponent = typeof icon === "boolean" && icon ? config.Icon : null;
    const CustomIcon = typeof icon !== "boolean" ? icon : null;

    return (
        <button
            {...props}
            type={type}
            disabled={disabled || loading}
            aria-busy={loading || undefined}
            className={`inline-flex items-center justify-center gap-2 rounded-md border font-medium transition-colors enabled:cursor-pointer disabled:cursor-not-allowed disabled:opacity-50 focus-visible:outline-2 focus-visible:outline-offset-2 ${config.style} ${sizeClasses[size] || sizeClasses.normal} ${className}`}
        >
            {loading ? (
                <span className="loading loading-spinner loading-sm" aria-hidden="true" />
            ) : (
                <>
                    {IconComponent && <IconComponent className="w-4 h-4 shrink-0" aria-hidden="true" />}
                    {CustomIcon}
                </>
            )}

            {children}
        </button>
    );
}