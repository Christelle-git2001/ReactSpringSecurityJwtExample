import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import Button from "../Design/Button.jsx";
import { obtenirConvocation, confirmerConvocation } from "../../api/etudiant.jsx";
import { FaBriefcase, FaCalendarAlt, FaClock, FaMapMarkerAlt } from "react-icons/fa";

function Info({ icone, label, valeur }) {
    return (
        <div className="flex items-center gap-4 bg-gray-50 p-4 rounded-lg">
            <div className="p-3 bg-blue-100 text-blue-600 rounded-full">{icone}</div>
            <div>
                <p className="text-xs text-gray-500 font-semibold uppercase">{label}</p>
                <p className="text-md font-medium text-gray-800">{valeur ?? "—"}</p>
            </div>
        </div>
    );
}

function ConvocationEntrevue() {
    const { id: idUrl } = useParams();
    const location = useLocation();
    const navigate = useNavigate();
    const { t, i18n } = useTranslation();

    const id = idUrl ?? location.state?.convocation?.id ?? location.state?.id;

    const [convocation, setConvocation] = useState(location.state?.convocation ?? null);
    const [loading, setLoading] = useState(!convocation);
    const [error, setError] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const charger = async () => {
        try {
            setConvocation(await obtenirConvocation(id));
            setError(null);
        } catch (err) {
            console.error("Erreur chargement convocation:", err);
            setError(err?.status === 404 ? "convocation.not_found" : "error.generic");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (id) charger();
        else setLoading(false);
    }, [id]);

    const handleConfirmer = async () => {
        setIsSubmitting(true);
        try {
            await confirmerConvocation(id);
            await charger();
        } catch (err) {
            console.error("Erreur confirmation:", err);
            setError("error.generic");
        } finally {
            setIsSubmitting(false);
        }
    };

    if (loading) {
        return <div className="p-8 text-center"><span className="loading loading-spinner" /></div>;
    }

    if (!convocation) {
        return (
            <p role="alert" className="w-full max-w-3xl mx-auto p-8 text-center text-gray-500">
                {t(error ?? "convocation.not_found", "Aucune convocation à afficher.")}
            </p>
        );
    }

    const date = convocation.dateHeureEntrevue ? new Date(convocation.dateHeureEntrevue) : null;
    const dateAffichee = date ? date.toLocaleDateString(i18n.language) : "—";
    const heureAffichee = date
        ? date.toLocaleTimeString(i18n.language, { hour: "2-digit", minute: "2-digit" })
        : "—";

    const estConfirmee = convocation.statut === "CONFIRMEE";

    return (
        <div className="w-full max-w-3xl mx-auto p-4 md:p-8">
            <div className="w-full rounded-xl bg-white p-6 shadow-md md:p-8 flex flex-col gap-6">
                <div className="flex justify-between items-start border-b pb-4">
                    <div>
                        <h2 className="text-2xl font-bold text-[#043462]">
                            {t("convocation.title", "Détails de l'entrevue")}
                        </h2>
                        <p className="text-sm text-gray-500 mt-1">
                            {t("convocation.subtitle", "Veuillez confirmer votre présence")}
                        </p>
                    </div>
                    {estConfirmee && (
                        <span className="badge badge-success text-white p-3">
                            {t("convocation.confirmed", "Confirmée")}
                        </span>
                    )}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <Info icone={<FaBriefcase />} label={t("convocation.offer", "Offre")} valeur={convocation.offreTitle} />
                    <Info icone={<FaMapMarkerAlt />} label={t("convocation.format", "Format")} valeur={convocation.format} />
                    <Info icone={<FaCalendarAlt />} label={t("convocation.date", "Date")} valeur={dateAffichee} />
                    <Info icone={<FaClock />} label={t("convocation.time", "Heure")} valeur={heureAffichee} />
                </div>

                {error && <p role="alert" className="text-sm text-red-700">{t(error)}</p>}

                <div className="flex flex-wrap justify-end gap-3 border-t border-gray-100 pt-5">
                    <Button variant="secondary" type="button" onClick={() => navigate(-1)} disabled={isSubmitting}>
                        {t("actions.back", "Retour")}
                    </Button>
                    {!estConfirmee && (
                        <Button variant="success" type="button" onClick={handleConfirmer} disabled={isSubmitting}>
                            {t("actions.accept", "Accepter")}
                        </Button>
                    )}
                </div>
            </div>
        </div>
    );
}

export default ConvocationEntrevue;