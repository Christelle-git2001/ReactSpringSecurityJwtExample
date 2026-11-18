import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { FaCheckCircle, FaTimes } from "react-icons/fa";
import Button from "../Design/Button.jsx";

const NIVEAUX = ["TOTALEMENT_EN_ACCORD", "PLUTOT_EN_ACCORD", "PLUTOT_EN_DESACCORD", "TOTALEMENT_EN_DESACCORD", "IMPOSSIBLE_DE_SE_PRONONCER"];
const DESACCORDS = ["PLUTOT_EN_DESACCORD", "TOTALEMENT_EN_DESACCORD"];
const CRITERES = [
    "tachesConformes", "mesuresAccueil", "tempsEncadrement", "hygieneSecurite", "climatTravail",
    "transportCommun", "salaireInteressant", "communicationSuperviseur", "equipementAdequat", "volumeTravail",
];
const CHAMPS_REQUIS = [
    "nomEntreprise", "personneContact", "adresse", "telephone", "ville", "codePostal",
    "nomStagiaire", "dateDebut", "dateFin", "numeroStage", "salaireHoraire",
    "nombreStagiaires", "memeStagiaire", "quartsVariables",
];
const HEURES = ["heuresMois1", "heuresMois2", "heuresMois3"];
const QUARTS_VIDES = [{ de: "", a: "" }, { de: "", a: "" }, { de: "", a: "" }];

const CHAMP = "mt-1 w-full rounded-lg border bg-white px-3 py-2 text-sm font-normal text-gray-800 focus:outline-none focus:border-[#043462] disabled:bg-gray-50 disabled:text-gray-700";

function etatInitial({ etudiant, employeur, offre, evaluationExistante }) {
    const base = {
        // 1. Entreprise
        nomEntreprise: employeur?.businessName ?? "",
        personneContact: employeur ? `${employeur.firstName ?? ""} ${employeur.lastName ?? ""}`.trim() : "",
        adresse: "",
        telephone: employeur?.phone ?? "",
        ville: employeur?.town ?? "",
        telecopieur: "",
        codePostal: "",
        // 2. Stagiaire
        nomStagiaire: `${etudiant?.firstName ?? ""} ${etudiant?.lastName ?? ""}`.trim(),
        dateDebut: offre?.startDate ?? "",
        dateFin: offre?.endDate ?? "",
        numeroStage: "",
        // 3. Évaluation
        reponses: {},
        heuresMois1: "", heuresMois2: "", heuresMois3: "",
        salaireHoraire: offre?.salary ?? "",
        // 4. Commentaires
        commentaires: "",
        // 5. Observations générales
        milieuPrivilegie: [],
        nombreStagiaires: "",
        memeStagiaire: "",
        quartsVariables: "",
        quarts: QUARTS_VIDES,
    };
    if (!evaluationExistante) return base;
    return {
        ...base,
        ...evaluationExistante,
        reponses: evaluationExistante.reponses ?? {},
        milieuPrivilegie: evaluationExistante.milieuPrivilegie ?? [],
        quarts: [...(evaluationExistante.quarts ?? []), ...QUARTS_VIDES].slice(0, 3),
    };
}

function Etoile() {
    return <span className="text-red-600" aria-hidden="true">*</span>;
}

function Champ({ label, erreur, requis = true, zoneTexte = false, ...props }) {
    const Balise = zoneTexte ? "textarea" : "input";
    return (
        <label className={`block text-sm font-semibold ${erreur ? "text-red-700" : "text-[#043462]"}`}>
            {label} {requis && <Etoile />}
            <Balise {...props} rows={zoneTexte ? 4 : undefined} aria-invalid={erreur}
                    className={`${CHAMP} ${erreur ? "border-red-400" : "border-gray-200"}`} />
        </label>
    );
}

function Section({ titre, children }) {
    return (
        <section className="rounded-xl border border-gray-200 p-4 bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">
            <h3 className="mb-3 text-base font-bold text-[#043462]">{titre}</h3>
            {children}
        </section>
    );
}

// Boutons radio (ou cases à cocher si multiple) présentés en pastilles
function Choix({ name, legende, options, value, onChange, erreur, disabled, multiple = false }) {
    const estChoisi = (v) => (multiple ? value.includes(v) : value === v);
    return (
        <fieldset className="flex flex-wrap gap-2" aria-invalid={erreur}>
            <legend className="sr-only">{legende}</legend>
            {options.map(o => (
                <label key={o.value}
                       className={`rounded-full border px-3 py-1 text-xs font-semibold transition-colors
                           ${disabled ? "cursor-default" : "cursor-pointer hover:border-[#043462]"}
                           ${estChoisi(o.value) ? "border-[#043462] bg-[#043462] text-white"
                           : `bg-white text-[#043462] ${erreur ? "border-red-400" : "border-[#043462]/25"} ${disabled ? "opacity-60" : ""}`}`}>
                    <input type={multiple ? "checkbox" : "radio"} className="sr-only" name={name} value={o.value}
                           checked={estChoisi(o.value)} disabled={disabled} onChange={() => onChange(o.value)} />
                    {o.label}
                </label>
            ))}
        </fieldset>
    );
}

function Question({ libelle, erreur, requis = true, children }) {
    return (
        <div className="flex flex-col gap-2 py-3">
            <p className={`text-sm font-medium ${erreur ? "text-red-700" : "text-gray-800"}`}>
                {libelle} {requis && <Etoile />}
            </p>
            {children}
        </div>
    );
}

/**
 * Évaluation du milieu de stage remplie par le professeur après sa visite.
 * - onSubmit(payload) : appel API, doit renvoyer l'évaluation enregistrée.
 * - evaluationExistante : si fournie, le formulaire s'ouvre en lecture seule.
 */
function EvaluationMilieuStage({ etudiant, employeur, offre, evaluationExistante, onSubmit, onClose, onSuccess }) {
    const { t } = useTranslation();
    const lectureSeule = Boolean(evaluationExistante);

    const [form, setForm] = useState(() => etatInitial({ etudiant, employeur, offre, evaluationExistante }));
    const [erreurs, setErreurs] = useState({});
    const [envoi, setEnvoi] = useState(false);
    const [erreurApi, setErreurApi] = useState(false);
    const desactive = envoi || lectureSeule;

    const modifier = (nom, valeur) => {
        setForm(f => ({ ...f, [nom]: valeur }));
        setErreurs(e => ({ ...e, [nom]: false }));
    };

    const champ = (nom) => ({
        label: t(`evaluation.fields.${nom}`),
        value: form[nom] ?? "",
        erreur: Boolean(erreurs[nom]),
        disabled: desactive,
        onChange: (e) => modifier(nom, e.target.value),
    });

    const repondre = (critere, valeur) => {
        setForm(f => ({ ...f, reponses: { ...f.reponses, [critere]: valeur } }));
        setErreurs(e => ({ ...e, [critere]: false }));
    };

    const basculerMilieu = (valeur) => modifier("milieuPrivilegie",
        form.milieuPrivilegie.includes(valeur)
            ? form.milieuPrivilegie.filter(v => v !== valeur)
            : [...form.milieuPrivilegie, valeur]);

    const modifierQuart = (index, cle, valeur) => {
        setForm(f => ({ ...f, quarts: f.quarts.map((q, i) => (i === index ? { ...q, [cle]: valeur } : q)) }));
        setErreurs(e => ({ ...e, quarts: false }));
    };

    const options = (valeurs) => valeurs.map(v => ({ value: v, label: t(`evaluation.options.${v}`) }));
    const niveaux = NIVEAUX.map(n => ({ value: n, label: t(`evaluation.levels.${n}`) }));
    const aDesaccord = CRITERES.some(c => DESACCORDS.includes(form.reponses[c]));

    const valider = () => {
        const e = {};
        CHAMPS_REQUIS.forEach(n => { if (!String(form[n] ?? "").trim()) e[n] = true; });
        CRITERES.forEach(c => { if (!form.reponses[c]) e[c] = true; });
        HEURES.forEach(n => { if (form[n] === "" || Number(form[n]) < 0) e[n] = true; });
        if (form.dateDebut && form.dateFin && form.dateFin < form.dateDebut) e.dateFin = true;
        if (aDesaccord && !form.commentaires.trim()) e.commentaires = true;
        if (form.milieuPrivilegie.length === 0) e.milieuPrivilegie = true;
        if (form.quartsVariables === "OUI" && (!form.quarts[0].de || !form.quarts[0].a)) e.quarts = true;
        setErreurs(e);
        return Object.keys(e).length === 0;
    };

    const soumettre = async (event) => {
        event.preventDefault();
        if (lectureSeule || !valider()) return;

        setEnvoi(true);
        setErreurApi(false);
        try {
            const payload = {
                ...form,
                heuresMois1: Number(form.heuresMois1),
                heuresMois2: Number(form.heuresMois2),
                heuresMois3: Number(form.heuresMois3),
                salaireHoraire: Number(form.salaireHoraire),
                quarts: form.quartsVariables === "OUI" ? form.quarts.filter(q => q.de && q.a) : [],
            };
            const evaluation = await onSubmit(payload);
            // Si le backend ne renvoie rien, on garde ce qui a été envoyé pour pouvoir le consulter
            onSuccess?.(evaluation ?? { ...payload, dateEvaluation: new Date().toISOString() });
        } catch {
            setErreurApi(true);
        } finally {
            setEnvoi(false);
        }
    };

    const aDesErreurs = Object.values(erreurs).some(Boolean);
    const dateSoumission = evaluationExistante?.dateEvaluation?.split("T")[0];

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
             role="dialog" aria-modal="true" aria-labelledby="titre-evaluation-milieu">
            <form onSubmit={soumettre} noValidate
                  className="flex max-h-[90vh] w-full max-w-3xl flex-col overflow-hidden rounded-2xl bg-white shadow-xl">

                {/* En-tête */}
                <div className="flex items-start gap-3 border-b border-gray-200 px-6 py-4">
                    <div className="min-w-0">
                        <h2 id="titre-evaluation-milieu" className="text-xl font-bold text-[#043462]">{t("evaluation.title")}</h2>
                        <p className="text-sm text-gray-600">{form.nomStagiaire} — {form.nomEntreprise}</p>
                    </div>
                    <button type="button" onClick={onClose} disabled={envoi} aria-label={t("evaluation.close")}
                            className="ml-auto text-gray-400 hover:text-gray-700">
                        <FaTimes size={16} />
                    </button>
                </div>

                {/* Contenu défilant */}
                <div className="flex flex-1 flex-col gap-4 overflow-y-auto px-6 py-4">

                    {lectureSeule && (
                        <p className="flex items-center gap-2 rounded-xl border border-green-200 bg-green-100 px-4 py-3 text-sm font-semibold text-green-800">
                            <FaCheckCircle aria-hidden="true" />
                            {dateSoumission ? t("evaluation.submitted_on", { date: dateSoumission }) : t("evaluation.success")}
                        </p>
                    )}

                    {/* 1. Identification de l'entreprise */}
                    <Section titre={t("evaluation.sections.company")}>
                        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                            <Champ type="text" {...champ("nomEntreprise")} />
                            <Champ type="text" {...champ("personneContact")} />
                            <Champ type="text" {...champ("adresse")} />
                            <Champ type="tel" {...champ("telephone")} />
                            <Champ type="text" {...champ("ville")} />
                            <Champ type="tel" requis={false} {...champ("telecopieur")} />
                            <Champ type="text" {...champ("codePostal")} />
                        </div>
                    </Section>

                    {/* 2. Identification du stagiaire */}
                    <Section titre={t("evaluation.sections.intern")}>
                        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                            <div className="sm:col-span-2">
                                <Champ type="text" {...champ("nomStagiaire")} />
                            </div>
                            <Champ type="date" {...champ("dateDebut")} />
                            <Champ type="date" {...champ("dateFin")} />
                        </div>
                        <Question libelle={t("evaluation.questions.numeroStage")} erreur={erreurs.numeroStage}>
                            <Choix name="numeroStage" legende={t("evaluation.questions.numeroStage")}
                                   options={options(["STAGE_1", "STAGE_2"])} value={form.numeroStage}
                                   onChange={v => modifier("numeroStage", v)} erreur={erreurs.numeroStage} disabled={desactive} />
                        </Question>
                    </Section>

                    {/* 3. Évaluation */}
                    <Section titre={t("evaluation.sections.evaluation")}>
                        <ul className="divide-y divide-[#043462]/10">
                            {CRITERES.map(c => (
                                <li key={c}>
                                    <Question libelle={t(`evaluation.criteres.${c}`)} erreur={erreurs[c]}>
                                        <Choix name={c} legende={t(`evaluation.criteres.${c}`)} options={niveaux}
                                               value={form.reponses[c]} onChange={v => repondre(c, v)}
                                               erreur={erreurs[c]} disabled={desactive} />
                                        {c === "tempsEncadrement" && (
                                            <div className="mt-1 grid grid-cols-1 gap-3 sm:grid-cols-3">
                                                {HEURES.map(n => <Champ key={n} type="number" min="0" {...champ(n)} />)}
                                            </div>
                                        )}
                                        {c === "salaireInteressant" && (
                                            <div className="mt-1 sm:max-w-xs">
                                                <Champ type="number" min="0" step="0.01" {...champ("salaireHoraire")} />
                                            </div>
                                        )}
                                    </Question>
                                </li>
                            ))}
                        </ul>
                        <p className="mt-2 text-xs text-gray-500">{t("evaluation.disagree_hint")}</p>
                    </Section>

                    {/* 4. Commentaires */}
                    <Section titre={t("evaluation.sections.comments")}>
                        <Champ zoneTexte requis={aDesaccord} {...champ("commentaires")} />
                    </Section>

                    {/* 5. Observations générales */}
                    <Section titre={t("evaluation.sections.observations")}>
                        <div className="divide-y divide-[#043462]/10">
                            <Question libelle={t("evaluation.questions.milieuPrivilegie")} erreur={erreurs.milieuPrivilegie}>
                                <Choix multiple name="milieuPrivilegie" legende={t("evaluation.questions.milieuPrivilegie")}
                                       options={options(["PREMIER_STAGE", "DEUXIEME_STAGE"])} value={form.milieuPrivilegie}
                                       onChange={basculerMilieu} erreur={erreurs.milieuPrivilegie} disabled={desactive} />
                            </Question>

                            <Question libelle={t("evaluation.questions.nombreStagiaires")} erreur={erreurs.nombreStagiaires}>
                                <Choix name="nombreStagiaires" legende={t("evaluation.questions.nombreStagiaires")}
                                       options={options(["UN", "DEUX", "TROIS", "PLUS_DE_TROIS"])} value={form.nombreStagiaires}
                                       onChange={v => modifier("nombreStagiaires", v)} erreur={erreurs.nombreStagiaires} disabled={desactive} />
                            </Question>

                            <Question libelle={t("evaluation.questions.memeStagiaire")} erreur={erreurs.memeStagiaire}>
                                <Choix name="memeStagiaire" legende={t("evaluation.questions.memeStagiaire")}
                                       options={options(["OUI", "NON"])} value={form.memeStagiaire}
                                       onChange={v => modifier("memeStagiaire", v)} erreur={erreurs.memeStagiaire} disabled={desactive} />
                            </Question>

                            <Question libelle={t("evaluation.questions.quartsVariables")} erreur={erreurs.quartsVariables || erreurs.quarts}>
                                <Choix name="quartsVariables" legende={t("evaluation.questions.quartsVariables")}
                                       options={options(["OUI", "NON"])} value={form.quartsVariables}
                                       onChange={v => modifier("quartsVariables", v)} erreur={erreurs.quartsVariables} disabled={desactive} />
                                {form.quartsVariables === "OUI" && (
                                    <div className="mt-1 flex flex-col gap-2">
                                        {form.quarts.map((q, i) => (
                                            <div key={i} className="flex flex-wrap items-center gap-2 text-sm text-[#043462]">
                                                <span className="w-6 font-semibold">{t("evaluation.fields.de")}</span>
                                                <input type="time" value={q.de} disabled={desactive}
                                                       aria-label={`${t("evaluation.fields.de")} ${i + 1}`}
                                                       onChange={e => modifierQuart(i, "de", e.target.value)}
                                                       className={`${CHAMP} mt-0 w-auto ${i === 0 && erreurs.quarts ? "border-red-400" : "border-gray-200"}`} />
                                                <span className="w-6 text-center font-semibold">{t("evaluation.fields.a")}</span>
                                                <input type="time" value={q.a} disabled={desactive}
                                                       aria-label={`${t("evaluation.fields.a")} ${i + 1}`}
                                                       onChange={e => modifierQuart(i, "a", e.target.value)}
                                                       className={`${CHAMP} mt-0 w-auto ${i === 0 && erreurs.quarts ? "border-red-400" : "border-gray-200"}`} />
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </Question>
                        </div>
                    </Section>
                </div>

                {/* Pied */}
                <div className="border-t border-gray-200 px-6 py-4">
                    {aDesErreurs && <p role="alert" className="mb-2 text-sm text-red-700">{t("evaluation.required")}</p>}
                    {erreurApi && <p role="alert" className="mb-2 text-sm text-red-700">{t("error.generic")}</p>}
                    <div className="flex flex-wrap items-center justify-between gap-3">
                        {!lectureSeule && <p className="text-xs text-gray-500">{t("evaluation.confirm_description")}</p>}
                        <div className="ml-auto flex gap-3">
                            <Button variant="secondary" type="button" onClick={onClose} disabled={envoi}>
                                {lectureSeule ? t("evaluation.close") : t("actions.cancel")}
                            </Button>
                            {!lectureSeule && (
                                <Button variant="success" type="submit" disabled={envoi}>
                                    {t("evaluation.submit")}
                                </Button>
                            )}
                        </div>
                    </div>
                </div>
            </form>
        </div>
    );
}

export default EvaluationMilieuStage;