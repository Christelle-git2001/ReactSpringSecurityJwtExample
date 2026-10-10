import LanguageSwitch from "../../../locales/LanguageSwitch.jsx";
import ThemeToggle from "../ThemeToggle.jsx";
import SideBarIcon from "../Icons/SideBarIcon.jsx";
import { useTranslation } from "react-i18next";
import logo from "../../../assets/logo.png";

function Header({ user, sidebarOpen }) {
    const { t } = useTranslation();

    const tooltipText = sidebarOpen
        ? t("actions.collapse")
        : t("actions.expand");

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
                <div className="flex flex-wrap gap-6">
                    <ThemeToggle />
                    <LanguageSwitch />
                </div>
            </div>
        </nav>
    );
}

export default Header;