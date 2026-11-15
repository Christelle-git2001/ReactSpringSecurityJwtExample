import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { FiCalendar, FiChevronDown, FiCheck } from "react-icons/fi";
import { getSessions, getSessionActuelle } from "../../api/session.jsx";

const SessionSwitch = ({ userId }) => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const location = useLocation();
    const [sessions, setSessions] = useState([]);
    const [selectedSession, setSelectedSession] = useState(
        () => localStorage.getItem("sessionCible") || ""
    );

    useEffect(() => {
        let actif = true;
        if (!userId) return;

        async function initSessions() {
            try {
                const data = await getSessions();
                if (!actif) return;
                const disponibles = data || [];
                setSessions(disponibles);

                const cachedSession = localStorage.getItem("sessionCible");
                const contientSession = (cle) => disponibles.some(
                    (session) => `${session.saison}-${session.annee}` === cle
                );
                if (contientSession(cachedSession)) {
                    setSelectedSession(cachedSession);
                    return;
                }

                let defaut = disponibles.length > 0
                    ? `${disponibles[0].saison}-${disponibles[0].annee}` : "";
                if (disponibles.length > 0) {
                    try {
                        const sessionActuelle = await getSessionActuelle();
                        const cleActuelle = `${sessionActuelle.saison}-${sessionActuelle.annee}`;
                        if (contientSession(cleActuelle)) defaut = cleActuelle;
                    } catch {
                        // La session inscrite la plus récente reste le choix par défaut.
                    }
                }
                if (!actif) return;
                setSelectedSession(defaut);
                if (defaut) {
                    localStorage.setItem("sessionCible", defaut);
                } else {
                    localStorage.removeItem("sessionCible");
                }
                if ((cachedSession || "") !== defaut) {
                    window.dispatchEvent(new CustomEvent("sessionChanged", { detail: defaut }));
                }
            } catch (err) {
                console.error("Erreur lors du chargement des sessions", err);
            }
        }

        initSessions();

        return () => {
            actif = false;
        };
    }, [userId]);

    const getHomeRoute = () => {
        const path = location.pathname;
        if (path.startsWith("/etudiant")) return "/etudiant";
        if (path.startsWith("/employeur")) return "/employeur";
        if (path.startsWith("/professeur")) return "/professeur";
        if (path.startsWith("/gestionnaire")) return "/gestionnaire";
        return "/";
    };

    const handleSelectSession = (session) => {
        const sessionKey = `${session.saison}-${session.annee}`;
        if (sessionKey === selectedSession) return;

        setSelectedSession(sessionKey);
        localStorage.setItem("sessionCible", sessionKey);
        window.dispatchEvent(new CustomEvent("sessionChanged", { detail: sessionKey }));

        const homeRoute = getHomeRoute();
        if (location.pathname !== homeRoute) {
            navigate(homeRoute);
        }
    };

    const formatSessionLabel = (saison, annee) => {
        if (!saison || !annee) return t("session.select");
        const saisonTraduite = t(`session.saisons.${saison.toUpperCase()}`, {
            defaultValue: saison.charAt(0).toUpperCase() + saison.slice(1).toLowerCase()
        });
        return `${saisonTraduite} ${annee}`;
    };

    if (sessions.length === 0 || !selectedSession) return null;

    const parts = selectedSession.split("-");
    const displayLabel = formatSessionLabel(parts[0], parts[1]);

    return (
        <div className="dropdown dropdown-end">
            <div
                tabIndex={0}
                role="button"
                aria-label={t("session.label")}
                className="inline-flex h-8 shrink-0 items-center gap-2 rounded-full bg-white px-3 text-xs font-semibold text-[#043462] shadow-sm transition-all cursor-pointer hover:bg-blue-50 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[#043462]"
            >
                <FiCalendar className="text-[#00CCCB] text-sm shrink-0" />
                <span>{displayLabel}</span>
                <FiChevronDown className="text-[#043462]/70 text-xs shrink-0" />
            </div>

            <ul
                tabIndex={0}
                className={`menu dropdown-content flex-nowrap mt-2 w-48 rounded-2xl bg-white p-1.5 text-xs font-medium text-[#043462] shadow-lg border border-slate-100 z-50 ${sessions.length >= 5 ? "max-h-52 overflow-y-auto" : ""}`}
            >
                <li className="menu-title shrink-0 px-3 py-1 text-[10px] uppercase tracking-wider text-slate-400">
                    {t("session.label")}
                </li>
                {sessions.map((session) => {
                    const sessionKey = `${session.saison}-${session.annee}`;
                    const isActive = selectedSession === sessionKey;

                    return (
                        <li key={sessionKey} className="shrink-0 pb-2">
                            <button
                                type="button"
                                onClick={() => handleSelectSession(session)}
                                className={`flex items-center justify-between rounded-xl px-3 py-2 transition-colors ${
                                    isActive
                                        ? "bg-[#043462] text-white font-semibold"
                                        : "text-[#043462] hover:bg-blue-50"
                                }`}
                            >
                                <span>{formatSessionLabel(session.saison, session.annee)}</span>
                                {isActive && <FiCheck className="text-[#00CCCB] text-sm" />}
                            </button>
                        </li>
                    );
                })}
            </ul>
        </div>
    );
};

export default SessionSwitch;