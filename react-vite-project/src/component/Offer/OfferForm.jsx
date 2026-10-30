import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Button from "../Design/Button.jsx";
import { getDepartements, getEmployeurs, getEtudiants } from "../../api/gestionnaire.jsx";
import { getDepartmentKey } from "../../utils/departementConverter.js";
import { validateOfferForm } from "../../utils/offerValidation.jsx";
import { FiUpload } from "react-icons/fi";
import { obtenirOffrePDF } from "../../api/employeur.jsx";
import { formaterTailleFichier } from "../../utils/cvFormatters.jsx";
import { usePdfDocument } from "../../utils/filesUtils.jsx";
import PdfActionButtons from "../PDF/PdfActionButtons.jsx";
import PdfPreviewDialog from "../PDF/PdfPreviewDialog.jsx";

const TAILLE_MAX_OCTETS = 10 * 1024 * 1024;

function OfferForm({
                       mode = "add",
                       titleKey,
                       submitLabelKey,
                       initialValues = {},
                       currentFileName = "",
                       requireFile = false,
                       targeted = false,
                       readOnlyTargetFields = false,
                       onSubmit,
                       onCancel,
                   }) {
    const { t } = useTranslation();

    const [errors, setErrors] = useState({});
    const [departements, setDepartements] = useState([]);
    const [employeurs, setEmployeurs] = useState([]);
    const [etudiants, setEtudiants] = useState([]);
    const [loading, setLoading] = useState(true);

    const [selectedDomain, setSelectedDomain] = useState("");
    const [selectedEmployeur, setSelectedEmployeur] = useState(
        initialValues.employeurId || initialValues.employeur?.id || ""
    );
    const [selectedEtudiant, setSelectedEtudiant] = useState(
        initialValues.etudiantId || initialValues.etudiant?.id || ""
    );
    const [fileName, setFileName] = useState("");
    const [selectedFile, setSelectedFile] = useState(null);
    const isEdit = mode === "edit";
    const { pdfUrl, pdfFile, error, dialogRef, viewPDF, fermerModal, gererTelechargement } = usePdfDocument({
        file: selectedFile,
        loadPdfResponse: () => obtenirOffrePDF(initialValues.id),
        fileNameFallback: fileName || currentFileName,
        documentId: initialValues.id,
        preload: isEdit && Boolean(currentFileName),
    });

    useEffect(() => {
        const fetchInitialData = async () => {
            try {
                const deps = await getDepartements();
                setDepartements(deps);

                if (targeted) {
                    const [empList, etudList] = await Promise.all([
                        getEmployeurs(),
                        getEtudiants(),
                    ]);
                    setEmployeurs(empList);
                    setEtudiants(etudList);

                    const initEmpId = initialValues.employeurId || initialValues.employeur?.id;
                    const initEtudId = initialValues.etudiantId || initialValues.etudiant?.id;

                    if (initEmpId) {
                        setSelectedEmployeur(initEmpId.toString());
                    }

                    if (initEtudId) {
                        setSelectedEtudiant(initEtudId.toString());
                        const foundEtudiant = etudList.find(
                            (etud) => Number(etud.id) === Number(initEtudId)
                        );
                        if (foundEtudiant) {
                            const studentDept = foundEtudiant.department || foundEtudiant.domain;
                            setSelectedDomain(getDepartmentKey(studentDept) || "");
                        }
                    }
                } else {
                    const currentDomain =
                        typeof initialValues.domain === "object"
                            ? initialValues.domain?.name
                            : initialValues.domain;

                    const matchingDomain = deps.find(
                        (d) =>
                            d.name?.toString().toUpperCase() ===
                            currentDomain?.toString().toUpperCase()
                    );

                    setSelectedDomain(
                        matchingDomain ? matchingDomain.name : currentDomain || ""
                    );
                }
            } catch (err) {
                console.error("Erreur lors du chargement des données:", err);
            } finally {
                setLoading(false);
            }
        };

        fetchInitialData();
    }, [initialValues?.employeurId,
        initialValues?.employeur?.id,
        initialValues?.etudiantId,
        initialValues?.etudiant?.id,
        initialValues?.domain,
        targeted,]);

    const handleEtudiantChange = (e) => {
        const etudiantId = e.target.value;
        setSelectedEtudiant(etudiantId);

        if (targeted) {
            if (etudiantId) {
                const foundEtudiant = etudiants.find(
                    (etud) => Number(etud.id) === Number(etudiantId)
                );

                if (foundEtudiant) {
                    const studentDept = foundEtudiant.department || foundEtudiant.domain;
                    const normalizedDept = getDepartmentKey(studentDept);
                    setSelectedDomain(normalizedDept || "");
                }
            } else {
                setSelectedDomain("");
            }
        }
    };

    const inputStyle = (hasError, isDisabled = false) =>
        `w-full rounded-lg border px-3.5 py-2.5 text-sm transition-colors outline-none focus:ring-2 ${
            isDisabled
                ? "bg-gray-100 text-gray-500 cursor-not-allowed border-gray-200"
                : hasError
                    ? "border-red-500 focus:ring-red-200"
                    : "border-gray-300 focus:border-[#0ee1cc] focus:ring-[#0ee1cc]/20"
        }`;

    const handleSubmit = async (e) => {
        e.preventDefault();

        const formData = new FormData(e.target);
        const fileInput = formData.get("file");
        const file = fileInput?.name ? fileInput : null;

        const title = formData.get("title");
        const domain = targeted ? selectedDomain : formData.get("domain");
        const salary = formData.get("salary");
        const description = formData.get("description");
        const startDate = formData.get("startDate");
        const endDate = formData.get("endDate");
        const displayEndDate = formData.get("displayEndDate");

        const employeurId = targeted ? selectedEmployeur : formData.get("employeurId");
        const etudiantId = targeted ? selectedEtudiant : formData.get("etudiantId");

        const newErrors = validateOfferForm({
            title,
            domain,
            salary,
            description,
            startDate,
            endDate,
            displayEndDate,
            file,
            requireFile,
        });

        if (targeted) {
            if (!employeurId) newErrors.employeurId = "add_offer.errors.select_company";
            if (!etudiantId) newErrors.etudiantId = "add_offer.errors.select_student";
        }

        if (file && file.size <= 0) {
            newErrors.file = "pdfVisio.lowFiles";
        }

        if (file && file.size > TAILLE_MAX_OCTETS) {
            newErrors.file = "pdfVisio.bigFiles";
        }

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        const dto = {
            title,
            domain,
            salary: Number(salary),
            description,
            startDate,
            endDate,
            displayEndDate,
            ...(targeted && {
                employeurId: Number(employeurId),
                etudiantId: Number(etudiantId),
            }),
        };

        setErrors({});

        try {
            await onSubmit(dto, file);
        } catch (err) {
            console.error(err);
            setErrors({ api: "error.generic" });
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center">
            <form
                noValidate
                onSubmit={handleSubmit}
                className="mx-auto max-w-2xl rounded-xl bg-white p-6 shadow-md md:p-8"
            >
                <h2 className="mb-6 text-xl font-bold text-[#043462]">
                    {t(titleKey)}
                </h2>

                {errors.api && (
                    <div className="mb-4 rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
                        {t(errors.api)}
                    </div>
                )}

                <div className="grid grid-cols-1 gap-5 md:grid-cols-2">
                    {targeted && (
                        <>
                            <div>
                                <div className="flex flex-col gap-1.5">
                                    <label htmlFor="employeurId" className="text-sm font-semibold text-[#043462]">
                                        {t("add_offer.targeted_company")}
                                    </label>
                                    <select
                                        id="employeurId"
                                        name="employeurId"
                                        value={selectedEmployeur}
                                        onChange={(e) => setSelectedEmployeur(e.target.value)}
                                        disabled={readOnlyTargetFields}
                                        className={inputStyle(errors.employeurId, readOnlyTargetFields)}
                                    >
                                        <option value="">{t("add_offer.select_company")}</option>
                                        {employeurs.map((emp) => (
                                            <option key={emp.id} value={emp.id}>
                                                 {emp.businessName}
                                            </option>
                                        ))}
                                    </select>
                                    {errors.employeurId && (
                                        <span className="text-xs font-medium text-red-500">
                                            {t(errors.employeurId)}
                                        </span>
                                    )}
                                </div>
                            </div>

                            <div>
                                <div className="flex flex-col gap-1.5">
                                    <label htmlFor="etudiantId" className="text-sm font-semibold text-[#043462]">
                                        {t("add_offer.targeted_student")}
                                    </label>
                                    <select
                                        id="etudiantId"
                                        name="etudiantId"
                                        value={selectedEtudiant}
                                        onChange={handleEtudiantChange}
                                        disabled={readOnlyTargetFields}
                                        className={inputStyle(errors.etudiantId, readOnlyTargetFields)}
                                    >
                                        <option value="">{t("add_offer.select_student")}</option>
                                        {etudiants.map((etud) => (
                                            <option key={etud.id} value={etud.id}>
                                                {etud.firstName} {etud.lastName} ({etud.matricule})
                                            </option>
                                        ))}
                                    </select>
                                    {errors.etudiantId && (
                                        <span className="text-xs font-medium text-red-500">
                                            {t(errors.etudiantId)}
                                        </span>
                                    )}
                                </div>
                            </div>
                        </>
                    )}

                    <div className="md:col-span-2">
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="title" className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.offer_title")}
                            </label>
                            <input
                                id="title"
                                name="title"
                                type="text"
                                defaultValue={initialValues.title || ""}
                                className={inputStyle(errors.title)}
                            />
                            {errors.title && (
                                <span className="text-xs font-medium text-red-500">{t(errors.title)}</span>
                            )}
                        </div>
                    </div>

                    <div>
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="domain" className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.domain")}
                            </label>
                            <select
                                id="domain"
                                name="domain"
                                value={selectedDomain}
                                onChange={(e) => setSelectedDomain(e.target.value)}
                                disabled={loading || targeted}
                                className={inputStyle(errors.domain, targeted)}
                            >
                                <option value="">
                                    {loading
                                        ? t("add_offer.loading_domains")
                                        : t("add_offer.select_domain")}
                                </option>
                                {departements.map((d) => {
                                    const deptName = typeof d === "object" ? d.name : d;
                                    return (
                                        <option key={deptName} value={deptName}>
                                            {t(`departement.${deptName}`)}
                                        </option>
                                    );
                                })}
                            </select>
                            {errors.domain && (
                                <span className="text-xs font-medium text-red-500">{t(errors.domain)}</span>
                            )}
                        </div>
                    </div>

                    <div>
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="salary" className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.salary")}
                            </label>
                            <input
                                id="salary"
                                name="salary"
                                type="number"
                                min="0"
                                step="0.01"
                                defaultValue={initialValues.salary || ""}
                                className={inputStyle(errors.salary)}
                            />
                            {errors.salary && (
                                <span className="text-xs font-medium text-red-500">{t(errors.salary)}</span>
                            )}
                        </div>
                    </div>

                    <div className="md:col-span-2">
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="description" className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.description")}
                            </label>
                            <textarea
                                id="description"
                                name="description"
                                maxLength="255"
                                rows="4"
                                defaultValue={initialValues.description || ""}
                                className={inputStyle(errors.description)}
                            />
                            {errors.description && (
                                <span className="text-xs font-medium text-red-500">{t(errors.description)}</span>
                            )}
                        </div>
                    </div>

                    <div>
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="startDate" className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.start_date")}
                            </label>
                            <input
                                id="startDate"
                                name="startDate"
                                type="date"
                                defaultValue={initialValues.startDate || ""}
                                className={inputStyle(errors.startDate)}
                            />
                            {errors.startDate && (
                                <span className="text-xs font-medium text-red-500">{t(errors.startDate)}</span>
                            )}
                        </div>
                    </div>

                    <div>
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="endDate" className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.end_date")}
                            </label>
                            <input
                                id="endDate"
                                name="endDate"
                                type="date"
                                defaultValue={initialValues.endDate || ""}
                                className={inputStyle(errors.endDate)}
                            />
                            {errors.endDate && (
                                <span className="text-xs font-medium text-red-500">{t(errors.endDate)}</span>
                            )}
                        </div>
                    </div>

                    <div>
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="displayEndDate" className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.display_end_date")}
                            </label>
                            <input
                                id="displayEndDate"
                                name="displayEndDate"
                                type="date"
                                defaultValue={initialValues.displayEndDate || ""}
                                className={inputStyle(errors.displayEndDate)}
                            />
                            {errors.displayEndDate && (
                                <span className="text-xs font-medium text-red-500">{t(errors.displayEndDate)}</span>
                            )}
                        </div>
                    </div>

                    <div className="md:col-span-2">
                        <div className="flex flex-col gap-1.5">
                            <span className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.document")}
                            </span>

                            {(fileName || currentFileName) && (
                                <div className="document-card my-2">
                                    <div className="document-card-info">
                                        <p className="document-card-name">{fileName || currentFileName}</p>
                                        <p className="document-card-details">
                                            PDF{pdfFile ? ` · ${formaterTailleFichier(pdfFile.size)}` : ""}
                                        </p>
                                    </div>
                                    <PdfActionButtons
                                        variant="card"
                                        disabled={!pdfUrl}
                                        onView={viewPDF}
                                        onDownload={() => gererTelechargement(fileName || currentFileName)}
                                    />
                                </div>
                            )}

                            {isEdit && error && !selectedFile && (
                                <p className="text-sm text-red-600">{t(error)}</p>
                            )}

                            <input
                                id="file"
                                name="file"
                                type="file"
                                accept=".pdf,application/pdf"
                                className="hidden"
                                onChange={(e) => {
                                    const file = e.target.files[0] || null;
                                    setFileName(file?.name || "");
                                    setSelectedFile(file);
                                }}
                            />

                            <label
                                htmlFor="file"
                                className="flex min-h-12 w-full cursor-pointer items-center justify-center gap-3 rounded-md bg-[#043462] px-4 py-3 font-semibold text-white hover:bg-[#03284d]"
                            >
                                <FiUpload className="size-5 shrink-0" aria-hidden="true" />
                                {t(fileName || currentFileName ? "documents.replace" : "add_offer.choose_file")}
                            </label>

                            <p className="text-center text-sm text-slate-500">{t("documents.file_hint", { maxSize: TAILLE_MAX_OCTETS / 1024 / 1024 })}</p>

                            {isEdit && (
                                <p className="text-xs text-gray-500 mt-1">
                                    {t("edit_offer.keep_file_note")}
                                </p>
                            )}

                            {errors.file && (
                                <span className="text-xs font-medium text-red-500">{t(errors.file)}</span>
                            )}
                        </div>
                    </div>
                </div>

                <div className="mt-8 flex justify-end gap-3 border-t border-gray-100 pt-5">
                    <Button variant="secondary" type="button" onClick={onCancel}>
                        {t("add_offer.cancel")}
                    </Button>
                    <Button type="submit">
                        {t(submitLabelKey)}
                    </Button>
                </div>
            </form>
            <PdfPreviewDialog dialogRef={dialogRef} pdfUrl={pdfUrl} onClose={fermerModal} />
        </div>
    );
}

export default OfferForm;