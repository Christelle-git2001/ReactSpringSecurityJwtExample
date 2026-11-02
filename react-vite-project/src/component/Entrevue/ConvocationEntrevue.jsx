import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import Button from "../Design/Button.jsx";
import Modal from "../Modal/Modal.jsx";
import { obtenirConvocation, accepterConvocation, refuserConvocation } from "../../api/etudiant.jsx";
import { FaBriefcase, FaCalendarAlt, FaCheckCircle, FaClock, FaMapMarkerAlt } from "react-icons/fa";

function Info({ icone, label, valeur }) {
    return (
        <div className="flex items-center gap-4 bg-gray-50 p-4 rounded-lg">
            <div className="p-3 bg-sky-100 text-[#043462] rounded-full" aria-hidden="true">{icone}</div>
            <div className="min-w-0">
                <p className="text-xs text-gray-500 font-semibold uppercase">{label}</p>
                <p className="text-md font-medium text-gray-800 break-words">{valeur ?? "—"}</p>
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
    const [isModalOpen, setIsModalOpen] = useState(false);

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

    const repondre = async (action) => {
        setIsSubmitting(true);
        try {
            await action(id);
            await charger();
        } catch (err) {
            console.error("Erreur réponse convocation:", err);
            setError(err?.status === 409 || err?.status === 400
                ? "convocation.invalid_state"
                : "error.generic");
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
    const dateAffichee = date ? convocation.dateHeureEntrevue.split("T")[0] : "—";
    const heureAffichee = date
        ? date.toLocaleTimeString(i18n.language, { hour: "2-digit", minute: "2-digit" })
        : "—";

    const estConfirmee = convocation.statut === "CONFIRMEE";
    const estRefusee = convocation.statut === "REFUSEE";
    const aRepondre = !estConfirmee && !estRefusee;
    const formatKeys = {
        EN_LIGNE: "entrevue.mode_online_title",
        PRESENTIEL: "entrevue.mode_onsite_title",
    };

    return (
        <div className="w-full max-w-3xl mx-auto p-4 md:p-8">
            <div className="w-full rounded-xl bg-white p-6 shadow-md md:p-8 flex flex-col gap-4">
                <div className="border-b border-gray-100 pb-4">
                    <h2 className="text-2xl font-bold text-[#043462]">{t("convocation.title")}</h2>
                    {aRepondre && (
                        <p className="text-sm text-gray-500 mt-1">
                            {t("convocation.subtitle")}
                            <span className="font-bold"> {convocation.employeur?.businessName}</span>
                        </p>
                    )}
                </div>
                {estConfirmee && (
                    <div role="status" className="flex items-center gap-3 rounded-lg bg-green-100 px-4 py-3 font-semibold text-[#043462]">
                        <FaCheckCircle className="shrink-0 text-green-600" aria-hidden="true" />
                        {t("convocation.attendance_confirmed")}
                    </div>
                )}

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <Info icone={<FaBriefcase />} label={t("convocation.offer")} valeur={convocation.offreTitle} />
                    <Info icone={<FaMapMarkerAlt />} label={t("convocation.format", "Format")}
                          valeur={formatKeys[convocation.format] ? t(formatKeys[convocation.format]) : convocation.format} />
                    <Info icone={<FaCalendarAlt />} label={t("convocation.date", "Date")} valeur={dateAffichee} />
                    <Info icone={<FaClock />} label={t("convocation.time", "Heure")} valeur={heureAffichee} />
                </div>

                {error && <p role="alert" className="text-sm text-red-700">{t(error)}</p>}

                <div className="flex flex-wrap justify-end gap-3 border-t border-gray-100 pt-5">
                    <Button variant="secondary" type="button" onClick={() => navigate(-1)} disabled={isSubmitting}>
                        {t("convocation.back")}
                    </Button>
                    {aRepondre && (
                        <>
                            <Button variant="danger" type="button" disabled={isSubmitting}
                                    onClick={() => setIsModalOpen(true)}>
                                {t("convocation.reject")}
                            </Button>
                            <Button variant="success" type="button" disabled={isSubmitting}
                                    onClick={() => repondre(accepterConvocation)}>
                                {t("convocation.accept")}
                            </Button>
                        </>
                    )}
                </div>
            </div>
            {isModalOpen && (
                <Modal
                    title={t("convocation.confirm_refusal")}
                    description={t("convocation.refusal_confirmation")}
                    agreeLabel={t("convocation.confirm_refusal")}
                    onClickCancel={() => setIsModalOpen(false)}
                    onClickCross={() => setIsModalOpen(false)}
                    onClickAgree={() => {
                        setIsModalOpen(false);
                        repondre(refuserConvocation);
                    }}
                />
            )}
        </div>
    );
}

export default ConvocationEntrevue;
