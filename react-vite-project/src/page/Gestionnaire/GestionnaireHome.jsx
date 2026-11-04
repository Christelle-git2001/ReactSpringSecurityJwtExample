import { useTranslation } from "react-i18next";
import { useCallback, useEffect, useMemo, useState } from "react";
import DashboardSummaryCard from "../../component/Design/Dashboard/DashboardSummaryCard.jsx";
import DashboardSection from "../../component/Design/Dashboard/DashboardSection.jsx";
import CvListItem from "../../component/Design/Lists/CvListItem.jsx";
import OfferListItem from "../../component/Design/Lists/OfferListItem.jsx";
import CandidatureListItem from "../../component/Design/Lists/CandidatureListItem.jsx";
import ExpandableList from "../../component/Design/Lists/ExpandableList.jsx";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design/Bars/FilterBar.jsx";
import { getOffresEnAttente, getCvsEnAttente, getCandidaturesByStatus } from "../../api/gestionnaire.jsx";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";
import { useNavigate } from "react-router-dom";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import { FaBriefcase, FaFileAlt, FaFileContract, FaPenNib } from "react-icons/fa";
import { useFilters } from "../../utils/useFilters.jsx";
import WelcomeMessage from "../../component/Design/WelcomeMessage.jsx";

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
    const [candidaturesStageTrouve, setCandidaturesStageTrouve] = useState([]);
    const [candidaturesContratCree, setCandidaturesContratCree] = useState([]);
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

    const handleViewCandidature = (candidature) => {
        navigate(`/gestionnaire/candidatures/${candidature.id}/contrat`, {
            state: { candidature, from: "/gestionnaire" }
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

    const chargerCandidaturesContrat = useCallback(async () => {
        try {
            const [stageTrouveData, contratCreeData] = await Promise.all([
                getCandidaturesByStatus("STAGE_TROUVE"),
                getCandidaturesByStatus("CONTRAT_CREE")
            ]);
            setCandidaturesStageTrouve(stageTrouveData || []);
            setCandidaturesContratCree(contratCreeData || []);
        } catch (err) {
            console.log(err);
        }
    }, []);

    const chargerDonnees = useCallback(async () => {
        await Promise.all([
            chargerCvs(),
            chargerOffres(),
            chargerCandidaturesContrat(),
        ]);
    }, [chargerCvs, chargerOffres, chargerCandidaturesContrat]);

    useEffect(() => {
        chargerDonnees().finally(() => setIsInitialLoading(false));
    }, [chargerDonnees]);

    return (
        <div className="pt-5 h-full">
            <div className="mx-auto flex w-full max-w-7xl flex-col gap-5 px-4">
                <WelcomeMessage user={user}/>

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
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 w-full">
                    <DashboardSummaryCard
                        icon={<FaFileAlt className="size-5" />}
                        value={cvs.length}
                        label={t("dashboard.pending_cvs_count")}
                        tone="blue"
                        ariaLabel={t("dashboard.view_pending_cvs")}
                        className="w-full"
                    />

                    <DashboardSummaryCard
                        icon={<FaBriefcase className="size-5" />}
                        value={offres.length}
                        label={t("dashboard.pending_offers_count")}
                        tone="green"
                        ariaLabel={t("dashboard.view_pending_offers")}
                        className="w-full"
                    />

                    <DashboardSummaryCard
                        icon={<FaFileContract className="size-5" />}
                        value={candidaturesStageTrouve.length}
                        label={t("dashboard.contracts_to_create_count")}
                        tone="cyan"
                        ariaLabel={t("dashboard.view_pending_contracts")}
                        className="w-full"
                    />

                    <DashboardSummaryCard
                        icon={<FaPenNib className="size-5" />}
                        value={candidaturesContratCree.length}
                        label={t("dashboard.contracts_to_sign_count")}
                        tone="purple"
                        ariaLabel={t("dashboard.view_created_contracts")}
                        className="w-full"
                    />
                </div>

                <div className="flex flex-col gap-5 pb-4">
                    <DashboardSection
                        title={t("cv.pending_cvs_title")}
                        collapsible
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
                            <div className="space-y-3">
                                {cvsFiltresRecents.map((cv) => (
                                    <CvListItem
                                        key={cv.id}
                                        cv={cv}
                                        onClick={() => handleViewCv(cv)}
                                        actionLabel={t("dashboard.view_cv")}
                                        actionClassName="bg-[#043462] text-white hover:bg-[#03284d]"
                                    />
                                ))}
                            </div>
                        )}
                    </DashboardSection>

                    <DashboardSection
                        title={t("offre.pending_offers")}
                        collapsible
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
                            <div className="space-y-3">
                                {offresFiltreesRecentes.map((offer) => (
                                    <OfferListItem
                                        key={offer.id}
                                        offer={offer}
                                        onClick={() => handleViewOffer(offer)}
                                        actionLabel={t("dashboard.view_offer")}
                                        actionClassName="bg-[#043462] text-white hover:bg-[#03284d]"
                                    />
                                ))}
                            </div>
                        )}
                    </DashboardSection>
                    <DashboardSection
                        title={t("dashboard.contracts_to_create_title")}
                        headerAction={
                            <span className={sectionCountClassName}>
                                {candidaturesStageTrouve.length} {t("dashboard.applications")}
                            </span>
                        }
                    >
                        {!isInitialLoading && candidaturesStageTrouve.length === 0 ? (
                            <EmptyContentMessage
                                message={t("dashboard.no_contracts_to_create")}
                            />
                        ) : (
                            <ExpandableList
                                items={candidaturesStageTrouve}
                                limit={3}
                                expandLabel={t("dashboard.view_more", "Voir plus")}
                                collapseLabel={t("dashboard.view_less", "Voir moins")}
                                renderItem={(candidature) => (
                                    <CandidatureListItem
                                        key={candidature.id}
                                        candidature={candidature}
                                        onClick={() => handleViewCandidature(candidature)}
                                        actionLabel={t("dashboard.create_contract")}
                                    />
                                )}
                            />
                        )}
                    </DashboardSection>

                    <DashboardSection
                        title={t("dashboard.contracts_to_sign_title")}
                        headerAction={
                            <span className={sectionCountClassName}>
                                {candidaturesContratCree.length} {t("dashboard.applications")}
                            </span>
                        }
                    >
                        {!isInitialLoading && candidaturesContratCree.length === 0 ? (
                            <EmptyContentMessage
                                message={t("dashboard.no_contracts_to_sign")}
                            />
                        ) : (
                            <ExpandableList
                                items={candidaturesContratCree}
                                limit={3}
                                expandLabel={t("dashboard.view_more", "Voir plus")}
                                collapseLabel={t("dashboard.view_less", "Voir moins")}
                                renderItem={(candidature) => (
                                    <CandidatureListItem
                                        key={candidature.id}
                                        candidature={candidature}
                                        onClick={() => handleViewCandidature(candidature)}
                                        actionLabel={t("dashboard.view_contract")}
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