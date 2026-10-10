import React, { useState } from "react";
import { Link, Outlet } from "react-router-dom";
import { useTranslation } from "react-i18next";
import {FiHome, FiLogOut, FiLogIn, FiPlusCircle, FiUser, FiFileText, FiBriefcase} from "react-icons/fi";
import Header from "./Header.jsx";
import SideBarIcon from "../Icons/SideBarIcon.jsx";


function SideBarLayout({ user, openDrawer }) {

    const { t } = useTranslation();
    const [sidebarOpen, setSidebarOpen] = useState(false);

    const rawRole = typeof user?.role === "object" ? user?.role?.name : user?.role;
    const role = rawRole?.toString().replace("ROLE_", "");

    const NAV_ITEMS = [
        { path: "/etudiant", label: t("nav.home"), roles:["ETUDIANT"], icon: <FiHome className="size-4 my-1.5" /> },
        { path: "/etudiant/account", label: t("nav.account"), roles: ["ETUDIANT"], icon: <FiUser className="size-4 my-1.5" /> },

        { path: "/employeur", label: t("nav.home"), roles:["EMPLOYEUR"], icon: <FiHome className="size-4 my-1.5" /> },
        { path: "/employeur/account", label: t("nav.account"), roles: ["EMPLOYEUR"], icon: <FiUser className="size-4 my-1.5" /> },
        { path: "/employeur/offres/nouvelle", label: t("nav.add_offer"), roles: ["EMPLOYEUR"], icon: <FiPlusCircle className="size-4 my-1.5" /> },
        { path: "/employeur/offers", label: t("nav.created_offers"), roles: ["EMPLOYEUR"], icon: <FiBriefcase className="size-4 my-1.5" /> },

        { path: "/gestionnaire", label: t("nav.home"), roles:["GESTIONNAIRE"], icon: <FiHome className="size-4 my-1.5" /> },
        //{ path: `/${role?.toLowerCase()}/account`, label: t("nav.account"), roles: null, icon: <FiUser className="size-4 my-1.5" />},
        //{ path: `/${role?.toLowerCase()}/offers`, label: t("nav.view_offers"), roles: null, icon: <FiBriefcase className="size-4 my-1.5" />},
        {path: "/gestionnaire/cvs", label: "CVs", roles: ["GESTIONNAIRE"], icon: <FiFileText className="size-4 my-1.5" />},
        { path: user?.isLoggedIn ? "/logout" : "/login",
            label: user?.isLoggedIn ? t("nav.logout") : t("nav.login"),
            roles: null,
            icon: user?.isLoggedIn ? <FiLogOut className="size-4 my-1.5" /> : <FiLogIn className="size-4 my-1.5" />
        },
    ];

    const visibleItems = NAV_ITEMS.filter(item => !item.roles || item.roles.includes(role));

    return (
        <div className="drawer lg:drawer-open">
            <input
                id="my-drawer-4"
                type="checkbox"
                className="drawer-toggle inline"
                checked={sidebarOpen}
                onChange={(event) => setSidebarOpen(event.target.checked)}
            />

            <div className="drawer-content">
                <Header user={user} sidebarOpen={sidebarOpen} />

                <div className="min-h-screen ...">
                    <Outlet context={{ openDrawer }} />
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
                        {visibleItems.map(item => (
                            <li key={item.path}>
                                <Link to={item.path}
                                      className="is-drawer-close:tooltip is-drawer-close:tooltip-right hover:bg-[#00CCCB] hover:text-white"
                                      data-tip={item.label}>
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
