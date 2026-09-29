import { useTranslation } from "react-i18next";

function LanguageSwitch() {
    const { i18n } = useTranslation();

    const currentLang = i18n.language?.startsWith("en") ? "en" : "fr";

    const toggleLanguage = () => {
        const nextLang = currentLang === "fr" ? "en" : "fr";
        i18n.changeLanguage(nextLang);
    };

    return (
        <label className="flex items-center gap-2 cursor-pointer select-none">
            <span className={`text-xs font-bold ${currentLang === "fr" ? "text-black" : "text-gray-400"}`}>
                FR
            </span>
            <input
                type="checkbox"
                checked={currentLang === "en"}
                onChange={toggleLanguage}
                className="toggle border-black bg-white checked:border-black checked:bg-black checked:text-white"
            />
            <span className={`text-xs font-bold ${currentLang === "en" ? "text-black" : "text-gray-400"}`}>
                EN
            </span>
        </label>
    );
}

export default LanguageSwitch;