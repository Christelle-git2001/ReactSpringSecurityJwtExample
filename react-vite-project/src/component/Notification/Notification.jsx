import { useTranslation } from "react-i18next";

export default function Notification() {
    const { t } = useTranslation();

    return (
        <section className="space-y-4">
            <h2 className="text-lg font-bold text-[#043462]">
                {t("notifications.title")}
            </h2>

            <p className="text-sm text-gray-600">
                {t("notifications.empty")}
            </p>
        </section>
    );
}