import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import AddEntrevue from "./AddEntrevue.jsx";
import Agenda from "./Agenda";
import { getCandidaturesRetenuesPourEntrevue, PlanifierEntrevue } from "../../api/employeur.jsx"

function GestionEntrevue() {
    const { t } = useTranslation();
    const [selectedDate, setSelectedDate] = useState("");

    const [candidaturesList, setCandidaturesList] = useState([]);
    const [entrevuesList, setEntrevuesList] = useState([]);

    useEffect(() => {
        getCandidaturesRetenuesPourEntrevue()
            .then((data) => {
                setCandidaturesList(data || []);
            })
            .catch((err) => {
                console.error("Erreur lors du chargement des candidatures :", err);
            });
    }, []);

    const handleAddEntrevue = async (entrevueDTO) => {
        try {
            const nouvelleEntrevueCreee = await PlanifierEntrevue(entrevueDTO);
            const candidatureInfo = candidaturesList.find(
                (c) => String(c.id) === String(entrevueDTO.candidatureId)
            );

            const entrevuePourAgenda = {
                id: nouvelleEntrevueCreee?.id || Date.now(),
                candidatureId: entrevueDTO.candidatureId,
                candidatNom: candidatureInfo
                    ? `${candidatureInfo.etudiantFirstName || ""} ${candidatureInfo.etudiantLastName || ""}`.trim()
                    : t("candidature.student_unknown", "Candidat inconnu"),
                posteTitre: candidatureInfo?.offreTitle || t("candidature.offer_unknown", "Poste inconnu"),
                date: entrevueDTO.dateHeureEntrevue.split("T")[0],
                heure: entrevueDTO.dateHeureEntrevue.split("T")[1]?.substring(0, 5) || "10:00",
                duree: "30 min",
                mode: entrevueDTO.format,
            };

            setEntrevuesList((prev) => [...prev, entrevuePourAgenda]);
        } catch (error) {
            console.error("Erreur lors de la planification :", error);
            throw error;
        }
    };

    return (
        <div>
            <div className="flex flex-col gap-1 text-start p-4">
                <h1 className="text-2xl font-bold text-[#043462] flex items-start gap-2">
                    {t("gestion_entrevue.title", "Gestion des entrevues")}
                </h1>
                <p className="text-sm text-gray-500">
                    {t("gestion_entrevue.subtitle", "Planifiez vos rendez-vous et consultez votre calendrier en temps réel.")}
                </p>
            </div>

            <div className="w-full h-full p-4 md:p-8 flex flex-col gap-16 items-center">
                <div className="flex flex-col lg:flex-row gap-8 w-full max-w-5xl justify-center lg:items-start">

                    <div className="w-full lg:w-[420px] lg:h-fit lg:sticky lg:top-6 shrink-0">
                        <AddEntrevue
                            candidatures={candidaturesList}
                            onSave={handleAddEntrevue}
                        />
                    </div>
                    <div className="hidden lg:block flex-1"></div>
                    <div className="w-full lg:max-w-xl">
                        <Agenda
                            entrevuesList={entrevuesList}
                            selectedDate={selectedDate}
                            onDateSelect={setSelectedDate}
                        />
                    </div>

                </div>
            </div>
        </div>
    );
}

export default GestionEntrevue;