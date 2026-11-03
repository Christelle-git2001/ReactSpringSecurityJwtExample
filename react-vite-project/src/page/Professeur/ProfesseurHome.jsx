import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";
import {getEtudiants} from "../../api/professeur.jsx";
import WelcomeMessage from "../../component/Design/WelcomeMessage.jsx";
import EtudiantListItem from "../../component/Design/Lists/EtudiantListItem.jsx";
import ExpandableList from "../../component/Design/Lists/ExpandableList.jsx";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";

const ProfesseurHome = ({user}) => {
    const navigate = useNavigate();
    const { t, i18n } = useTranslation();
    const [isInitialLoading,setIsInitialLoading] = useState(false);
    const [listeEtudiantACharge, setListeEtudiantACharge] = useState([]);
    const [filtreValue, setFiltreValue] = useState("")


    const initListeEtudiant = async () =>  {
        if (!user){
            navigate("/login");
        }
        try{
            setListeEtudiantACharge(await getEtudiants())
            setIsInitialLoading(false);
        }catch (e){
            console.log(e);
        }
    }

    const filtreActif = (event) => {
        setFiltreValue(event)
    }

    useEffect(() => {
        if (listeEtudiantACharge.length <= 0){
            setIsInitialLoading(true);
        }
        initListeEtudiant();
    }, []);
    return(
    <>
       <WelcomeMessage user={user}/>
        <div className="w-full mt-5">
            <SearchBar onChange={filtreActif} value={filtreValue}/>
            <details open className="collapse collapse-arrow bg-transparent mt-5">

                <summary className="collapse-title text-xl font-bold text-[#043462] px-0">
                    {t("professeur.etudiant_section_title")} ({listeEtudiantACharge.length})
                </summary>

                <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                    <div className="my-4 h-px w-full bg-[#043462]" aria-hidden="true" />
                    <div className="w-full px-2 sm:px-4 pb-8 space-y-8">
                        <div className="grid grid-cols-12 gap-4 w-full">
                            {listeEtudiantACharge.filter((etudiant) => {
                                return Object.values(etudiant).some((valeur) =>
                                String(valeur).toLowerCase().includes(String(filtreValue).toLowerCase())
                                );
                            }).map((etudiant) => (
                                <div key={etudiant.id} className="col-span-6">
                                    <EtudiantListItem etudiant={etudiant} />
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </details>
        </div>
    </>
  );
}
export default ProfesseurHome;