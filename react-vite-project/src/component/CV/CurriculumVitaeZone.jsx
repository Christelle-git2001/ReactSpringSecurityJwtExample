import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import AjoutCV from "./AjoutCV.jsx";
import ShowCv from "./ShowCv.jsx";
import { obtenirCvInfo } from "../../api/etudiant.jsx";

const CurriculumVitaeZone = () => {
    const { t } = useTranslation();
    const [refreshTrigger, setRefreshTrigger] = useState(0);
    const [cv, setCv] = useState(null);
    const [error, setError] = useState(null);

    const handleCvAjoute = () => {
        setRefreshTrigger(prev => prev + 1);
    };

    const chargerCv = async () => {
        try {
            const data = await obtenirCvInfo();
            setCv(data);
            setError(null);
        } catch (error) {
            if (error?.status === 404) {
                setError(null);
                setCv(null);
                return;
            }

            if (error?.status === 401) {
                setError("token.doesntExist");
                return;
            }

            setError("error.generic");
        }
    };

    useEffect(() => {
        chargerCv();
    }, [refreshTrigger]);

    return (
        <div className="w-full">
            {cv?.statut !== "ACCEPTEE" && (
                <AjoutCV onCvAjoute={handleCvAjoute} />
            )}

            {error && (
                <p className="text-red-500 text-sm mt-2">
                    {t(error)}
                </p>
            )}

            <ShowCv
                cv={cv}
                onCvDeleted={handleCvAjoute}
            />
        </div>
    );

};

export default CurriculumVitaeZone;