import { Routes, Route, Navigate } from "react-router-dom";

import PageLayout from "../Design/PageLayout.jsx";
import PublicLayout from "../Design/PublicLayout.jsx";

import MainContainer from "../Design/MainContainer";
import About from "../Design/About";
import LoginForm from "../auth/LoginForm";

import InscriptionHome from "../../page/InscriptionHome.jsx";

import Logout from "../auth/Logout";
import ErrorPage from "../Design/ErrorPage";
import EmployeurHome from "../../page/EmployeurHome";
import GestionnaireHome from "../../page/GestionnaireHome.jsx";
import EtudiantHome from "../../page/EtudiantHome.jsx";
import ProfesseurHome from "../../page/ProfesseurHome.jsx";

export default function AppRoutes({
                                      user,
                                      error,
                                      message,
                                      setError,
                                      setUser,
                                      addEtudiant,
                                      addProfesseur,
                                      setMessage
                                  }) {
    return (
        <Routes>

            <Route element={<PublicLayout />}>
                <Route path="/login" element={<LoginForm user={user} setError={setError} />} />

                <Route
                    path="/inscription"
                    element={
                        <InscriptionHome
                            addEtudiant={addEtudiant}
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
                <Route  path="/etudiant" element={<EtudiantHome />} />
                <Route path="/employeur" element={<EmployeurHome />} />
                <Route path="/professeur" element={<ProfesseurHome />} />
                <Route path="/gestionnaire" element={<GestionnaireHome />} />
                <Route path="/error" element={<ErrorPage error={error} />} />
            </Route>

        </Routes>
    );
}
