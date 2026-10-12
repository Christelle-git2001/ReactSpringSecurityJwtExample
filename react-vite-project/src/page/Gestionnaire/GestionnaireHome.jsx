import { useTranslation } from "react-i18next";
import {useCallback, useEffect, useMemo, useState} from "react";
import DashboardSummaryCard from "../../component/Design/Cards/Dashboard/DashboardSummaryCard.jsx";
import DashboardCvItem from "../../component/Design/Cards/Dashboard/DashboardCvItem.jsx";
import DashboardOfferItem from "../../component/Design/Cards/Dashboard/DashboardOfferItem.jsx";
import {getOffresEnAttente, getCvsEnAttente} from "../../api/gestionnaire.jsx";
import {useNavigate} from "react-router-dom";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import {isNewSince} from "../../utils/isNewItem.jsx";
import {FaBriefcase, FaFileAlt} from "react-icons/fa";
import {FiChevronDown, FiChevronUp} from "react-icons/fi";

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
    const [showRecentActivity, setShowRecentActivity] = useState(true);

    const cvsRecents = useMemo(
        () => trierParDateDesc(cvs, (cv) => cv.uploadDate),
        [cvs]
    );

    const offresRecentes = useMemo(
        () => trierParDateDesc(offres, (offer) => offer.createdAt),
        [offres]
    );

    const viewCvInList = (cv) => {
        navigate("/gestionnaire/cvs/attente", { state: { targetItemId: cv.id } });
    };

    const viewOfferInList = (offer) => {
        navigate("/gestionnaire/offers/attente", { state: { targetItemId: offer.id } });
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

                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    <DashboardSummaryCard
                        icon={<FaFileAlt className="size-6" />}
                        value={cvs.length}
                        label={t("dashboard.pending_cvs_count")}
                        tone="blue"
                        ariaLabel={t("dashboard.view_pending_cvs")}
                    />

                    <DashboardSummaryCard
                        icon={<FaBriefcase className="size-6" />}
                        value={offres.length}
                        label={t("dashboard.pending_offers_count")}
                        tone="green"
                        ariaLabel={t("dashboard.view_pending_offers")}
                    />
                </div>

                <div className="rounded-xl border border-gray-100 bg-white shadow-sm p-5">
                    <div className="mb-4 flex items-center justify-between gap-3">
                        <h2 className="text-lg font-bold text-[#043462]">
                            {t("dashboard.recent_activity")}
                        </h2>
                        <button
                            type="button"
                            onClick={() => setShowRecentActivity((current) => !current)}
                            className="btn btn-circle btn-sm border-none bg-gray-50 text-[#043462] hover:bg-gray-100"
                            aria-label={
                                showRecentActivity
                                    ? t("actions.collapse")
                                    : t("actions.expand")
                            }
                        >
                            {showRecentActivity ? (
                                <FiChevronUp className="size-4" />
                            ) : (
                                <FiChevronDown className="size-4" />
                            )}
                        </button>
                    </div>
                    <div className="mb-4 h-px w-full bg-[#043462]" />

                    {showRecentActivity && (
                        <div className="space-y-3">
                            {cvsRecents.slice(0, 3).map((cv) => (
                                <DashboardCvItem
                                    key={`cv-${cv.id}`}
                                    cv={cv}
                                    onClick={() => viewCvInList(cv)}
                                    variant="row"
                                    isNew={isNewSince(cv.uploadDate, user?.previousLoginAt)}
                                    actionLabel={t("dashboard.view_cv")}
                                />
                            ))}

                            {offresRecentes.slice(0, 3).map((offer) => (
                                <DashboardOfferItem
                                    key={`offer-${offer.id}`}
                                    offer={offer}
                                    onClick={() => viewOfferInList(offer)}
                                    variant="row"
                                    isNew={isNewSince(offer.createdAt, user?.previousLoginAt)}
                                    actionLabel={t("dashboard.view_offer")}
                                />
                            ))}

                            {!isInitialLoading && cvs.length === 0 && offres.length === 0 && (
                                <EmptyContentMessage message={t("dashboard.no_recent_activity")} />
                            )}
                        </div>
                    )}
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
                    <div className="rounded-xl border border-gray-100 bg-white shadow-sm p-5">
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="font-bold text-[#043462]">
                                {t("cv.pending_cvs_title")}
                            </h2>
                            <button
                                type="button"
                                onClick={() => navigate("/gestionnaire/cvs/attente")}
                                className="text-xs font-semibold text-blue-600 hover:underline hover:underline-offset-4"
                            >
                                {t("dashboard.view_all")} ({cvs.length})
                            </button>
                        </div>
                        <div className="mb-4 h-px w-full bg-[#043462]" />

                        <div className="space-y-3">
                            {!isInitialLoading && cvs.length === 0 && !error && (
                                <EmptyContentMessage message={t("cv.none_pending")} />
                            )}

                            {cvsRecents.slice(0, 3).map((cv) => (
                                <DashboardCvItem
                                    key={cv.id}
                                    cv={cv}
                                    onClick={() => viewCvInList(cv)}
                                    isNew={isNewSince(cv.uploadDate, user?.previousLoginAt)}
                                    actionLabel={t("dashboard.view_cv")}
                                />
                            ))}
                        </div>
                    </div>

                    <div className="rounded-xl border border-gray-100 bg-white shadow-sm p-5">
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="font-bold text-[#043462]">
                                {t("offre.pending_offers")}
                            </h2>
                            <button
                                type="button"
                                onClick={() => navigate("/gestionnaire/offers/attente")}
                                className="text-xs font-semibold text-green-600 hover:underline hover:underline-offset-4"
                            >
                                {t("dashboard.view_all_feminine")} ({offres.length})
                            </button>
                        </div>
                        <div className="mb-4 h-px w-full bg-[#043462]" />

                        <div className="space-y-3">
                            {!isInitialLoading && offres.length === 0 && (
                                <EmptyContentMessage message={t("offre.no_offers")} />
                            )}

                            {offresRecentes.slice(0, 3).map((offer) => (
                                <DashboardOfferItem
                                    key={offer.id}
                                    offer={offer}
                                    onClick={() => viewOfferInList(offer)}
                                    isNew={isNewSince(offer.createdAt, user?.previousLoginAt)}
                                    actionLabel={t("dashboard.view_offer")}
                                />
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default GestionnaireHome;