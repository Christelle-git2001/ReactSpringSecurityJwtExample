import { useState, useEffect } from "react";
import Button from "../ui/Button.jsx";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { getDepartements } from "../../api/http.jsx";
import { creerOffreEmployeur } from "../../api/employeur.jsx";

function AddOffer() {
    const navigate = useNavigate();
    const { t } = useTranslation();

    const [errors, setErrors] = useState({});
    const [departements, setDepartements] = useState([]);
    const [loading, setLoading] = useState(true);
    const [fileName, setFileName] = useState("");

    useEffect(() => {
        getDepartements()
            .then(setDepartements)
            .finally(() => setLoading(false));
    }, []);

    const handleSubmit = (e) => {
        e.preventDefault();

        const formData = new FormData(e.target);

        const title = formData.get("title");
        const domain = formData.get("domain");
        const salary = formData.get("salary");
        const description = formData.get("description");
        const startDate = formData.get("startDate");
        const endDate = formData.get("endDate");
        const file = formData.get("file");

        const dto = {
            title,
            domain,
            salary: Number(salary),
            description,
            startDate,
            endDate
        };

        const newErrors = {};

        // Titre
        if (!title) {
            newErrors.title = "add_offer.errors.title_required";
        }

        // Domaine
        if (!domain) {
            newErrors.domain = "add_offer.errors.domain_required";
        }

        // Description
        if (!description) {
            newErrors.description = "add_offer.errors.description_required";
        }

        // Salaire
        if (!salary) {
            newErrors.salary = "add_offer.errors.salary_required";
        } else if (Number(salary) < 0) {
            newErrors.salary = "add_offer.errors.salary_negative";
        }

        // Date de début
        if (!startDate) {
            newErrors.startDate = "add_offer.errors.startDate_required";
        }

        // Date de fin
        if (!endDate) {
            newErrors.endDate = "add_offer.errors.endDate_required";
        }

        // Fichier PDF
        if (!file || file.size === 0) {
            newErrors.file = "add_offer.errors.file_required";
        }

        // Vérification des dates
        if (startDate && endDate && endDate <= startDate) {
            newErrors.endDate = "add_offer.errors.endDate_invalid";
        }

        // S'il y a des erreurs, on les affiche
        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        setErrors({});

        creerOffreEmployeur(dto, file)
            .then(() => {
                navigate("/employeur");
            })
            .catch((err) => {
                console.error(err);

                setErrors({
                    api: "error.generic"
                });
            });
    };

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
                    {t("add_offer.title")}
                </h2>

                {errors.api && (
                    <div className="mb-4 rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
                        {t(errors.api)}
                    </div>
                )}

                <div className="grid grid-cols-1 gap-5 md:grid-cols-2">

                    {/* Titre */}
                    <div className="md:col-span-2">
                        <div className="flex flex-col gap-1.5">
                            <label
                                htmlFor="title"
                                className="text-sm font-semibold text-[#043462]"
                            >
                                {t("add_offer.offer_title")}
                            </label>

                            <input
                                id="title"
                                name="title"
                                type="text"
                                className={inputStyle(errors.title)}
                            />

                            {errors.title && (
                                <span className="text-xs font-medium text-red-500">
                                    {t(errors.title)}
                                </span>
                            )}
                        </div>
                    </div>

                    {/* Domaine */}
                    <div>
                        <div className="flex flex-col gap-1.5">
                            <label
                                htmlFor="domain"
                                className="text-sm font-semibold text-[#043462]"
                            >
                                {t("add_offer.domain")}
                            </label>

                            <select
                                id="domain"
                                name="domain"
                                disabled={loading}
                                className={inputStyle(errors.domain)}
                            >
                                <option value="">
                                    {loading
                                        ? t("add_offer.loading_domains")
                                        : t("add_offer.select_domain")}
                                </option>

                                {departements.map((d) => (
                                    <option key={d.name} value={d.name}>
                                        {t(`departement.${d.name}`)}
                                    </option>
                                ))}
                            </select>

                            {errors.domain && (
                                <span className="text-xs font-medium text-red-500">
                                    {t(errors.domain)}
                                </span>
                            )}
                        </div>
                    </div>

                    {/* Salaire */}
                    <div>
                        <div className="flex flex-col gap-1.5">
                            <label
                                htmlFor="salary"
                                className="text-sm font-semibold text-[#043462]"
                            >
                                {t("add_offer.salary")}
                            </label>

                            <input
                                id="salary"
                                name="salary"
                                type="number"
                                min="0"
                                className={inputStyle(errors.salary)}
                            />

                            {errors.salary && (
                                <span className="text-xs font-medium text-red-500">
                                    {t(errors.salary)}
                                </span>
                            )}
                        </div>
                    </div>

                    {/* Description */}
                    <div className="md:col-span-2">
                        <div className="flex flex-col gap-1.5">
                            <label
                                htmlFor="description"
                                className="text-sm font-semibold text-[#043462]"
                            >
                                {t("add_offer.description")}
                            </label>

                            <textarea
                                id="description"
                                name="description"
                                rows="4"
                                className={inputStyle(errors.description)}
                            />

                            {errors.description && (
                                <span className="text-xs font-medium text-red-500">
                                    {t(errors.description)}
                                </span>
                            )}
                        </div>
                    </div>

                    {/* Date de début */}
                    <div>
                        <div className="flex flex-col gap-1.5">
                            <label
                                htmlFor="startDate"
                                className="text-sm font-semibold text-[#043462]"
                            >
                                {t("add_offer.start_date")}
                            </label>

                            <input
                                id="startDate"
                                name="startDate"
                                type="date"
                                className={inputStyle(errors.startDate)}
                            />

                            {errors.startDate && (
                                <span className="text-xs font-medium text-red-500">
                                    {t(errors.startDate)}
                                </span>
                            )}
                        </div>
                    </div>

                    {/* Date de fin */}
                    <div>
                        <div className="flex flex-col gap-1.5">
                            <label
                                htmlFor="endDate"
                                className="text-sm font-semibold text-[#043462]"
                            >
                                {t("add_offer.end_date")}
                            </label>

                            <input
                                id="endDate"
                                name="endDate"
                                type="date"
                                className={inputStyle(errors.endDate)}
                            />

                            {errors.endDate && (
                                <span className="text-xs font-medium text-red-500">
                                    {t(errors.endDate)}
                                </span>
                            )}
                        </div>
                    </div>

                    {/* Fichier */}
                    <div className="md:col-span-2">
                        <div className="flex flex-col gap-1.5">
                            <label
                                htmlFor="file"
                                className="inline-block cursor-pointer rounded-md bg-[#043462] px-4 py-2 text-sm font-semibold text-white hover:bg-[#03284d]"
                            >
                                {fileName || t("add_offer.choose_file")}
                            </label>

                            <input
                                id="file"
                                name="file"
                                type="file"
                                accept=".pdf,application/pdf"
                                className="hidden"
                                onChange={(e) => {
                                    const file = e.target.files[0];

                                    if (file) {
                                        setFileName(file.name);
                                    } else {
                                        setFileName("");
                                    }
                                }}
                            />

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
                        {t("add_offer.submit")}
                    </Button>
                </div>
            </form>
        </div>
    );
}

export default AddOffer;