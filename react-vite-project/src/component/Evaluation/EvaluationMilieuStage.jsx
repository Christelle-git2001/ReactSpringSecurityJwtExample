import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { FaCheckCircle, FaTimes } from "react-icons/fa";
import Button from "../Design/Button.jsx";
import { CRITERES, CHAMPS_SUPERVISEUR, HEURES, formulaireVide } from "../../utils/evaluationMilieuMapper.js";

const NIVEAUX = ["TOTALEMENT_EN_ACCORD", "PLUTOT_EN_ACCORD", "PLUTOT_EN_DESACCORD", "TOTALEMENT_EN_DESACCORD", "IMPOSSIBLE_DE_SE_PRONONCER"];
const DESACCORDS = ["PLUTOT_EN_DESACCORD", "TOTALEMENT_EN_DESACCORD"];
const SUPERVISEUR_REQUIS = ["nom", "fonction"];

const CHAMP = "mt-1 w-full rounded-lg border bg-white px-3 py-2 text-sm font-normal text-gray-800 focus:outline-none focus:border-[#043462] disabled:bg-gray-50 disabled:text-gray-700";

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

function Info({ label, value }) {
    return (
        <div className="min-w-0">
            <p className="text-xs font-semibold uppercase tracking-wide text-gray-400">{label}</p>
            <p className="mt-0.5 text-sm font-medium text-gray-800 break-words">{value || "—"}</p>
        </div>
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

function Choix({ name, legende, options, value, onChange, erreur, disabled }) {
    return (
        <fieldset className="flex flex-wrap gap-2" aria-invalid={erreur}>
            <legend className="sr-only">{legende}</legend>
            {options.map(o => (
                <label key={o.value}
                       className={`rounded-full border px-3 py-1 text-xs font-semibold transition-colors
                           ${disabled ? "cursor-default" : "cursor-pointer hover:border-[#043462]"}
                           ${value === o.value ? "border-[#043462] bg-[#043462] text-white"
                           : `bg-white text-[#043462] ${erreur ? "border-red-400" : "border-[#043462]/25"} ${disabled ? "opacity-60" : ""}`}`}>
                    <input type="radio" className="sr-only" name={name} value={o.value}
                           checked={value === o.value} disabled={disabled} onChange={() => onChange(o.value)} />
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


function EvaluationMilieuStage({ etudiant, employeur, offre, evaluationExistante, onSubmit, onClose, onSuccess }) {
    const { t } = useTranslation();
    const lectureSeule = Boolean(evaluationExistante);

    const [form, setForm] = useState(() => evaluationExistante ?? formulaireVide());
    const [erreurs, setErreurs] = useState({});
    const [envoi, setEnvoi] = useState(false);
    const [erreurApi, setErreurApi] = useState(false);
    const desactive = envoi || lectureSeule;

    const modifier = (nom, valeur) => {
        setForm(f => ({ ...f, [nom]: valeur }));
        setErreurs(e => ({ ...e, [nom]: false }));
    };

    const champ = (nom) => ({
        label: t(`evaluation_milieu.fields.${nom}`),
        value: form[nom] ?? "",
        erreur: Boolean(erreurs[nom]),
        disabled: desactive,
        onChange: (e) => modifier(nom, e.target.value),
    });

    const champSuperviseur = (nom) => ({
        label: t(`evaluation_milieu.supervisor.${nom}`),
        requis: SUPERVISEUR_REQUIS.includes(nom),
        value: form.superviseur[nom] ?? "",
        erreur: Boolean(erreurs[`superviseur.${nom}`]),
        disabled: desactive,
        onChange: (e) => {
            const valeur = e.target.value;
            setForm(f => ({ ...f, superviseur: { ...f.superviseur, [nom]: valeur } }));
            setErreurs(er => ({ ...er, [`superviseur.${nom}`]: false }));
        },
    });

    const repondre = (critere, valeur) => {
        setForm(f => ({ ...f, reponses: { ...f.reponses, [critere]: valeur } }));
        setErreurs(e => ({ ...e, [critere]: false }));
    };

    const modifierQuart = (index, cle, valeur) => {
        setForm(f => ({ ...f, quarts: f.quarts.map((q, i) => (i === index ? { ...q, [cle]: valeur } : q)) }));
        setErreurs(e => ({ ...e, quarts: false }));
    };

    const options = (paires) => paires.map(([value, cle]) => ({ value, label: t(`evaluation_milieu.options.${cle}`) }));
    const niveaux = NIVEAUX.map(n => ({ value: n, label: t(`evaluation_milieu.levels.${n}`) }));
    const ouiNon = options([["OUI", "OUI"], ["NON", "NON"]]);
    const aDesaccord = CRITERES.some(c => DESACCORDS.includes(form.reponses[c.id]));

    const valider = () => {
        const e = {};
        SUPERVISEUR_REQUIS.forEach(n => { if (!String(form.superviseur[n] ?? "").trim()) e[`superviseur.${n}`] = true; });
        CRITERES.forEach(c => { if (!form.reponses[c.id]) e[c.id] = true; });
        HEURES.forEach(n => { if (form[n] === "" || Number(form[n]) < 0) e[n] = true; });
        if (aDesaccord && !form.commentaires.trim()) e.commentaires = true;
        ["noStagePrivilegier", "nbStagiaireAccueil", "engagerMemeStagiaire", "quartsVariables"].forEach(n => { if (!form[n]) e[n] = true; });
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
            await onSubmit(form);
            onSuccess?.(form);
        } catch {
            setErreurApi(true);
        } finally {
            setEnvoi(false);
        }
    };

    const aDesErreurs = Object.values(erreurs).some(Boolean);
    const nomStagiaire = `${etudiant?.firstName ?? ""} ${etudiant?.lastName ?? ""}`.trim();
    const contact = employeur ? `${employeur.firstName ?? ""} ${employeur.lastName ?? ""}`.trim() : "";

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
             role="dialog" aria-modal="true" aria-labelledby="titre-evaluation-milieu">
            <form onSubmit={soumettre} noValidate
                  className="flex max-h-[90vh] w-full max-w-3xl flex-col overflow-hidden rounded-2xl bg-white shadow-xl">

                {/* En-tête */}
                <div className="flex items-start gap-3 border-b border-gray-200 px-6 py-4">
                    <div className="min-w-0">
                        <h2 id="titre-evaluation-milieu" className="text-xl font-bold text-[#043462]">{t("evaluation_milieu.title")}</h2>
                        <p className="text-sm text-gray-600">{nomStagiaire} — {employeur?.businessName}</p>
                    </div>
                    <button type="button" onClick={onClose} disabled={envoi} aria-label={t("evaluation_milieu.close")}
                            className="ml-auto text-gray-400 hover:text-gray-700">
                        <FaTimes size={16} />
                    </button>
                </div>

                {/* Contenu défilant */}
                <div className="flex flex-1 flex-col gap-4 overflow-y-auto px-6 py-4">

                    {lectureSeule && (
                        <p className="flex items-center gap-2 rounded-xl border border-green-200 bg-green-100 px-4 py-3 text-sm font-semibold text-green-800">
                            <FaCheckCircle aria-hidden="true" />
                            {t("evaluation_milieu.success")}
                        </p>
                    )}

                    <Section titre={t("evaluation_milieu.sections.internship_info")}>
                        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                            <Info label={t("evaluation_milieu.fields.nomEntreprise")} value={employeur?.businessName} />
                            <Info label={t("evaluation_milieu.fields.personneContact")} value={contact} />
                            <Info label={t("evaluation_milieu.fields.telephone")} value={employeur?.phone} />
                            <Info label={t("evaluation_milieu.fields.ville")} value={employeur?.town} />
                            <Info label={t("evaluation_milieu.fields.nomStagiaire")} value={nomStagiaire} />
                            <Info label={t("evaluation_milieu.fields.salaireHoraire")} value={offre?.salary} />
                            <Info label={t("evaluation_milieu.fields.dateDebut")} value={offre?.startDate} />
                            <Info label={t("evaluation_milieu.fields.dateFin")} value={offre?.endDate} />
                        </div>
                    </Section>

                    {/* 2. Superviseur en entreprise */}
                    <Section titre={t("evaluation_milieu.sections.supervisor")}>
                        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                            {CHAMPS_SUPERVISEUR.map(n => (
                                <Champ key={n} type={n === "email" ? "email" : n === "telephone" ? "tel" : "text"} {...champSuperviseur(n)} />
                            ))}
                        </div>
                    </Section>

                    {/* 3. Évaluation */}
                    <Section titre={t("evaluation_milieu.sections.evaluation")}>
                        <ul className="divide-y divide-[#043462]/10">
                            {CRITERES.map(c => (
                                <li key={c.id}>
                                    <Question libelle={t(`evaluation_milieu.criteres.${c.id}`)} erreur={erreurs[c.id]}>
                                        <Choix name={c.id} legende={t(`evaluation_milieu.criteres.${c.id}`)} options={niveaux}
                                               value={form.reponses[c.id]} onChange={v => repondre(c.id, v)}
                                               erreur={erreurs[c.id]} disabled={desactive} />
                                        {c.id === "tempsEncadrement" && (
                                            <div className="mt-1 grid grid-cols-1 gap-3 sm:grid-cols-3">
                                                {HEURES.map(n => <Champ key={n} type="number" min="0" {...champ(n)} />)}
                                            </div>
                                        )}
                                    </Question>
                                </li>
                            ))}
                        </ul>
                        <p className="mt-2 text-xs text-gray-500">{t("evaluation_milieu.disagree_hint")}</p>
                    </Section>

                    {/* 4. Commentaires */}
                    <Section titre={t("evaluation_milieu.sections.comments")}>
                        <Champ zoneTexte requis={aDesaccord} {...champ("commentaires")} />
                    </Section>

                    {/* 5. Observations générales */}
                    <Section titre={t("evaluation_milieu.sections.observations")}>
                        <div className="divide-y divide-[#043462]/10">
                            <Question libelle={t("evaluation_milieu.questions.milieuPrivilegie")} erreur={erreurs.noStagePrivilegier}>
                                <Choix name="noStagePrivilegier" legende={t("evaluation_milieu.questions.milieuPrivilegie")}
                                       options={options([["1", "PREMIER_STAGE"], ["2", "DEUXIEME_STAGE"]])}
                                       value={form.noStagePrivilegier} onChange={v => modifier("noStagePrivilegier", v)}
                                       erreur={erreurs.noStagePrivilegier} disabled={desactive} />
                            </Question>

                            <Question libelle={t("evaluation_milieu.questions.nombreStagiaires")} erreur={erreurs.nbStagiaireAccueil}>
                                <Choix name="nbStagiaireAccueil" legende={t("evaluation_milieu.questions.nombreStagiaires")}
                                       options={options([["1", "UN"], ["2", "DEUX"], ["3", "TROIS"], ["4", "PLUS_DE_TROIS"]])}
                                       value={form.nbStagiaireAccueil} onChange={v => modifier("nbStagiaireAccueil", v)}
                                       erreur={erreurs.nbStagiaireAccueil} disabled={desactive} />
                            </Question>

                            <Question libelle={t("evaluation_milieu.questions.memeStagiaire")} erreur={erreurs.engagerMemeStagiaire}>
                                <Choix name="engagerMemeStagiaire" legende={t("evaluation_milieu.questions.memeStagiaire")}
                                       options={ouiNon} value={form.engagerMemeStagiaire}
                                       onChange={v => modifier("engagerMemeStagiaire", v)}
                                       erreur={erreurs.engagerMemeStagiaire} disabled={desactive} />
                            </Question>

                            <Question libelle={t("evaluation_milieu.questions.quartsVariables")} erreur={erreurs.quartsVariables || erreurs.quarts}>
                                <Choix name="quartsVariables" legende={t("evaluation_milieu.questions.quartsVariables")}
                                       options={ouiNon} value={form.quartsVariables}
                                       onChange={v => modifier("quartsVariables", v)}
                                       erreur={erreurs.quartsVariables} disabled={desactive} />
                                {form.quartsVariables === "OUI" && (
                                    <div className="mt-1 flex flex-col gap-2">
                                        {form.quarts.map((q, i) => (
                                            <div key={i} className="flex flex-wrap items-center gap-2 text-sm text-[#043462]">
                                                <span className="w-6 font-semibold">{t("evaluation_milieu.fields.de")}</span>
                                                <input type="time" value={q.de} disabled={desactive}
                                                       aria-label={`${t("evaluation_milieu.fields.de")} ${i + 1}`}
                                                       onChange={e => modifierQuart(i, "de", e.target.value)}
                                                       className={`${CHAMP} mt-0 w-auto ${i === 0 && erreurs.quarts ? "border-red-400" : "border-gray-200"}`} />
                                                <span className="w-6 text-center font-semibold">{t("evaluation_milieu.fields.a")}</span>
                                                <input type="time" value={q.a} disabled={desactive}
                                                       aria-label={`${t("evaluation_milieu.fields.a")} ${i + 1}`}
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
                    {aDesErreurs && <p role="alert" className="mb-2 text-sm text-red-700">{t("evaluation_milieu.required")}</p>}
                    {erreurApi && <p role="alert" className="mb-2 text-sm text-red-700">{t("error.generic")}</p>}
                    <div className="flex flex-wrap items-center justify-between gap-3">
                        {!lectureSeule && <p className="text-xs text-gray-500">{t("evaluation_milieu.confirm_description")}</p>}
                        <div className="ml-auto flex gap-3">
                            <Button variant="secondary" type="button" onClick={onClose} disabled={envoi}>
                                {lectureSeule ? t("evaluation_milieu.close") : t("actions.cancel")}
                            </Button>
                            {!lectureSeule && (
                                <Button variant="success" type="submit" disabled={envoi}>
                                    {t("evaluation_milieu.submit")}
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