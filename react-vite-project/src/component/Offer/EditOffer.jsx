import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Button from "../Design/Button.jsx";
import { getDepartements } from "../../api/http.jsx";
import { modifierOffreEmployeur } from "../../api/employeur.jsx";

function EditOffer() {
    const location = useLocation();
    const navigate = useNavigate();
    const { t } = useTranslation();

    const offer = location.state?.offer;

    const [errors, setErrors] = useState({});
    const [departements, setDepartements] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedDomain, setSelectedDomain] = useState("");
    const [selectedFile, setSelectedFile] = useState(null);

    useEffect(() => {
        if (!offer) {
            setLoading(false);
            return;
        }

        getDepartements()
            .then((data) => {
                setDepartements(data);

                const currentDomain =
                    typeof offer.domain === "object"
                        ? offer.domain?.name
                        : offer.domain;

                const matchingDomain = data.find(
                    (d) =>
                        d.name?.toString().toUpperCase() ===
                        currentDomain?.toString().toUpperCase()
                );

                setSelectedDomain(
                    matchingDomain
                        ? matchingDomain.name
                        : currentDomain || ""
                );
            })
            .finally(() => setLoading(false));
    }, [offer]);

    const handleSubmit = (e) => {
        e.preventDefault();

        const formData = new FormData(e.target);

        const title = formData.get("title");
        const domain = formData.get("domain");
        const salary = formData.get("salary");
        const description = formData.get("description");
        const startDate = formData.get("startDate");
        const endDate = formData.get("endDate");

        const newErrors = {};

        // Validation des champs
        if (!title) {
            newErrors.title = t("add_offer.errors.title_required");
        }

        if (!domain) {
            newErrors.domain = t("add_offer.errors.domain_required");
        }

        if (!description) {
            newErrors.description = t("add_offer.errors.description_required");
        }

        if (!salary) {
            newErrors.salary = t("add_offer.errors.salary_required");
        } else if (Number(salary) < 0) {
            newErrors.salary = t("add_offer.errors.salary_negative");
        }

        if (!startDate) {
            newErrors.startDate = t("add_offer.errors.startDate_required");
        }

        if (!endDate) {
            newErrors.endDate = t("add_offer.errors.endDate_required");
        }

        if (startDate && endDate && endDate <= startDate) {
            newErrors.endDate = t("add_offer.errors.endDate_invalid");
        }

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        // Création du DTO JSON attendu par Spring Boot
        const dto = {
            title,
            domain,
            salary: Number(salary),
            description,
            startDate,
            endDate
        };

        setErrors({});

        // Appel à l'API en passant l'ID, le DTO et le Fichier
        modifierOffreEmployeur(offer.id, dto, selectedFile)
            .then(() => {
                navigate("/employeur");
            })
            .catch((err) => {
                console.error(err);
                setErrors({
                    api: "Une erreur est survenue lors de la modification de l'offre."
                });
            });
    };

    if (!offer) {
        return (
            <main className="flex min-h-[50vh] flex-col items-center justify-center p-8">
                <p className="text-base font-medium text-red-600">
                    {t("edit_offer.no_offer")}
                </p>

                <Button
                    onClick={() => navigate("/employeur")}
                    className="mt-4 bg-[#043462] text-white"
                >
                    {t("edit_offer.back")}
                </Button>
            </main>
        );
    }

    const inputStyle = (hasError) =>
        `w-full rounded-lg border px-3.5 py-2.5 text-sm transition-colors outline-none focus:ring-2 ${
            hasError
                ? "border-red-500 focus:ring-red-200"
                : "border-gray-300 focus:border-[#0ee1cc] focus:ring-[#0ee1cc]/20"
        }`;

    return (
        <div className="min-h-screen flex items-center justify-center">
            <form
                onSubmit={handleSubmit}
                className="mx-auto max-w-2xl rounded-xl bg-white p-6 shadow-md md:p-8"
            >
                <h2 className="mb-6 text-xl font-bold text-[#043462]">
                    {t("edit_offer.title", "Modifier l'offre")}
                </h2>

                {errors.api && (
                    <div className="mb-4 rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
                        {errors.api}
                    </div>
                )}

                <div className="grid grid-cols-1 gap-5 md:grid-cols-2">

                    {/* Titre */}
                    <div className="md:col-span-2">
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="title" className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.offer_title")}
                            </label>
                            <input
                                id="title"
                                name="title"
                                type="text"
                                defaultValue={offer.title}
                                className={inputStyle(errors.title)}
                            />
                            {errors.title && (
                                <span className="text-xs font-medium text-red-500">{errors.title}</span>
                            )}
                        </div>
                    </div>

                    {/* Domaine */}
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
                                disabled={loading}
                                className={inputStyle(errors.domain)}
                            >
                                <option value="">
                                    {loading ? t("add_offer.loading_domains") : t("add_offer.select_domain")}
                                </option>
                                {departements.map((d) => (
                                    <option key={d.name} value={d.name}>
                                        {t(`departement.${d.name}`)}
                                    </option>
                                ))}
                            </select>
                            {errors.domain && (
                                <span className="text-xs font-medium text-red-500">{errors.domain}</span>
                            )}
                        </div>
                    </div>

                    {/* Salaire */}
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
                                defaultValue={offer.salary}
                                className={inputStyle(errors.salary)}
                            />
                            {errors.salary && (
                                <span className="text-xs font-medium text-red-500">{errors.salary}</span>
                            )}
                        </div>
                    </div>

                    {/* Description */}
                    <div className="md:col-span-2">
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="description" className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.description")}
                            </label>
                            <textarea
                                id="description"
                                name="description"
                                rows="4"
                                defaultValue={offer.description}
                                className={inputStyle(errors.description)}
                            />
                            {errors.description && (
                                <span className="text-xs font-medium text-red-500">{errors.description}</span>
                            )}
                        </div>
                    </div>

                    {/* Date de début */}
                    <div>
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="startDate" className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.start_date")}
                            </label>
                            <input
                                id="startDate"
                                name="startDate"
                                type="date"
                                defaultValue={offer.startDate}
                                className={inputStyle(errors.startDate)}
                            />
                            {errors.startDate && (
                                <span className="text-xs font-medium text-red-500">{errors.startDate}</span>
                            )}
                        </div>
                    </div>

                    {/* Date de fin */}
                    <div>
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="endDate" className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.end_date")}
                            </label>
                            <input
                                id="endDate"
                                name="endDate"
                                type="date"
                                defaultValue={offer.endDate}
                                className={inputStyle(errors.endDate)}
                            />
                            {errors.endDate && (
                                <span className="text-xs font-medium text-red-500">{errors.endDate}</span>
                            )}
                        </div>
                    </div>

                    {/* Document / Fichier */}
                    <div className="md:col-span-2">
                        <div className="flex flex-col gap-1.5">
                            <span className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.document")}
                            </span>

                            {/* Indication du fichier déjà enregistré sur l'offre */}
                            {offer.fileName && (
                                <p className="text-xs text-gray-600 mt-2">
                                    {t("edit_offer.current_file", "Fichier actuel :")}{" "}
                                    <span className="font-medium text-[#043462]">{offer.fileName}</span>
                                </p>
                            )}

                            {/* Input file masqué lié à son label stylisé */}
                            <div className="flex items-center gap-3">
                                <label
                                    htmlFor="file"
                                    className="inline-block cursor-pointer rounded-md bg-[#043462] px-4 py-2 text-sm font-semibold text-white hover:bg-[#03284d] transition-colors"
                                >
                                    {selectedFile ? selectedFile.name : t("add_offer.choose_file")}
                                </label>

                                <input
                                    id="file"
                                    name="file"
                                    type="file"
                                    accept=".pdf,application/pdf"
                                    className="hidden"
                                    onChange={(e) => {
                                        const file = e.target.files[0] || null;
                                        setSelectedFile(file);
                                    }}
                                />
                            </div>

                            {/* Message d'aide */}
                            <p className="text-xs text-gray-500 mt-2">
                                {t("edit_offer.keep_file_note")}
                            </p>

                            {/* Message d'erreur */}
                            {errors.file && (
                                <span className="text-xs font-medium text-red-500">
                                    {t(errors.file)}
                                </span>
                            )}
                        </div>
                    </div>
                </div>

                {/* Boutons */}
                <div className="mt-8 flex justify-end gap-3 border-t border-gray-100 pt-5">
                    <Button
                        type="button"
                        onClick={() => navigate("/employeur")}
                        className="bg-gray-500 text-white hover:bg-gray-600"
                    >
                        {t("add_offer.cancel")}
                    </Button>

                    <Button
                        type="submit"
                        className="bg-[#043462] text-white hover:bg-[#03284d]"
                    >
                        {t("edit_offer.save")}
                    </Button>
                </div>
            </form>
        </div>
    );
}

export default EditOffer;