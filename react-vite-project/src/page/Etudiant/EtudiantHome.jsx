import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useOutletContext } from "react-router-dom";
import CvOnLandingPage from "../../component/CV/CvOnLandingPage.jsx";
import OffreEtudiantCard from "../../component/Design/Cards/OffreEtudiantCard.jsx";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design/Bars//FilterBar.jsx";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import OffreDetailsDrawerContent from "../../component/Offer/OffreDetailsDrawerContent.jsx";
import { useOffreFilters } from "../../utils/useOffreFilters.jsx";
import {obtenirCandidaturesEtudiant, obtenirOffres} from "../../api/etudiant.jsx";
import { getDepartmentKey } from "../../utils/departementConverter.js";

const EtudiantHome = ({ user }) => {
    const { t } = useTranslation();
    const { openDrawer } = useOutletContext();

    const [offres, setOffres] = useState([]);
    const [selectedTown, setSelectedTown] = useState("TOUS");
    const [isInitialLoading, setIsInitialLoading] = useState(true);
    const [error, setError] = useState(null);
    const hasDepartment = Boolean(user?.department);
    const [idsPostules, setIdsPostules] = useState([]);

    useEffect(() => {
        obtenirCandidaturesEtudiant()
            .then((liste) => setIdsPostules(liste.map((c) => c.offreId)))
            .catch((console.error))
    }, []);

    const {
        searchTerm,
        setSearchTerm,
        offresFiltrees: offresFiltreesHook,
    } = useOffreFilters(offres);

    const handleView = async (offer) => {
        openDrawer(<OffreDetailsDrawerContent
            offer={offer}
            showStatus={false}
            hasApplied={idsPostules.includes(offer.id)}
            onApplied={(id) => setIdsPostules((prev) =>
                prev.includes(id) ? prev : [...prev, id]
            )}

        />);
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
        if (!user?.department) return;

        try {
            const data = await obtenirOffres(getDepartmentKey(user.department));
            setOffres(data || []);
            setError(null);
        } catch (error) {
            console.error("Erreur lors du chargement des offres:", error);
            setError("etudiant.offers.error");
        }
    }, [user?.department]);

    useEffect(() => {
        if (!hasDepartment) {
            setIsInitialLoading(true);
            return;
        }

        chargerOffres().finally(() => setIsInitialLoading(false));
    }, [chargerOffres, hasDepartment]);

    return (
        <div className="w-full mx-auto px-6 sm:px-8 lg:px-10 py-6 flex flex-col items-center">
            <CvOnLandingPage />

            <div className="w-full max-w-7xl">
                <h1 className="welcome-message text-left">
                    {t("home.welcome")} {user?.firstName} {user?.lastName}{" "}
                    <span aria-hidden="true"></span>
                </h1>
            </div>

            <div className="w-full max-w-7xl space-y-4 mb-6 mt-6">
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

            <div className="w-full max-w-7xl">
                <div className="collapse collapse-arrow bg-transparent">
                    <input type="checkbox" className="peer" defaultChecked />

                    <div className="collapse-title text-xl font-bold text-[#043462] px-0">
                        {t("nav.view_offers")}
                    </div>

                    <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                        <div className="w-full px-2 sm:px-4 pb-8">
                            {!isInitialLoading && hasDepartment && error && (
                                <EmptyContentMessage
                                    message={t("etudiant.offers.cv_approval_required")}
                                    textClassName="text-gray-500 font-medium"
                                />
                            )}

                            {!isInitialLoading && hasDepartment && !error && (
                                offresFinales.length === 0 ? (
                                    <EmptyContentMessage
                                        message={
                                            offres.length === 0
                                                ? t("offre.no_offers")
                                                : t("offre.no_matching_offers")
                                        }
                                    />
                                ) : (
                                    <div className="grid grid-cols-[repeat(auto-fit,minmax(22rem,1fr))] gap-4 w-full mt-4">
                                        {offresFinales.map((offer) => (
                                            <OffreEtudiantCard
                                                key={offer.id}
                                                offer={offer}
                                                onView={handleView}
                                                hasApplied={idsPostules.includes(offer.id)}
                                                onApplied={(id) => setIdsPostules((prev) =>
                                                    prev.includes(id) ? prev : [...prev, id]
                                                )}
                                            />
                                        ))}
                                    </div>
                                )
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default EtudiantHome;