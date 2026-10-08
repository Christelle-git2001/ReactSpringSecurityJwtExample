import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useOutletContext } from "react-router-dom";
import { useTranslation } from "react-i18next";
import OffreEtudiant from "../../component/Design/Cards/OffreEtudiant.jsx";
import SearchBar from "../../component/Design/SearchBar.jsx";
import FilterBar from "../../component/Design/FilterBar.jsx";
import { PDFContent } from "../../component/PDF/PDFVisioneuse.jsx";
import { useOffreFilters } from "../../utils/useOffreFilters.jsx";
import { obtenirOffres } from "../../api/etudiant.jsx";
import { obtenirOffrePDF } from "../../api/employeur.jsx";
import { getDepartmentKey } from "../../utils/departementConverter.js";

const ViewOffers = ({ user }) => {
    const { t } = useTranslation();
    const { openDrawer } = useOutletContext();

    const [offres, setOffres] = useState([]);
    const [etat, setEtat] = useState("chargement");
    const [selectedTown, setSelectedTown] = useState("TOUS");

    const {
        searchTerm,
        setSearchTerm,
        offresFiltrees: offresFiltreesHook,
    } = useOffreFilters(offres);

    const availableTowns = useMemo(() => {
        const towns = offres
            .map((o) => o?.employeur?.town)
            .filter(Boolean);
        return [...new Set(towns)];
    }, [offres]);

    const offresFinales = useMemo(() => {
        return offresFiltreesHook.filter((offer) => {
            if (selectedTown === "TOUS") return true;
            return offer?.employeur?.town === selectedTown;
        });
    }, [offresFiltreesHook, selectedTown]);

    const charger = useCallback(async () => {
        setEtat("chargement");
        try {
            const data = await obtenirOffres(getDepartmentKey(user?.department));
            setOffres(data || []);
            setEtat("ok");
        } catch (error) {
            console.error("Erreur lors du chargement des offres:", error);
            setEtat("erreur");
        }
    }, [user]);

    useEffect(() => {
        charger();
    }, [charger]);

    const handleDownloadFile = (blobUrl, fileName) => {
        const link = document.createElement("a");
        link.href = blobUrl;
        link.download = fileName || "offre-de-stage.pdf";
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    const handleView = async (offer) => {
        let pdfBlobUrl = null;

        try {
            const response = await obtenirOffrePDF(offer.id);
            if (response?.ok) {
                const blob = await response.blob();
                pdfBlobUrl = URL.createObjectURL(blob);
            }
        } catch (error) {
            console.error("Erreur lors de la récupération du PDF de l'offre:", error);
        }

        openDrawer(
            <div className="space-y-4 p-2">
                <div className="bg-base-100 p-4 rounded-lg shadow-md space-y-4">
                    <div className="flex justify-between items-start">
                        <h2 className="text-xl font-bold">{offer.title}</h2>
                    </div>

                    <ul className="text-sm space-y-1">
                        <li><strong>{t("offre.employeur")} :</strong> {offer.employeur?.businessName}</li>
                        <li><strong>{t("offre.lieu")} :</strong> {offer.employeur?.town}</li>
                        {offer.salary && <li><strong>{t("offre.salary")} :</strong> {offer.salary} $ / h </li>}
                        <li><strong>{t("offre.date_debut")} :</strong> {offer.startDate}</li>
                        <li><strong>{t("offre.date_fin")} :</strong> {offer.endDate}</li>
                    </ul>

                    <p className="text-sm">
                        <strong>{t("offre.description")} :</strong><br />
                        {offer.description}
                    </p>

                    {pdfBlobUrl ? (
                        <PDFContent cvUrlAAffiche={pdfBlobUrl} width={300} />
                    ) : (
                        <p className="text-xs text-gray-500">{t("offre.document_error")}</p>
                    )}
                </div>

                <div className="flex justify-center items-center">
                    {pdfBlobUrl && (
                        <button
                            type="button"
                            onClick={() => handleDownloadFile(pdfBlobUrl, offer.fileName || `offre_${offer.id}.pdf`)}
                            className="btn btn-primary flex items-center gap-2"
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                            </svg>
                            {t("pdfVisio.download")}
                        </button>
                    )}
                </div>
            </div>
        );
    };

    return (
        <div className="w-full max-w-7xl mx-auto px-4 flex flex-col items-center">
            <div className="w-full max-w-4xl space-y-4 mb-6">
                <SearchBar
                    value={searchTerm}
                    onChange={setSearchTerm}
                    onClear={() => setSearchTerm("")}
                />
                <FilterBar
                    selectedTown={selectedTown}
                    onTownChange={setSelectedTown}
                    availableTowns={availableTowns}
                />
            </div>

            <div className="w-full">
                {etat === "chargement" && (
                    <div className="flex justify-center py-8">
                        <span className="loading loading-spinner loading-lg" />
                    </div>
                )}

                {etat === "erreur" && (
                    <div className="items-center flex justify-center w-full py-8">
                        <p className="text-red-500 font-medium">
                            {t("etudiant.offers.error") || t("offre.no_offers")}
                        </p>
                    </div>
                )}

                {etat === "ok" && (
                    offresFinales.length === 0 ? (
                        <div className="items-center flex justify-center w-full py-8">
                            <p className="text-gray-500 font-medium">
                                {offres.length === 0
                                    ? t("offre.no_offers")
                                    : t("offre.no_matching_offers")}
                            </p>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 justify-center justify-items-center w-full">
                            {offresFinales.map((offer) => (
                                <OffreEtudiant
                                    key={offer.id}
                                    offer={offer}
                                    onView={handleView}
                                />
                            ))}
                        </div>
                    )
                )}
            </div>
        </div>
    );
};

export default ViewOffers;