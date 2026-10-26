import { useTranslation } from "react-i18next";
import {useEffect, useState} from "react";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";
import {FiArrowRight, FiArrowUp, FiArrowLeft, FiBriefcase, FiFileText, FiUserPlus, FiBell} from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import {obtenirCvGestionnaire, obtenirOffreGestionnaire} from "../../api/gestionnaire.jsx";
import { getCandidaturesEmployeur, getOffresEmployeur } from "../../api/employeur.jsx"
import ListItem from "../Design/Lists/ListItem.jsx";
import {ImCross} from "react-icons/im";
import {FaCheck, FaQuestion} from "react-icons/fa";

const ROUTES_PAR_TYPE = {
    CV_ATTENTE_VALIDATION: "/gestionnaire/cvs/details",
    OFFRE_ATTENTE_VALIDATION: "/gestionnaire/offres/details",
    CV_STATUT_CHANGE: "/etudiant/cv#televerser",
    NOUVELLE_OFFRE_STAGE: "/etudiant",
    NOUVELLE_CANDIDATURE: "/employeur/candidatures/details"
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
    CV_STATUT_CHANGE_REFUSEE: {
        icon: <ImCross />,
        tone : "red"
    },
    CV_STATUT_CHANGE_ACCEPTEE: {
        icon : <FaCheck />,
        tone : "green"
    },
    CV_STATUT_CHANGE: {
        icon : <FaQuestion />,
        tone : "yellow"
    },
    NOUVELLE_OFFRE_STAGE: {
            icon: <FiBriefcase className="size-5" />,
            tone: "green",
    },
    NOUVELLE_CANDIDATURE: {
        icon: <FiUserPlus className="size-5" />,
        tone: "yellow",
    },
};

export default function Notification({ notifications = [], onMarkAsRead }) {
    const { t, i18n } = useTranslation();
    const [error, setError] = useState(null);
    const [sortOrder, setSortOrder] = useState("desc");
    const displayedNotifications = (notifications).slice(0, 5);
    const navigate = useNavigate();

    const formaterDate = (value) => {
        if (!value) return null;
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) return null;

        return new Intl.DateTimeFormat(i18n.resolvedLanguage || i18n.language, {
            dateStyle: "medium",
            timeStyle: "short",
        }).format(date);
    };

    const onClick = async (notification) => {
        const destination = ROUTES_PAR_TYPE[notification.notificationType];

        if (!notification.estLu) {
            try {
                await marquerNotificationLue(notification.id);
                onMarkAsRead(notification.id);
            } catch {
                setError(t("notifications.read_error"));
                return;
            }
        }


        if (destination) {
            navigate(destination);
        }
    };

    const notificationsTriees = [...notifications].sort((a, b) => {
        const comparaison = (a.dateNotifiee ?? "").localeCompare(b.dateNotifiee ?? "");
        return sortOrder === "asc" ? comparaison : -comparaison;
    });

    return (
        <div className="dropdown dropdown-end">
            <div tabIndex={0} role="button" className="btn btn-ghost btn-circle">
                <div className="indicator">
                    <FiBell className="size-5 text-white" />
                    {notifications.length > 0 && (
                        <span className="badge badge-sm indicator-item bg-red-600 text-white border-none">
                            {notifications.length}
                        </span>
                    )}
                </div>
            </div>

            <div
                tabIndex={0}
                className="dropdown-content z-[100] mt-3 w-80 rounded-box bg-base-100 shadow-xl border border-slate-100"
            >
                <div className="p-3 border-b border-slate-100 flex justify-between items-center">
                    <h3 className="font-bold text-[#043462]">{t("notifications.title")}</h3>
                    {error && <span className="text-xs text-red-500">{error}</span>}
                </div>

                <ul className="max-h-[350px] overflow-y-auto p-2 space-y-1">
                    {displayedNotifications.length === 0 ? (
                        <li className="p-4 text-center text-sm text-slate-500">
                            {t("notifications.empty")}
                        </li>
                    ) : (
                        displayedNotifications.map(notification => {
                            const isStatusChange = notification.notificationType === "CV_STATUT_CHANGE";
                            const statut = notification.variables?.["STATUT_FICHIER"];
                            const configKey = isStatusChange && statut
                                ? `${notification.notificationType}_${statut}`
                                : notification.notificationType;
                            const config = CONFIG_PAR_TYPE[configKey];

                            const titre = t(notification.titre, {
                                ...notification.variables,
                                defaultValue: t("notifications.title"),
                            });
                            const description = t(notification.description, {
                                ...notification.variables,
                                defaultValue: t("notifications.generic_message"),
                            });

                            return (
                                <li key={notification.id}>
                                    <button
                                        onClick={() => onClick(notification)}
                                        className="w-full text-left flex items-start gap-3 p-2 rounded-lg hover:bg-slate-50 transition-colors"
                                    >
                                        <div className={`mt-0.5 p-1.5 rounded-full shrink-0 bg-${config?.tone}-100 text-${config?.tone}-600`}>
                                            {config?.icon}
                                        </div>
                                        <div className="flex-1 min-w-0">
                                            <p className="text-sm font-semibold text-[#043462] truncate">
                                                {titre}
                                            </p>
                                            <p className="text-xs text-slate-600 line-clamp-2">
                                                {description}
                                            </p>
                                            <p className="text-[10px] text-slate-400 mt-1">
                                                {formaterDate(notification.dateNotifiee)}
                                            </p>
                                        </div>
                                    </button>
                                </li>
                            );
                        })
                    )}
                </ul>
            </div>
        </div>
    );
}