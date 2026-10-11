import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import OffreEtudiantCard from "../../component/Design/Cards/OffreEtudiantCard.jsx";
import SearchBar from "../../component/Design/SearchBar.jsx";
import FilterBar from "../../component/Design/FilterBar.jsx";
import { useOffreFilters } from "../../utils/useOffreFilters.jsx";
import { obtenirOffres } from "../../api/etudiant.jsx";
import { getDepartmentKey } from "../../utils/departementConverter.js";
import OffreDetailsDrawerContent from "../../component/Offer/OffreDetailsDrawerContent.jsx";
import {useOutletContext} from "react-router-dom";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";

const EtudiantOffers = ({ user, showFilters = true }) => {
    const { t } = useTranslation();
    const [offres, setOffres] = useState([]);
    const [selectedTown, setSelectedTown] = useState("TOUS");

    const {
        searchTerm,
        setSearchTerm,
        offresFiltrees: offresFiltreesHook,
    } = useOffreFilters(offres);

    const { openDrawer } = useOutletContext();

    const handleView = async (offer) => {
        openDrawer(<OffreDetailsDrawerContent offer={offer} />);
    };

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

    const chargerOffres = useCallback(async () => {
        if (!user?.department) return

        setIsLoading(true);
        setError(null);
        try {
            const data = await obtenirOffres(getDepartmentKey(user.department));
            setOffres(data || []);
        } catch (error) {
            console.error("Erreur lors du chargement des offres:", error);
            setError("etudiant.offers.error");
        } finally {
            setIsLoading(false);
        }
    }, [user?.department]);

    useEffect(() => {
        chargerOffres();
    }, [chargerOffres]);

    return (
        <div className="w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 flex flex-col items-center">
            {showFilters && (
                <div className="w-full max-w-4xl space-y-4 mt-6 mb-6">
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
            )}

            <div className="w-full">
                {isLoading && (
                    <div className="flex justify-center py-8">
                        <span className="loading loading-spinner loading-lg" />
                    </div>
                )}

                {!isLoading && error && (
                    <EmptyContentMessage
                        message={t("etudiant.offers.error")}
                        textClassName="text-red-500 font-medium"
                    />
                )}

                {!isLoading && !error && (
                    offresFinales.length === 0 ? (
                        <EmptyContentMessage
                            message={
                                offres.length === 0
                                    ? t("offre.no_offers")
                                    : t("offre.no_matching_offers")
                            }
                        />
                    ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 justify-center justify-items-center w-full">
                            {offresFinales.map((offer) => (
                                <OffreEtudiantCard
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

export default EtudiantOffers;