import { useTranslation } from "react-i18next";
import {useEffect, useState} from "react";
import {TbCircleNumber1Filled, TbCircleNumber2Filled} from "react-icons/tb";
import ProfesseurListItem from "../../component/Design/Lists/ProfesseurListItem.jsx";
import ExpandableList from "../../component/Design/Lists/ExpandableList.jsx";
import {FaCheckCircle} from "react-icons/fa";
import ScrollBarList from "../../component/Design/Lists/ScrollBarList.jsx";
import ListItem from "../../component/Design/Lists/ListItem.jsx";
import Modal from "../../component/Modal/Modal.jsx";
import Button from "../../component/Design/Button.jsx";
import {assignStudentToTeacher, getEtudiants, getProfesseurs} from "../../api/gestionnaire.jsx";
import {getDepartmentKey} from "../../utils/departementConverter.js";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design/Bars/FilterBar.jsx";

const GestionnaireAttribution = () => {
    const { t } = useTranslation();
    const [error, setError] = useState("");
    const [filtreValueProfesseur, setFiltreValueProfesseur] = useState("")
    const [professeurSelectione, setProfesseurSelectione] = useState(null);
    const [professeurChangeValue, setProfesseurChangeValue] = useState(null);
    const [professeurSelectioneTemp, setProfesseurSelectioneTemp] = useState(null)
    const [etudiantSelectionnes, setEtudiantSelectionnes] = useState(
        []
    );
    const [etudiantAssigne, setEtudiantAssigne] = useState([])
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isAssign, setIsAssign] = useState(false)
    const [professeurs, setProfesseurs] = useState(
        [
        ]
    )
    const [etudiants, setEtudiants] = useState(
        [
        ]
    );
    const [selectedDepartment, setSelectedDepartment] = useState("TOUS");

    const filtreActif = (event) => {
        setFiltreValueProfesseur(event)
    }

    const getListSorted = () => {
        if (!professeurSelectioneTemp) return professeurs;

        return [...professeurs].sort((a, b) => {
            if (a.id === professeurSelectioneTemp.id) return -1;
            if (b.id === professeurSelectioneTemp.id) return 1;
            return 0;
        });
    };

    const onClickEtudiant = (etudiant) => {
        const isAlreadySelected = etudiantSelectionnes.some(e => e.id === etudiant.id);
        setIsAssign(false);
        if (!isAlreadySelected) {
            setEtudiantSelectionnes([...etudiantSelectionnes, etudiant]);
        } else {
            setEtudiantSelectionnes(etudiantSelectionnes.filter(e => e.id !== etudiant.id));
        }
    }

    const handleProfessorSelected = (professeur) => {
        if (!professeurSelectione && !professeurSelectioneTemp || etudiantSelectionnes.length <= 0) {
            setProfesseurSelectione(professeur);
        }
        else if (professeur.id === professeurSelectioneTemp.id){
            setProfesseurSelectione(professeurSelectioneTemp);
            setProfesseurSelectioneTemp(null);
        }
        else{
            setProfesseurChangeValue(professeur)
            setIsModalOpen(true);
        }
    }

    const handleChangeProfesseur = () => {
        if (!isAssign) {
            setProfesseurSelectioneTemp(professeurSelectione);
            setProfesseurSelectione(null);
        }
    }

    const onAssign = async () => {
        const etudiantIds = etudiantSelectionnes.map(etudiant => etudiant.id);
        try {
            await assignStudentToTeacher(etudiantIds, professeurSelectione.id)
            setEtudiantAssigne(etudiantSelectionnes);
            setEtudiantSelectionnes([]);
            setIsAssign(true);
        }catch (e){
            setError(t("error.generic") +  ": " + e.status);
        }
    }

    const restart = () => {
        setEtudiantAssigne([]);
        setIsAssign(false);
        setProfesseurSelectione(null);
        setProfesseurSelectioneTemp(null);
        setError("");
    }

    const handleCancelModal = () => {
        setProfesseurChangeValue(null);
        setProfesseurSelectione(professeurSelectioneTemp);
        setProfesseurSelectioneTemp(null);
        setIsModalOpen(false);
    };

    const handleAgreeModal = () => {
        setEtudiantSelectionnes([]);
        setProfesseurSelectione(professeurChangeValue);
        setProfesseurSelectioneTemp(null);
        setProfesseurChangeValue(null);
        setIsModalOpen(false);
    };

    useEffect(() => {
        const init = async () => {
            try {
                setProfesseurs(await getProfesseurs());
                setEtudiants(await getEtudiants());
            }catch (e) {
                setError(t("error.generic") +  ": " + e.status)
            }
        }
        init();
    }, []);

    useEffect(() => {
        const refetch = async () => {
            try {
                setEtudiants(await getEtudiants());
            }catch (e) {
                setError(t("error.generic") +  ": " + e.status);
            }
        }
        refetch();
    }, [etudiantSelectionnes]);


    return(
        <div>
            {error && (
                <p className="bg-red-500 text-red-950 p-3">{error}</p>
            )}
            <div className="grid grid-cols-12 mt-2">
                <div className={professeurSelectione && !isAssign ? "col-span-6 mt-5" : "col-span-12"}>
                    <div className="grid grid-cols-12">
                        <div className="col-span-9">
                            <h1 className="text-start text-3xl p-5 pb-0 font-bold">{t("gestionnaire.assignation.title")}</h1>
                            <p className=" mt-2 p-5 pt-0">
                                {professeurSelectione
                                    ? t("gestionnaire.assignation.description_with_teacher")
                                    : t("gestionnaire.assignation.description_without_teacher")}
                            </p>
                        </div>
                        {isAssign && (
                            <div className="col-span-3">
                                <div className="flex justify-center items-center  h-full">
                                    <Button type="button" onClick={restart} disabled={!isAssign} className="btn">{t("gestionnaire.assignation.etudiant.assign_restart")}</Button>
                                </div>
                            </div>
                        )}
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
                        <div className="col-span-12 mt-3">
                            <div className="w-full">
                                {professeurSelectione && (
                                    <ProfesseurListItem
                                        professeur={professeurSelectione}
                                        onClick={handleChangeProfesseur}
                                        nbStudent={etudiants.filter(e => e.professeurId === professeurSelectione.id).length}
                                        actionLabel={t("dashboard.view_cv")}
                                    />
                                )}
                                {!professeurSelectione && (
                                    <>
                                        <SearchBar onChange={filtreActif} value={filtreValueProfesseur}/>
                                        <FilterBar
                                            selectedDomain={selectedDepartment}
                                            onDomainChange={setSelectedDepartment}
                                            availableDomains={[...new Set(professeurs.map(p => p.department))]}                                        />
                                        {etudiantSelectionnes.length > 0 && (<p className="font-bold text-sm m-3 p-3 bg-red-950 text-white text-center">{t("gestionnaire.assignation.select.warningAnotherProfessor")}</p>)}
                                        <div className="w-full cursor-pointer">
                                            <ExpandableList
                                                items={getListSorted().filter((item) => {
                                                    return Object.values(item).some((valeur) =>
                                                        String(valeur).toLowerCase().includes(String(filtreValueProfesseur).toLowerCase())
                                                    );
                                                }).filter(p => p.department === selectedDepartment || selectedDepartment === "TOUS")}
                                                profSelected={professeurSelectioneTemp}
                                                limit={professeurs.length}
                                                renderItem={(professeur) => (
                                                    <ProfesseurListItem
                                                        professeur={professeur}
                                                        profSelected={professeurSelectioneTemp}
                                                        nbStudent={etudiants.filter(e => e.professeurId === professeur.id).length}
                                                        textClassName={"w-full inline-block cursor-pointer  border-5 rounded-2xl border-purple-500"}
                                                        onClick={() => handleProfessorSelected(professeur)}
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
                {professeurSelectione && !isAssign && (
                    <div className="col-span-6 h-[300px] max-h-full bg-gray-100 rounded-lg me-1 ms-5">
                       <ScrollBarList
                            title={t("gestionnaire.assignation.select.studentList.title")}
                            emptyMessage={t("gestionnaire.assignation.select.studentList.empty")}
                            liste={etudiantSelectionnes}
                            renderItem={(etudiant) => (
                                <ListItem
                                    key={etudiant.id}
                                    variant="compact"
                                    detailRows={
                                    [
                                        {label : t("general.nom"), value : `${etudiant.firstName} ${etudiant.lastName}`},
                                        {label : t("general.matricule"), value : `${etudiant.matricule}`}
                                    ]
                                    }
                                />
                            )}
                        />
                        <div className="flex justify-center mt-2">
                            <Button type="button" onClick={onAssign} disabled={etudiantSelectionnes <= 0} className="btn">{t("gestionnaire.assignation.button.text")}</Button>
                        </div>
                    </div>
                )}
            </div>
            {professeurSelectione && (
                <div className="mt-5">
                    {isAssign ? (
                        <div className="flex">
                            <FaCheckCircle  className="text-5xl text-green-900 " />
                            <div className="flex flex-col">
                                <p className="ms-2">
                                    {t("gestionnaire.assignation.etudiant.assign_title", {
                                        prenom: professeurSelectione.firstName,
                                        nom: professeurSelectione.lastName
                                    })}
                                </p>
                                <p className="text-xs ms-2 text-purple-600/80 font-medium">
                                    {t("gestionnaire.assignation.etudiant.assign_tips")}
                                </p>
                            </div>
                        </div>
                    ):(
                        <div className="flex">
                            <TbCircleNumber2Filled className="text-5xl text-blue-900 " />
                            <div className="flex flex-col">
                                <p className="ms-2">
                                    {t("gestionnaire.assignation.select.student", {
                                        prenom: professeurSelectione.firstName,
                                        nom: professeurSelectione.lastName
                                    })}
                                </p>
                                <p className="text-xs ms-2 text-purple-600/80 font-medium">
                                    {t("gestionnaire.assignation.select.finishToSelect")}
                                </p>
                            </div>
                        </div>
                    )}
                    <div>
                        <ScrollBarList
                            emptyMessage={t("gestionnaire.assignation.etudiant.empty", { departement: t("departement." + getDepartmentKey(professeurSelectione.department)) })}
                            liste={ isAssign  ? etudiantAssigne : etudiants.filter(etudiant => etudiant.department === professeurSelectione.department && !etudiant.professeurNom)}
                            renderItem={(etudiant) => (
                                <ListItem
                                    key={etudiant.id}
                                    icon={<span className="font-bold text-black">{etudiant.firstName?.charAt(0)}</span>}
                                    title={`${etudiant.firstName} ${etudiant.lastName}`}
                                    subtitle={etudiant.email}
                                    isClickableCard={!isAssign}
                                    isSelected={etudiantSelectionnes.some((e) => e.id === etudiant.id)}
                                    onClick={isAssign ? undefined : () => onClickEtudiant(etudiant)}
                                    detailRows={[
                                        { label: t("general.matricule"), value: etudiant.matricule },
                                        { label: t("general.departement"), value: t("departement." + getDepartmentKey(etudiant.department)) },
                                    ]}
                                />
                            )}/>
                    </div>
                </div>
            )}
            {isModalOpen && (
                <Modal
                    title={t("gestionnaire.assignation.professeur.professeur_modal_title")}
                    description={t("gestionnaire.assignation.professeur.professeur_modal_description")}
                    onClickCancel={handleCancelModal}
                    onClickAgree={handleAgreeModal}
                    onClickCross={handleCancelModal}
                />
            )}
        </div>
    )
};

export default GestionnaireAttribution;