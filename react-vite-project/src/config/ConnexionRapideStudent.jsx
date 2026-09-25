import {useNavigate} from "react-router-dom";
import fetcher from "../utils/fetcher.js";

const ConnexionRapideStudent = () => {
    const navigate = useNavigate();
    const connexion  = async () =>{
        try {
            const response = await fetcher('/api/auth/login/student', {
                method: 'POST',
            });

            if (!response.ok) {
                throw new Error('Erreur lors de la connexion rapide');
            }
            const token = await response.text();
            localStorage.setItem('token', token);

            navigate('/etudiant/dashboard');
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div className={"text-white"}>
            <button type="button" className={"bg-blue-500 p-3"} onClick={connexion}>Connexion Rapide Etudiant</button>
        </div>
    )
}
export default ConnexionRapideStudent;