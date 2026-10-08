import {useNavigate} from "react-router-dom";
import fetcher from "../utils/fetcher.js";

const ConnexionRapideGestionnaire = () => {
    const navigate = useNavigate();
    const connexion = async () => {
        try {
            const response = await fetcher('/api/auth/login/gestionnaire', {
                method: 'POST',
            });

            if (!response.ok) {
                throw new Error('Erreur lors de la connexion rapide');
            }
            const token = await response.text();
            localStorage.setItem('token', token);

            navigate('/gestionnaire');
        } catch (error) {
            console.error(error);
        }
    };
    return (
        <div className="mt-4 text-center">
            <button
                type="button"
                className="bg-yellow-500 p-5 rounded-3xl underline font-bold text-black transition-colors hover:text-blue-600 cursor-pointer"
                onClick={connexion}
            >
               Gestionnaire
            </button>
        </div>
    );
};

export default ConnexionRapideGestionnaire;