import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import RegisterForm from "./RegisterForm.jsx";
import { getDepartements } from "../../api/http.jsx";
import { COMMON_USER_FIELDS } from "./formFields";

function AddProfesseur(props) {
    const { t } = useTranslation();
    const [departements, setDepartements] = useState([]);

    useEffect(() => {
        getDepartements()
            .then(setDepartements)
            .catch(() => setDepartements([]));
    }, []);

    const fields = [
        COMMON_USER_FIELDS[0],
        COMMON_USER_FIELDS[1],
        COMMON_USER_FIELDS[2],
        COMMON_USER_FIELDS[3],
        {
            name: "matricule",
            labelKey: "matricule",
            minLength: "7",
            maxLength: "7"
        },
        {
            name: "department",
            labelKey: "department",
            type: "select",
            placeholderKey: "department_placeholder",
            fullWidth: true,
            options: departements.map((dep) => ({
                value: dep.name,
                label: t(`departement.${dep.name}`),
            })),
        },
        COMMON_USER_FIELDS[4],
        COMMON_USER_FIELDS[5],
    ];

    return (
        <RegisterForm
            translationPrefix="add_professeur"
            fields={fields}
            {...props}
        />
    );
}

export default AddProfesseur;