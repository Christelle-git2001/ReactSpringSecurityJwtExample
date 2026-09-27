import React from "react";
import { Link, Outlet } from "react-router-dom";
import LanguageSwitch from "../../locales/LanguageSwitch.jsx";
import { FiHome, FiLogOut, FiLogIn } from "react-icons/fi";


function SideBarLayout({ user }){

    const NAV_ITEMS = [
        {path: "/", label: "Accueil", roles: null, icon: <FiHome className="size-4 my-1.5" />},
        {path:user?.isLoggedIn ? "/logout" : "/login", label:user?.isLoggedIn ? "logout" : "login", roles: null, icon: user?.isLoggedIn ? <FiLogOut className="size-4 my-1.5" /> : <FiLogIn className="size-4 my-1.5" />},
    ];

    const role = user?.role?.toString() || user?.role;

    const getVisibleNavItems = () => {
        return NAV_ITEMS.filter((item) => {
            if (!item.roles) return true;
            return role && item.roles.includes(role);
        });
    };

    const visibleItems = getVisibleNavItems();

    return (
        <div className="drawer lg:drawer-open">
            <input id="my-drawer-4" type="checkbox" className="drawer-toggle inline" />
            <div className="drawer-content">
                {/* Navbar */}
                <nav className="navbar w-full">
                    <label htmlFor="my-drawer-4" aria-label="open sidebar" className="btn btn-square btn-ghost drawer-button hover:bg-[#00CCCB] hover:text-white">
                        {/* Sidebar toggle icon */}
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" strokeLinejoin="round" strokeLinecap="round" strokeWidth="2" fill="none" stroke="currentColor" className="my-1.5 inline-block size-4"><path d="M4 4m0 2a2 2 0 0 1 2 -2h12a2 2 0 0 1 2 2v12a2 2 0 0 1 -2 2h-12a2 2 0 0 1 -2 -2z"></path><path d="M9 4v16"></path><path d="M14 10l2 2l-2 2"></path></svg>
                    </label>
                    <div className="px-4">{user?.isLoggedIn && (<span>{user.firstName} {user.lastName}</span>)} </div>
                    <div className="fixed top-4 right-4 z-50">
                        <LanguageSwitch />
                    </div>
                </nav>
                {/* Page content here */}
                <div className="p-4 bg-base-200">
                    <Outlet></Outlet>
                </div>
            </div>

            <div className="drawer-side is-drawer-close:overflow-visible">
                <label htmlFor="my-drawer-4" aria-label="close sidebar" className="drawer-overlay"></label>
                <div className="flex min-h-full flex-col items-start is-drawer-close:w-14 is-drawer-open:w-64">
                    {/* Sidebar content here */}
                    <ul className="menu w-full grow">
                        {/* List item */}

                        {visibleItems.map(item => (
                            <li key={item.path}>
                                <Link to={item.path} className="is-drawer-close:tooltip is-drawer-close:tooltip-right hover:bg-[#00CCCB] hover:text-white" data-tip={item.label}>
                                    {item.icon}
                                    <span className="is-drawer-close:hidden">{item.label}</span>
                                </Link>
                            </li>
                        ))}


                        {/* List item */}
                        <li>
                            <button className="is-drawer-close:tooltip is-drawer-close:tooltip-right hover:bg-[#00CCCB] hover:text-white " data-tip="Settings">
                                {/* Settings icon */}
                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" strokeLinejoin="round" strokeLinecap="round" strokeWidth="2" fill="none" stroke="currentColor" className="my-1.5 inline-block size-4"><path d="M20 7h-9"></path><path d="M14 17H5"></path><circle cx="17" cy="17" r="3"></circle><circle cx="7" cy="7" r="3"></circle></svg>
                                <span className="is-drawer-close:hidden">Settings</span>
                            </button>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    )
}

export default SideBarLayout;