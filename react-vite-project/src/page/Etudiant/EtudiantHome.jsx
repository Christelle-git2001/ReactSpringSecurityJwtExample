import React, { useCallback, useEffect, useState, useMemo, useRef } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import CvOnLandingPage from "../../component/CV/CvOnLandingPage.jsx";
import OffreEtudiantCard from "../../component/Design/Cards/OffreEtudiantCard.jsx";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design/Bars/FilterBar.jsx";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import PageSeparator from "../../component/Design/PageSeparator.jsx";
import StatutEtudiant from "../../component/Candidature/StatutEtudiant.jsx";
import PdfPreviewDialog from "../../component/PDF/PdfPreviewDialog.jsx";
import { useFilters } from "../../utils/useFilters.jsx";
import { obtenirCandidaturesEtudiant, obtenirOffres, obtenirToutesConvocations } from "../../api/etudiant.jsx";
import { getDepartmentKey } from "../../utils/departementConverter.js";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";
import MesCandidatures from "../../component/Candidature/MesCandidatures.jsx";
import Sort from "../../component/Design/Sort.jsx";
import { getCandidatureStatus, OPTIONS_STATUT_CANDIDATURE } from "../../utils/candidatureStatus.js";
import { getStatusConfig } from "../../utils/getStatusConfig.jsx";
import WelcomeMessage from "../../component/Design/WelcomeMessage.jsx";

const EtudiantHome = ({ user }) => {
    const { t } = useTranslation();

    const location = useLocation();
    const navigate = useNavigate();

    const [offres, setOffres] = useState([]);
    const [sortOrder, setSortOrder] = useState("desc");
    const [unreadOfferIds, setUnreadOfferIds] = useState([]);
    const [isInitialLoading, setIsInitialLoading] = useState(true);
    const [error, setError] = useState(null);
    const hasDepartment = Boolean(user?.department);
    const [idsPostules, setIdsPostules] = useState([]);
    const [candidatures, setCandidatures] = useState([]);
    const [convocations, setConvocations] = useState([]);
    const [chargementCandidatures, setChargementCandidatures] = useState(true);
    const [previewUrl, setPreviewUrl] = useState(null);
    const previewDialogRef = useRef(null);

    useEffect(() => {
        if (previewUrl && !previewDialogRef.current?.open) {
            previewDialogRef.current?.showModal();
        }
    }, [previewUrl]);

    const fermerApercu = () => {
        previewDialogRef.current?.close();
        setPreviewUrl(null);
    };

    const offreFilters = useFilters(offres, {
        getDomain: (o) => o.domain,
        getTown: (o) => o?.employeur?.town,
        getStatus: (o) => {
            const candidature = candidatures.find(c => String(c.offreId) === String(o.id));
            return getCandidatureStatus(candidature, convocations.find(c => c.candidatureId === candidature?.id)) ?? "SANS_CANDIDATURE";
        },
        getSearchableText: (o) => `${o.title || ""} ${o.description || ""} ${o?.employeur?.businessName || ""}`,
    });

    useEffect(() => {
        Promise.all([obtenirCandidaturesEtudiant(), obtenirToutesConvocations()])
            .then(([liste, entrevues]) => {
                setConvocations(entrevues ?? []);
                setIdsPostules(liste.map((c) => c.offreId));
                setCandidatures(liste);
            })
            .catch(console.error)
            .finally(() => setChargementCandidatures(false));
    }, []);

    useEffect(() => {
        if (location.state?.candidatureId || location.state?.id) offreFilters.resetFilters();
    }, [location.key]);

    const actualiserCandidatures = useCallback((liste) => {
        setCandidatures(liste);
        setIdsPostules(liste.map(c => c.offreId));
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
            }
            setUnreadOfferIds((prev) =>
                prev.filter((id) => id !== String(offreId))
            );
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

    const { nouvellesOffres, autresOffres } = useMemo(() => {
        const nouvelles = [];
        const autres = [];

        const offresTriees = [...offreFilters.itemsFiltres].sort((a, b) => {
            const comparaison = (a.createdAt ?? "").localeCompare(b.createdAt ?? "") || Number(a.id) - Number(b.id);
            return sortOrder === "asc" ? comparaison : -comparaison;
        });
        offresTriees.forEach((offer) => {
            if (unreadOfferIds.includes(String(offer.id))
                    && !idsPostules.some((id) => String(id) === String(offer.id))) {
                nouvelles.push(offer);
            } else {
                autres.push(offer);
            }
        });

        return {
            nouvellesOffres: nouvelles,
            autresOffres: autres,
        };
    }, [offreFilters.itemsFiltres, unreadOfferIds, idsPostules, sortOrder]);

    return (
        <div className="w-full mx-auto px-6 sm:px-8 lg:px-10 py-6 flex flex-col items-center">
            <PdfPreviewDialog
                dialogRef={previewDialogRef}
                pdfUrl={previewUrl}
                onClose={fermerApercu}
            />
            <CvOnLandingPage />

            <WelcomeMessage user={user}/>

            <div className="w-full max-w-7xl space-y-4 mb-6 mt-6">
                <StatutEtudiant user={user} candidatures={candidatures} loading={chargementCandidatures} />

                <SearchBar
                    value={offreFilters.searchTerm}
                    onChange={offreFilters.setSearchTerm}
                    onClear={() => offreFilters.setSearchTerm("")}
                />

                <FilterBar
                    selectedStatus={offreFilters.selectedStatus}
                    onStatusChange={offreFilters.setSelectedStatus}
                    statusOptions={["TOUS", ...OPTIONS_STATUT_CANDIDATURE]}
                    statusLabels={{ TOUS: t("status.tous"), A_CONFIRMER: t("convocation.interview_to_confirm"),
                        EN_ATTENTE_REPONSE: t("convocation.awaiting_decision"),
                        ...Object.fromEntries(OPTIONS_STATUT_CANDIDATURE.filter(s => !["A_CONFIRMER", "EN_ATTENTE_REPONSE"].includes(s))
                            .map(s => [s, getStatusConfig(s, t, "candidatureEtudiant").label])) }}
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
                        <div className="my-4 h-px w-full bg-[#043462]" aria-hidden="true" />
                        <div className="flex justify-end mb-4">
                            <Sort sortOrder={sortOrder} onSortChange={setSortOrder}
                                  className="border border-[#043462]/20 bg-white text-[#043462] shadow-sm" />
                        </div>
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
                                        {nouvellesOffres.length > 0 && (
                                            <div className="mb-4 flex items-center gap-4">
                                                <div className="h-px flex-1 bg-green-400/50" aria-hidden="true" />
                                                <h3 className="text-base font-bold text-green-800 uppercase">
                                                    {t("offre.new")}
                                                </h3>
                                                <div className="h-px flex-1 bg-green-400/50" aria-hidden="true" />
                                            </div>
                                        )}
                                        <div className="grid grid-cols-[repeat(auto-fit,minmax(min(100%,22rem),1fr))] items-start gap-4 w-full">
                                            {[...nouvellesOffres, ...autresOffres].map((offer, index) => (
                                                <div key={offer.id} className="contents">
                                                    {index === nouvellesOffres.length && index > 0 && (
                                                        <PageSeparator />
                                                    )}
                                                    <OffreEtudiantCard
                                                        user={{ ...user, statut: candidatures.some(c => c.statutEtudiant === "STAGE_TROUVE") ? "STAGE_TROUVE" : user?.statut }}
                                                        key={offer.id}
                                                        offer={offer}
                                                        onPreview={setPreviewUrl}
                                                        onRead={unreadOfferIds.includes(String(offer.id)) ? marquerNotificationOffreCommeLue : undefined}
                                                        hasApplied={idsPostules.includes(offer.id)}
                                                        onApplied={(id) => {
                                                            setIdsPostules((prev) =>
                                                                prev.includes(id) ? prev : [...prev, id]
                                                            );
                                                            obtenirCandidaturesEtudiant().then(actualiserCandidatures).catch(console.error);
                                                        }}
                                                    />
                                                </div>
                                            ))}
                                        </div>
                                    </>
                                )
                            )}
                        </div>
                    </div>
                </details>
                <MesCandidatures candidatures={candidatures} onCandidaturesChange={actualiserCandidatures}
                                 convocations={convocations} onConvocationsChange={setConvocations}
                                 filters={offreFilters} offres={offres} loading={chargementCandidatures} />
            </div>
        </div>
    );
};

export default EtudiantHome;
