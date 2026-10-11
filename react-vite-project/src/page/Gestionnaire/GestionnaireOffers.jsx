import React, { useCallback, useState, useEffect } from "react";
import {getOffresEnAttente} from "../../api/gestionnaire.jsx";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import OffreStageCard from "../../component/Design/Cards/OffreStageCard.jsx";
import SearchBar from "../../component/Design/SearchBar.jsx";
import FilterBar from "../../component/Design/FilterBar.jsx";
import {useOffreFilters} from "../../utils/useOffreFilters.jsx";
import AddOfferRapide from "../../config/AddOfferRapide.jsx";
import { isNewSince } from "../../utils/isNewItem.jsx";

const GestionnaireOffers = ({ user }) => {
    const { t } = useTranslation();
    const [searchParams] = useSearchParams();
    const targetOfferId = Number(searchParams.get("offerId"));
    const [offres, setOffres] = useState([]);
    const navigate = useNavigate();

    const chargerOffres = useCallback(() => {
        getOffresEnAttente().then((res) => {setOffres(res);})
            .catch((err) => console.log(err));
    }, []);

    useEffect(() => {
        chargerOffres();
    }, [chargerOffres]);

    useEffect(() => {
        if (!targetOfferId || offres.length === 0) return;

        const targetElement = document.getElementById(`offer-${targetOfferId}`);
        targetElement?.scrollIntoView({ behavior: "smooth", block: "center" });
    }, [targetOfferId, offres]);

    const handleView = (offer) => {
        navigate(`/gestionnaire/offres/details`, { state: { offer, from:"/gestionnaire/offers" } });
    };

    const {
        searchTerm,
        setSearchTerm,
        selectedDomain,
        setSelectedDomain,
        availableDomains,
        offresFiltrees,
    } = useOffreFilters(offres);

    const offresAffichees = targetOfferId && !offresFiltrees.some((offer) => offer.id === targetOfferId)
        ? [
            ...offres.filter((offer) => offer.id === targetOfferId),
            ...offresFiltrees,
        ]
        : offresFiltrees;


    return (
        <>
            <div className="w-full max-w-7xl mx-auto px-4 flex flex-col items-center">
                <div className="w-full max-w-4xl space-y-4 mb-6">
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
                <div className="w-full">
                    <AddOfferRapide onCreated={chargerOffres}/>
                    {offresAffichees.length === 0 ? (
                        <div className="items-center flex justify-center w-full py-8">
                            <p className="text-gray-500 font-medium">
                                {offres.length === 0
                                    ? t("offre.no_offers")
                                    : t("offre.no_matching_offers")}
                            </p>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 justify-center justify-items-center w-full ">
                            {offresAffichees.map((offer) => (
                                <div
                                    key={offer.id}
                                    id={`offer-${offer.id}`}
                                    className={offer.id === targetOfferId ? "scroll-mt-24 rounded-xl ring-2 ring-[#00CCCB] ring-offset-4" : "scroll-mt-24"}
                                >
                                    <OffreStageCard
                                        offer={offer}
                                        onView={handleView}
                                        isNew={isNewSince(offer.createdAt, user?.previousLoginAt)}
                                    />
                                </div>
                            ))}
                        </div>
                    )}
                </div>

            </div>
        </>
    );
}
export default GestionnaireOffers;
