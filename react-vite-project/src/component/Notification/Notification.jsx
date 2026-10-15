import { useTranslation } from "react-i18next";
import {useEffect, useState} from "react";
import { getNotifications } from "../../api/notification.jsx";

export default function Notification({ userId }) {
    const { t } = useTranslation();
    const [notifications, setNotifications] = useState([]);
    const [error, setError] = useState(null);
    useEffect(() => {
        let actif = true;

        const chargerNotifications = async () => {
            setError(null);
            setNotifications([]);

            try {
                const data = await getNotifications(userId);

                if (actif) {
                    setNotifications(
                        (data ?? []).filter(
                            notification =>
                                notification.notificationType ===
                                "CV_ATTENTE_VALIDATION"
                        )
                    );
                }
            } catch {
                if (actif) {
                    setError("notifications.load_error");
                }
            }
        };

        chargerNotifications();

        return () => {
            actif = false;
        };
    }, [userId])

    return (
        <div className="space-y-3">
            {error ? (
                <p role="alert" className="text-sm text-red-700">
                    {t(error)}
                </p>
            ) : notifications.length === 0 ? (
                <p>{t("notifications.empty")}</p>
            ) : (
                notifications.map(notification => (
                    <div
                        key={notification.id}
                        className="rounded-lg bg-white p-3"
                    >
                        <p>{t("notifications.new_cv")}</p>
                        <p>{notification.variables?.ETUDIANT_NOM}</p>
                    </div>
                ))
            )}
        </div>
    );
}
