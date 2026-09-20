import { Routes, Route } from "react-router-dom";
import PageLayout from "../Design/PageLayout.jsx";
import MainContainer from "../Design/MainContainer";
import About from "../Design/About";
import LoginForm from "../auth/LoginForm";
import AddEtudiant from "../Profil/Etudiant/AddEtudiant.jsx";
import Logout from "../auth/Logout";
import ErrorPage from "../Design/ErrorPage";
import EmprunteurHome from "../../page/EmprunteurHome.jsx";
import PreposeHome from "../../page/PreposeHome";
import GestionnaireHome from "../../page/GestionnaireHome.jsx";

export default function AppRoutes({ user, error, message, setError, setUser,addEtudiant }) {
    return (
            <Routes>
                <Route path="/" element={<PageLayout user={user}/>}>
                    <Route index element={<MainContainer setError={setError}/>}/>
                    <Route path='about' element={<About/>}/>
                    <Route path='login' element={<LoginForm setError={setError}/>}/>
                    <Route path='addetudiant' element={<AddEtudiant onAdd={addEtudiant} error={error} message={message}/>}/>
                    <Route path='logout' element={<Logout setUser={setUser}/>}/>
                    <Route path='emprunteur' element={<EmprunteurHome/>}/>
                    <Route path='prepose' element={<PreposeHome/>}/>
                    <Route path='gestionnaire' element={<GestionnaireHome/>}/>
                    <Route path='error' element={<ErrorPage error={error}/>}/>
                </Route>
            </Routes>
    );
}
