import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import OffreStageCard from "../../component/Design/Cards/OffreStageCard.jsx";
import { getOffresEmployeur, getCandidaturesEmployeur } from "../../api/employeur.jsx";
import { useTranslation } from "react-i18next";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design/Bars/FilterBar.jsx";
import { useFilters } from "../../utils/useFilters.jsx";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";

const EmployeurHome = ({ user }) => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const [offres, setOffres] = useState([]);
    const [candidatures, setCandidatures] = useState([]);
    const [selectedOfferId, setSelectedOfferId] = useState(null);

    const offreFilters = useFilters(offres, {
        getDomain: (o) => o.domain,
        getStatus: (o) => o.statut,
        getSearchableText: (o) => [
            o.title,
            o.description,
            ...candidatures
                .filter((c) => c.offreId === o.id)
                .map((c) => `${c.etudiantFirstName || ""} ${c.etudiantLastName || ""}`),
        ].join(" "),
    });

    const filteredOffres = offreFilters.itemsFiltres.filter((o) => {
        if (!selectedOfferId || selectedOfferId === "TOUS") return true;
        return o.id === selectedOfferId;
    });

    const handleEdit = (offer) => {
        navigate("/employeur/offres/modifier", {
            state: { offer }
        });
    };

    const marquerNotificationCandidatureCommeLue = async (candidatureId) => {
        if (!user?.id) return;
        try {
            const notifications = await getNotifications(user.id);
            const notification = notifications.find(
                (item) =>
                    item.notificationType === "NOUVELLE_CANDIDATURE" &&
                    String(item.variables?.CANDIDATURE_ID) === String(candidatureId) &&
                    !item.estLu
            );

            if (notification) {
                await marquerNotificationLue(notification.id);
            }
        } catch (error) {
            console.error("Erreur lors du marquage de la notification :", error);
        }
    };

    const handleViewCandidature = async (candidature) => {
        await marquerNotificationCandidatureCommeLue(candidature.id);

        const matchedOffer = offres.find((o) => o.id === candidature.offreId);
        navigate(`/employeur/candidatures/details`, {
            state: {
                candidature,
                offer: matchedOffer,
                from: "/employeur"
            }
        });
    };

    useEffect(() => {
        getOffresEmployeur()
            .then((res) => setOffres(res))
            .catch((err) => {
                console.error("Erreur lors du chargement des offres:", err);
                if (err?.message === "UNAUTHORIZED" || err?.status === 401) {
                    navigate("/login");
                }
            });
    }, [navigate]);

    useEffect(() => {
        getCandidaturesEmployeur()
            .then((res) => setCandidatures(res))
            .catch((err) => {
                console.error("Erreur lors du chargement des candidatures:", err);
                if (err?.message === "UNAUTHORIZED" || err?.status === 401) {
                    navigate("/login");
                }
            });
    }, [navigate]);

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
                    onChange={offreFilters.setSearchTerm}
                    onClear={() => offreFilters.setSearchTerm("")}
                />

                <FilterBar
                    selectedStatus={offreFilters.selectedStatus}
                    onStatusChange={offreFilters.setSelectedStatus}
                    selectedDomain={offreFilters.selectedDomain}
                    onDomainChange={offreFilters.setSelectedDomain}
                    availableDomains={offreFilters.availableDomains}
                    selectedOfferId={selectedOfferId}
                    onOfferChange={setSelectedOfferId}
                    availableOffers={offres}
                />
            </div>
            <div className="w-full max-w-7xl">
                <details open className="collapse collapse-arrow bg-transparent">

                    <summary className="collapse-title text-xl font-bold text-[#043462] px-0">
                        <span> {t("nav.view_offers")} </span>
                        <span> ({filteredOffres.length}) </span>
                    </summary>

                    <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                        <div className="my-4 h-px w-full bg-[#043462]" aria-hidden="true" />
                        <div className="w-full px-2 sm:px-4 pb-8">
                            {filteredOffres.length === 0 ? (
                                <EmptyContentMessage
                                    message={
                                        offres.length === 0
                                            ? t("offre.no_offers")
                                            : t("offre.no_matching_offers")
                                    }
                                />
                            ) : (
                                <div className="grid grid-cols-[repeat(auto-fit,minmax(22rem,1fr))] items-start gap-4 w-full mt-4">
                                    {filteredOffres.map((offer) => (
                                        <OffreStageCard
                                            key={offer.id}
                                            offer={offer}
                                            candidatures={candidatures}
                                            onViewCandidature={handleViewCandidature}
                                            onEdit={handleEdit}
                                        />
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </details>
            </div>
        </div>
    );
};

export default EmployeurHome;