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

            if (!title) {
                newErrors.title = "Veuillez remplir le titre de l'offre.";
            }

            if (!domain) {
                newErrors.domain = "Veuillez remplir le domaine.";
            }

            if (!salary) {
                newErrors.salary = "Veuillez remplir le salaire.";
            } else if (Number(salary) < 0) {
                newErrors.salary = "Le salaire ne peut pas être négatif.";
            }

            if (!startDate) {
                newErrors.startDate = "Veuillez sélectionner une date de début.";
            }

            if (!endDate) {
                newErrors.endDate = "Veuillez sélectionner une date de fin.";
            }

            if (!file || file.size === 0) {
                newErrors.file = "Veuillez sélectionner un document PDF.";
            }

            if (Object.keys(newErrors).length > 0) {
                setErrors(newErrors);
                return;
            }

            if (endDate <= startDate) {
                setErrors({
                    endDate: "La date de fin doit être après la date de début."
                });
                return;
            }

            setErrors({});


        const newOffer = {
            title: formData.get("title"),
            domain: formData.get("domain"),
            salary: formData.get("salary"),
            startDate: formData.get("startDate"),
            endDate: formData.get("endDate"),
            fileName: file.name,
            status: "EN_ATTENTE"
        };
//TODO avec Backend (enregistrer plusieurs offres)
        navigate("/employeur", {
            state: {
                offer: newOffer
            }
        });
    };
    return (

        <main className="min-h-screen bg-gray-100 px-6 py-8">
            <div className="mx-auto max-w-3xl">

                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-[#043462]">
                        Ajouter une offre de stage
                    </h1>

                    <p className="mt-2 text-gray-600">
                        Remplissez les informations de votre offre de stage.
                    </p>

                    <div className="mt-3 h-px w-full bg-[#0FFFDF]"></div>
                </div>

                <form
                    onSubmit={handleSubmit}
                    className="rounded-xl bg-white p-6 shadow"
                >

                    {/* Titre */}
                    <div className="mb-5">
                        <label
                            htmlFor="title"
                            className="mb-2 block font-medium text-[#043462]"
                        >
                            Titre de l'offre
                        </label>

                        <input
                            id="title"
                            name="title"
                            type="text"
                            className="w-full rounded-md border border-gray-300 px-4 py-2"
                        />

                        {errors.title && (
                            <div className="mt-2 flex items-center justify-center gap-2 text-sm text-error">
                                <span>⚠️</span>
                                <span>{errors.title}</span>
                            </div>
                        )}
                    </div>

                    {/* Domaine */}
                    <div className="mb-5">
                        <label
                            htmlFor="domain"
                            className="mb-2 block font-medium text-[#043462]"
                        >
                            Domaine
                        </label>

                        <select
                            id="domain"
                            name="domain"
                            disabled={loading}
                            className="w-full rounded-md border border-gray-300 bg-white px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        >
                            <option value="">
                                {loading
                                    ? "Chargement des domaines..."
                                    : "Choisir un domaine"}
                            </option>

                            {secteurs.map((s) => (
                                <option key={s.name} value={s.name}>
                                    {t(`secteur.${s.name}`)}
                                </option>
                            ))}
                        </select>

                        {errors.domain && (
                            <div className="mt-2 flex items-center justify-center gap-2 text-sm text-error">
                                <span>⚠️</span>
                                <span>{errors.domain}</span>
                            </div>
                        )}
                    </div>
                    {/* Salaire */}
                    <div className="mb-5">
                        <label
                            htmlFor="salary"
                            className="mb-2 block font-medium text-[#043462]"
                        >
                            Salaire
                        </label>

                        <input
                            id="salary"
                            name="salary"
                            type="number"
                            min = "0"
                            className="w-full rounded-md border border-gray-300 px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        />
                        {errors.salary && (
                            <div className="mt-2 flex items-center justify-center gap-2 text-sm text-error">
                                <span>⚠️</span>
                                <span>{errors.salary}</span>
                            </div>
                        )}
                    </div>

                    {/* Date de début */}
                    <div className="mb-5">
                        <label
                            htmlFor="startDate"
                            className="mb-2 block font-medium text-[#043462]"
                        >
                            Date de début
                        </label>

                        <input
                            id="startDate"
                            name="startDate"
                            type="date"
                            className="w-full rounded-md border border-gray-300 px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        />
                        {errors.startDate && (
                            <div className="mt-2 flex items-center justify-center gap-2 text-sm text-error">
                                <span>⚠️</span>
                                <span>{errors.startDate}</span>
                            </div>
                        )}
                    </div>

                    {/* Date de fin */}
                    <div className="mb-5">
                        <label
                            htmlFor="endDate"
                            className="mb-2 block font-medium text-[#043462]"
                        >
                            Date de fin
                        </label>

                        <input
                            id="endDate"
                            name="endDate"
                            type="date"
                            className="w-full rounded-md border border-gray-300 px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        />
                    </div>
                    {errors.endDate && (
                        <div className="mt-2 flex items-center justify-center gap-2 text-sm text-error">
                            <span>⚠️</span>
                            <span>{errors.endDate}</span>
                        </div>
                    )}
                    {/* Document complémentaire Obligatoire */}
                    <div className="mb-5">
                        <label
                            htmlFor="file"
                            className="mb-2 block font-medium text-[#043462]"
                        >
                            Document complémentaire (PDF)
                        </label>

                        <input
                            id="file"
                            name="file"
                            type="file"
                            accept=".pdf,application/pdf"
                            className="w-full rounded-md border border-gray-300 px-4 py-2"
                        />
                        {errors.file && (
                            <div className="mt-2 flex items-center justify-center gap-2 text-sm text-error">
                                <span>⚠️</span>
                                <span>{errors.file}</span>
                            </div>
                        )}
                    </div>
                    <div className="mt-8 flex justify-end gap-4">
                        <Button
                            type="button"
                            onClick={() => navigate("/employeur")}
                            className="bg-gray-400 hover:bg-gray-500"
                        >
                            Annuler
                        </Button>

                        <Button
                            type="submit"
                        >
                            Accepter
                        </Button>
                    </div>

                </form>
            </div>
        </main>
    );
}

export default AddOffer;