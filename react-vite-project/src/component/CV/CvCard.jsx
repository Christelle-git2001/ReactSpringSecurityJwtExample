import React, {useState} from "react";
import Button from "../Design/Button.jsx";
import { useTranslation } from "react-i18next";
import { formaterDate, formaterTailleFichier } from "../../utils/CvFormatters.jsx";
import { FiChevronDown, FiChevronUp } from "react-icons/fi";
import ShowDocument from "../PDF/ShowDocument.jsx";
import { obtenirCvPDFGestionnaire } from "../../api/http.jsx";
import { getCvStatusConfig } from "../../utils/cvStatusConfig.jsx";

function CvCard({ cv, onApprove, onReject }) {
    const { t } = useTranslation();
    const [isOpen, setIsOpen] = useState(false);
    const [error, setError] = useState(null);
    const [showCommentaire, setShowCommentaire] = useState(false);
    const [commentaires, setCommentaires] = useState("");

    const isAccepted = cv.statut === "ACCEPTEE";
    const isRejected = cv.statut === "REFUSEE";
    const isFinalStatus = isAccepted || isRejected;

    const status = getCvStatusConfig(cv?.statut, t);

    const getStudentName = () => {
        if (cv.etudiant?.firstName || cv.etudiant?.lastName) {
            return `${cv.etudiant?.firstName || ""} ${cv.etudiant?.lastName || ""}`.trim();
        }

        return t("cv.student_unknown");
    };

    const handleApprove = async () => {
        setError("");
        onApprove(cv.id);
    };

    const handleReject = async () => {
        if (!commentaires.trim()) {
            setShowCommentaire(true);
            setError(t("cv.rejection_comment_required"));
            return;
        }

        setError(null);
        onReject(cv.id, commentaires);
    };

    return (
        <div className="rounded-xl border border-base-300 bg-base-100 p-5 mt-5 shadow-sm bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">
            <div className="flex items-start justify-between gap-4">
                <div className="min-w-0 flex-1">
                    <div className="mb-2">
                        <span className={`badge badge-xs ${status.className}`}>
                            {status.label}
                        </span>
                    </div>
                    <p className="text-2l font-bold text-[#043462]">
                        {getStudentName()}
                    </p>

                    <p className="mt-1 text-sm text-gray-600">
                        {cv.fileName || t("cv.file_without_name")}
                    </p>
                </div>

                <div className="flex shrink-0 flex-wrap items-center justify-end gap-2">
                    {!isFinalStatus && (
                        <button
                            type="button"
                            onClick={() => {
                                setIsOpen(true);
                                setShowCommentaire(!showCommentaire);
                            }}
                            className="text-sm font-semibold text-black underline transition-colors hover:text-[#043462] cursor-pointer"
                        >
                            {t("cv.leave_comment")}
                        </button>
                    )}

                    <Button
                        type="button"
                        onClick={handleReject}
                        disabled={isFinalStatus}
                        className="bg-red-600 text-white cursor-pointer hover:bg-red-700"
                    >
                        {t("actions.reject")}
                    </Button>

                    <Button
                        type="button"
                        onClick={handleApprove}
                        disabled={isFinalStatus}
                        className="bg-green-600 text-white cursor-pointer hover:bg-green-800"
                    >
                        {t("actions.approve")}
                    </Button>

                    <button
                        type="button"
                        onClick={() => setIsOpen(!isOpen)}
                        className="btn btn-ghost btn-xs btn-circle text-base-content/70 hover:text-primary"
                        aria-label={
                            isOpen
                                ? t("actions.collapse")
                                : t("actions.expand")
                        }
                    >
                        {isOpen ? (
                            <FiChevronUp className="size-5 stroke-[3]" />
                        ) : (
                            <FiChevronDown className="size-5 stroke-[3]" />
                        )}
                    </button>
                </div>
            </div>

            {isOpen && (
                <div className="mt-6 border-t border-gray-100 pt-4">
                    <ul className="flex flex-col gap-2 text-xs">
                        <li>
                            <strong className="text-[#043462]">
                                {t("cv.file_size")}
                            </strong>{" "}
                            : {formaterTailleFichier(cv.fileSize)}
                        </li>

                        <li>
                            <strong className="text-[#043462]">
                                {t("cv.upload_date")}
                            </strong>{" "}
                            : {formaterDate(cv.uploadDate)}
                        </li>
                    </ul>

                    {cv.rejectionComment && (
                        <div className="mt-4">
                            <p className="text-xs font-bold text-[#043462]">
                                {t("cv.previous_rejection_comment")}
                            </p>
                            <p className="mt-1 whitespace-pre-line rounded-lg bg-gray-50/70 px-3 py-2 text-sm text-gray-800">
                                {cv.rejectionComment}
                            </p>
                        </div>
                    )}

                    <div className="mt-4">
                        <ShowDocument
                            fileName={cv?.fileName}
                            loadPdfResponse={() => obtenirCvPDFGestionnaire(cv?.id)}
                        />
                    </div>
                </div>
            )}

            {isOpen && showCommentaire && !isFinalStatus && (
                <div className="mt-4 border-t border-red-200 pt-4">
                    <label
                        htmlFor={`rejectionComment-${cv.id}`}
                        className="text-sm font-semibold text-red-600"
                    >
                        {t("cv.rejection_comment")}{" "}
                        <span className="text-red-700">*</span>
                    </label>

                    <textarea
                        id={`rejectionComment-${cv.id}`}
                        rows="3"
                        value={commentaires}
                        onChange={(e) => {
                            setCommentaires(e.target.value);
                            setError("");
                        }}
                        placeholder={t("cv.rejection_comment_placeholder")}
                        className="mt-1 w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-[#043462] focus:outline-none"
                    />

                    {error && (
                        <p className="mt-1 text-xs font-medium text-red-500">
                            {error}
                        </p>
                    )}
                </div>
            )}
        </div>
    );
}
export default CvCard;
