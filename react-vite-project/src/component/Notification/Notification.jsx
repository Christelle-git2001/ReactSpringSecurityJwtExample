import { useTranslation } from "react-i18next";
import {useEffect, useState} from "react";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";
import { FiArrowLeft, FiBell, FiFileText } from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import { getTousLesCvs } from "../../api/gestionnaire.jsx";

export default function Notification({ userId, onClose }) {
    const { t, i18n } = useTranslation();
    const [notifications, setNotifications] = useState([]);
    const [error, setError] = useState(null);

    const navigate = useNavigate();

    const ouvrirCv = async (notification) => {
        try {
            const cvId = new URLSearchParams(
                notification.urlCible.split("?")[1]
            ).get("cvId");

            const cvs = await getTousLesCvs();
            const cv = cvs.find(cv => String(cv.id) === cvId);

            if (!cv) {
                setError("notifications.cv_unavailable");
                return;
            }

            if (!notification.estLu) {
                try {
                    await marquerNotificationLue(notification.id);

                    setNotifications(current =>
                        current.filter(item => item.id !== notification.id)
                    );
                } catch {
                    setError("notifications.read_error");
                    return;
                }
            }

            onClose?.();
            navigate(notification.urlCible, {
                state: { cv, from: "/gestionnaire" },
            });
        } catch {
            setError("notifications.open_error");
        }
    };

    useEffect(() => {
        let notificationsStillDisplayed = true;

        const chargerNotifications = async () => {
            setError(null);
            setNotifications([]);

            try {
                const data = await getNotifications(userId);

                if (notificationsStillDisplayed) {
                    setNotifications(
                        (data ?? []).filter(notification => !notification.estLu)
                            .sort((a, b) => (b.dateNotifiee ?? "").localeCompare(a.dateNotifiee ?? "")
                        )
                    );
                }
            } catch {
                if (notificationsStillDisplayed) {
                    setError("notifications.load_error");
                }
            }
        };

        chargerNotifications();

        return () => {
            notificationsStillDisplayed = false;
        };
    }, [userId]);

    const formaterDate = (value) => {
        if (!value) return null;
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) return null;

        return new Intl.DateTimeFormat(i18n.resolvedLanguage || i18n.language, {
            dateStyle: "medium",
            timeStyle: "short",
        }).format(date);
    };

    return (
        <section className="-m-4 min-h-full bg-white text-[#043462]">
            {error ? (
                <div className="space-y-3 p-4">
                    <button
                        type="button"
                        onClick={() => setError(null)}
                        className="inline-flex items-center gap-2 rounded-lg px-3 py-2 text-sm hover:bg-blue-50"
                    >
                        <FiArrowLeft aria-hidden="true" />
                        {t("actions.back")}
                    </button>
                    <p role="alert" className="text-sm text-red-700">
                        {t(error)}
                    </p>
                </div>
            ) : notifications.length === 0 ? (
                <p className="px-4 py-8 text-center text-sm text-slate-500">
                    {t("notifications.empty")}
                </p>
            ) : (
                <ul className="space-y-1 p-2">
                    {notifications.map(notification => {
                        const isCv = notification.notificationType === "CV_ATTENTE_VALIDATION";
                        const date = formaterDate(notification.dateNotifiee);

                        return (
                            <li
                                key={notification.id}
                                className={`flex gap-3 rounded-xl border-b border-slate-100 p-3 ${
                                    notification.estLu ? "bg-white" : "bg-blue-50"
                                }`}
                            >
                                <button
                                    type="button"
                                    onClick={() => ouvrirCv(notification)}
                                    className="flex w-full gap-3 text-left"
                                >
                                    <div className="grid size-9 shrink-0 place-items-center rounded-xl bg-blue-100 text-blue-600" aria-hidden="true">
                                        {isCv ? <FiFileText className="size-5" /> : <FiBell className="size-5" />}
                                    </div>

                                    <div className="min-w-0 flex-1 space-y-1">
                                        <div className="flex flex-wrap items-center gap-2">
                                            <p className="break-words text-sm font-bold">
                                                {isCv
                                                    ? notification.variables?.ETUDIANT_NOM || t("notifications.title")
                                                    : t(notification.titre, { defaultValue: t("notifications.title") })}
                                            </p>
                                        </div>
                                        <p className="text-sm font-normal leading-relaxed text-[#043462]">
                                            {isCv
                                                ? t("notifications.new_cv")
                                                : t(notification.description, { defaultValue: t("notifications.generic_message") })}
                                        </p>
                                        {date && (
                                            <time dateTime={notification.dateNotifiee} className="block text-[11px] text-slate-500">
                                                {date}
                                            </time>
                                        )}
                                    </div>

                                    {!notification.estLu && (
                                        <span aria-hidden="true" className="mt-2 size-2 shrink-0 rounded-full bg-blue-600" />
                                    )}
                                </button>
                            </li>
                        );
                    })}
                </ul>
            )}
        </section>
    );
}