import ListItem from "./ListItem.jsx";
import {useTranslation} from "react-i18next";
import {getDepartmentKey} from "../../../utils/departementConverter.js";
import {FaChalkboardTeacher} from "react-icons/fa";

const ProfesseurListItem = ({professeur, onClick, textClassName}) => {
    const { t } = useTranslation();
    const departement = getDepartmentKey(professeur.departement)
    const detailsRows = [
        departement && {label : t("general.departement"), value : t("departement." + professeur.departement) },
        professeur.matricule && {label : t("general.matricule"), value: professeur.matricule}
    ];
    return (
        <div onClick={onClick} className={textClassName}>
            <ListItem
                icon={<FaChalkboardTeacher className="size-5" />}
                title={professeur?.firstName + " " + professeur?.lastName || t("gestionnaire.assignation.professeur_without_name")}
                subtitle={professeur?.email || t("gestionnaire.assignation.professeur_without_email")}
                detailRows={detailsRows}
                tone="purple"
            />
        </div>
    );
};
export default ProfesseurListItem;