import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import RegisterForm from "./RegisterForm.jsx";
import { getSecteursEmployeur} from "../../api/http.jsx";
import { COMMON_USER_FIELDS } from "./formFields";

function AddEmployeur(props) {
    const { t } = useTranslation();
    const [secteurs, setSecteurs] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getSecteursEmployeur()
            .then(setSecteurs)
            .finally(() => setLoading(false));
    }, []);

    const fields = [
        COMMON_USER_FIELDS[0],
        COMMON_USER_FIELDS[1],
        COMMON_USER_FIELDS[2],
        COMMON_USER_FIELDS[3],
        { name: "town", labelKey: "town" },
        { name: "businessName", labelKey: "business_name", fullWidth: true },
        {
            name: "businessSector",
            labelKey: "business_sector",
            type: "select",
            placeholderKey: "business_sector_placeholder",
            loadingKey: "loading_sectors",
            loading: loading,
            fullWidth: true,
            options: secteurs.map((s) => ({
                value: s.name,
                label: t(`secteur.${s.name}`),
            })),
        },
        COMMON_USER_FIELDS[4],
        COMMON_USER_FIELDS[5],
    ];

    return <RegisterForm translationPrefix="add_employeur" fields={fields} {...props} />;
}

export default AddEmployeur;