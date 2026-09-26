import { useTranslation } from "react-i18next";

function LanguageSwitch() {
    const { i18n } = useTranslation();

    const currentLang = i18n.language?.startsWith("en") ? "en" : "fr";

    const toggleLanguage = () => {
        const nextLang = currentLang === "fr" ? "en" : "fr";
        i18n.changeLanguage(nextLang);
    };

    return (
        <button
            onClick={toggleLanguage}
            className="flex items-center gap-2 px-3 py-1.5 rounded-full border border-gray-300 bg-white text-gray-800 hover:bg-gray-50 transition-colors shadow-sm font-medium text-sm"
        >
            <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                strokeWidth={1.5}
                stroke="currentColor"
                className="w-5 h-5 text-gray-600"
            >
                <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M12 21a9 9 0 100-18 9 9 0 000 18zM3.6 9h16.8M3.6 15h16.8M12 3a15.3 15.3 0 014 9 15.3 15.3 0 01-4 9 15.3 15.3 0 01-4-9 15.3 15.3 0 014-9z"
                />
            </svg>

            <span>{currentLang === "fr" ? "FR" : "EN"}</span>
        </button>
    );
}

export default LanguageSwitch;