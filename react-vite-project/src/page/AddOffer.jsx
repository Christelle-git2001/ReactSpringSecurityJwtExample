import { useState, useEffect } from "react";
import Button from "../component/ui/Button.jsx";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { getSecteursEmployeur } from "../api/http.jsx";

function AddOffer() {
    const navigate = useNavigate();
    const { t } = useTranslation();

    const [errors, setErrors] = useState({});
    const [secteurs, setSecteurs] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getSecteursEmployeur()
            .then(setSecteurs)
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
        const file = formData.get("file");

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

        if (!file || file.size === 0) {
            newErrors.file = t("add_offer.errors.file_required");
        }

        if (startDate && endDate && endDate <= startDate) {
            newErrors.endDate = t("add_offer.errors.endDate_invalid");
        }

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        setErrors({});

        const newOffer = {
            title,
            domain,
            salary,
            startDate,
            endDate,
            fileName: file.name,
            status: "EN_ATTENTE"
        };

        //TODO Back end (Ajouter Offre)

        navigate("/employeur", { state: { offer: newOffer } });
    };

    const inputStyle = (hasError) =>
        `w-full rounded-lg border px-3.5 py-2.5 text-sm transition-colors outline-none focus:ring-2 ${
            hasError
                ? "border-red-500 focus:ring-red-200"
                : "border-gray-300 focus:border-[#0ee1cc] focus:ring-[#0ee1cc]/20"
        }`;

    return (
        <form
            onSubmit={handleSubmit}
            className="m-auto max-w-2xl rounded-xl bg-white p-6 shadow-md md:p-8"
        >
            <h2 className="mb-6 text-xl font-bold text-[#043462]">
                {t("add_offer.title", "Ajouter une offre")}
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
                            disabled={loading}
                            className={inputStyle(errors.domain)}
                        >
                            <option value="">
                                {loading
                                    ? t("add_offer.loading_domains")
                                    : t("add_offer.select_domain")}
                            </option>
                            {secteurs.map((s) => (
                                <option key={s.name} value={s.name}>
                                    {t(`secteur.${s.name}`)}
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
                            className={inputStyle(errors.endDate)}
                        />
                        {errors.endDate && (
                            <span className="text-xs font-medium text-red-500">{errors.endDate}</span>
                        )}
                    </div>
                </div>

                {/* Fichier */}
                <div className="md:col-span-2">
                    <div className="flex flex-col gap-1.5">
                        <label htmlFor="file" className="text-sm font-semibold text-[#043462]">
                            {t("add_offer.document")}
                        </label>
                        <input
                            id="file"
                            name="file"
                            type="file"
                            accept=".pdf,application/pdf"
                            className="w-full rounded-lg border border-gray-300 bg-gray-50 px-3 py-2 text-sm text-gray-600 file:mr-4 file:rounded-md file:border-0 file:bg-[#043462] file:px-3 file:py-1.5 file:text-xs file:font-semibold file:text-white hover:file:bg-[#03284d]"
                        />
                        {errors.file && (
                            <span className="text-xs font-medium text-red-500">{errors.file}</span>
                        )}
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
                    {t("add_offer.submit")}
                </Button>
            </div>
        </form>
    );
}

export default AddOffer;