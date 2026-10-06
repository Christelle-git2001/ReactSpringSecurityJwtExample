import React, {useState} from "react";
import Button from "../Design/Button.jsx";
import { useTranslation } from "react-i18next";
import { formaterDate, formaterTailleFichier } from "../../utils/CvFormatters.jsx";
import { FiChevronDown, FiChevronUp } from "react-icons/fi";

function ReadOnlyField({ label, value, isMultiline = false }) {
    return (
        <div className="flex flex-col gap-1.5">
            <span className="text-sm font-semibold text-[#043462]">
                {label}
            </span>
            <div
                className={`w-full rounded-lg border border-gray-200 bg-gray-50/70 px-3.5 py-2.5 text-sm text-gray-800 font-medium ${
                    isMultiline ? "whitespace-pre-line min-h-[100px]" : ""
                }`}
            >
                {value || "-"}
            </div>
        </div>
    );
}

function CvCard({ cv, onApprove, onReject }) {
    const { t } = useTranslation();
    const [isOpen, setIsOpen] = useState(false);
    const [error, setError] = useState(null);
    const [showCommentaire, setShowCommentaire] = useState(false);
    const [commentaires, setCommentaires] = useState("");

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
        <div className="rounded-xl border border-base-300 bg-base-100 p-5 shadow-sm">
            <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                <div className="flex items-start gap-3">
                    <button
                        type="button"
                        onClick={() => setIsOpen(!isOpen)}
                        className="mt-1 flex h-8 w-8 items-center justify-center rounded-md border border-gray-300 text-[#043462] hover:bg-gray-100"
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

                    <div>
                        <p className="font-bold text-[#043462]">
                            {getStudentName()}
                        </p>

                        <p className="text-sm text-gray-600">
                            {cv.fileName || t("cv.file_without_name")}
                        </p>
                    </div>
                </div>

                <div className="flex flex-wrap items-center gap-3">
                    <button
                        type="button"
                        onClick={() => setShowCommentaire(!showCommentaire)}
                        className="text-sm font-semibold text-black underline transition-colors hover:text-[#043462]"
                    >
                        {t("cv.leave_comment")}
                    </button>

                    <Button
                        type="button"
                        onClick={handleReject}
                        className="bg-red-600 text-white hover:bg-red-700"
                    >
                        {t("actions.reject")}
                    </Button>

                    <Button
                        type="button"
                        onClick={handleApprove}
                        className="bg-green-600 text-white hover:bg-green-800"
                    >
                        {t("actions.approve")}
                    </Button>
                </div>
            </div>

            {showCommentaire && (
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
                {isOpen && (
                    <div className="grid grid-cols-1 gap-4 border-t border-gray-100 pt-5 md:grid-cols-2">
                        <ReadOnlyField
                            label={t("cv.content_type")}
                            value={cv.contentType}
                        />

                        <ReadOnlyField
                            label={t("cv.file_size")}
                            value={formaterTailleFichier(cv.fileSize)}
                        />

                        <ReadOnlyField
                            label={t("cv.upload_date")}
                            value={formaterDate(cv.uploadDate)}
                        />

                        <ReadOnlyField
                            label={t("cv.status")}
                            value={cv.statut}
                        />

                        {cv.rejectionComment && (
                            <div className="md:col-span-2">
                                <ReadOnlyField
                                    label={t("cv.previous_rejection_comment")}
                                    value={cv.rejectionComment}
                                    isMultiline
                                />
                            </div>
                        )}
                    </div>
                )}
        </div>
    );
}
export default CvCard;
