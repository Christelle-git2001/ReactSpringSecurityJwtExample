import { useTranslation } from "react-i18next";

const sizeClasses = {
    sm: "px-2 py-1 text-xs",
    md: "px-2 py-2 text-sm",
    lg: "px-3 py-2 text-base",
};

function NewLabel({ isNew, size = "md", className = "" }) {
    const { t } = useTranslation();

    if (!isNew) {
        return null;
    }

    return (
        <span className={`absolute -top-4 left-4 z-10 rounded-md bg-green-800 font-bold text-white shadow-md ${sizeClasses[size] || sizeClasses.md} ${className}`}>
            {t("common.new_label")}
        </span>
    );
}

export default NewLabel;