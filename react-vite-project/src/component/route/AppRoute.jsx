import { Routes, Route, Navigate } from "react-router-dom";

import PageLayout from "../Design/PageLayout.jsx";
import PublicLayout from "../Design/PublicLayout.jsx";

import MainContainer from "../Design/MainContainer";
import About from "../Design/About";
import LoginForm from "../auth/LoginForm";

import InscriptionHome from "../../page/InscriptionHome.jsx";

import Logout from "../auth/Logout";
import ErrorPage from "../Design/ErrorPage";
import EmprunteurHome from "../../page/EmprunteurHome.jsx";
import PreposeHome from "../../page/PreposeHome";
import GestionnaireHome from "../../page/GestionnaireHome.jsx";

export default function AppRoutes({
                                      user,
                                      error,
                                      message,
                                      setError,
                                      setUser,
                                      addEtudiant,
                                      addProfesseur,
                                      addEmployeur,
                                      setMessage
                                  }) {
    return (
        <Routes>

            <Route element={<PublicLayout />}>
                <Route path="/login" element={<LoginForm setError={setError} />} />

                <Route
                    path="/inscription"
                    element={
                        <InscriptionHome
                            addEtudiant={addEtudiant}
                            addEmployeur={addEmployeur}
                            addProfesseur={addProfesseur}
                            error={error}
                            message={message}
                            setError={setError}
                            setMessage={setMessage}
                        />

                    }
                />

                <Route path="/" element={<Navigate to="/login" />} />
            </Route>

            <Route element={<PageLayout user={user} />}>
                <Route path="/home" element={<MainContainer setError={setError} />} />
                <Route path="/about" element={<About />} />
                <Route path="/logout" element={<Logout setUser={setUser} />} />
                <Route path="/emprunteur" element={<EmprunteurHome />} />
                <Route path="/prepose" element={<PreposeHome />} />
                <Route path="/gestionnaire" element={<GestionnaireHome />} />
                <Route path="/error" element={<ErrorPage error={error} />} />
            </Route>

        </Routes>
    );
}
