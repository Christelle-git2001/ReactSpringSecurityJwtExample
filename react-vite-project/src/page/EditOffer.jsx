import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Button from "../component/ui/Button.jsx";

function EditOffer() {
    const location = useLocation();
    const navigate = useNavigate();

    const offer = location.state?.offer;

    const [errors, setErrors] = useState({});

    const handleSubmit = (e) => {
        e.preventDefault();

        const formData = new FormData(e.target);

        const title = formData.get("title");
        const domain = formData.get("domain");
        const salary = formData.get("salary");
        const startDate = formData.get("startDate");
        const endDate = formData.get("endDate");

        const newErrors = {};

        if (!title) {
            newErrors.title = "Veuillez remplir le titre de l'offre.";
        }

        if (!domain) {
            newErrors.domain = "Veuillez remplir le domaine.";
        }

        if (!salary) {
            newErrors.salary = "Veuillez remplir le salaire.";
        }

        if (!startDate) {
            newErrors.startDate =
                "Veuillez sélectionner une date de début.";
        }

        if (!endDate) {
            newErrors.endDate =
                "Veuillez sélectionner une date de fin.";
        }

        if (startDate && endDate && endDate <= startDate) {
            newErrors.endDate =
                "La date de fin doit être après la date de début.";
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

        // TODO avec Backend : modifier l'offre
        navigate("/employeur", {
            state: {
                offer: updatedOffer
            }
        });
    };

    // Si aucune offre n'a été transmise
    if (!offer) {
        return (
            <main className="min-h-screen bg-gray-100 p-8">
                <p className="text-red-600">
                    Aucune offre à modifier.
                </p>

                <Button
                    onClick={() => navigate("/employeur")}
                    className="mt-4"
                >
                    Retour
                </Button>
            </main>
        );
    }

    return (
        <main className="min-h-screen bg-gray-100 px-6 py-8">
            <div className="mx-auto max-w-3xl">

                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-[#043462]">
                        Modifier l'offre de stage
                    </h1>

                    <p className="mt-2 text-gray-600">
                        Modifiez les informations de votre offre de stage.
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
                            defaultValue={offer.title}
                            className="w-full rounded-md border border-gray-300 px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        />

                        {errors.title && (
                            <div className="mt-2 flex items-center gap-2 text-sm text-error">
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

                        <input
                            id="domain"
                            name="domain"
                            type="text"
                            defaultValue={offer.domain}
                            className="w-full rounded-md border border-gray-300 px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        />

                        {errors.domain && (
                            <div className="mt-2 flex items-center gap-2 text-sm text-error">
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
                            defaultValue={offer.salary}
                            className="w-full rounded-md border border-gray-300 px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        />

                        {errors.salary && (
                            <div className="mt-2 flex items-center gap-2 text-sm text-error">
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
                            defaultValue={offer.startDate}
                            className="w-full rounded-md border border-gray-300 px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        />

                        {errors.startDate && (
                            <div className="mt-2 flex items-center gap-2 text-sm text-error">
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
                            defaultValue={offer.endDate}
                            className="w-full rounded-md border border-gray-300 px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        />

                        {errors.endDate && (
                            <div className="mt-2 flex items-center gap-2 text-sm text-error">
                                <span>⚠️</span>
                                <span>{errors.endDate}</span>
                            </div>
                        )}
                    </div>

                    {/* Document */}
                    <div className="mb-5">
                        <label
                            className="mb-2 block font-medium text-[#043462]"
                        >
                            Document complémentaire
                        </label>

                        <div className="rounded-md bg-gray-100 px-4 py-3 text-sm">
                            📄 {offer.fileName}
                        </div>

                        <p className="mt-1 text-xs text-gray-500">
                            Le document pourra être modifié avec le backend.
                        </p>
                    </div>

                    {/* Boutons */}
                    <div className="mt-8 flex justify-end gap-4">

                        <Button
                            type="button"
                            onClick={() => navigate("/employeur")}
                            className="bg-gray-400 hover:bg-gray-500"
                        >
                            Annuler
                        </Button>

                        <Button type="submit">
                            Enregistrer les modifications
                        </Button>

                    </div>

                </form>
            </div>
        </main>
    );
}

export default EditOffer;