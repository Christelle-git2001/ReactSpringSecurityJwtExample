import { Routes, Route } from "react-router-dom";
import PageLayout from "../Component/Design/PageLayout";
import MainContainer from "../Component/Design/MainContainer";
import About from "../Component/Design/About";
import LoginForm from "../Component/auth/LoginForm";
import AddEtudiant from "../Component/Profil/Etudiant/AddEtudiant";
import Logout from "../Component/auth/Logout";
import ErrorPage from "../Component/Design/ErrorPage";
import EmprunteurHome from "../page/EmprunteurHome";
import PreposeHome from "../page/PreposeHome";
import GestionnaireHome from "../page/GestionnaireHome";

export default function AppRoutes({ user, error, message, setError, setUser }) {
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
