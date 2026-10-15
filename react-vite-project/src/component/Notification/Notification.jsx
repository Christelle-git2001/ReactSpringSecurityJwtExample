import { useTranslation } from "react-i18next";

export default function Notification() {
    const { t } = useTranslation();

    return (
        <section className="space-y-4">
            <p className="text-sm text-gray-600">
                {t("notifications.empty")}
            </p>
        </section>
    );
}