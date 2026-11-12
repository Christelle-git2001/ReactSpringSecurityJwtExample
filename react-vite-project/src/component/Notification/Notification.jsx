import { useTranslation } from "react-i18next";
import { useRef, useState} from "react";
import { marquerNotificationLue } from "../../api/notification.jsx";
import { formaterDate as formaterDateHeure } from "../../utils/cvFormatters.jsx";
import {obtenirCvGestionnaire, obtenirOffreGestionnaire, getCandidaturesByStatus} from "../../api/gestionnaire.jsx";
import { getCandidaturesEmployeur, getOffresEmployeur } from "../../api/employeur.jsx";
import Sort from "../Design/Sort.jsx";

import {
    FiBriefcase,
    FiFileText,
    FiUserPlus,
    FiBell,
    FiX,

} from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import {ImCross} from "react-icons/im";
import {FaCalendarAlt, FaCheck, FaQuestion} from "react-icons/fa";
import {PiStudentBold} from "react-icons/pi";
import { obtenirCandidaturesEtudiant } from "../../api/etudiant.jsx";

const ROUTES_PAR_TYPE = {
    CV_ATTENTE_VALIDATION: "/gestionnaire/cvs/details",
    OFFRE_ATTENTE_VALIDATION: "/gestionnaire/offres/details",
    OFFRE_STATUT_CHANGE: "/employeur",
    CV_STATUT_CHANGE: "/etudiant/cv#televerser",
    NOUVELLE_OFFRE_STAGE: "/etudiant",
    PROFESSEUR_ASSIGNE_ETUDIANT: "/professeur?filter=new",
    CONVOCATION_ENTREVUE: "/etudiant",
    CANDIDATURE_ACCEPTEE: "/etudiant",
    CANDIDATURE_REFUSEE: "/etudiant",
    CONVOCATION_ACCEPTEE: "/employeur/candidatures/details",
    CONVOCATION_REFUSEE: "/employeur/candidatures/details",
    STAGE_TROUVE: "/employeur/candidatures/details",
    CONTRAT_A_CREER: "/gestionnaire",
    CONTRAT_A_SIGNER: "/gestionnaire",
    NOUVELLE_CANDIDATURE: "/employeur/candidatures/details",
    NOUVEAU_CONTRAT: (pathname) => {
        switch (true) {
            case pathname.startsWith("/employeur"):
                return "/employeur/candidatures/details";
            case pathname.startsWith("/etudiant"):
                return "/etudiant/contrat";
            default:
                return "/error";
        }
    }
};

const CONFIG_PAR_TYPE = {
    CONTRAT_A_SIGNER: { icon: <FiFileText className="size-5" />, tone: "blue" },
    NOUVEAU_CONTRAT: { icon: <FiFileText className="size-5" />, tone: "purple" },
    OFFRE_STATUT_CHANGE_ACCEPTEE: { icon: <FaCheck className="size-4" />, tone: "green" },
    OFFRE_STATUT_CHANGE_REFUSEE: { icon: <ImCross className="size-4" />, tone: "red" },
    CV_ATTENTE_VALIDATION: {
        icon: <FiFileText className="size-5" />,
        tone: "blue",
    },
    OFFRE_ATTENTE_VALIDATION: {
        icon: <FiBriefcase className="size-5" />,
        tone: "green",
    },
    CV_STATUT_CHANGE_REFUSEE: {
        icon: <ImCross className="size-4" />,
        tone: "red"
    },
    CV_STATUT_CHANGE_ACCEPTEE: {
        icon: <FaCheck className="size-4" />,
        tone: "green"
    },
    CV_STATUT_CHANGE: {
        icon: <FaQuestion className="size-4" />,
        tone: "yellow"
    },
    NOUVELLE_OFFRE_STAGE: {
        icon: <FiBriefcase className="size-5" />,
        tone: "green",
    },
    NOUVELLE_CANDIDATURE: {
        icon: <FiUserPlus className="size-5" />,
        tone: "yellow",
    },
    CONVOCATION_ENTREVUE: {
        icon: <FaCalendarAlt className="size-5" />,
        tone: "fuchsia",
    },
    CANDIDATURE_ACCEPTEE: { icon: <FaCheck className="size-4" />, tone: "green" },
    CANDIDATURE_REFUSEE: { icon: <ImCross className="size-4" />, tone: "red" },
    CONVOCATION_ACCEPTEE: {
        icon: <FaCheck className="size-4" />,
        tone: "green",
    },
    CONVOCATION_REFUSEE: {
        icon: <ImCross className="size-4" />,
        tone: "red",
    },
    ETUDIANT_ASSIGNE_PROFESSEUR : {
        icon : <PiStudentBold/>,
        tone : "blue"
    },
    PROFESSEUR_ASSIGNE_ETUDIANT : {
        icon : <PiStudentBold/>,
        tone : "blue"
    },
    STAGE_TROUVE: {
        icon: <FaCheck className="size-4" />,
        tone: "green",
    },
    CONTRAT_A_CREER: {
        icon: <FiFileText className="size-5" />,
        tone: "blue",
    },
};

const TONE_CLASSES = {
    blue: "bg-blue-100 text-blue-600",
    green: "bg-green-100 text-green-600",
    red: "bg-red-100 text-red-600",
    yellow: "bg-yellow-100 text-yellow-600",
    gray: "bg-slate-100 text-slate-600",
    purple: "bg-purple-100 text-purple-600",
    fuchsia: "bg-fuchsia-100 text-fuchsia-600",
    STAGE_TROUVE: {
        icon: <FaCheck />,
        tone: "green",
    },
};

export default function Notification({ notifications = [], onMarkAsRead }) {
    const { t } = useTranslation();
    const [error, setError] = useState(null);
    const [sortOrder, setSortOrder] = useState("desc");
    const navigate = useNavigate();
    const dropdownRef = useRef(null);


    const closeNotification = () => {
        if (dropdownRef.current) {
            dropdownRef.current.removeAttribute("open");
        }
    };

    const formaterDate = (value) => {
        if (!value) return null;
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) return null;

        const dateFormatee = formaterDateHeure(value);
        if (date.toDateString() === new Date().toDateString()) {
            return `${t("agenda.today")} ${dateFormatee.slice(11)}`;
        }
        return dateFormatee;
    };

    const notificationsTriees = [...notifications].sort((a, b) => {
        const comparaison = (a.dateNotifiee ?? "").localeCompare(b.dateNotifiee ?? "");
        return sortOrder === "asc" ? comparaison : -comparaison;
    });

    const displayedNotifications = notificationsTriees.slice(0, 5);

    const onClick = async (notification) => {
        const routeConfig = ROUTES_PAR_TYPE[notification.notificationType];
        const role = window.location.pathname;
        const destination = typeof routeConfig === "function"
            ? routeConfig(role)
            : routeConfig;

        if (!notification.estLu && !["NOUVEAU_CONTRAT", "CONTRAT_A_SIGNER"].includes(notification.notificationType)) {
            try {
                await marquerNotificationLue(notification.id);
                onMarkAsRead?.(notification.id);
            } catch {
                setError(t("notifications.read_error"));
                return;
            }
        }

        try {
            let state = { from: "/gestionnaire" };

            if (notification.notificationType === "CV_ATTENTE_VALIDATION") {
                const cv = await obtenirCvGestionnaire(notification.urlCible);
                if (!cv) {
                    setError(t("notifications.cv_unavailable"));
                    return;
                }
                state.cv = cv;

            } else if (notification.notificationType === "OFFRE_ATTENTE_VALIDATION") {
                const offre = await obtenirOffreGestionnaire(notification.urlCible);
                if (!offre) {
                    setError(t("notifications.offre_unavailable"));
                    return;
                }
                state.offer = offre;

            } else if (notification.notificationType === "NOUVELLE_OFFRE_STAGE") {
                const offreId = notification.variables?.OFFRE_ID;
                if (!offreId) {
                    setError(t("notifications.open_error"));
                    return;
                }
                state.offreId = offreId;
                state.from = "/etudiant";

            } else if (notification.notificationType === "CONVOCATION_ENTREVUE") {
                state.id = notification.variables?.ENTREVUE_ID;
                state.from = "/etudiant";
            } else if (["CANDIDATURE_ACCEPTEE", "CANDIDATURE_REFUSEE"].includes(notification.notificationType)) {
                const candidatureId = notification.variables?.CANDIDATURE_ID;
                if (!candidatureId) {
                    setError(t("notifications.open_error"));
                    return;
                }
                state.candidatureId = candidatureId;
                state.from = "/etudiant";
            }
            else if (["CONTRAT_A_CREER", "CONTRAT_A_SIGNER"].includes(notification.notificationType)) {
                const candidatureId = notification.variables?.CANDIDATURE_ID;
                if (!candidatureId) {
                    setError(t("notifications.open_error"));
                    return;
                }
                const candidatures = await getCandidaturesByStatus(
                    notification.notificationType === "CONTRAT_A_SIGNER" ? "CONTRAT_CREE" : "STAGE_TROUVE");
                const candidature = candidatures.find(c => String(c.id) === String(candidatureId));
                if (!candidature) {
                    setError(t("notifications.not_found"));
                    return;
                }
                closeNotification();
                navigate(`/gestionnaire/candidatures/${candidature.id}/contrat`, {
                    state: { candidature, from: "/gestionnaire" },
                });
                return;
            }
            else if (["NOUVELLE_CANDIDATURE", "CONVOCATION_ACCEPTEE", "CONVOCATION_REFUSEE", "STAGE_TROUVE"]
                .includes(notification.notificationType)
                || (notification.notificationType === "NOUVEAU_CONTRAT" && role.startsWith("/employeur"))) {
                const candidatureId = notification.variables?.CANDIDATURE_ID;
                const [candidatures, offres] = await Promise.all([
                    getCandidaturesEmployeur(),
                    getOffresEmployeur()
                ]);
                const candidature = candidatures.find(c => c.id === Number(candidatureId));
                if (!candidature) {
                    setError(t("notifications.open_error"));
                    return;
                }
                state.candidature = candidature;
                state.offer = offres.find(o => o.id === candidature.offreId);
                state.from = "/employeur";

            } else if (notification.notificationType === "NOUVEAU_CONTRAT") {
                const candidatures = await obtenirCandidaturesEtudiant();
                const candidature = candidatures.find(c => String(c.id) === String(notification.variables?.CANDIDATURE_ID));
                if (!candidature) {
                    setError(t("notifications.not_found"));
                    return;
                }
                state.candidature = candidature;
                state.from = "/etudiant";
            }
            closeNotification();
            navigate(destination, { state });
        }catch (error) {
            console.error("Erreur lors de l'ouverture:", error);
            setError(t(error?.status === 404 ? "notifications.not_found" : "notifications.open_error"));
        }
    };



    return (
        <details ref={dropdownRef} className="dropdown dropdown-end">
            <summary role="button" className="btn btn-ghost btn-circle">
                <div className="indicator">
                    <FiBell className="size-5 text-white" />
                    {notifications.length > 0 && (
                        <span className="badge badge-sm indicator-item bg-red-600 text-white border-none">
                            {notifications.length}
                        </span>
                    )}
                </div>
            </summary>

            <div
                className="dropdown-content z-[100] mt-3 w-80 max-w-[calc(100vw-2rem)] max-sm:fixed! max-sm:top-16! max-sm:right-4! rounded-box bg-base-100 shadow-xl border border-slate-100"
            >
                <div className="p-3 border-b border-slate-100 flex justify-between items-center">
                    <h3 className="font-bold text-[#043462]">{t("notifications.title")}</h3>
                    <div className="flex items-center gap-2">

                        <Sort
                            sortOrder={sortOrder}
                            onSortChange={setSortOrder}
                            title={t("notifications.sort_by_date")}
                            newestLabel={t("notifications.newest_first")}
                            oldestLabel={t("notifications.oldest_first")}
                        />
                    </div>
                    <button
                        onClick={closeNotification}
                        className="btn btn-ghost btn-xs btn-circle text-slate-500 hover:bg-slate-200"
                        aria-label="Fermer"
                    >
                        <FiX className="size-4" />
                    </button>
                    {error && (
                        <span role="alert" className="text-xs text-red-500">
                            {error}
                        </span>
                    )}
                </div>

                <ul className="max-h-[350px] overflow-y-auto p-2 space-y-1">
                    {displayedNotifications.length === 0 ? (
                        <li className="p-4 text-center text-sm text-slate-500">
                            {t("notifications.empty")}
                        </li>
                    ) : (
                        displayedNotifications.map(notification => {
                            const isStatusChange = ["CV_STATUT_CHANGE", "OFFRE_STATUT_CHANGE"].includes(notification.notificationType);
                            const statut = notification.variables?.["STATUT_FICHIER"];
                            const configKey = isStatusChange && statut
                                ? `${notification.notificationType}_${statut}`
                                : notification.notificationType;
                            const config = CONFIG_PAR_TYPE[configKey];
                            const toneClass = TONE_CLASSES[config?.tone] ?? TONE_CLASSES.gray;

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
                                        <div className={`mt-0.5 flex size-8 items-center justify-center rounded-full shrink-0 ${toneClass}`}>
                                            {config?.icon ?? <FiBell className="size-5" />}
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
        </details>
    );
}
