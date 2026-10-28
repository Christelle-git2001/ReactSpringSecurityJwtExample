import { useTranslation } from "react-i18next";
import {useState} from "react";
import {TbCircleNumber1Filled} from "react-icons/tb";
import ProfesseurListItem from "../../component/Design/Lists/ProfesseurListItem.jsx";
import ExpandableList from "../../component/Design/Lists/ExpandableList.jsx";
import {FaCheckCircle} from "react-icons/fa";

const GestionnaireAttribution = () => {
    const { t } = useTranslation();
    const [professeurSelectione, setProfesseurSelectione] = useState(null);
    const [dropdownProfIsOpen, setDropdownProfIsOpen] = useState(false);
    const [professeurs, setProfesseurs] = useState(
        [
            {id:1, firstName:"Jean", lastName:"Dupont",email: "JeanDupont@hotmail.com",departement : "INFORMATIQUE", matricule : "58584545"},
            {id: 2, firstName: "Sophie", lastName:"Tremblay", email: "SophieTremblay@hotmail.com", departement: "INFORMATIQUE", matricule: "58584546"},
            {id: 3, firstName: "Marc-Antoine", lastName:"Gagnon", email: "MarcGagnon@hotmail.com", departement: "TRAVAIL_SOCIAL", matricule: "58584547"},
            {id: 4, firstName: "Sylviane", lastName:"Gagnon", email: "SylvianeGagnon@hotmail.com", departement: "TRAVAIL_SOCIAL", matricule: "5896524"}
        ]
    )

    const handleProfessorSelected = (professeur) => {
        setProfesseurSelectione(professeur)
        setDropdownProfIsOpen(false)
    }

    return(
        <div>
            <div>
                <h1 className="text-start text-3xl p-5 font-bold">{t("gestionnaire.assignation.title")}</h1>
            </div>

            <div className="grid grid-cols-12 mt-2">
                <div className="col-span-6">
                    <div className="grid grid-cols-12 ">
                        <div className="col-span-12 ">
                            <p className="p-4">{t("gestionnaire.assignation.description")}</p>
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
                                {professeurSelectione && dropdownProfIsOpen && (<p className="font-bold text-sm m-3 p-3 bg-red-950 text-white text-center">Modifié le professeur ? Cela effacera les élèves sélectionnés.</p>)}
                                {dropdownProfIsOpen && (
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
                                )}
                            </div>
                        </div>
                    </div>
                </div>
                {professeurSelectione && (
                    <div className="col-span-6 bg-gray-100 h-50 overflow-y-auto flex flex-col">
                        <h2 className="text-center rounded-3xl text-xl m-2 font-semibold">Élèves Sélectionnés</h2>
                        <p className="p-4">Aucun étudiant Sélectionné</p>
                    </div>
                )}
            </div>
        </div>
    )
};

export default GestionnaireAttribution;