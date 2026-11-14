import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import LanguageSwitch from "../../../locales/LanguageSwitch.jsx";
import ThemeToggle from "../ThemeToggle.jsx";
import SideBarIcon from "../Icons/SideBarIcon.jsx";
import SessionSwitch from "../SessionSwitch.jsx";
import logo from "../../../assets/logo.png";
import Notification from "../../Notification/Notification.jsx";
import { getNotifications } from "../../../api/notification.jsx";
import { useNotificationTrigger } from "../../Notification/UtilNotification.jsx";
import { FiUser, FiBriefcase, FiUserCheck, FiClipboard, FiMoreVertical } from "react-icons/fi";
import ReinscriptionButton from "../Buttons/ReinscriptionButton.jsx";

const ROLES_AVEC_NOTIFICATIONS = ["GESTIONNAIRE", "ETUDIANT", "EMPLOYEUR", "PROFESSEUR"];

const ROLE_ICONS = {
    GESTIONNAIRE: FiClipboard,
    ETUDIANT: FiUser,
    EMPLOYEUR: FiBriefcase,
    PROFESSEUR: FiUserCheck
};

function Header({ sidebarOpen, user }) {
    const { t } = useTranslation();
    const { pathname } = useLocation();
    const [notifications, setNotifications] = useState([]);

    const rawRole = typeof user?.role === "object" ? user?.role?.name : user?.role;
    const role = rawRole?.toString().replace("ROLE_", "");

    const RoleIcon = ROLE_ICONS[role] || FiUser;
    const fetchKey = useNotificationTrigger((state) => state.refreshKey);
    const roleLabel = role ? t(`roles.${role.toLowerCase()}`, { defaultValue: role }) : "";
    const fullName = `${user?.firstName || ""} ${user?.lastName || ""}`.trim();

    const peutRecevoir = Boolean(user?.isLoggedIn) && ROLES_AVEC_NOTIFICATIONS.includes(role);

    useEffect(() => {
        let actif = true;

        if (!peutRecevoir) {
            setNotifications([]);
            return;
        }

        getNotifications(user.id)
            .then((data) => {
                if (actif) setNotifications((data ?? []).filter((n) => !n.estLu));
            })
            .catch(() => {
                if (actif) setNotifications([]);
            });

        return () => {
            actif = false;
        };
    }, [peutRecevoir, user?.id, pathname, fetchKey]);

    const handleMarkAsRead = (id) => {
        setNotifications((prev) => prev.filter((n) => n.id !== id));
    };

    const tooltipText = sidebarOpen ? t("sidebar.collapse") : t("sidebar.expand");

    return (
        <nav className="navbar w-full bg-gradient-to-r from-[#043462] via-[#2F4A7A] to-[#6C4CCF] justify-between px-2 sm:px-3">

            <div className="navbar-start w-auto flex items-center gap-1.5 sm:gap-3">
                <label
                    htmlFor="my-drawer-4"
                    aria-label={tooltipText}
                    data-tip={tooltipText}
                    className="btn btn-square btn-ghost drawer-button tooltip tooltip-bottom hover:bg-[#00CCCB] hover:text-white text-white shrink-0"
                >
                    <SideBarIcon />
                </label>

                <div className="text-[#00CCCB] font-semibold flex items-center gap-2 shrink-0">
                    <img src={logo} alt="logo" className="h-8 w-8" />
                    <span className="hidden sm:inline">IKey</span>
                </div>

                {user?.isLoggedIn && (
                    <>
                        <div className="h-8 w-px bg-white/25 hidden sm:block shrink-0" />

                        <div
                            className="flex items-center gap-2 tooltip tooltip-bottom shrink-0"
                            data-tip={`${fullName} (${roleLabel})`}
                        >
                            <div className="size-9 rounded-lg bg-white/15 flex items-center justify-center shrink-0">
                                <RoleIcon className="text-white text-lg" />
                            </div>

                            <div className="hidden xl:flex flex-col leading-tight text-left">
                                <span className="text-sm font-semibold text-white truncate max-w-[130px]">
                                    {fullName}
                                </span>
                                <span className="text-xs text-white/70">
                                    {roleLabel}
                                </span>
                            </div>
                        </div>
                    </>
                )}

                <div className="h-8 w-px bg-white/25 hidden sm:block shrink-0" />

                <div className="shrink-0">
                    <SessionSwitch />
                </div>
            </div>

            <div className="navbar-end w-auto flex items-center gap-1.5 sm:gap-2.5 text-slate-800">
                <div className="hidden lg:block text-white">
                    <ReinscriptionButton user={user} />
                </div>

                {peutRecevoir && (
                    <Notification
                        notifications={notifications}
                        onMarkAsRead={handleMarkAsRead}
                    />
                )}

                <div className="hidden lg:flex items-center gap-2">
                    <ThemeToggle />
                    <LanguageSwitch />
                </div>

                <div className="dropdown dropdown-end lg:hidden">
                    <div tabIndex={0} role="button" className="btn btn-ghost btn-sm btn-square text-white">
                        <FiMoreVertical className="text-lg" />
                    </div>
                    <ul tabIndex={0} className="dropdown-content menu mt-2 w-48 rounded-2xl bg-white p-2 text-slate-800 shadow-xl border border-slate-100 z-50">
                        <li className="menu-title text-[10px] uppercase text-slate-400 px-3 py-1">
                            {t("header.actions", { defaultValue: "Actions" })}
                        </li>
                        <div className="px-2 py-1.5">
                            <ReinscriptionButton user={user} />
                        </div>

                        <li className="menu-title text-[10px] uppercase text-slate-400 px-3 py-1 mt-1">
                            {t("header.preferences", { defaultValue: "Préférences" })}
                        </li>
                        <div className="flex items-center justify-between px-3 py-2">
                            <span className="text-xs font-medium">
                                {t("header.theme", { defaultValue: "Thème" })}
                            </span>
                            <ThemeToggle />
                        </div>
                        <div className="flex items-center justify-between px-3 py-2 border-t border-slate-100">
                            <span className="text-xs font-medium">
                                {t("header.language", { defaultValue: "Langue" })}
                            </span>
                            <LanguageSwitch />
                        </div>
                    </ul>
                </div>
            </div>
        </nav>
    );
}

export default Header;