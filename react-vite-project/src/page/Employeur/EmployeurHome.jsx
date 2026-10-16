import React, { useEffect, useState } from "react";
import { useOutletContext, useNavigate } from "react-router-dom";
import OffreStageCard from "../../component/Design/Cards/OffreStageCard.jsx";
import CandidatureCard from "../../component/Design/Cards/CandidatureCard.jsx";
import { getOffresEmployeur, getCandidaturesEmployeur } from "../../api/employeur.jsx";
import { useTranslation } from "react-i18next";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design/Bars/FilterBar.jsx";
import { useFilters } from "../../utils/useFilters.jsx";
import OffreDetailsDrawerContent from "../../component/Offer/OffreDetailsDrawerContent.jsx";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";

const EmployeurHome = ({ user }) => {
    const { t } = useTranslation();
    const { openDrawer } = useOutletContext();
    const navigate = useNavigate();

    const [offres, setOffres] = useState([]);
    const [candidatures, setCandidatures] = useState([]);

    const offreFilters = useFilters(offres, {
        getDomain: (o) => o.domain,
        getStatus: (o) => o.statut,
        getSearchableText: (o) => `${o.title} ${o.description}`,
    });

    const candidatureFilters = useFilters(candidatures, {
        getDomain: (c) => c.etudiantDepartment,
        getSearchableText: (c) => `${c.etudiantFirstName || ""} ${c.etudiantLastName || ""} ${c.offreTitle || ""}`,
    });

    const handleSearchChange = (term) => {
        offreFilters.setSearchTerm(term);
        candidatureFilters.setSearchTerm(term);
    };

    const handleDomainChange = (domain) => {
        offreFilters.setSelectedDomain(domain);
        candidatureFilters.setSelectedDomain(domain);
    };

    const handleStatusChange = (status) => {
        offreFilters.setSelectedStatus(status);
        candidatureFilters.setSelectedStatus(status);
    };

    const handleView = async (offer) => {
        openDrawer(<OffreDetailsDrawerContent offer={offer} />);
    };

    const handleEdit = (offer) => {
        navigate("/employeur/offres/modifier", {
            state: { offer }
        });
    };

    const handleViewCandidature = (candidature) => {
        const matchedOffer = offres.find((o) => o.id === candidature.offreId);
        navigate(`/employeur/candidatures/details`, {
            state: {
                candidature,
                offer: matchedOffer,
                from: "/employeur"
            }
        });
    };

    const handleViewOfferFromCandidature = (candidature) => {
        const matchedOffer = offres.find((o) => o.id === candidature.offreId);
        if (matchedOffer) {
            openDrawer(<OffreDetailsDrawerContent offer={matchedOffer} />);
        } else {
            console.warn("Offre non trouvée pour l'ID :", candidature.offreId);
        }
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

    const combinedDomains = Array.from(
        new Set([...offreFilters.availableDomains, ...candidatureFilters.availableDomains])
    );

    return (
        <div className="w-full mx-auto px-6 sm:px-8 lg:px-10 py-6 flex flex-col items-center">
            <div className="w-full max-w-7xl">
                <h1 className="welcome-message text-left">
                    {t("home.welcome")} {user?.firstName} {user?.lastName}
                </h1>
            </div>

            <div className="w-full max-w-7xl space-y-4 mb-6 mt-6">
                <SearchBar
                    value={offreFilters.searchTerm}
                    onChange={handleSearchChange}
                    onClear={() => handleSearchChange("")}
                />

                <FilterBar
                    selectedStatus={offreFilters.selectedStatus}
                    onStatusChange={handleStatusChange}
                    selectedDomain={offreFilters.selectedDomain}
                    onDomainChange={handleDomainChange}
                    availableDomains={combinedDomains}
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
                            {offreFilters.itemsFiltres.length === 0 ? (
                                <EmptyContentMessage
                                    message={
                                        offres.length === 0
                                            ? t("offre.no_offers")
                                            : t("offre.no_matching_offers")
                                    }
                                />
                            ) : (
                                <div className="grid grid-cols-[repeat(auto-fit,minmax(22rem,1fr))] gap-4 w-full mt-4">
                                    {offreFilters.itemsFiltres.map((offer) => (
                                        <OffreStageCard
                                            key={offer.id}
                                            offer={offer}
                                            candidatures={candidatures}
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
            <div className="w-full max-w-7xl mt-4">
                <div className="collapse collapse-arrow bg-transparent">
                    <input type="checkbox" className="peer" defaultChecked />

                    <div className="collapse-title text-xl font-bold text-[#043462] px-0">
                        {t("candidature.list_title")}
                    </div>

                    <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                        <div className="w-full px-2 sm:px-4 pb-8">
                            {candidatureFilters.itemsFiltres.length === 0 ? (
                                <EmptyContentMessage
                                    message={
                                        candidatures.length === 0
                                            ? t("candidature.no_candidatures")
                                            : t("candidature.no_matching_candidatures")
                                    }
                                />
                            ) : (
                                <div className="flex flex-col gap-3 mt-4">
                                    {candidatureFilters.itemsFiltres.map((candidature) => (
                                        <CandidatureCard
                                            key={candidature.id}
                                            candidature={candidature}
                                            onView={handleViewCandidature}
                                            onViewOffer={handleViewOfferFromCandidature}
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

export default EmployeurHome;