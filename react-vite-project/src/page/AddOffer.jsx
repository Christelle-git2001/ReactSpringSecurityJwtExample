import Button from "../component/ui/Button.jsx";
function AddOffer() {
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

                <form className="rounded-xl bg-white p-6 shadow">

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

                    {/* Lieu */}
                    <div className="mb-5">
                        <label
                            htmlFor="location"
                            className="mb-2 block font-medium text-[#043462]"
                        >
                            Lieu
                        </label>

                        <input
                            id="location"
                            name="location"
                            type="text"
                            className="w-full rounded-md border border-gray-300 px-4 py-2 outline-none focus:border-[#0ee1cc]"
                        />
                    </div>

                    {/* Entreprise */}
                    <div className="mb-5">
                        <label
                            htmlFor="company"
                            className="mb-2 block font-medium text-[#043462]"
                        >
                            Entreprise
                        </label>

                        <input
                            id="company"
                            name="company"
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
                    {/* Document complémentaire */}
                    <div className="mb-5">
                        <label
                            htmlFor="file"
                            className="mb-2 block font-medium text-[#043462]"
                        >
                            Document complémentaire (PDF, optionnel)
                        </label>

                        <input
                            id="file"
                            name="file"
                            type="file"
                            accept=".pdf,application/pdf"
                            className="w-full rounded-md border border-gray-300 px-4 py-2"
                        />
                    </div>
                    <div className="mt-8 flex justify-end gap-4">
                        <Button
                            type="button"
                            onClick={() => {}}
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