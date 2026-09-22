import { useTranslation } from "react-i18next";

function LanguageSwitche() {
    const { i18n } = useTranslation();

    function changeLanguage(lang) {
        i18n.changeLanguage(lang);
    }

    return (
        <div className="flex gap-2">
            <button
                onClick={() => changeLanguage("fr")}
                className={`px-3 py-1 rounded ${i18n.language === "fr" ? "bg-green-500 text-white" : "bg-gray-500"}`}
            >
                FR
            </button>
            <button
                onClick={() => changeLanguage("en")}
                className={`px-3 py-1 rounded ${i18n.language === "en" ? "bg-green-500 text-white" : "bg-gray-500"}`}
            >
                EN
            </button>
        </div>
    )
}

export default LanguageSwitche;