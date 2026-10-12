import { useTranslation } from "react-i18next";
import {useCallback, useEffect, useMemo, useState} from "react";
import DashboardSummaryCard from "../../component/Design/Dashboard/DashboardSummaryCard.jsx";
import DashboardSection from "../../component/Design/Dashboard/DashboardSection.jsx";
import CvListItem from "../../component/Design/Lists/CvListItem.jsx";
import OfferListItem from "../../component/Design/Lists/OfferListItem.jsx";
import ExpandableList from "../../component/Design/Lists/ExpandableList.jsx";
import {getOffresEnAttente, getCvsEnAttente} from "../../api/gestionnaire.jsx";
import {useNavigate} from "react-router-dom";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import {isNewSince} from "../../utils/isNewItem.jsx";
import {FaBriefcase, FaFileAlt} from "react-icons/fa";

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

    const cvsRecents = useMemo(
        () => trierParDateDesc(cvs, (cv) => cv.uploadDate),
        [cvs]
    );

    const offresRecentes = useMemo(
        () => trierParDateDesc(offres, (offer) => offer.createdAt),
        [offres]
    );

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

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
                    <DashboardSection title={`${t("cv.pending_cvs_title")} (${cvs.length})`}>
                        {!isInitialLoading && cvs.length === 0 && !error ? (
                            <EmptyContentMessage message={t("cv.none_pending")} />
                        ) : (
                            <ExpandableList
                                items={cvsRecents}
                                limit={6}
                                expandLabel={t("dashboard.view_more", "Voir plus")}
                                collapseLabel={t("dashboard.view_less", "Voir moins")}
                                renderItem={(cv) => (
                                    <CvListItem
                                        cv={cv}
                                        onClick={() => handleViewCv(cv)}
                                        isNew={isNewSince(cv.uploadDate, user?.previousLoginAt)}
                                        actionLabel={t("dashboard.view_cv")}
                                    />
                                )}
                            />
                        )}
                    </DashboardSection>

                    <DashboardSection title={`${t("offre.pending_offers")} (${offres.length})`}>
                        {!isInitialLoading && offres.length === 0 ? (
                            <EmptyContentMessage message={t("offre.no_offers")} />
                        ) : (
                            <ExpandableList
                                items={offresRecentes}
                                limit={6}
                                expandLabel={t("dashboard.view_more", "Voir plus")}
                                collapseLabel={t("dashboard.view_less", "Voir moins")}
                                renderItem={(offer) => (
                                    <OfferListItem
                                        offer={offer}
                                        onClick={() => handleViewOffer(offer)}
                                        isNew={isNewSince(offer.createdAt, user?.previousLoginAt)}
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
