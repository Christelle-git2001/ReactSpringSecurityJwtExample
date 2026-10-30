import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { createInstance } from "i18next";
import { I18nextProvider, initReactI18next } from "react-i18next";
import LanguageSwitch from "../../locales/LanguageSwitch.jsx";

async function renderSwitcher(language = "fr") {
    const i18n = createInstance();
    await i18n.use(initReactI18next).init({
        lng: language,
        fallbackLng: "fr",
        resources: {
            fr: { translation: { language: { label: "Langue" } } },
            en: { translation: { language: { label: "Language" } } },
        },
    });
    render(<I18nextProvider i18n={i18n}><LanguageSwitch /></I18nextProvider>);
    return i18n;
}

it("permet de choisir EN puis FR et actualise la langue active", async () => {
    const i18n = await renderSwitcher();
    expect(screen.getByRole("group", { name: "Langue" })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Français" })).toHaveAttribute("aria-pressed", "true");

    fireEvent.click(screen.getByRole("button", { name: "English" }));
    expect(i18n.language).toBe("en");
    expect(screen.getByRole("group", { name: "Language" })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "English" })).toHaveAttribute("aria-pressed", "true");
    expect(screen.getByRole("button", { name: "Français" })).toHaveAttribute("aria-pressed", "false");

    fireEvent.click(screen.getByRole("button", { name: "Français" }));
    expect(i18n.language).toBe("fr");
    expect(screen.getByRole("button", { name: "Français" })).toHaveAttribute("aria-pressed", "true");
});

it("reconnait une variante regionale comme anglais et ne bascule pas la langue active au clic", async () => {
    const i18n = await renderSwitcher("en-CA");
    expect(screen.getByRole("button", { name: "English" })).toHaveAttribute("aria-pressed", "true");
    fireEvent.click(screen.getByRole("button", { name: "English" }));
    expect(i18n.language).toBe("en-CA");
});