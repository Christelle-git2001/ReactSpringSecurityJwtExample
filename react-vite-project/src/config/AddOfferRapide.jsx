import BASE_URL from "./Config.jsx";

const AddOfferRapide = ({ onCreated }) => {
    const createOffer = async () => {
        try {
            const response = await fetch(`${BASE_URL}/api/auth/create/offre`, {
                method: 'POST',
            });

            if (!response.ok) {
                throw new Error('Erreur lors de la connexion rapide');
            }

            onCreated?.();
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