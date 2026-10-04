import React, { useEffect, useState } from "react";
import { useOutletContext, useNavigate } from "react-router-dom";
import Card from "../../component/Design/Cards/Card.jsx";
import { getOffresEmployeur, obtenirOffrePDF } from "../../api/employeur.jsx";
import { useTranslation } from "react-i18next";
import SearchBar from "../../component/Design/SearchBar.jsx";
import FilterBar from "../../component/Design/FilterBar.jsx";
import { PDFContent } from "../../component/PDF/PDFVisioneuse.jsx";
import { useOffreFilters } from "../../utils/useOffreFilters.jsx";
import { FiRotateCcw } from "react-icons/fi";

const EmployeurHome = () => {
    const { t } = useTranslation();
    const { openDrawer } = useOutletContext();
    const navigate = useNavigate();

    const [offres, setOffres] = useState([]);

    const {
        searchTerm,
        setSearchTerm,
        selectedStatus,
        setSelectedStatus,
        selectedDomain,
        setSelectedDomain,
        availableDomains,
        hasActiveFilters,
        offresFiltrees,
        resetFilters
    } = useOffreFilters(offres);

    const handleDownloadFile = (blobUrl, fileName) => {
        const link = document.createElement("a");
        link.href = blobUrl;
        link.download = fileName || "offre-de-stage.pdf";
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    const renderStatusBadge = (statut) => {
        switch (statut) {
            case "ACCEPTEE":
                return <span className="badge badge-success text-white">{t("status.acceptee")}</span>;
            case "REFUSEE":
                return <span className="badge badge-error text-white">{t("status.refusee")}</span>;
            case "EN_ATTENTE":
            default:
                return <span className="badge badge-warning text-white">{t("status.en_attente")}</span>;
        }
    };

    const handleView = async (offer) => {
        let pdfBlobUrl = null;

        try {
            const response = await obtenirOffrePDF(offer.id);
            if (response.ok) {
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
                        {renderStatusBadge(offer.statut)}
                    </div>
                    {offer.statut === "REFUSEE" && offer.rejectionComment && (
                        <div className="bg-red-50 border-red-500 p-3 rounded">
                            <p className="text-sm font-semibold text-red-700"> {t("offre.rejection_comment")}</p>
                            <p className="text-xs text-red-600 mt-1">{offer.rejectionComment}</p>
                        </div>
                    )}
                    <ul className="text-sm space-y-1">
                        <li><strong>{t("offre.domaine")} :</strong> {offer.domain}</li>
                        <li><strong>{t("offre.date_debut")} :</strong> {offer.startDate}</li>
                        <li><strong>{t("offre.date_fin")} :</strong> {offer.endDate}</li>
                        {offer.salary && <li><strong>{t("offre.salaire", "Salaire")} :</strong> {offer.salary}</li>}
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

    const handleEdit = (offer) => {
        navigate("/employeur/offres/modifier", {
            state: { offer }
        });
    };

    useEffect(() => {
        getOffresEmployeur()
            .then((res) => setOffres(res))
            .catch((err) => console.error(err));
    }, []);

    return (
        <div className="w-full max-w-7xl mx-auto px-4 flex flex-col items-center">
            <div className="w-full max-w-4xl space-y-4 mb-6">
                    <SearchBar
                        value={searchTerm}
                        onChange={setSearchTerm}
                        onClear={() => setSearchTerm("")}
                    />
                <FilterBar
                    selectedStatus={selectedStatus}
                    onStatusChange={setSelectedStatus}
                    selectedDomain={selectedDomain}
                    onDomainChange={setSelectedDomain}
                    availableDomains={availableDomains}
                />
            </div>

            <div className="w-full flex flex-col md:flex-row gap-8 items-start">
                <div className="w-full md:w-2/3 flex justify-center">
                    <div className="flex flex-col gap-6 w-full max-w-xl">
                        {offresFiltrees.length === 0 ? (
                            <div className="items-center flex justify-center w-full py-8">
                                <p className="text-gray-500 font-medium">
                                    {offres.length === 0
                                        ? t("offre.no_offers")
                                        : t("offre.no_matching_offers")}
                                </p>
                            </div>
                        ) : (
                            offresFiltrees.map((offer) => (
                                <Card
                                    key={offer.id}
                                    offer={offer}
                                    onView={handleView}
                                    onEdit={handleEdit}
                                />
                            ))
                        )}
                    </div>
                </div>

                <div className="
                    w-full md:w-1/3 bg-white p-6 rounded-lg shadow-md
                    h-auto md:h-[85vh] lg:h-[90vh]
                    overflow-y-auto sticky top-4
                ">
                    <h3 className="font-semibold text-sm mb-4">Candidatures</h3>
                    <p className="text-xs text-gray-600">Aucune candidature pour le moment</p>
                </div>
            </div>
        </div>
    );
};

export default EmployeurHome;