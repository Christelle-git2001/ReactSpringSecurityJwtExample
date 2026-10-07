import { useState, useEffect } from "react";
import fetcher from "../../utils/fetcher.js";
import {getOffresEnAttente} from "../../api/gestionnaire.jsx";
import { useOutletContext, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Card from "../../component/Design/Cards/Card.jsx";
import SearchBar from "../../component/Design/SearchBar.jsx";
import FilterBar from "../../component/Design/FilterBar.jsx";
import {useOffreFilters} from "../../utils/useOffreFilters.jsx";

const GestionnaireOffers = () => {
    const { t } = useTranslation();
    const [offres, setOffres] = useState([]);
    const { openDrawer } = useOutletContext();
    const navigate = useNavigate();

    useEffect(() => {
        getOffresEnAttente().then((res) => {setOffres(res);})
            .catch((err) => console.log(err));
    }, []);

    const handleView = (offer) => {
        navigate(`/gestionnaire/offres/details`, { state: { offer } });
    };

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
                    {offresFiltrees.length === 0 ? (
                        <div className="items-center flex justify-center w-full py-8">
                            <p className="text-gray-500 font-medium">
                                {offres.length === 0
                                    ? t("offre.no_offers")
                                    : t("offre.no_matching_offers")}
                            </p>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 justify-center justify-items-center w-full ">
                            {offresFiltrees.map((offer) => (
                                <Card
                                    key={offer.id}
                                    offer={offer}
                                    onView={handleView}
                                />
                            ))}
                        </div>
                    )}
                </div>

            </div>
        </>
    );
}
export default GestionnaireOffers;