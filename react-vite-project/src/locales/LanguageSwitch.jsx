import { useTranslation } from "react-i18next";

function LanguageSwitch() {
    const { t, i18n } = useTranslation();

    const currentLang = (i18n.resolvedLanguage || i18n.language)?.startsWith("en") ? "en" : "fr";

    return (
        <div role="group" aria-label={t("language.label")}
             className="inline-flex shrink-0 self-center items-center rounded-full bg-white p-0.5 text-xs font-semibold shadow-sm">
            {[{ code: "fr", label: "Français" }, { code: "en", label: "English" }].map(({ code, label }) => (
                <button
                    key={code}
                    type="button"
                    lang={code}
                    aria-label={label}
                    aria-pressed={currentLang === code}
                    onClick={() => {
                        if (currentLang !== code) i18n.changeLanguage(code);
                    }}
                    className={`h-7 min-w-9 rounded-full px-2 transition-colors cursor-pointer focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[#043462] ${
                        currentLang === code ? "bg-[#043462] text-white" : "text-[#043462] hover:bg-blue-50"
                    }`}
                >
                    {code.toUpperCase()}
                </button>
            ))}
        </div>
    );
}

export default LanguageSwitch;
