import { useTranslation } from "react-i18next";

function NewLabel({ isNew }) {
    const { t } = useTranslation();

    if (!isNew) {
        return null;
    }

    return (
        <span className="absolute -top-4 left-4 z-10 rounded-md bg-green-800 px-2 py-2 text-sm font-bold text-white shadow-md">
            {t("common.new_label")}
        </span>
    );
}

export default NewLabel;