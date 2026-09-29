import React, { useState } from "react";
import { Link, Outlet } from "react-router-dom";
import LanguageSwitch from "../../locales/LanguageSwitch.jsx";
import { FiHome, FiLogOut, FiLogIn } from "react-icons/fi";
import { useTranslation } from "react-i18next";

function SideBarLayout({ user, openDrawer }) {

    const { t } = useTranslation();

    const rawRole = typeof user?.role === "object" ? user?.role?.name : user?.role;
    const role = rawRole?.toString().replace("ROLE_", "");

    const NAV_ITEMS = [
        { path: "/employeur", label: t("nav.home"), roles:["EMPLOYEUR"], icon: <FiHome className="size-4 my-1.5" /> },
        { path: "/etudiant", label: t("nav.home"), roles:["ETUDIANT"], icon: <FiHome className="size-4 my-1.5" /> },
        { path: user?.isLoggedIn ? "/logout" : "/login",
            label: user?.isLoggedIn ? t("nav.logout") : t("nav.login"),
            roles: null,
            icon: user?.isLoggedIn ? <FiLogOut className="size-4 my-1.5" /> : <FiLogIn className="size-4 my-1.5" />
        },
    ];

    const visibleItems = NAV_ITEMS.filter(item => !item.roles || item.roles.includes(role));

    return (
        <div className="drawer lg:drawer-open">
            <input id="my-drawer-4" type="checkbox" className="drawer-toggle inline" />

            <div className="drawer-content">
                <nav className="navbar w-full bg-gradient-to-r from-[#043462] via-[#2F4A7A] to-[#6C4CCF]">
                <label htmlFor="my-drawer-4" aria-label="open sidebar"
                           className="btn btn-square btn-ghost drawer-button hover:bg-[#00CCCB] hover:text-white text-white">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
                             strokeWidth="2" fill="none" stroke="currentColor"
                             className="my-1.5 inline-block size-4">
                            <path d="M4 4m0 2a2 2 0 0 1 2 -2h12a2 2 0 0 1 2 2v12a2 2 0 0 1 -2 2h-12a2 2 0 0 1 -2 -2z"></path>
                            <path d="M9 4v16"></path>
                            <path d="M14 10l2 2l-2 2"></path>
                        </svg>
                    </label>

                    <div className="px-4 text-[#00CCCB] font-semibold">
                        {user?.isLoggedIn && (<span>{user.firstName} {user.lastName}</span>)}
                    </div>

                    <div className="fixed top-4 right-4 z-50">
                        <LanguageSwitch />
                    </div>
                </nav>

                <div className="p-4 bg-base-200 flex flex-col items-center w-full">
                    <Outlet context={{ openDrawer }} />
                </div>
            </div>

            <div className="drawer-side is-drawer-close:overflow-visible bg-[#043462] text-white">
                <label htmlFor="my-drawer-4" className="drawer-overlay"></label>

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
