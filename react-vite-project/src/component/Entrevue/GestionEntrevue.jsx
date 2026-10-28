import React, { useState, useEffect} from "react";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import AddEntrevue from "./AddEntrevue.jsx";
import Agenda from "./Agenda";
import {
    getCandidaturesRetenuesPourEntrevue,
    PlanifierEntrevue,
    getEntrevuesEmployeur
} from "../../api/employeur.jsx";

function GestionEntrevue() {
    const { t } = useTranslation();
    const location = useLocation();
    const preselectedCandidatureId = location.state?.selectedCandidatureId;
    const [selectedDate, setSelectedDate] = useState("");

    const [candidaturesList, setCandidaturesList] = useState([]);
    const [entrevuesList, setEntrevuesList] = useState([]);
    const [loading, setLoading] = useState(true);

    const mapEntrevueDtoToUi = (dto) => {
        const [datePart, timePart] = (dto.dateHeureEntrevue || "").split("T");

        return {
            id: dto.id,
            candidatureId: dto.candidatureId,
            candidatNom: dto.etudiantFirstName || dto.etudiantLastName
                ? `${dto.etudiantFirstName || ""} ${dto.etudiantLastName || ""}`.trim()
                : t("candidature.student_unknown"),
            posteTitre: dto.offreTitle || t("candidature.offer_unknown"),
            date: datePart || "",
            heure: timePart ? timePart.substring(0, 5) : "00:00",
            duree: "30 min",
            mode: dto.format || "EN_LIGNE",
        };
    };

    useEffect(() => {
        setLoading(true);
        Promise.all([
            getCandidaturesRetenuesPourEntrevue(),
            getEntrevuesEmployeur()
        ])
            .then(([candidaturesData, entrevuesData]) => {
                setCandidaturesList(candidaturesData || []);

                const formattedEntrevues = (entrevuesData || []).map(mapEntrevueDtoToUi);
                setEntrevuesList(formattedEntrevues);
            })
            .catch((err) => {
                console.error( err);
            })
            .finally(() => {
                setLoading(false);
            });
    }, []);

    const handleAddEntrevue = async (entrevueDTO) => {
        try {
            const nouvelleEntrevueCreee = await PlanifierEntrevue(entrevueDTO);

            const candidatureInfo = candidaturesList.find(
                (c) => String(c.id) === String(entrevueDTO.candidatureId)
            );

            const entrevuePourAgenda = mapEntrevueDtoToUi({
                ...nouvelleEntrevueCreee,
                etudiantFirstName: candidatureInfo?.etudiantFirstName,
                etudiantLastName: candidatureInfo?.etudiantLastName,
                offreTitle: candidatureInfo?.offreTitle,
            });

            setEntrevuesList((prev) => [...prev, entrevuePourAgenda]);

            setCandidaturesList((prev) =>
                prev.filter((c) => String(c.id) !== String(entrevueDTO.candidatureId))
            );
        } catch (error) {
            console.error(error);
            throw error;
        }
    };

    return (
        <div>
            <div className="flex flex-col gap-1 text-start p-4">
                <h1 className="text-2xl font-bold text-[#043462] flex items-start gap-2">
                    {t("gestion_entrevue.title")}
                </h1>
                <p className="text-sm text-gray-500">
                    {t("gestion_entrevue.subtitle")}
                </p>
            </div>

            <div className="w-full h-full p-4 md:p-8 flex flex-col gap-16 items-center">
                <div className="flex flex-col lg:flex-row gap-8 w-full max-w-5xl justify-center lg:items-start">

                    <div className="w-full lg:w-[420px] lg:h-fit lg:sticky lg:top-6 shrink-0">
                        <AddEntrevue
                            candidatures={candidaturesList}
                            initialCandidatureId={preselectedCandidatureId}
                            onSave={handleAddEntrevue}
                        />
                    </div>
                    <div className="hidden lg:block flex-1"></div>
                    <div className="w-full lg:max-w-xl">
                        {loading ? (
                            <div className="flex items-center justify-center p-12 bg-white rounded-2xl border border-gray-200">
                                <span className="loading loading-spinner loading-md text-[#043462]" />
                            </div>
                        ) : (
                            <Agenda
                                entrevuesList={entrevuesList}
                                selectedDate={selectedDate}
                                onDateSelect={setSelectedDate}
                            />
                        )}
                    </div>

                </div>
            </div>
        </div>
    );
}

export default GestionEntrevue;