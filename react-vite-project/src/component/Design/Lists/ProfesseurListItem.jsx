import ListItem from "./ListItem.jsx";
import {useTranslation} from "react-i18next";
import {getDepartmentKey} from "../../../utils/departementConverter.js";
import {FaChalkboardTeacher} from "react-icons/fa";

const ProfesseurListItem = ({professeur, onClick, textClassName, profSelected}) => {
    const { t } = useTranslation();
    const departement = getDepartmentKey(professeur.department)
    const detailsRows = [
        departement && {label : t("general.departement"), value : t("departement." + departement) },
       {label : t("general.matricule"), value: professeur.matricule}
    ];
    const classNameBadge = "bg-blue-200 border-5 rounded-2xl border-blue-200";
    const estSelectionner = profSelected === professeur;
    return (
        <div onClick={onClick} className={estSelectionner ? textClassName : ""}>
            <ListItem
                icon={<FaChalkboardTeacher className="size-5" />}
                title={professeur?.firstName + " " + professeur?.lastName || t("gestionnaire.assignation.professeur.professeur_without_name")}
                subtitle={professeur?.email || t("")}
                detailRows={detailsRows}
                badge={estSelectionner ? t("gestionnaire.assignation.professeur.professeur_is_selected") : ""}
                badgeClassName={classNameBadge}
                tone="purple"
            />
        </div>
    );
};
export default ProfesseurListItem;