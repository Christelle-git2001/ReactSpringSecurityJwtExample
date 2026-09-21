import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import fr from "./locales/fr/translation.json";
import en from "./locales/en/translation.json";

const resources = {
    fr: {
        translation: fr
    },
    en: {
        translation: en
    }
};

i18n
    .use(initReactI18next) // passes i18n down to react-i18next
    .init({
        resources,
        lng: "fr",
        interpolation: {
            escapeValue: false // react already safes from xss
        }
    });

export default i18n;