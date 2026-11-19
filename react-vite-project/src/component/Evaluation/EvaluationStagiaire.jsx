import React, { useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { FaTimes } from "react-icons/fa";
import { evaluerStagiaire } from "../../api/employeur.jsx";
import Button from "../Design/Button.jsx";
import GroupeCriteresEvaluation from "./GroupeCriteresEvaluation.jsx";
import {
    ChampChoixEvaluation,
    ChampEvaluation,
    ChoixEvaluation,
    Etoile,
    SectionEvaluation,
} from "./ComposantsEvaluation.jsx";

const GROUPES = [
    {
        dto: "evaluationProductiviteDTO",
        titre: "productivite",
        criteres: ["planificationScore", "comprehensionScore", "rythmeScore", "prioriteScore", "echeancierScore"],
    },
    {
        dto: "qualiteTravailDTO",
        titre: "qualiteTravail",
        criteres: ["completeTaskScore", "minutieScore", "checkBoxScore", "perfectionnementResearchScore", "analystScore"],
    },
    {
        dto: "qualiteSocialDTO",
        titre: "qualiteSociale",
        criteres: ["socialScore", "teamWorkScore", "adaptationScore", "critiqueAcceptationScore", "respectScore", "listenScore"],
    },
    {
        dto: "habiletePersonelleDTO",
        titre: "habiletesPersonnelles",
        criteres: ["motivationScore", "tellIdeaScore", "initiativeScore", "securityScore", "responsabiliteScore", "ponctuelScore"],
    },
];

const NIVEAUX = ["1", "2", "3", "4", "NA"];

function EvaluationStagiaireModal({ candidature, offer, onClose, onSuccess }) {
    const { t } = useTranslation();
    const [superviseur, setSuperviseur] = useState({ firstName: "", lastName: "", fonction: "", phone: "" });
    const [groupes, setGroupes] = useState(() => Object.fromEntries(
        GROUPES.map(groupe => [groupe.dto, {
            ...Object.fromEntries(groupe.criteres.map(critere => [critere, ""])),
            commentaire: "",
        }])
    ));
    const [appreciation, setAppreciation] = useState({
        attenteEmployeurScore: "",
        descriptionAppreciation: "",
        discussionAvecStagiaire: "",
        supervisionTime: "",
        renewExperience: "",
        experienceSuffisante: "",
    });
    const [erreurs, setErreurs] = useState({});
    const [envoi, setEnvoi] = useState(false);
    const [erreurApi, setErreurApi] = useState(false);
    const formulaireRef = useRef(null);

    const modifierSuperviseur = (champ, valeur) => {
        setSuperviseur(s => ({ ...s, [champ]: valeur }));
        setErreurs(e => ({ ...e, [champ]: false }));
    };

    const modifierGroupe = (dto, champ, valeur) => {
        setGroupes(g => ({ ...g, [dto]: { ...g[dto], [champ]: valeur } }));
        setErreurs(e => ({ ...e, [`${dto}.${champ}`]: false }));
    };

    const modifierAppreciation = (champ, valeur) => {
        setAppreciation(a => ({ ...a, [champ]: valeur }));
        setErreurs(e => ({ ...e, [champ]: false }));
    };

    const valider = () => {
        const nouvellesErreurs = {};
        ["firstName", "lastName", "fonction", "phone"].forEach(champ => {
            if (!superviseur[champ].trim()) nouvellesErreurs[champ] = true;
        });
        GROUPES.forEach(groupe => {
            groupe.criteres.forEach(critere => {
                if (!groupes[groupe.dto][critere]) nouvellesErreurs[`${groupe.dto}.${critere}`] = true;
            });
        });
        if (!appreciation.attenteEmployeurScore) nouvellesErreurs.attenteEmployeurScore = true;
        if (!appreciation.discussionAvecStagiaire) nouvellesErreurs.discussionAvecStagiaire = true;
        if (appreciation.supervisionTime === "" || Number(appreciation.supervisionTime) < 0) nouvellesErreurs.supervisionTime = true;
        if (!appreciation.renewExperience) nouvellesErreurs.renewExperience = true;
        if (!appreciation.experienceSuffisante.trim()) nouvellesErreurs.experienceSuffisante = true;

        setErreurs(nouvellesErreurs);
        return Object.keys(nouvellesErreurs).length === 0;
    };

    const soumettre = async event => {
        event.preventDefault();
        if (!valider()) {
            requestAnimationFrame(() => {
                const premierChampInvalide = formulaireRef.current?.querySelector('[aria-invalid="true"]');
                if (!premierChampInvalide) return;

                const estChoix = premierChampInvalide.matches("fieldset");
                const cible = estChoix
                    ? premierChampInvalide.querySelector("input")
                    : premierChampInvalide;
                const zoneDefilement = estChoix
                    ? premierChampInvalide.closest("li")
                    : premierChampInvalide.closest("label");

                cible?.focus({ preventScroll: true });
                (zoneDefilement ?? premierChampInvalide)
                    .scrollIntoView?.({ behavior: "smooth", block: "center" });
            });
            return;
        }

        setEnvoi(true);
        setErreurApi(false);
        try {
            const evaluation = await evaluerStagiaire({
                candidatureID: candidature.id,
                supervisueurStageDTO: superviseur,
                ...Object.fromEntries(GROUPES.map(groupe => [
                    groupe.dto,
                    {
                        ...groupes[groupe.dto],
                        ...Object.fromEntries(groupe.criteres.map(critere => [
                            critere,
                            groupes[groupe.dto][critere] === "NA" ? 0 : Number(groupes[groupe.dto][critere]),
                        ])),
                    },
                ])),
                appreciationGlobalDTO: {
                    ...appreciation,
                    attenteEmployeurScore: Number(appreciation.attenteEmployeurScore),
                    discussionAvecStagiaire: appreciation.discussionAvecStagiaire === "true",
                    supervisionTime: Number(appreciation.supervisionTime),
                },
            });
            onSuccess?.(evaluation);
        } catch {
            setErreurApi(true);
        } finally {
            setEnvoi(false);
        }
    };

    const optionsEchelle = NIVEAUX.map(value => ({
        value,
        label: t(`evaluation.levels.${value}`),
    }));
    const optionsOuiNon = [
        { value: "true", label: t("evaluation.options.yes") },
        { value: "false", label: t("evaluation.options.no") },
    ];
    const optionsAccueil = [
        { value: "OUI", label: t("evaluation.options.yes") },
        { value: "NON", label: t("evaluation.options.no") },
        { value: "PEUT_ETRE", label: t("evaluation.options.maybe") },
    ];

    const titreStagiaire = `${candidature?.etudiantFirstName ?? ""} ${candidature?.etudiantLastName ?? ""}`.trim();
    const titreOffre = candidature?.offreTitle || offer?.title;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
             role="dialog" aria-modal="true" aria-labelledby="titre-evaluation">
            <form ref={formulaireRef} onSubmit={soumettre} noValidate
                  className="flex max-h-[90vh] w-full max-w-3xl flex-col overflow-hidden rounded-2xl bg-white shadow-xl">
                <div className="flex items-start gap-3 border-b border-gray-200 px-6 py-4">
                    <div className="min-w-0">
                        <h2 id="titre-evaluation" className="text-xl font-bold text-[#043462]">{t("evaluation.title")}</h2>
                        <p className="text-sm text-gray-600">{titreStagiaire}{titreOffre ? ` — ${titreOffre}` : ""}</p>
                    </div>
                    <button type="button" onClick={onClose} disabled={envoi} aria-label={t("actions.cancel")}
                            className="ml-auto text-gray-400 hover:text-gray-700">
                        <FaTimes size={16} />
                    </button>
                </div>

                <div className="flex flex-1 flex-col gap-4 overflow-y-auto px-6 py-4">
                    <SectionEvaluation titre={t("evaluation.sections.supervisor")}>
                        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                            <ChampEvaluation label={t("evaluation.fields.supervisorFirstName")} value={superviseur.firstName}
                                   erreur={erreurs.firstName} disabled={envoi}
                                   onChange={e => modifierSuperviseur("firstName", e.target.value)} />
                            <ChampEvaluation label={t("evaluation.fields.supervisorLastName")} value={superviseur.lastName}
                                   erreur={erreurs.lastName} disabled={envoi}
                                   onChange={e => modifierSuperviseur("lastName", e.target.value)} />
                            <ChampEvaluation label={t("evaluation.fields.supervisorRole")} value={superviseur.fonction}
                                   erreur={erreurs.fonction} disabled={envoi}
                                   onChange={e => modifierSuperviseur("fonction", e.target.value)} />
                            <ChampEvaluation label={t("evaluation.fields.supervisorPhone")} type="tel" value={superviseur.phone}
                                   erreur={erreurs.phone} disabled={envoi}
                                   onChange={e => modifierSuperviseur("phone", e.target.value)} />
                        </div>
                    </SectionEvaluation>

                    {GROUPES.map(groupe => (
                        <GroupeCriteresEvaluation
                            key={groupe.dto}
                            groupe={groupe}
                            valeurs={groupes[groupe.dto]}
                            erreurs={erreurs}
                            options={optionsEchelle}
                            disabled={envoi}
                            onChange={modifierGroupe}
                        />
                    ))}

                    <SectionEvaluation titre={t("evaluation.sections.overall")}>
                        <div className="flex flex-col gap-4">
                            <ChampChoixEvaluation
                                label={t("evaluation.fields.overallRating")}
                                name="attenteEmployeurScore"
                                options={[5, 4, 3, 2, 1].map(score => ({
                                    value: String(score),
                                    label: t(`evaluation.overallLevels.${score}`),
                                }))}
                                value={appreciation.attenteEmployeurScore}
                                onChange={v => modifierAppreciation("attenteEmployeurScore", v)}
                                erreur={erreurs.attenteEmployeurScore}
                                disabled={envoi}
                            />
                            <ChampEvaluation label={t("evaluation.fields.overallComments")}
                                   value={appreciation.descriptionAppreciation} zoneTexte requis={false} disabled={envoi}
                                   onChange={e => modifierAppreciation("descriptionAppreciation", e.target.value)} />
                            <ChampChoixEvaluation
                                label={t("evaluation.fields.discussedWithIntern")}
                                name="discussionAvecStagiaire"
                                options={optionsOuiNon}
                                value={appreciation.discussionAvecStagiaire}
                                onChange={v => modifierAppreciation("discussionAvecStagiaire", v)}
                                erreur={erreurs.discussionAvecStagiaire}
                                disabled={envoi}
                            />
                            <ChampEvaluation label={t("evaluation.fields.supervisionHours")} type="number" min="0" step="0.5"
                                   value={appreciation.supervisionTime} erreur={erreurs.supervisionTime} disabled={envoi}
                                   onChange={e => modifierAppreciation("supervisionTime", e.target.value)} />
                            <ChampChoixEvaluation
                                label={t("evaluation.fields.welcomeAgain")}
                                name="renewExperience"
                                options={optionsAccueil}
                                value={appreciation.renewExperience}
                                onChange={v => modifierAppreciation("renewExperience", v)}
                                erreur={erreurs.renewExperience}
                                disabled={envoi}
                            />
                            <ChampEvaluation label={t("evaluation.fields.trainingSufficient")}
                                   value={appreciation.experienceSuffisante} zoneTexte
                                   erreur={erreurs.experienceSuffisante} disabled={envoi}
                                   onChange={e => modifierAppreciation("experienceSuffisante", e.target.value)} />
                        </div>
                    </SectionEvaluation>
                </div>

                <div className="border-t border-gray-200 px-6 py-4">
                    {Object.values(erreurs).some(Boolean) &&
                        <p role="alert" className="mb-2 text-sm text-red-700">{t("evaluation.required")}</p>}
                    {erreurApi && <p role="alert" className="mb-2 text-sm text-red-700">{t("error.generic")}</p>}
                    <div className="flex flex-wrap items-center justify-between gap-3">
                        <p className="text-xs text-gray-500">{t("evaluation.confirm_description")}</p>
                        <div className="ml-auto flex gap-3">
                            <Button variant="secondary" type="button" onClick={onClose} disabled={envoi}>
                                {t("actions.cancel")}
                            </Button>
                            <Button variant="success" type="submit" disabled={envoi}>
                                {t("evaluation.submit")}
                            </Button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    );
}

export default EvaluationStagiaireModal;