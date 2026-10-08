import fetcher from "../utils/fetcher.js";

const AddOfferRapide = () => {
    const createOffer = async () => {
        try {
            const token = localStorage.getItem("token");
            const headers = {};

            if (token) {
                headers["Authorization"] = `Bearer ${token}`;
            }
            const response = await fetcher('/api/auth/create/offre', {
                method: 'POST',
                headers:headers
            });

            if (!response.ok) {
                throw new Error('Erreur lors de la connexion rapide');
            }
        } catch (error) {
            console.error(error);
        }
    };
    return (
        <div className="mt-4 text-center">
            <button
                type="button"
                className="bg-yellow-500 p-5 rounded-3xl underline font-bold text-black transition-colors hover:text-blue-600 cursor-pointer"
                onClick={createOffer}
            >
                Ajouter une offre rapidement
            </button>
        </div>
    );
};

export default AddOfferRapide;