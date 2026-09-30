import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Button from "../component/ui/Button.jsx";
import { getDepartements } from "../api/http.jsx";

function EditOffer() {
    const location = useLocation();
    const navigate = useNavigate();
    const { t } = useTranslation();

    const offer = location.state?.offer;

    const [errors, setErrors] = useState({});
    const [departements, setDepartements] = useState([]);
    const [loading, setLoading] = useState(true);

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
        const startDate = formData.get("startDate");
        const endDate = formData.get("endDate");

        const newErrors = {};

        if (!title) newErrors.title = t("add_offer.errors.title_required");
        if (!domain) newErrors.domain = t("add_offer.errors.domain_required");

        if (!salary) {
            newErrors.salary = t("add_offer.errors.salary_required");
        } else if (Number(salary) < 0) {
            newErrors.salary = t("add_offer.errors.salary_negative");
        }

        if (!startDate) newErrors.startDate = t("add_offer.errors.startDate_required");
        if (!endDate) newErrors.endDate = t("add_offer.errors.endDate_required");

        if (startDate && endDate && endDate <= startDate) {
            newErrors.endDate = t("add_offer.errors.endDate_invalid");
        }

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        const updatedOffer = {
            ...offer,
            title,
            domain,
            salary,
            startDate,
            endDate,
            status: offer.status
        };

        //TODO Back end (Modifier)

        navigate("/employeur", { state: { offer: updatedOffer } });
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

                    {/* Département / Domaine */}
                    <div>
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="domain" className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.domain")}
                            </label>
                            <select
                                id="domain"
                                name="domain"
                                defaultValue={offer.domain}
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

                    {/* Date début */}
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

                    {/* Date fin */}
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

                    {/* Document (Lecture seule) */}
                    <div className="md:col-span-2">
                        <div className="flex flex-col gap-1.5">
                            <label className="text-sm font-semibold text-[#043462]">
                                {t("add_offer.document")}
                            </label>
                            <div className="rounded-lg border border-gray-200 bg-gray-50 px-3.5 py-2.5 text-sm text-gray-700">
                                {offer.fileName || "—"}
                            </div>
                            <p className="text-xs text-gray-500">
                                {t("edit_offer.document_note")}
                            </p>
                        </div>
                    </div>
                </div>

                {/* Boutons */}
                <div className="mt-8 flex justify-end gap-3 border-t border-gray-100 pt-5">
                    <Button
                        type="button"
                        onClick={() => navigate("/employeur")}
                        className="bg-gray-100 text-gray-700 hover:bg-gray-200"
                    >
                        {t("add_offer.cancel")}
                    </Button>

                    <Button type="submit" className="bg-[#043462] text-white hover:bg-[#03284d]">
                        {t("edit_offer.save")}
                    </Button>
                </div>
            </form>
        </div>
    );
}

export default EditOffer;