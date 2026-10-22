import { useTranslation } from "react-i18next";
import { useCallback, useEffect, useMemo, useState } from "react";
import DashboardSummaryCard from "../../component/Design/Dashboard/DashboardSummaryCard.jsx";
import DashboardSection from "../../component/Design/Dashboard/DashboardSection.jsx";
import CvListItem from "../../component/Design/Lists/CvListItem.jsx";
import OfferListItem from "../../component/Design/Lists/OfferListItem.jsx";
import ExpandableList from "../../component/Design/Lists/ExpandableList.jsx";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design/Bars/FilterBar.jsx";
import { getOffresEnAttente, getCvsEnAttente } from "../../api/gestionnaire.jsx";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";
import { useNavigate } from "react-router-dom";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import { FaBriefcase, FaFileAlt } from "react-icons/fa";
import { useFilters } from "../../utils/useFilters.jsx";

const trierParDateDesc = (items, getDate) => {
    return [...items].sort((itemA, itemB) => {
        const dateA = new Date(getDate(itemA) || 0).getTime();
        const dateB = new Date(getDate(itemB) || 0).getTime();

        return dateB - dateA;
    });
};

const GestionnaireHome = ({ user }) => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [cvs, setCvs] = useState([]);
    const [offres, setOffres] = useState([]);
    const [error, setError] = useState(null);
    const [isInitialLoading, setIsInitialLoading] = useState(true);

    const cvFilters = useFilters(cvs, {
        getDomain: (cv) => cv.etudiant?.department,
        getSearchableText: (cv) => `${cv.etudiant?.firstName || ""} ${cv.etudiant?.lastName || ""} ${cv.fileName || ""}`,
    });

    const offreFilters = useFilters(offres, {
        getDomain: (o) => o.domain,
        getStatus: (o) => o.statut,
        getSearchableText: (o) => `${o.title || ""} ${o.description || ""}`,
    });

    const handleSearchChange = (term) => {
        cvFilters.setSearchTerm(term);
        offreFilters.setSearchTerm(term);
    };

    const handleDomainChange = (domain) => {
        cvFilters.setSelectedDomain(domain);
        offreFilters.setSelectedDomain(domain);
    };

    const combinedDomains = useMemo(() => {
        return Array.from(
            new Set([...cvFilters.availableDomains, ...offreFilters.availableDomains])
        );
    }, [cvFilters.availableDomains, offreFilters.availableDomains]);

    const cvsFiltresRecents = useMemo(
        () => trierParDateDesc(cvFilters.itemsFiltres, (cv) => cv.uploadDate),
        [cvFilters.itemsFiltres]
    );

    const offresFiltreesRecentes = useMemo(
        () => trierParDateDesc(offreFilters.itemsFiltres, (offer) => offer.createdAt),
        [offreFilters.itemsFiltres]
    );

    const hasActiveFilters = cvFilters.searchTerm.trim() !== "" || cvFilters.selectedDomain !== "TOUS";

    const getSectionCountLabel = (filteredCount, totalCount, singularKey, pluralKey = singularKey) => {
        if (hasActiveFilters) {
            return t(filteredCount > 1 ? "dashboard.results_count" : "dashboard.result_count", {
                count: filteredCount
            });
        }

        return t(totalCount > 1 ? pluralKey : singularKey, { count: totalCount });
    };

    const sectionCountClassName = "rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-[#043462]";

    const marquerNotificationLueSiExiste = async (type, id) => {
        try {
            const notifications = await getNotifications(user.id);
            const notification = notifications.find(
                (n) =>
                    n.notificationType === type &&
                    !n.estLu &&
                    String(n.urlCible).endsWith(`/${id}`)
            );

            if (notification) {
                await marquerNotificationLue(notification.id);
            }
        } catch (err) {
            console.error("Erreur lors du marquage de la notification :", err);
        }
    };

    const handleViewCv = async (cv) => {
        await marquerNotificationLueSiExiste("CV_ATTENTE_VALIDATION", cv.id);
        navigate("/gestionnaire/cvs/details", {
            state: {cv, from: "/gestionnaire"}
        });
    };

    const handleViewOffer = async (offer) => {
        await marquerNotificationLueSiExiste("OFFRE_ATTENTE_VALIDATION", offer.id);
        navigate("/gestionnaire/offres/details", {
            state: {offer, from: "/gestionnaire"}
        });
    };

    const chargerCvs = useCallback(async () => {
        try {
            const data = await getCvsEnAttente();
            setCvs(data || []);
            setError(null);
        } catch (err) {
            setError("error.generic");
        }
    }, []);

    const chargerOffres = useCallback(async () => {
        try {
            const data = await getOffresEnAttente();
            setOffres(data || []);
        } catch (err) {
            console.log(err);
        }
    }, []);

    const chargerDonnees = useCallback(async () => {
        await Promise.all([
            chargerCvs(),
            chargerOffres(),
        ]);
    }, [chargerCvs, chargerOffres]);

    useEffect(() => {
        chargerDonnees().finally(() => setIsInitialLoading(false));
    }, [chargerDonnees]);

    return (
        <div className="pt-5 h-full">
            <div className="mx-auto flex w-full max-w-7xl flex-col gap-5 px-4">
                <div>
                    <h1 className="text-4xl font-bold text-[#043462]">
                        {t("home.welcome")} {user?.firstName} {user?.lastName}
                    </h1>
                    <p className="mt-2 text-sm text-gray-500">
                        {t("dashboard.manager_subtitle")}
                    </p>
                </div>

                <SearchBar
                    value={cvFilters.searchTerm}
                    onChange={handleSearchChange}
                    onClear={() => handleSearchChange("")}
                    placeholder={t("dashboard.search_placeholder")}
                    className="mx-auto max-w-4xl"
                />

                <FilterBar
                    selectedDomain={cvFilters.selectedDomain}
                    onDomainChange={handleDomainChange}
                    availableDomains={combinedDomains}
                    className="mx-auto max-w-4xl"
                />

                <div className="flex w-full flex-wrap justify-start gap-4">
                    <DashboardSummaryCard
                        icon={<FaFileAlt className="size-5" />}
                        value={cvs.length}
                        label={t("dashboard.pending_cvs_count")}
                        tone="blue"
                        ariaLabel={t("dashboard.view_pending_cvs")}
                        className="w-full sm:w-[260px]"
                    />

                    <DashboardSummaryCard
                        icon={<FaBriefcase className="size-5" />}
                        value={offres.length}
                        label={t("dashboard.pending_offers_count")}
                        tone="green"
                        ariaLabel={t("dashboard.view_pending_offers")}
                        className="w-full sm:w-[260px]"
                    />
                </div>

                <div className="flex flex-col gap-5 pb-4">
                    <DashboardSection
                        title={t("cv.pending_cvs_title")}
                        headerAction={
                            <span className={sectionCountClassName}>
                                {getSectionCountLabel(cvsFiltresRecents.length, cvs.length, "dashboard.cv_count", "dashboard.cv_count_plural")}
                            </span>
                        }
                    >
                        {!isInitialLoading && cvsFiltresRecents.length === 0 && !error ? (
                            <EmptyContentMessage
                                message={cvs.length === 0 ? t("cv.none_pending") : t("offre.no_matching_offers")}
                            />
                        ) : (
                            <ExpandableList
                                items={cvsFiltresRecents}
                                limit={3}
                                expandLabel={t("dashboard.view_more", "Voir plus")}
                                collapseLabel={t("dashboard.view_less", "Voir moins")}
                                renderItem={(cv) => (
                                    <CvListItem
                                        cv={cv}
                                        onClick={() => handleViewCv(cv)}
                                        actionLabel={t("dashboard.view_cv")}
                                        actionClassName="bg-blue-600 text-white hover:bg-blue-700"
                                    />
                                )}
                            />
                        )}
                    </DashboardSection>

                    <DashboardSection
                        title={t("offre.pending_offers")}
                        headerAction={
                            <span className={sectionCountClassName}>
                                {getSectionCountLabel(offresFiltreesRecentes.length, offres.length, "dashboard.offer_count", "dashboard.offer_count_plural")}
                            </span>
                        }
                    >
                        {!isInitialLoading && offresFiltreesRecentes.length === 0 ? (
                            <EmptyContentMessage
                                message={offres.length === 0 ? t("offre.no_offers") : t("offre.no_matching_offers")}
                            />
                        ) : (
                            <ExpandableList
                                items={offresFiltreesRecentes}
                                limit={3}
                                expandLabel={t("dashboard.view_more", "Voir plus")}
                                collapseLabel={t("dashboard.view_less", "Voir moins")}
                                renderItem={(offer) => (
                                    <OfferListItem
                                        offer={offer}
                                        onClick={() => handleViewOffer(offer)}
                                        actionLabel={t("dashboard.view_offer")}
                                        actionClassName="bg-green-600 text-white hover:bg-green-700"
                                    />
                                )}
                            />
                        )}
                    </DashboardSection>
                </div>
            </div>
        </div>
    );
};

export default GestionnaireHome;