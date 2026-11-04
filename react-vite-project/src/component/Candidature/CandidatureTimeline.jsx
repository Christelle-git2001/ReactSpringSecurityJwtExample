import React from "react";
import { useTranslation } from "react-i18next";
import { FaTimesCircle } from "react-icons/fa";

function CandidatureTimeline({ statut, stageTrouve = false, statutEntrevue, afficherRetenue = true }) {
    const { t } = useTranslation();
    const estRefusee = statut === "ENTREVUE_REFUSEE";

    // TODO : Ajouter les bons status le moment venu  (voir enum BE)
    const steps = [
        { key: "SOUMISE", label: t("candidature.status.soumise") },
        { key: "RETENUE_POUR_ENTREVUE", label: t("candidature.status.retenue") },
        { key: "ENTREVUE_PLANIFIEE", label: t(estRefusee ? "candidature.status.entrevue_refusee" : statutEntrevue === "TERMINEE" && statut === "ENTREVUE_PLANIFIEE" ? "convocation.awaiting_decision" : "candidature.status.planifiee") },
        { key: "ACCEPTEE", label: t("candidature.status.acceptee") },
        { key: "STAGE_TROUVE", label: t("candidature.status.stage_trouve") },
        { key: "CONTRAT_CREE", label: t("candidature.status.contrat") }
    ];

    const order = steps.map(s => s.key);
    const etapesVisibles = steps.filter(step => afficherRetenue || step.key !== "RETENUE_POUR_ENTREVUE");
    const statutTimeline = estRefusee ? "ENTREVUE_PLANIFIEE"
        : statut === "CONTRAT_CREE" ? statut : stageTrouve ? "STAGE_TROUVE" : statut;

    const isCompleted = (stepKey) => {
        const currentIndex = order.indexOf(statutTimeline);
        const stepIndex = order.indexOf(stepKey);
        return stepIndex <= currentIndex;
    };

    return (
        <div className="w-full bg-transparent p-4 rounded-xl mb-6 flex justify-center">
            <ul className="timeline timeline-horizontal inline-flex justify-center max-w-3xl">

                {etapesVisibles.map((step, index) => {
                    const completed = isCompleted(step.key);
                    const refus = estRefusee && step.key === "ENTREVUE_PLANIFIEE";
                    const completedClass = refus ? "text-red-800 bg-red-100 font-bold" : "text-[#043462] bg-[#99F0EF] font-bold";

                    return (
                        <li key={step.key}>
                            {index !== 0 && (
                                <hr
                                    style={{ backgroundColor: completed ? "#99F0EF" : undefined }}
                                    className={completed ? "!bg-[#99F0EF]" : "bg-gray-200"}
                                />
                            )}

                            {index % 2 === 0 ? (
                                <div className={`timeline-start timeline-box text-xs md:text-sm ${completed ? completedClass : "text-gray-400"}`}>
                                    {step.label}
                                </div>
                            ) : (
                                <div className={`timeline-end timeline-box text-xs md:text-sm ${completed ? completedClass : "text-gray-400"}`}>
                                    {step.label}
                                </div>
                            )}

                            <div className={`timeline-middle ${refus ? "text-red-600" : completed ? "text-[#043462]" : "text-gray-300"}`}>
                                {refus ? <FaTimesCircle className="h-5 w-5" aria-hidden="true" /> : <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    viewBox="0 0 20 20"
                                    fill="currentColor"
                                    className="h-5 w-5"
                                >
                                    <path
                                        fillRule="evenodd"
                                        d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.857-9.809a.75.75 0 00-1.214-.882l-3.483 4.79-1.88-1.88a.75.75 0 10-1.06 1.061l2.5 2.5a.75.75 0 001.137-.089l4-5.5z"
                                        clipRule="evenodd"
                                    />
                                </svg>}
                            </div>

                            {index < etapesVisibles.length - 1 && (
                                <hr
                                    style={{ backgroundColor: completed ? "#99F0EF" : undefined }}
                                    className={completed ? "!bg-[#99F0EF]" : "bg-gray-200"}
                                />
                            )}
                        </li>
                    );
                })}

            </ul>
        </div>
    );
}

export default CandidatureTimeline;
