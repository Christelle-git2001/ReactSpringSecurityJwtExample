import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { getCvsEnAttente, getOffresEnAttente } from "../../api/gestionnaire.jsx";
import SearchBar from "../../component/Design/SearchBar.jsx";
import FilterBar from "../../component/Design/FilterBar.jsx";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import CvCard from "../../component/Design/Cards/CvCard.jsx";
import OffreStageCard from "../../component/Design/Cards/OffreStageCard.jsx";
import { useOffreFilters } from "../../utils/useOffreFilters.jsx";

const GestionnaireHome = ({ user }) => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const [cvs, setCvs] = useState([]);
    const [offres, setOffres] = useState([]);

    const chargerCvs = useCallback(async () => {
        try {
            const data = await getCvsEnAttente();
            setCvs(data || []);
        } catch (err) {
            console.error("Erreur lors du chargement des CVs:", err);
        }
    }, []);

    const chargerOffres = useCallback(async () => {
        try {
            const data = await getOffresEnAttente();
            setOffres(data || []);
        } catch (err) {
            console.error("Erreur lors du chargement des offres:", err);
        }
    }, []);

    const chargerDonnees = useCallback(async () => {
        await Promise.all([chargerCvs(), chargerOffres()]);
    }, [chargerCvs, chargerOffres]);

    useEffect(() => {
        chargerDonnees();

        const intervalId = setInterval(() => {
            chargerDonnees();
        }, 10000);

        return () => clearInterval(intervalId);
    }, [chargerDonnees]);

    const handleViewOffer = (offer) => {
        navigate("/gestionnaire/offres/details", {
            state: { offer, from: "/gestionnaire" }
        });
    };

    const handleViewCv = (cv) => {
        navigate("/gestionnaire/cvs/details", {
            state: { cv, from: "/gestionnaire" }
        });
    };

    const {
        searchTerm,
        setSearchTerm,
        selectedDomain,
        setSelectedDomain,
        availableDomains,
        offresFiltrees,
        cvsFiltres,
    } = useOffreFilters(offres, cvs);

    return (
        <div className="w-full mx-auto px-6 sm:px-8 lg:px-10 py-6 flex flex-col items-center">
            <div className="w-full max-w-7xl">
                <h1 className="welcome-message text-left text-4xl font-bold text-[#043462]">
                    {t("home.welcome")} {user?.firstName} {user?.lastName} <span aria-hidden="true"></span>
                </h1>
                <p className="mt-2 text-sm text-gray-500">
                    {t("dashboard.manager_subtitle")}
                </p>
            </div>

            <div className="w-full max-w-7xl space-y-4 mb-6 mt-6">
                <SearchBar
                    value={searchTerm}
                    onChange={setSearchTerm}
                    onClear={() => setSearchTerm("")}
                />

                <FilterBar
                    selectedDomain={selectedDomain}
                    onDomainChange={setSelectedDomain}
                    availableDomains={availableDomains}
                />
            </div>

            <div className="w-full max-w-7xl space-y-6">
                <div className="collapse collapse-arrow bg-transparent">
                    <input type="checkbox" className="peer" defaultChecked />

                    <div className="collapse-title text-xl font-bold text-[#043462] px-0">
                        {t("offre.pending_offers")} ({offresFiltrees.length})
                    </div>

                    <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                        <div className="w-full pb-8">
                            {offresFiltrees.length === 0 ? (
                                <EmptyContentMessage
                                    message={
                                        offres.length === 0
                                            ? t("offre.no_offers")
                                            : t("offre.no_matching_offers")
                                    }
                                />
                            ) : (
                                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 justify-items-center w-full mt-4">
                                    {offresFiltrees.map((offer) => (
                                        <OffreStageCard
                                            key={offer.id}
                                            offer={offer}
                                            onView={handleViewOffer}
                                            showPdfActions={false}
                                        />
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
                <div className="collapse collapse-arrow bg-transparent">
                    <input type="checkbox" className="peer" defaultChecked />

                    <div className="collapse-title text-xl font-bold text-[#043462] px-0">
                        {t("cv.pending_cvs_title")} ({cvsFiltres.length})
                    </div>

                    <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                        <div className="w-full pb-8">
                            {cvsFiltres.length === 0 ? (
                                <EmptyContentMessage
                                    message={
                                        cvs.length === 0
                                            ? t("cv.none_pending")
                                            : t("cv.no_matching_cvs", "Aucun CV ne correspond aux critères de recherche.")
                                    }
                                />
                            ) : (
                                <div className="space-y-4 w-full max-w-3xl mt-4">
                                    {cvsFiltres.map((cv) => (
                                        <CvCard
                                            key={cv.id}
                                            cv={cv}
                                            onView={handleViewCv}
                                        />
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default GestionnaireHome;