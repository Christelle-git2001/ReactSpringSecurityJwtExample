import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import AjoutCV from "./AjoutCV.jsx";
import ShowCv from "./ShowCv.jsx";
import { obtenirCvInfo } from "../../api/etudiant.jsx";
import {useNavigate} from "react-router-dom";

const CurriculumVitaeZone = ({onSend}) => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const [refreshTrigger, setRefreshTrigger] = useState(0);
    const [cv, setCv] = useState(null);
    const [error, setError] = useState(null);

    const handleCvAjoute = () => {
        setRefreshTrigger(prev => prev + 1);
        onSend?.();
    };

    const handleCvSupprime = () => {

        setCv(null);
        setError(null);
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

            if (error?.message === "UNAUTHORIZED" || error?.status === 401) {
                navigate("/login");
            }

            setError("error.generic");
        }
    };

    useEffect(() => {
        chargerCv();
    }, [refreshTrigger]);

    return (
        <div className="w-full">
            {error && (
                <p className="text-red-500 text-sm mt-2">
                    {t(error)}
                </p>
            )}
            <div className="divide-y-4 divide-teal-700 divide-solid">
                {cv && (
                    <ShowCv
                        cv={cv}
                        onCvDeleted={handleCvSupprime}
                    />
                )}
                {(cv?.statut === "REFUSEE" || !cv) && (
                    <AjoutCV onCvAjoute={handleCvAjoute}/>
                )}
            </div>
        </div>
    );

};

export default CurriculumVitaeZone;