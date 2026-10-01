import React, {useState} from "react";
import {useTranslation} from "react-i18next";
import AjoutCV from "./AjoutCV.jsx";
import ShowCv from "./ShowCv.jsx";

const CurriculumVitaeZone = () => {
    const { t } = useTranslation();
    const [refreshTrigger, setRefreshTrigger] = useState(0);
    const handleCvAjoute = () => {
        setRefreshTrigger(prev => prev + 1);
    };
    return (
        <div className=" w-full">
           <AjoutCV onCvAjoute={handleCvAjoute}/>
            <ShowCv refreshTrigger={refreshTrigger}/>
        </div>
    )
};export default CurriculumVitaeZone;