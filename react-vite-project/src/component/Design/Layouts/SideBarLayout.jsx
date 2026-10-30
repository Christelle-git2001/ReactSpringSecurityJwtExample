import React, { useEffect, useState } from "react";
import { Link, Outlet } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { FiHome, FiLogOut, FiLogIn, FiPlusCircle, FiFile, FiBell } from "react-icons/fi";
import Header from "./Header.jsx";
import SideBarIcon from "../Icons/SideBarIcon.jsx";
import Drawer from "./../Drawer.jsx";
import Notification from "../../Notification/Notification.jsx";
import { getNotifications } from "../../../api/notification.jsx";
import {PiStudentBold} from "react-icons/pi";

function SideBarLayout({ user }) {
    const { t } = useTranslation();
    const [sidebarOpen, setSidebarOpen] = useState(false);
    const [drawerContent, setDrawerContent] = useState(null);
    const [isDrawerOpen, setIsDrawerOpen] = useState(false);
    const [drawerTitle, setDrawerTitle] = useState("drawer.details");

    const openDrawer = (content, title = "drawer.details") => {
        setDrawerContent(content);
        setDrawerTitle(title);
        setIsDrawerOpen(true);
    };

    const closeDrawer = () => {
        setIsDrawerOpen(false);
        setDrawerContent(null);
        setDrawerTitle(null)
    };

    const rawRole = typeof user?.role === "object" ? user?.role?.name : user?.role;
    const role = rawRole?.toString().replace("ROLE_", "");

    const [nombreNotifications, setNombreNotifications] = useState(0);

    useEffect(() => {
        let actif = true;

        if (!user?.isLoggedIn || !["GESTIONNAIRE", "ETUDIANT", "EMPLOYEUR"].includes(role)) {
            setNombreNotifications(0);
            return;
        }

        getNotifications(user.id)
            .then(data => {
                if (actif) {
                    setNombreNotifications(
                        (data ?? []).filter(notification => !notification.estLu).length
                    );
                }
            })
            .catch(() => {
                if (actif) setNombreNotifications(0);
            });

        return () => {
            actif = false;
        };
    }, [user?.id, user?.isLoggedIn, role, isDrawerOpen]);

    const NAV_ITEMS = [
        { path: "/etudiant", label: t("nav.home"), roles: ["ETUDIANT"], icon: <FiHome className="size-4 my-1.5" /> },
        { path: "/etudiant/cv#televerser", label: t("nav.cv"), roles: ["ETUDIANT"], icon: <FiFile className="size-4 my-1.5" /> },
        { path: "/employeur", label: t("nav.home"), roles: ["EMPLOYEUR"], icon: <FiHome className="size-4 my-1.5" /> },
        { path: "/employeur/offres/nouvelle", label: t("nav.add_offer"), roles: ["EMPLOYEUR"], icon: <FiPlusCircle className="size-4 my-1.5" /> },
        { path: "/gestionnaire", label: t("nav.home"), roles: ["GESTIONNAIRE"], icon: <FiHome className="size-4 my-1.5" /> },
        { path: "/gestionnaire/offres/nouvelle-ciblee", label: t("nav.add_offer"), roles: ["GESTIONNAIRE"], icon: <FiPlusCircle className="size-4 my-1.5" /> },
        { path: "/gestionnaire/attribution",label: t("nav.assignation"), roles: ["GESTIONNAIRE"], icon: <PiStudentBold />},
        { path: "/professeur", label: t("nav.home"), roles: ["PROFESSEUR"], icon: <FiHome className="size-4 my-1.5" /> },
        {
            path: user?.isLoggedIn ? "/logout" : "/login",
            label: user?.isLoggedIn ? t("nav.logout") : t("nav.login"),
            roles: null,
            icon: user?.isLoggedIn ? <FiLogOut className="size-4 my-1.5" /> : <FiLogIn className="size-4 my-1.5" />
        },
    ];

    const visibleItems = NAV_ITEMS.filter(item => !item.roles || item.roles.includes(role));

    return (
        <div className="drawer lg:drawer-open h-screen overflow-hidden">
            <input
                id="my-drawer-4"
                type="checkbox"
                className="drawer-toggle inline"
                checked={sidebarOpen}
                onChange={(event) => setSidebarOpen(event.target.checked)}
            />

            <div className="drawer-content flex flex-col h-screen">
                <Header user={user} sidebarOpen={sidebarOpen} />

                <div className="flex-1 flex overflow-hidden p-4 bg-[radial-gradient(circle_at_top,_#f0ddff11,_#d9b8ff22_70%)] flex w-full">
                    <div className="flex-1 overflow-y-auto scrollbar-thin scrollbar-thumb-gray-300">
                        <Outlet context={{ openDrawer, closeDrawer }} />
                    </div>
                    <Drawer isOpen={isDrawerOpen} onClose={closeDrawer} content={drawerContent} title={drawerTitle}/>
                </div>
            </div>
            <div className="drawer-side is-drawer-close:overflow-visible bg-[#043462] text-white">
                <label htmlFor="my-drawer-4" className="drawer-overlay"></label>

                {sidebarOpen && (
                    <label
                        htmlFor="my-drawer-4"
                        aria-label={t("sidebar.collapse")}
                        data-tip={t("sidebar.collapse")}
                        className="btn btn-square btn-ghost fixed bottom-6 right-6 z-[60] lg:hidden tooltip tooltip-left bg-[#043462] text-white hover:bg-[#00CCCB] hover:text-white"
                    >
                        <SideBarIcon />
                    </label>
                )}

                <div className="flex min-h-full flex-col items-start is-drawer-close:w-14 is-drawer-open:w-64">
                    <ul className="menu w-full grow">

                        {user?.isLoggedIn && ["GESTIONNAIRE", "ETUDIANT", "EMPLOYEUR"].includes(role) && (
                            <li>
                                <button
                                    type="button"
                                    className="is-drawer-close:tooltip is-drawer-close:tooltip-right hover:bg-[#00CCCB] hover:text-white"
                                    data-tip={t("notifications.title")}
                                    aria-label={t("notifications.title")}
                                    onClick={() => {
                                        if (drawerTitle === "notifications.title") {
                                            closeDrawer();
                                        } else {
                                            openDrawer(<Notification userId={user.id} onClose={closeDrawer}/>, "notifications.title");
                                        }
                                        setSidebarOpen(false);
                                    }}
                                >
                                    <span className="relative inline-flex">
                                        <FiBell className="size-4 my-1.5" />
                                        {nombreNotifications > 0 && (
                                            <span className="absolute -right-2 -top-1 rounded-full bg-red-600 px-1 text-[10px] font-bold text-white">
                                                {nombreNotifications}
                                            </span>
                                        )}
                                    </span>
                                    <span className="is-drawer-close:hidden">
                                    {t("notifications.title")}
                                </span>
                                </button>
                            </li>
                        )}

                        {visibleItems.map(item => (
                            <li key={item.path}>
                                <Link
                                    to={item.path}
                                    className="is-drawer-close:tooltip is-drawer-close:tooltip-right hover:bg-[#00CCCB] hover:text-white"
                                    data-tip={item.label}
                                    onClick={() => setSidebarOpen(false)}
                                >
                                    {item.icon}
                                    <span className="is-drawer-close:hidden">{item.label}</span>
                                </Link>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
}

export default SideBarLayout;
