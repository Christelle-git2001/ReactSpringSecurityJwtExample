import React from "react";
import { useTranslation } from "react-i18next";
import { ChampEvaluation, ChoixEvaluation, Etoile, SectionEvaluation } from "./ComposantsEvaluation.jsx";

function GroupeCriteresEvaluation({ groupe, valeurs, erreurs, options, disabled, onChange }) {
    const { t } = useTranslation();

    return (
        <SectionEvaluation
            titre={t(`evaluation.sections.${groupe.titre}`)}
            sousTitre={t(`evaluation.subtitles.${groupe.titre}`)}
        >
            <ul className="divide-y divide-[#043462]/10">
                {groupe.criteres.map(critere => {
                    const erreur = erreurs[`${groupe.dto}.${critere}`];
                    const libelle = t(`evaluation.criteria.${critere}`);
                    return (
                        <li key={critere} className="py-3">
                            <p className={`mb-2 text-sm font-medium ${erreur ? "text-red-700" : "text-gray-800"}`}>
                                {libelle} <Etoile />
                            </p>
                            <ChoixEvaluation
                                name={`${groupe.dto}.${critere}`}
                                legende={libelle}
                                options={options}
                                value={valeurs[critere]}
                                onChange={valeur => onChange(groupe.dto, critere, valeur)}
                                erreur={erreur}
                                disabled={disabled}
                            />
                        </li>
                    );
                })}
            </ul>
            <ChampEvaluation
                label={t("evaluation.fields.comments")}
                value={valeurs.commentaire}
                zoneTexte
                requis={false}
                disabled={disabled}
                onChange={event => onChange(groupe.dto, "commentaire", event.target.value)}
            />
        </SectionEvaluation>
    );
}

export default GroupeCriteresEvaluation;