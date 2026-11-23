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
import { getOffresEnAttente, getCvsEnAttente, getCandidaturesByStatus, getContratByCandidatureId, rappelerSignatureContrat } from "../../api/gestionnaire.jsx";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";
import { useNavigate } from "react-router-dom";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import { useFilters } from "../../utils/useFilters.jsx";
import WelcomeMessage from "../../component/Design/WelcomeMessage.jsx";
import Sort from "../../component/Design/Sort.jsx";
import toast from "react-hot-toast";
import DashboardMetricSection from "../../component/Design/Dashboard/DashboardMetricSection.jsx";
import {gestionnaireStatsMock, metricConfig} from "../../config/MetricConfig.jsx";

const trierParDate = (items, getDate, ordre) => {
    return [...items].sort((itemA, itemB) => {
        const dateA = new Date(getDate(itemA) || 0).getTime();
        const dateB = new Date(getDate(itemB) || 0).getTime();

        return ordre === "asc" ? dateA - dateB : dateB - dateA;
    });
};

const GestionnaireHome = ({ user }) => {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    const [cvs, setCvs] = useState([]);
    const [offres, setOffres] = useState([]);
    const [candidaturesContratCree, setCandidaturesContratCree] = useState([]);
    const [error, setError] = useState(null);
    const [sortContratsOrder, setSortContratsOrder] = useState("desc");
    const [sortCvsOrder, setSortCvsOrder] = useState("desc");
    const [sortOffresOrder, setSortOffresOrder] = useState("desc");
    const [isInitialLoading, setIsInitialLoading] = useState(true);
    const allowedSections = metricConfig.filter(
        (section) => !section.roles || section.roles.includes(user.role)
    );
    const [activeTabId, setActiveTabId] = useState(allowedSections[0]?.id ?? "");
    const activeCategory =
        allowedSections.find((cat) => cat.id === activeTabId) ?? allowedSections[0];
    const sectionTabs = allowedSections.map((category) => ({
        id: category.id,
        label: t(category.title),
    }));
    const nombreContratsSignes = candidaturesContratCree.filter(c => c.contratSigne).length;
    const nombreContratsASigner = candidaturesContratCree.filter(c => c.contratASigner).length;

    const contratsTries = useMemo(() => [...candidaturesContratCree].sort((a, b) => {
        const dateA = Date.parse(a.dateCreationContrat || "");
        const dateB = Date.parse(b.dateCreationContrat || "");
        if (Number.isNaN(dateA) !== Number.isNaN(dateB)) return Number.isNaN(dateA) ? 1 : -1;
        const comparaison = (Number.isNaN(dateA) ? 0 : dateA - dateB) || (a.contratId || 0) - (b.contratId || 0);
        return sortContratsOrder === "asc" ? comparaison : -comparaison;
    }), [candidaturesContratCree, sortContratsOrder]);

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
        () => trierParDate(cvFilters.itemsFiltres, (cv) => cv.uploadDate, sortCvsOrder),
        [cvFilters.itemsFiltres, sortCvsOrder]
    );

    const offresFiltreesRecentes = useMemo(
        () => trierParDate(offreFilters.itemsFiltres, (offer) => offer.createdAt, sortOffresOrder),
        [offreFilters.itemsFiltres, sortOffresOrder]
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
            const contratCreeData = await getCandidaturesByStatus("CONTRAT_CREE");
            const candidaturesAvecContrat = await Promise.all((contratCreeData || []).map(async candidature => {
                try {
                    const contrat = await getContratByCandidatureId(candidature.id);
                    if (!contrat) return null;
                    const premieresSignaturesCompletes = Boolean(contrat.dateSignatureEtudiant && contrat.dateSignatureEmployeur);
                    return {
                        ...candidature,
                        contratId: contrat.id,
                        dateCreationContrat: contrat.createdAt,
                        contratASigner: premieresSignaturesCompletes && !contrat.dateSignatureGestionnaire,
                        contratSigne: premieresSignaturesCompletes && Boolean(contrat.dateSignatureGestionnaire),
                    };
                } catch (err) {
                    console.error(err);
                    return null;
                }
            }));
            setCandidaturesContratCree(candidaturesAvecContrat.filter(Boolean));
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

    const handleRappelerSignature = async (candidature) => {
        try {
            const nombre = await rappelerSignatureContrat(candidature.contratId);
            toast.success(t(nombre > 0 ? "dashboard.reminder_sent" : "dashboard.no_signature_to_remind"));
        } catch {
            toast.error(t("error.generic"));
        }
    };

    const formaterDateFooter = (value, labelKey) => {
        const date = value ? new Date(value) : null;
        if (!date || Number.isNaN(date.getTime())) return null;
        return (
            <span>{t(labelKey)} <time dateTime={value}>
                {date.toLocaleString(i18n.language || "fr", { dateStyle: "short", timeStyle: "short" })}
            </time></span>
        );
    };

    useEffect(() => {
        if (!allowedSections.some((cat) => cat.id === activeTabId)) {
            setActiveTabId(allowedSections[0]?.id ?? "");
        }
    }, [allowedSections, activeTabId]);
    console.log("activeCategory:", activeCategory);
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

                <DashboardMetricSection
                    tabs={sectionTabs}
                    activeTabId={activeTabId}
                    tone={activeCategory.tone}
                    onTabChange={setActiveTabId}
                >
                    {activeCategory.cards.map((card) => (
                        <DashboardSummaryCard
                            key={card.id}
                            icon={card.icon}
                            tone={activeCategory.tone}
                            label={t(card.label)}
                            value={gestionnaireStatsMock?.[activeCategory.dataKey]?.[card.key] ?? 0}
                        />
                    ))}
                </DashboardMetricSection>

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
                                <div className="flex justify-end">
                                    <Sort sortOrder={sortCvsOrder} onSortChange={setSortCvsOrder} />
                                </div>
                                {cvsFiltresRecents.map((cv) => (
                                    <CvListItem
                                        key={cv.id}
                                        cv={cv}
                                        footer={formaterDateFooter(cv.uploadDate, "dashboard.cv_uploaded_on")}
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
                                <div className="flex justify-end">
                                    <Sort sortOrder={sortOffresOrder} onSortChange={setSortOffresOrder} />
                                </div>
                                {offresFiltreesRecentes.map((offer) => (
                                    <OfferListItem
                                        key={offer.id}
                                        offer={offer}
                                        footer={formaterDateFooter(offer.createdAt, "dashboard.offer_created_on")}
                                        onClick={() => handleViewOffer(offer)}
                                        actionLabel={t("dashboard.view_offer")}
                                        actionClassName="bg-[#043462] text-white hover:bg-[#03284d]"
                                    />
                                ))}
                            </div>
                        )}
                    </DashboardSection>
                    <DashboardSection
                        title={t("dashboard.contracts_title")}
                        headerAction={
                            <span className={sectionCountClassName}>
                                {t(candidaturesContratCree.length > 1 ? "dashboard.application_count_plural" : "dashboard.application_count", { count: candidaturesContratCree.length })}
                            </span>
                        }
                    >
                        {!isInitialLoading && candidaturesContratCree.length === 0 ? (
                            <EmptyContentMessage
                                message={t("dashboard.no_contracts_to_sign")}
                            />
                        ) : (
                            <div>
                                <div className="mb-3 flex justify-end">
                                    <Sort sortOrder={sortContratsOrder} onSortChange={setSortContratsOrder}
                                          title={t("dashboard.sort_contract_creation")} />
                                </div>
                            <ExpandableList
                                items={contratsTries}
                                limit={3}
                                expandLabel={t("dashboard.view_more", "Voir plus")}
                                collapseLabel={t("dashboard.view_less", "Voir moins")}
                                renderItem={(candidature) => (
                                    <CandidatureListItem
                                        key={candidature.id}
                                        candidature={candidature}
                                        onClick={() => handleViewCandidature(candidature)}
                                        actionLabel={t("dashboard.view_contract")}
                                        onRappel={handleRappelerSignature}
                                    />
                                )}
                            />
                            </div>
                        )}
                    </DashboardSection>
                </div>
            </div>
        </div>
    );
};

export default GestionnaireHome;