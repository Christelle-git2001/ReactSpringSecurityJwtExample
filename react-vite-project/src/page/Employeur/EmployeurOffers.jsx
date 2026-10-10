import React, { useEffect, useState } from "react";
import { useOutletContext, useNavigate } from "react-router-dom";
import OffreStageCard from "../../component/Design/Cards/OffreStageCard.jsx";
import { getOffresEmployeur, obtenirOffrePDF } from "../../api/employeur.jsx";
import { useTranslation } from "react-i18next";
import SearchBar from "../../component/Design/SearchBar.jsx";
import FilterBar from "../../component/Design/FilterBar.jsx";
import { useOffreFilters } from "../../utils/useOffreFilters.jsx";
import OffreDetailsDrawerContent  from "../../component/Offer/OffreDetailsDrawerContent.jsx";

const EmployeurOffers = () => {
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
        offresFiltrees,
    } = useOffreFilters(offres);

    const handleView = async (offer) => {
        openDrawer(<OffreDetailsDrawerContent offer={offer} />);
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
                            <OffreStageCard
                                key={offer.id}
                                offer={offer}
                                onView={handleView}
                                onEdit={handleEdit}
                            />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default EmployeurOffers;