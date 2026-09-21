import {Route, Routes, useNavigate} from "react-router-dom";
import "../../css/InscriptionHome.css";

function InscriptionHome(){
    const navigate = useNavigate();
    return (
        <div className="inscription-page">
            <div className="inscription-card">
                <h1 className="inscription-title">Vous êtes ?</h1>
            </div>
            <div className="inscription-buttons">
                <button
                    className="inscription-btn inscription-btn-etudiant"
                    onClick={() => navigate('/inscription/addetudiant')}>
                    Etudiant
                </button>
            </div>
            <div>
                <button
                    className="inscription-btn inscription-btn-employeur"
                    onClick={() => navigate('/inscription/addemployeur')}>
                    Employeur
                </button>
            </div>
        </div>
    )
}

export default InscriptionHome;