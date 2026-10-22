import { useTranslation } from "react-i18next";
import { FiStar } from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import { getDepartmentKey } from "../../utils/departementConverter.js";
import { getStatusConfig } from "../../utils/getStatusConfig.jsx";
import "../../css/EtudiantHome.css";
import PostulerButton from "../Design/PostulerButton.jsx";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";

function OffreDetailsDrawerContent({
                                       offer,
                                       candidatures = [],
                                       userId,
                                       showStatus = true,
                                       hasApplied = false,
                                       onApplied,
                                       onCloseDrawer
                                   }) {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const isTargeted = Boolean(offer?.isTarget);

    const departmentKey = getDepartmentKey(offer?.domain);
    const translatedDepartment = departmentKey
        ? t(`departement.${departmentKey}`, offer?.domain)
        : offer?.domain;

    const status = getStatusConfig(offer?.statut, t);

    const employeurDisplay = offer?.employeur
        ? `${offer.employeur.businessName ? ` ${offer.employeur.businessName}` : ""}`
        : null;

    const etudiantDisplay = offer?.etudiant
        ? `${offer.etudiant.firstName} ${offer.etudiant.lastName}`
        : offer?.etudiantName;

    const candidaturesOffre = candidatures.filter(c => c.offreId === offer?.id);

    const marquerNotificationCommeLue = async (candidatureId) => {
        if (!userId) return;
        try {
            const notifications = await getNotifications(userId);
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

    const handleSelectCandidature = async (candidature) => {
        await marquerNotificationCommeLue(candidature.id);
        onCloseDrawer?.();
        navigate("/employeur/candidatures/details", {
            state: {
                candidature,
                offer,
                from: "/employeur"
            }
        });
    };

    return (
        <div className="space-y-4 p-2">
            <div className="relative bg-base-100 p-4 rounded-lg shadow-md space-y-4">

                <div className="flex justify-between items-start gap-3">
                    <h2 className="text-xl font-bold text-[#043462] break-words flex-1">
                        {offer?.title}
                    </h2>

                    {showStatus && (
                        <span
                            title={status.label}
                            className={`badge ${status.className} text-white shrink-0 max-w-[150px] truncate whitespace-nowrap`}
                        >
                            {status.label}
                        </span>
                    )}
                </div>

                {showStatus && offer?.statut === "REFUSEE" && offer?.rejectionComment && (
                    <div className="bg-red-50 border border-red-200 p-3 rounded-lg">
                        <p className="text-sm font-semibold text-red-700">
                            {t("offre.rejection_comment")}
                        </p>
                        <p className="text-xs text-red-600 mt-1 break-words">
                            {offer.rejectionComment}
                        </p>
                    </div>
                )}

                <ul className="text-sm space-y-1.5">
                    {isTargeted && (
                        <>
                            {employeurDisplay && (
                                <li>
                                    <strong className="text-[#043462]">{t("add_offer.targeted_company", "Employeur")} :</strong> {employeurDisplay}
                                </li>
                            )}
                            {etudiantDisplay && (
                                <li>
                                    <strong className="text-[#043462]">{t("add_offer.targeted_student", "Étudiant ciblé")} :</strong> {etudiantDisplay}
                                </li>
                            )}
                        </>
                    )}

                    <li><strong className="text-[#043462]">{t("offre.domaine")} :</strong> {translatedDepartment}</li>
                    <li><strong className="text-[#043462]">{t("offre.date_debut")} :</strong> {offer?.startDate}</li>
                    <li><strong className="text-[#043462]">{t("offre.date_fin")} :</strong> {offer?.endDate}</li>
                    <li><strong className="text-[#043462]">{t("offre.date_fin_affichage")} :</strong> {offer?.displayEndDate}</li>
                    {offer?.salary && (
                        <li><strong className="text-[#043462]">{t("offre.salary")} :</strong> {offer.salary} $ / h</li>
                    )}
                </ul>

                <p className="text-sm pt-2 border-t border-gray-100">
                    <strong className="text-[#043462]">{t("offre.description")} :</strong><br />
                    <span className="whitespace-pre-line text-gray-700 break-words">{offer?.description}</span>
                </p>

                {showStatus && (
                    <div className="pt-4 border-t border-gray-100 space-y-2">
                        <h3 className="font-bold text-sm text-[#043462]">
                            {t("candidature.list_title")} ({candidaturesOffre.length})
                        </h3>

                        {candidaturesOffre.length === 0 ? (
                            <p className="text-xs text-gray-500 italic">
                                {t("candidature.no_candidatures")}
                            </p>
                        ) : (
                            <div className="flex flex-col gap-1.5 max-h-40 overflow-y-auto">
                                {candidaturesOffre.map((cand) => (
                                    <button
                                        key={cand.id}
                                        type="button"
                                        onClick={() => handleSelectCandidature(cand)}
                                        className="text-left text-xs font-medium text-[#043462] hover:text-[#00CCCB] hover:underline p-1 rounded transition-colors"
                                    >
                                        {cand.etudiantFirstName} {cand.etudiantLastName}
                                    </button>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {!showStatus && (
                    <div className="pt-4 border-t border-gray-100">
                        <PostulerButton
                            offerId={offer?.id}
                            hasApplied={hasApplied}
                            onApplied={onApplied}
                        />
                    </div>
                )}
            </div>
        </div>
    );
}

export default OffreDetailsDrawerContent;