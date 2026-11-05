import React from "react";
import { useTranslation } from "react-i18next";
import { FaCheck, FaTimesCircle } from "react-icons/fa";

function CandidatureTimeline({ statut, statutEntrevue, afficherRetenue = true, regrouperEntrevue = false, afficherSignature = false, contratSigne = false }) {
    const { t } = useTranslation();
    const estRefusee = statut === "ENTREVUE_REFUSEE";
    const candidatureRefusee = statut === "REFUSEE";

    const steps = [
        { key: "SOUMISE", label: t("candidature.status.soumise") },
        { key: "RETENUE_POUR_ENTREVUE", label: t("candidature.status.retenue") },
        { key: "ENTREVUE_PLANIFIEE", label: t(estRefusee ? "candidature.status.entrevue_refusee" : regrouperEntrevue ? "candidature.interview_step" : "candidature.status.planifiee") },
        { key: "CONFIRMEE", label: t("candidature.status.confirmee") },
        { key: "ACCEPTEE", label: t(candidatureRefusee ? "candidature.status.refusee" : "candidature.status.acceptee") },
        { key: "STAGE_TROUVE", label: t("candidature.status.stage_trouve") },
        ...(afficherSignature ? [{ key: "SIGNATURE_DEMARREE", label: t("candidature.status.signature_started") }] : []),
        { key: "CONTRAT_CREE", label: t(afficherSignature ? "candidature.status.contrat_signe" : "candidature.status.contrat") }
    ];

    const order = steps.map(s => s.key);
    const etapesVisibles = steps.filter(step => (afficherRetenue || step.key !== "RETENUE_POUR_ENTREVUE")
        && (!regrouperEntrevue || step.key !== "CONFIRMEE"));
    const statutTimeline = afficherSignature && statut === "CONTRAT_CREE" && !contratSigne ? "SIGNATURE_DEMARREE" : candidatureRefusee ? "ACCEPTEE" : estRefusee ? "ENTREVUE_PLANIFIEE"
        : statut === "ENTREVUE_PLANIFIEE" && ["CONFIRMEE", "TERMINEE"].includes(statutEntrevue)
            ? "CONFIRMEE" : statut;

    const isCompleted = (stepKey) => {
        const currentIndex = order.indexOf(statutTimeline);
        const stepIndex = order.indexOf(stepKey);
        return stepIndex <= currentIndex;
    };

    const etatEntrevue = statutEntrevue === "TERMINEE" || ["ACCEPTEE", "REFUSEE", "STAGE_TROUVE", "CONTRAT_CREE"].includes(statut) ? "TERMINEE"
        : statutEntrevue === "CONFIRMEE" ? "CONFIRMEE" : "ENTREVUE_PLANIFIEE";
    const contenuEtape = (step) => (
        <>
            {regrouperEntrevue && step.key === "ENTREVUE_PLANIFIEE"
                ? <span className="block text-center">{step.label}</span>
                : step.label}
            {regrouperEntrevue && step.key === "ENTREVUE_PLANIFIEE" && !estRefusee && (
                <span className="mt-2 grid grid-cols-3 text-[10px] font-medium">
                    {["ENTREVUE_PLANIFIEE", "CONFIRMEE", "TERMINEE"].map((etat, index) => {
                        const atteint = isCompleted(step.key) && index <= ["ENTREVUE_PLANIFIEE", "CONFIRMEE", "TERMINEE"].indexOf(etatEntrevue);
                        return (
                            <span key={etat} className="relative flex flex-col items-center gap-1">
                                {index < 2 && <span aria-hidden="true" className="absolute left-1/2 top-2 h-px w-full bg-[#043462]/30" />}
                                <span aria-hidden="true" className={`relative flex size-4 items-center justify-center rounded-full border ${atteint ? "border-[#043462] bg-[#043462] text-white" : "border-[#043462]/30 bg-white"}`}>
                                    {atteint && <FaCheck className="size-2.5" />}
                                </span>
                                <span className={`rounded-full px-2 py-0.5 ${isCompleted(step.key) && etat === etatEntrevue ? "bg-[#043462] text-white" : "text-[#043462]"}`}>
                                    {t(`candidature.interview_states.${etat}`)}
                                </span>
                            </span>
                        );
                    })}
                </span>
            )}
        </>
    );

    return (
        <div className="w-full bg-transparent p-4 rounded-xl mb-6 flex justify-center">
            <ul className="timeline timeline-horizontal inline-flex justify-center max-w-3xl">

                {etapesVisibles.map((step, index) => {
                    const completed = isCompleted(step.key);
                    const refus = (estRefusee && step.key === "ENTREVUE_PLANIFIEE") || (candidatureRefusee && step.key === "ACCEPTEE");
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
                                    {contenuEtape(step)}
                                </div>
                            ) : (
                                <div className={`timeline-end timeline-box text-xs md:text-sm ${completed ? completedClass : "text-gray-400"}`}>
                                    {contenuEtape(step)}
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
