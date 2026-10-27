import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import AddEntrevue from "./AddEntrevue.jsx";
import Agenda from "./Agenda";

function GestionEntrevue() {
    const { t } = useTranslation();
    const [selectedDate, setSelectedDate] = useState("");

    const [candidaturesList] = useState([
        {
            id: 1,
            etudiantFirstName: "Thomas",
            etudiantLastName: "Dubois",
            offreTitle: "Développeur Front-End React",
        },
        {
            id: 2,
            etudiantFirstName: "Sarah",
            etudiantLastName: "Martin",
            offreTitle: "Analyste Assurance Qualité",
        },
        {
            id: 3,
            etudiantFirstName: "Alexandre",
            etudiantLastName: "Gagnon",
            offreTitle: "Développeur Full-Stack Node.js",
        },
    ]);

    const [entrevuesList, setEntrevuesList] = useState([
        {
            id: 101,
            candidatureId: 1,
            candidatNom: "Thomas Dubois",
            posteTitre: "Développeur Front-End React",
            date: "2026-10-28",
            heure: "10:00",
            duree: "30 min",
            mode: "EN_LIGNE",
        },
    ]);

    const handleAddEntrevue = (nouvelleEntrevue) => {
        setEntrevuesList((prev) => [...prev, nouvelleEntrevue]);
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

            <div className="w-full h-full p-4 md:p-8 flex flex-col gap-6 items-center">

                <div className="flex flex-col lg:flex-row gap-8 w-full max-w-5xl justify-center lg:items-start">

                    <div className="w-full lg:w-[420px] lg:h-fit lg:sticky lg:top-6 shrink-0">
                        <AddEntrevue
                            candidatures={candidaturesList}
                            onSave={handleAddEntrevue}
                        />
                    </div>

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