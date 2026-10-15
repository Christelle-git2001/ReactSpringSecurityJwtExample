import React, { useEffect, useState } from "react";
import { useOutletContext, useNavigate } from "react-router-dom";
import OffreStageCard from "../../component/Design/Cards/OffreStageCard.jsx";
import CandidatureCard from "../../component/Design/Cards/CandidatureCard.jsx";
import { getOffresEmployeur , getCandidaturesEmployeur} from "../../api/employeur.jsx";
import { useTranslation } from "react-i18next";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design/Bars/FilterBar.jsx";
import { useOffreFilters } from "../../utils/useOffreFilters.jsx";
import OffreDetailsDrawerContent from "../../component/Offer/OffreDetailsDrawerContent.jsx";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";

const EmployeurHome = ({ user }) => {
    const { t } = useTranslation();
    const { openDrawer } = useOutletContext();
    const navigate = useNavigate();

    const [offres, setOffres] = useState([]);
    const [candidatures, setCandidatures] = useState([]);

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

    useEffect(() => {
        getCandidaturesEmployeur()
            .then((res) => setCandidatures(res))
            .catch((err) => console.error(err));
    }, []);

    return (
        <div className="w-full mx-auto px-6 sm:px-8 lg:px-10 py-6 flex flex-col items-center">
            <div className="w-full max-w-7xl">
                <h1 className="welcome-message text-left">
                    {t("home.welcome")} {user?.firstName} {user?.lastName}
                </h1>
            </div>
            <div className="w-full max-w-7xl space-y-4 mb-6 mt-6">
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
            <div className="w-full max-w-7xl">
                <div className="collapse collapse-arrow bg-transparent">
                    <input type="checkbox" className="peer" defaultChecked />

                    <div className="collapse-title text-xl font-bold text-[#043462] px-0">
                        {t("nav.view_offers")}
                    </div>

                    <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                        <div className="w-full px-2 sm:px-4 pb-8">

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
                                            onView={handleView}
                                            onEdit={handleEdit}
                                        />
                                    ))}
                                </div>
                            )}

                        </div>
                    </div>
                </div>
            </div>
            <div className="w-full max-w-7xl">
                <div className="collapse collapse-arrow bg-transparent">
                    <input type="checkbox" className="peer" defaultChecked />

                    <div className="collapse-title text-xl font-bold text-[#043462] px-0">
                        {t("nav.view_offers")}
                    </div>

                    <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                        <div className="w-full px-2 sm:px-4 pb-8">
                            {candidatures.map((candidature) => (
                                <CandidatureCard
                                    Key={candidature.id}
                                    candidature={candidature}
                                />
                            ))}
                        </div>
                    </div>
                </div>
            </div>

        </div>
    );
};

export default EmployeurHome;
