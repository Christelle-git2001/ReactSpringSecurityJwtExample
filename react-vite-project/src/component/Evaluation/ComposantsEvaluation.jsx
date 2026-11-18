import React from "react";

const CHAMP = "mt-1 w-full rounded-lg border bg-white px-3 py-2 text-sm font-normal text-gray-800 focus:outline-none focus:border-[#043462]";

export function Etoile() {
    return <span className="text-red-600" aria-hidden="true">*</span>;
}

export function ChampEvaluation({ label, erreur, requis = true, zoneTexte = false, ...props }) {
    const Balise = zoneTexte ? "textarea" : "input";
    return (
        <label className={`block text-sm font-semibold ${erreur ? "text-red-700" : "text-[#043462]"}`}>
            {label} {requis && <Etoile />}
            <Balise {...props} rows={zoneTexte ? 3 : undefined} aria-invalid={erreur}
                    className={`${CHAMP} ${erreur ? "border-red-400" : "border-gray-200"}`} />
        </label>
    );
}

export function SectionEvaluation({ titre, sousTitre, children }) {
    return (
        <section className="rounded-xl border border-gray-200 p-4 bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">
            <h3 className="mb-1 text-base font-bold text-[#043462]">{titre}</h3>
            {sousTitre && <p className="mb-3 text-sm text-gray-600">{sousTitre}</p>}
            {children}
        </section>
    );
}

export function ChoixEvaluation({ name, legende, options, value, onChange, erreur, disabled }) {
    return (
        <fieldset className="flex flex-wrap gap-2" aria-invalid={erreur}>
            <legend className="sr-only">{legende}</legend>
            {options.map(option => (
                <label key={option.value}
                       className={`cursor-pointer rounded-full border px-3 py-1 text-xs font-semibold transition-colors
                           ${value === option.value ? "border-[#043462] bg-[#043462] text-white"
                           : `bg-white text-[#043462] hover:border-[#043462] ${erreur ? "border-red-400" : "border-[#043462]/25"}`}`}>
                    <input type="radio" className="sr-only" name={name} value={option.value}
                           checked={value === option.value} disabled={disabled}
                           onChange={() => onChange(option.value)} />
                    {option.label}
                </label>
            ))}
        </fieldset>
    );
}

export function ChampChoixEvaluation({ label, ...props }) {
    return (
        <div>
            <p className={`mb-2 text-sm font-medium ${props.erreur ? "text-red-700" : "text-gray-800"}`}>
                {label} <Etoile />
            </p>
            <ChoixEvaluation legende={label} {...props} />
        </div>
    );
}