import { useTranslation } from "react-i18next";
import {useEffect, useState} from "react";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";
import { FiArrowDown, FiArrowUp, FiArrowLeft, FiBriefcase, FiFileText } from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import {obtenirCvGestionnaire, obtenirOffreGestionnaire} from "../../api/gestionnaire.jsx";
import ListItem from "../Design/Lists/ListItem.jsx";

const ROUTES_PAR_TYPE = {
    CV_ATTENTE_VALIDATION: "/gestionnaire/cvs/details",
    OFFRE_ATTENTE_VALIDATION: "/gestionnaire/offres/details",
};

const CONFIG_PAR_TYPE = {
    CV_ATTENTE_VALIDATION: {
        icon: <FiFileText className="size-5" />,
        tone: "blue",
    },
    OFFRE_ATTENTE_VALIDATION: {
        icon: <FiBriefcase  className="size-5" />,
        tone: "green",
    },
};

export default function Notification({ userId, onClose }) {
    const { t, i18n } = useTranslation();
    const [notifications, setNotifications] = useState([]);
    const [error, setError] = useState(null);
    const [sortOrder, setSortOrder] = useState("desc");

    const navigate = useNavigate();

    const ouvrirCv = async (notification) => {
        const destination = ROUTES_PAR_TYPE[notification.notificationType];
        if (!destination) return;

        try {
            const cv = await obtenirCvGestionnaire(notification.urlCible);

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
            navigate(destination, {
                state: { cv, from: "/gestionnaire" },
            });
        } catch (err) {
            setError(err?.status === 404
                ? "notifications.cv_unavailable"
                : "notifications.open_error");
        }
    };

    const ouvrirOffre = async (notification) => {
        const destination = ROUTES_PAR_TYPE[notification.notificationType];
        if (!destination) return;

        try {
            const offre = await obtenirOffreGestionnaire(notification.urlCible);
            if (!offre) {
                setError("notifications.offre_unavailable");
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
            navigate(destination, {
                state: { offer:offre ,from: "/gestionnaire" },
            });
        } catch (err) {
            setError(err?.status === 404
                ? "notifications.offre_unavailable"
                : "notifications.open_error");
        }
    }


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

    const notificationsTriees = [...notifications].sort((a, b) => {
        const comparaison = (a.dateNotifiee ?? "").localeCompare(b.dateNotifiee ?? "");
        return sortOrder === "asc" ? comparaison : -comparaison;
    });

    return (
        <section className="-m-4 min-h-full bg-white text-[#043462]">
            <div className="flex justify-end border-b border-slate-100 bg-white px-3 py-2">
                <button
                    type="button"
                    onClick={() => setSortOrder(current => current === "desc" ? "asc" : "desc")}
                    className="inline-flex cursor-pointer items-center gap-2 rounded-lg px-3 py-2 text-xs font-medium hover:bg-blue-50 focus-visible:outline-2 focus-visible:outline-blue-600"
                    title={t("notifications.sort_by_date")}
                    aria-label={t(sortOrder === "desc" ? "notifications.oldest_first" : "notifications.newest_first")}
                >
                    {sortOrder === "desc"
                        ? <FiArrowDown className="size-4" aria-hidden="true" />
                        : <FiArrowUp className="size-4" aria-hidden="true" />}
                    {t(sortOrder === "desc" ? "notifications.newest_first" : "notifications.oldest_first")}
                </button>
            </div>
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
                    {notificationsTriees.map(notification => {
                        const isCv = notification.notificationType === "CV_ATTENTE_VALIDATION";
                        const isOffre = notification.notificationType === "OFFRE_ATTENTE_VALIDATION";

                        const config = CONFIG_PAR_TYPE[notification.notificationType] ;


                        const titre = t(notification.titre, {
                            ...notification.variables,
                            defaultValue: t("notifications.title"),
                        });
                        const description = t(notification.description, {
                            ...notification.variables,
                            defaultValue: t("notifications.generic_message"),
                        });

                        return (
                            <li
                                key={notification.id}
                                className="rounded-xl border-b border-slate-100 bg-blue-50"
                            >
                                <ListItem
                                    wrapText
                                    showChevron={false}
                                    variant="row"
                                    tone={config?.tone}
                                    icon={config?.icon}
                                    title={titre}
                                    subtitle={description}
                                    meta={formaterDate(notification.dateNotifiee)}
                                    ariaLabel={titre}
                                    onClick={() => {
                                        if (isCv) ouvrirCv(notification);
                                        else if
                                        (isOffre) ouvrirOffre(notification);
                                    }}
                                />
                            </li>
                        );
                    })}
                </ul>
            )}
        </section>
    );
}