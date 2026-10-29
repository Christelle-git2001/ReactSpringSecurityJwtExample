import React from "react";
import { useTranslation } from "react-i18next";

function CandidatureTimeline({ statut }) {
    const { t } = useTranslation();

    // TODO : Ajouter les bons status le moment venu actuellement il n'existe que SOUMISE et RETENUE_POUR_ENTREVUE (voir enum BE)
    const steps = [
        { key: "SOUMISE", label: t("candidature.status.soumise") },
        { key: "RETENUE_POUR_ENTREVUE", label: t("candidature.status.retenue") },
        { key: "ENTREVUE_PLANIFIEE", label: t("candidature.status.planifiee") },
        { key: "ACCEPTEE", label: t("candidature.status.acceptee") },
    ];

    const order = steps.map(s => s.key);

    const isCompleted = (stepKey) => {
        const currentIndex = order.indexOf(statut);
        const stepIndex = order.indexOf(stepKey);
        return stepIndex <= currentIndex;
    };

    return (
        <div className="w-full bg-transparent p-4 rounded-xl mb-6 flex justify-center">
            <ul className="timeline timeline-horizontal w-full max-w-3xl">

                {steps.map((step, index) => {
                    const completed = isCompleted(step.key);

                    return (
                        <li key={step.key}>
                            {index !== 0 && (
                                <hr
                                    style={{ backgroundColor: completed ? "#99F0EF" : undefined }}
                                    className={completed ? "!bg-[#99F0EF]" : "bg-gray-200"}
                                />
                            )}
                            {index % 2 === 0 ? (
                                <div className={`timeline-start timeline-box text-xs md:text-sm ${completed ? "text-[#043462] bg-[#99F0EF] font-bold" : "text-gray-400"}`}>
                                    {step.label}
                                </div>
                            ) : (
                                <div className={`timeline-end timeline-box text-xs md:text-sm ${completed ? "text-[#043462] bg-[#99F0EF] font-bold" : "text-gray-400"}`}>
                                    {step.label}
                                </div>
                            )}
                            <div className={`timeline-middle ${completed ? "text-[#043462] " : "text-gray-300"}`}>
                                <svg
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
                                </svg>
                            </div>
                            {index < steps.length - 1 && (
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