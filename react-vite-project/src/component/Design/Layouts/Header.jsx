import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import LanguageSwitch from "../../../locales/LanguageSwitch.jsx";
import ThemeToggle from "../ThemeToggle.jsx";
import SideBarIcon from "../Icons/SideBarIcon.jsx";
import logo from "../../../assets/logo.png";
import Notification from "../../Notification/Notification.jsx";
import { getNotifications } from "../../../api/notification.jsx";

const ROLES_AVEC_NOTIFICATIONS = ["GESTIONNAIRE", "ETUDIANT", "EMPLOYEUR"];

function Header({ sidebarOpen, user }) {
    const { t } = useTranslation();
    const { pathname } = useLocation();
    const [notifications, setNotifications] = useState([]);

    const rawRole = typeof user?.role === "object" ? user?.role?.name : user?.role;
    const role = rawRole?.toString().replace("ROLE_", "");
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
    }, [peutRecevoir, user?.id, pathname]);

    const handleMarkAsRead = (id) => {
        setNotifications((prev) => prev.filter((n) => n.id !== id));
    };

    const tooltipText = sidebarOpen
        ? t("sidebar.collapse")
        : t("sidebar.expand");

    return (
        <nav className="navbar w-full bg-gradient-to-r from-[#043462] via-[#2F4A7A] to-[#6C4CCF]">
            <label
                htmlFor="my-drawer-4"
                aria-label={tooltipText}
                data-tip={tooltipText}
                className="btn btn-square btn-ghost drawer-button tooltip tooltip-bottom hover:bg-[#00CCCB] hover:text-white text-white"
            >
                <SideBarIcon />
            </label>

            <div className="px-4 text-[#00CCCB] font-semibold flex items-center gap-2">
                <img src={logo} alt="logo" className="h-8 w-8" />
                <span>IKey</span>
            </div>

            <div className="fixed top-4 right-4 z-50">
                <div className="flex flex-wrap items-center gap-3 sm:gap-6">
                    {peutRecevoir && (
                        <Notification
                            notifications={notifications}
                            onMarkAsRead={handleMarkAsRead}
                        />
                    )}
                    <ThemeToggle />
                    <LanguageSwitch />
                </div>
            </div>
        </nav>
    );
}

export default Header;