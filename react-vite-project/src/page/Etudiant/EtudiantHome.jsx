import React, { useCallback, useEffect, useState, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import CvOnLandingPage from "../../component/CV/CvOnLandingPage.jsx";
import OffreEtudiantCard from "../../component/Design/Cards/OffreEtudiantCard.jsx";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design/Bars/FilterBar.jsx";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import { useFilters } from "../../utils/useFilters.jsx";
import { obtenirCandidaturesEtudiant, obtenirOffres } from "../../api/etudiant.jsx";
import { getDepartmentKey } from "../../utils/departementConverter.js";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";

const EtudiantHome = ({ user }) => {
    const { t } = useTranslation();

    const location = useLocation();
    const navigate = useNavigate();

    const [offres, setOffres] = useState([]);
    const [unreadOfferIds, setUnreadOfferIds] = useState([]);
    const [isInitialLoading, setIsInitialLoading] = useState(true);
    const [error, setError] = useState(null);
    const hasDepartment = Boolean(user?.department);
    const [idsPostules, setIdsPostules] = useState([]);

    const offreFilters = useFilters(offres, {
        getDomain: (o) => o.domain,
        getTown: (o) => o?.employeur?.town,
        getStatus: (o) => o.statut,
        getSearchableText: (o) => `${o.title || ""} ${o.description || ""} ${o?.employeur?.businessName || ""}`,
    });

    useEffect(() => {
        obtenirCandidaturesEtudiant()
            .then((liste) => setIdsPostules(liste.map((c) => c.offreId)))
            .catch(console.error);
    }, []);

    const chargerNotificationsOffres = useCallback(async () => {
        if (!user?.id) return;
        try {
            const notifications = await getNotifications(user.id);
            const ids = notifications
                .filter(
                    (n) =>
                        !n.estLu &&
                        n.notificationType === "NOUVELLE_OFFRE_STAGE"
                )
                .map((n) => String(n.variables?.OFFRE_ID));
            setUnreadOfferIds(ids);
        } catch (err) {
            console.error("Erreur lors de la récupération des notifications:", err);
        }
    }, [user?.id]);

    useEffect(() => {
        chargerNotificationsOffres();
    }, [chargerNotificationsOffres]);

    const marquerNotificationOffreCommeLue = useCallback(async (offreId) => {
        if (!user?.id) return;
        try {
            const notifications = await getNotifications(user.id);
            const notification = notifications.find(
                (item) =>
                    item.notificationType === "NOUVELLE_OFFRE_STAGE" &&
                    String(item.variables?.OFFRE_ID) === String(offreId) &&
                    !item.estLu
            );

            if (notification) {
                await marquerNotificationLue(notification.id);
                setUnreadOfferIds((prev) =>
                    prev.filter((id) => id !== String(offreId))
                );
            }
        } catch (error) {
            console.error("Erreur lors du marquage :", error);
        }
    }, [user?.id]);

    const ouvrirOffreParId = useCallback((offreId) => {
        const offre = offres.find(
            (item) => String(item.id) === String(offreId)
        );

        if (offre) {
            document.getElementById(`offre-${offre.id}`)?.scrollIntoView({ behavior: "smooth", block: "nearest" });
            marquerNotificationOffreCommeLue(offre.id);
        }
    }, [offres, marquerNotificationOffreCommeLue]);

    useEffect(() => {
        const offreId = location.state?.offreId;

        if (!offreId || isInitialLoading) return;

        ouvrirOffreParId(offreId);

        navigate(location.pathname, {
            replace: true,
            state: null,
        });
    }, [location.state, location.pathname, isInitialLoading, offres, navigate, ouvrirOffreParId]);

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

    const { offresNonConsultees, autresOffres } = useMemo(() => {
        const nonConsultees = [];
        const autres = [];

        offreFilters.itemsFiltres.forEach((offer) => {
            if (unreadOfferIds.includes(String(offer.id))) {
                nonConsultees.push(offer);
            } else {
                autres.push(offer);
            }
        });

        return {
            offresNonConsultees: nonConsultees,
            autresOffres: autres,
        };
    }, [offreFilters.itemsFiltres, unreadOfferIds]);

    return (
        <div className="w-full mx-auto px-6 sm:px-8 lg:px-10 py-6 flex flex-col items-center">
            <CvOnLandingPage />

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
                    selectedTown={offreFilters.selectedTown}
                    onTownChange={offreFilters.setSelectedTown}
                    availableTowns={offreFilters.availableTowns}
                />
            </div>

            <div className="w-full max-w-7xl">
                <details open className="collapse collapse-arrow bg-transparent">

                    <summary className="collapse-title text-xl font-bold text-[#043462] px-0">
                        {t("nav.view_offers")}
                    </summary>

                    <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                        <div className="w-full px-2 sm:px-4 pb-8 space-y-8">
                            {!isInitialLoading && hasDepartment && error && (
                                <EmptyContentMessage
                                    message={t("etudiant.offers.cv_approval_required")}
                                    textClassName="text-gray-500 font-medium"
                                />
                            )}

                            {!isInitialLoading && hasDepartment && !error && (
                                offreFilters.itemsFiltres.length === 0 ? (
                                    <EmptyContentMessage
                                        message={
                                            offres.length === 0
                                                ? t("offre.no_offers")
                                                : t("offre.no_matching_offers")
                                        }
                                    />
                                ) : (
                                    <>
                                        {offresNonConsultees.length > 0 && (
                                            <div className="grid grid-cols-[repeat(auto-fit,minmax(22rem,1fr))] gap-4 w-full">
                                                {offresNonConsultees.map((offer) => (
                                                    <OffreEtudiantCard
                                                        key={offer.id}
                                                        offer={offer}
                                                        onRead={marquerNotificationOffreCommeLue}
                                                        hasApplied={idsPostules.includes(offer.id)}
                                                        onApplied={(id) =>
                                                            setIdsPostules((prev) =>
                                                                prev.includes(id) ? prev : [...prev, id]
                                                            )
                                                        }
                                                    />
                                                ))}
                                            </div>
                                        )}

                                        {offresNonConsultees.length > 0 && autresOffres.length > 0 && (
                                            <div className="flex items-center gap-4 my-6">
                                                <div className="h-px bg-emerald-600 flex-1" />
                                                <span className="text-xs font-semibold text-emerald-600 uppercase tracking-wider">
                                                    {t("offre.new")}
                                                </span>
                                                <div className="h-px bg-emerald-600 flex-1" />
                                            </div>
                                        )}

                                        {autresOffres.length > 0 && (
                                            <div className="grid grid-cols-[repeat(auto-fit,minmax(22rem,1fr))] gap-4 w-full">
                                                {autresOffres.map((offer) => (
                                                    <OffreEtudiantCard
                                                        key={offer.id}
                                                        offer={offer}
                                                        hasApplied={idsPostules.includes(offer.id)}
                                                        onApplied={(id) =>
                                                            setIdsPostules((prev) =>
                                                                prev.includes(id) ? prev : [...prev, id]
                                                            )
                                                        }
                                                    />
                                                ))}
                                            </div>
                                        )}
                                    </>
                                )
                            )}
                        </div>
                    </div>
                </details>
            </div>
        </div>
    );
};

export default EtudiantHome;