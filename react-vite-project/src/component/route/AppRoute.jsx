import { Routes, Route } from "react-router-dom";
import PageLayout from "../Design/PageLayout.jsx";
import MainContainer from "../Design/MainContainer";
import About from "../Design/About";
import LoginForm from "../auth/LoginForm";
import AddEtudiant from "../Profil/Etudiant/AddEtudiant.jsx";
import Logout from "../auth/Logout";
import ErrorPage from "../Design/ErrorPage";
import GestionnaireHome from "../../page/GestionnaireHome.jsx";
import AddEmployeur from "../Profil/Employeur/AddEmployeur.jsx";
import InscriptionHome from "../Profil/InscriptionHome.jsx";
import EmployeurHome from "../../page/EmployeurHome.jsx";
import EtudiantHome from "../../page/EtudiantHome.jsx";
import ProfesseurHome from "../../page/ProfesseurHome.jsx";

export default function AppRoutes({ user, error, message, setError, setUser,addEtudiant }) {
    return (
            <Routes>
                <Route path="/" element={<PageLayout user={user}/>}>
                    <Route index element={<MainContainer setError={setError}/>}/>
                    <Route path='about' element={<About/>}/>
                    <Route path='login' element={<LoginForm setError={setError}/>}/>
                    <Route path='inscription'>
                        <Route index element={<InscriptionHome/>}/>
                        <Route path='addetudiant' element={<AddEtudiant onAdd={addEtudiant} error={error} message={message}/>}/>
                        <Route path='addemployeur' element={<AddEmployeur/>}/>
                    </Route>
                    <Route path='logout' element={<Logout setUser={setUser}/>}/>
                    <Route path='gestionnaire' element={<GestionnaireHome/>}/>
                    <Route path='employeur' element={<EmployeurHome/>}/>
                    <Route path='etudiant' element={<EtudiantHome/>}/>
                    <Route path='professeur' element={<ProfesseurHome/>}/>
                    <Route path='error' element={<ErrorPage error={error}/>}/>
                </Route>
            </Routes>
    );
}
