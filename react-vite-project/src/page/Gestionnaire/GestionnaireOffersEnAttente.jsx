import { useCallback, useState } from "react";
import { getOffresEnAttente } from "../../api/gestionnaire.jsx";
import { useNavigate } from "react-router-dom";
import OffreStageCard from "../../component/Design/Cards/OffreStageCard.jsx";
import SearchBar from "../../component/Design/SearchBar.jsx";
import FilterBar from "../../component/Design/FilterBar.jsx";
import { useOffreFilters } from "../../utils/useOffreFilters.jsx";
import AddOfferRapide from "../../config/AddOfferRapide.jsx";
import { isNewSince } from "../../utils/isNewItem.jsx";
import GestionnaireItemsPage, { useGestionnaireItems } from "../../utils/GestionnaireItemsPage.jsx";

const GestionnaireOffersEnAttente = ({ user }) => {
    const navigate = useNavigate();
    const [error, setError] = useState(null);

    const handleLoadError = useCallback((err) => {
        setError(err ? "error.generic" : null);
    }, []);

    const {
        items: offres,
        reloadItems: chargerOffres,
        targetItemId: targetOfferId,
    } = useGestionnaireItems({
        fetchItems: getOffresEnAttente,
        targetParamName: "offerId",
        targetElementPrefix: "offer",
        onError: handleLoadError,
    });

    const handleView = (offer) => {
        navigate("/gestionnaire/offres/details", { state: { offer, from: "/gestionnaire/offers/attente" } });
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
        <GestionnaireItemsPage
            controls={
                <>
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
                </>
            }
            errorMessage={error}
            items={offres}
            displayedItems={offresAffichees}
            targetItemId={targetOfferId}
            targetElementPrefix="offer"
            emptyItemsMessageKey="offre.no_offers"
            emptyFilteredMessageKey="offre.no_matching_offers"
            beforeList={<AddOfferRapide onCreated={chargerOffres}/>}
            listClassName="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 justify-center justify-items-center w-full"
            renderItem={(offer) => (
                <OffreStageCard
                    offer={offer}
                    onView={handleView}
                    isNew={isNewSince(offer.createdAt, user?.previousLoginAt)}
                />
            )}
        />
    );
}
export default GestionnaireOffersEnAttente;