import { useTranslation } from "react-i18next";
import {useCallback, useEffect, useMemo, useState} from "react";
import DashboardSummaryCard from "../../component/Design/Dashboard/DashboardSummaryCard.jsx";
import DashboardSection from "../../component/Design/Dashboard/DashboardSection.jsx";
import CvListItem from "../../component/Design/Lists/CvListItem.jsx";
import OfferListItem from "../../component/Design/Lists/OfferListItem.jsx";
import ExpandableList from "../../component/Design/Lists/ExpandableList.jsx";
import SearchBar from "../../component/Design/Bars//SearchBar.jsx";
import FilterBar from "../../component/Design/Bars//FilterBar.jsx";
import {getOffresEnAttente, getCvsEnAttente} from "../../api/gestionnaire.jsx";
import {useNavigate} from "react-router-dom";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import {FaBriefcase, FaFileAlt} from "react-icons/fa";
import {useOffreFilters} from "../../utils/useOffreFilters.jsx";

const trierParDateDesc = (items, getDate) => {
    return [...items].sort((itemA, itemB) => {
        const dateA = new Date(getDate(itemA) || 0).getTime();
        const dateB = new Date(getDate(itemB) || 0).getTime();

        return dateB - dateA;
    });
};

const GestionnaireHome = ({user}) => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [cvs, setCvs] = useState([]);
    const [offres, setOffres] = useState([]);
    const [error, setError] = useState(null);
    const [isInitialLoading, setIsInitialLoading] = useState(true);

    const {
        searchTerm,
        setSearchTerm,
        selectedDomain,
        setSelectedDomain,
        availableDomains,
        offresFiltrees,
        cvsFiltres,
    } = useOffreFilters(offres, cvs);

    const cvsFiltresRecents = useMemo(
        () => trierParDateDesc(cvsFiltres, (cv) => cv.uploadDate),
        [cvsFiltres]
    );

    const offresFiltreesRecentes = useMemo(
        () => trierParDateDesc(offresFiltrees, (offer) => offer.createdAt),
        [offresFiltrees]
    );

    const hasActiveFilters = searchTerm.trim() !== "" || selectedDomain !== "TOUS";

    const getSectionCountLabel = (filteredCount, totalCount, singularKey, pluralKey = singularKey) => {
        if (hasActiveFilters) {
            return t(filteredCount > 1 ? "dashboard.results_count" : "dashboard.result_count", {
                count: filteredCount
            });
        }

        return t(totalCount > 1 ? pluralKey : singularKey, { count: totalCount });
    };

    const sectionCountClassName = "rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-[#043462]";

    const handleViewCv = (cv) => {
        navigate("/gestionnaire/cvs/details", {
            state: { cv, from: "/gestionnaire" }
        });
    };

    const handleViewOffer = (offer) => {
        navigate("/gestionnaire/offres/details", {
            state: { offer, from: "/gestionnaire" }
        });
    };

    const chargerCvs = useCallback(async () => {
        try {
            const data = await getCvsEnAttente();
            setCvs(data);
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
                        {t("home.welcome")} {user?.firstName} {user?.lastName} <span aria-hidden="true">👋</span>
                    </h1>
                    <p className="mt-2 text-sm text-gray-500">
                        {t("dashboard.manager_subtitle")}
                    </p>
                </div>

                <SearchBar
                    value={searchTerm}
                    onChange={setSearchTerm}
                    onClear={() => setSearchTerm("")}
                    placeholder={t("dashboard.search_placeholder")}
                    className="mx-auto max-w-4xl"
                />

                <FilterBar
                    selectedDomain={selectedDomain}
                    onDomainChange={setSelectedDomain}
                    availableDomains={availableDomains}
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
                                    />
                                )}
                            />
                        )}
                    </DashboardSection>
                </div>
            </div>
        </div>
    );
}

export default GestionnaireHome;
