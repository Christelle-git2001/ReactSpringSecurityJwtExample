import Button from "../component/ui/Button.jsx";
import { useNavigate } from "react-router-dom";

function AddOffer() {
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();

        const formData = new FormData(e.target);
        const file = formData.get("file");

        const newOffer = {
            title: formData.get("title"),
            domain: formData.get("domain"),
            salary: formData.get("salary"),
            startDate: formData.get("startDate"),
            endDate: formData.get("endDate"),
            fileName: file.name
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
                            className="w-full rounded-md border border-gray-300 px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        />
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
                            className="w-full rounded-md border border-gray-300 px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        />
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
                            className="w-full rounded-md border border-gray-300 px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        />
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
                            required
                            className="w-full rounded-md border border-gray-300 px-4 py-2"
                        />
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