import { useState, useEffect, useCallback } from "react";
import { useTranslation } from "react-i18next";
import {getContratByCandidatureId} from "../../api/gestionnaire.jsx";
import {obtenirContratPDF} from "../../api/pdf.jsx";

export const useContrat = (candidatureId, status) => {
    const { t, i18n } = useTranslation();
    const [contrat, setContrat] = useState(null);
    const [contratPdfBlobUrl, setContratPdfBlobUrl] = useState(null);
    const [hasContrat, setHasContrat] = useState(false);
    const [isContratLoading, setIsContratLoading] = useState(false);
    const [error, setError] = useState(null);

    const chargerContratSiExiste = useCallback(async () => {
        if (!candidatureId) return;
        setIsContratLoading(true);
        setError(null);

        try {
            let contratData = null;

            try {
                contratData = await getContratByCandidatureId(candidatureId);
            } catch (err) {
                if (status === "CONTRAT_CREE") {
                    setError(t("contrat.aucun_contrat"));
                }
            }

            if (contratData && contratData.id) {
                setContrat(contratData);
                const currentLang = i18n.language || "fr";
                const res = await obtenirContratPDF(contratData.id, currentLang);
                if (res && res.ok) {
                    const blob = await res.blob();
                    const url = URL.createObjectURL(blob);
                    setContratPdfBlobUrl(url);
                    setHasContrat(true);
                }
            }
        } catch (err) {
            console.error(err);
        } finally {
            setIsContratLoading(false);
        }
    }, [candidatureId, i18n.language, t]);

    useEffect(() => {
        chargerContratSiExiste();

        return () => {
            if (contratPdfBlobUrl) {
                URL.revokeObjectURL(contratPdfBlobUrl);
            }
        };
    }, [chargerContratSiExiste]);

    return {
        contrat,
        contratPdfBlobUrl,
        hasContrat,
        isContratLoading,
        error,
        rechargerContrat: chargerContratSiExiste
    };
};