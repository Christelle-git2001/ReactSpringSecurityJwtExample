import { useTranslation } from "react-i18next";
import {useState} from "react";
import {TbCircleNumber1Filled, TbCircleNumber2Filled} from "react-icons/tb";
import ProfesseurListItem from "../../component/Design/Lists/ProfesseurListItem.jsx";
import ExpandableList from "../../component/Design/Lists/ExpandableList.jsx";
import {FaCheckCircle} from "react-icons/fa";
import ScrollBarList from "../../component/Design/Lists/ScrollBarList.jsx";
import EtudiantItem from "../../component/Design/Lists/EtudiantItem.jsx";
import EtudiantItemShort from "../../component/Design/Lists/EtudiantItemShort.jsx";

const GestionnaireAttribution = () => {
    const { t } = useTranslation();
    const [professeurSelectione, setProfesseurSelectione] = useState(null);
    const [dropdownProfIsOpen, setDropdownProfIsOpen] = useState(false);
    const [etudiantSelectionnes, setEtudiantSelectionnes] = useState(
        []
    );
    const [professeurs, setProfesseurs] = useState(
        [
            {id:1, firstName:"Jean", lastName:"Dupont",email: "JeanDupont@hotmail.com",departement : "INFORMATIQUE", matricule : "58584545"},
            {id: 2, firstName: "Sophie", lastName:"Tremblay", email: "SophieTremblay@hotmail.com", departement: "INFORMATIQUE", matricule: "58584546"},
            {id: 3, firstName: "Marc-Antoine", lastName:"Gagnon", email: "MarcGagnon@hotmail.com", departement: "TRAVAIL_SOCIAL", matricule: "58584547"},
            {id: 4, firstName: "Sylviane", lastName:"Gagnon", email: "SylvianeGagnon@hotmail.com", departement: "TRAVAIL_SOCIAL", matricule: "5896524"}
        ]
    )
    const [etudiants, setEtudiants] = useState(
        [
            {id: 1, firstName: "Alexandre", lastName: "Bouchard", email: "AlexandreBouchard@hotmail.com", departement: "INFORMATIQUE", matricule: "59123451", professeurAssigne: ""},
            {id: 2, firstName: "Camille", lastName: "Roy", email: "CamilleRoy@hotmail.com", departement: "INFORMATIQUE", matricule: "59123452", professeurAssigne: ""},
            {id: 3, firstName: "Gabriel", lastName: "Côté", email: "GabrielCote@hotmail.com", departement: "TRAVAIL_SOCIAL", matricule: "59123453", professeurAssigne: ""},
            {id: 4, firstName: "Maude", lastName: "Lavoie", email: "MaudeLavoie@hotmail.com", departement: "TRAVAIL_SOCIAL", matricule: "59123454", professeurAssigne: ""},
            {id: 5, firstName: "William", lastName: "Beaulieu", email: "WilliamBeaulieu@hotmail.com", departement: "INFORMATIQUE", matricule: "59123455", professeurAssigne: ""},
            {id: 6, firstName: "Florence", lastName: "Morin", email: "FlorenceMorin@hotmail.com", departement: "TRAVAIL_SOCIAL", matricule: "59123456", professeurAssigne: ""},
            {id: 7, firstName: "Thomas", lastName: "Pelletier", email: "ThomasPelletier@hotmail.com", departement: "INFORMATIQUE", matricule: "59123457", professeurAssigne: ""},
            {id: 8, firstName: "Béatrice", lastName: "Gauthier", email: "BeatriceGauthier@hotmail.com", departement: "TRAVAIL_SOCIAL", matricule: "59123458", professeurAssigne: ""},
            {id: 9, firstName: "Olivier", lastName: "Simard", email: "OlivierSimard@hotmail.com", departement: "INFORMATIQUE", matricule: "59123459", professeurAssigne: ""},
            {id: 10, firstName: "Charlotte", lastName: "Bélanger", email: "CharlotteBelanger@hotmail.com", departement: "TRAVAIL_SOCIAL", matricule: "59123460", professeurAssigne: ""}
        ]
    );

    const handleProfessorSelected = (professeur) => {
        setProfesseurSelectione(professeur)
        setDropdownProfIsOpen(false)
    }

    return(
        <div>
            <div className="grid grid-cols-12 mt-2">
                <div className={professeurSelectione ? "col-span-6" : "col-span-12"}>
                    <div className="grid grid-cols-12 ">
                        <div className="col-span-12">
                            <h1 className="text-start text-3xl p-5 pb-0 font-bold">{t("gestionnaire.assignation.title")}</h1>
                            <p className=" mt-2 p-5 pt-0">
                                {professeurSelectione
                                    ? t("gestionnaire.assignation.description_with_teacher")
                                    : t("gestionnaire.assignation.description_without_teacher")}
                            </p>
                        </div>
                        <div className="col-span-12 ">
                            {professeurSelectione ? (
                                <div className="flex items-center">
                                    <FaCheckCircle  className="text-5xl text-green-900 " />
                                    <div className="flex flex-col ms-2 text-left">
                                        <p className="font-medium text-gray-900 text-sm">
                                            {t("gestionnaire.assignation.select.professorSelected")}
                                        </p>
                                        <p className="text-xs text-purple-600/80 font-medium">
                                            {t("gestionnaire.assignation.select.anotherProfessor")}
                                        </p>
                                    </div>
                                </div>
                            ):(
                                <div className="flex items-center">
                                    <TbCircleNumber1Filled className="text-5xl text-blue-900 " />
                                    <p className="ms-2">{t("gestionnaire.assignation.select.professor")}</p>
                                </div>
                            )}

                        </div>
                        <div className="col-span-12">
                            <div className="dropdown w-full">
                                {professeurSelectione ? (
                                    <ProfesseurListItem
                                        professeur={professeurSelectione}
                                        onClick={() => setDropdownProfIsOpen(!dropdownProfIsOpen)}
                                        actionLabel={t("dashboard.view_cv")}
                                        textClassName={"w-full inline-block cursor-pointer" + (dropdownProfIsOpen ? " border-5 rounded-2xl border-purple-500" : "")}
                                    />
                                ):(
                                    <div role="button" onClick={() => setDropdownProfIsOpen(!dropdownProfIsOpen)} className="btn w-full inline-block cursor-pointer rounded-md bg-[#043462] px-4 py-2 text-sm font-semibold text-white hover:bg-[#03284d]">{t("general.liste." + (dropdownProfIsOpen ? "close" : "open"))}</div>
                                    )}
                                {dropdownProfIsOpen && (
                                    <>
                                        {professeurSelectione && (<p className="font-bold text-sm m-3 p-3 bg-red-950 text-white text-center">{t("gestionnaire.assignation.select.warningAnotherProfessor")}</p>)}
                                        <div className="w-full cursor-pointer">
                                            <ExpandableList
                                                items={professeurs.filter(prof => prof.id !== professeurSelectione?.id)}
                                                limit={professeurs.length}
                                                expandLabel={t("dashboard.view_more", "Voir plus")}
                                                collapseLabel={t("dashboard.view_less", "Voir moins")}
                                                renderItem={(professeur) => (
                                                    <ProfesseurListItem
                                                        professeur={professeur}
                                                        onClick={() => handleProfessorSelected(professeur)}
                                                        actionLabel={t("dashboard.view_cv")}
                                                    />
                                                )}
                                            />
                                        </div>
                                    </>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
                {professeurSelectione && (
                    <div className="col-span-6 h-[330px] max-h-full bg-gray-100 rounded-lg overflow-hidden me-1 ms-5">
                        <ScrollBarList
                            title={t("gestionnaire.assignation.select.studentList.title")}
                            emptyMessage={t("gestionnaire.assignation.select.studentList.empty")}
                            liste={etudiants}
                            renderItem={(etudiant) => (
                                <EtudiantItemShort key={etudiant.id} etudiant={etudiant}/>
                            )}/>
                        />
                    </div>
                )}
            </div>
            {professeurSelectione && (
                <div className="mt-5">
                    <div className="flex">
                        <TbCircleNumber2Filled className="text-5xl text-blue-900 " />
                        <div className="flex flex-col">
                            <p className="ms-2">
                                {t("gestionnaire.assignation.select.student", {
                                    prenom: professeurSelectione.firstName,
                                    nom: professeurSelectione.lastName
                                })}
                            </p>                            <p className="text-xs ms-2 text-purple-600/80 font-medium">
                                {t("gestionnaire.assignation.select.finishToSelect")}
                            </p>
                        </div>
                    </div>
                    <div>
                        <ScrollBarList
                            emptyMessage={t("gestionnaire.assignation.etudiant.empty", { departement: professeurSelectione.departement })}
                            liste={etudiants.filter(etudiant => etudiant.departement === professeurSelectione.departement)}
                            renderItem={(etudiant) => (
                                <EtudiantItem key={etudiant.id} etudiant={etudiant}/>
                            )}/>
                    </div>
                </div>
            )}
        </div>
    )
};

export default GestionnaireAttribution;