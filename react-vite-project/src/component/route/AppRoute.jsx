import { Routes, Route, Navigate } from "react-router-dom";
import PublicLayout from "../Design/Layouts/PublicLayout.jsx";
import MainContainer from "../Design/MainContainer";
import LoginForm from "../auth/LoginForm";
import InscriptionHome from "../../page/InscriptionHome.jsx";
import Logout from "../auth/Logout";
import ErrorPage from "../../page/ErrorPage.jsx";
import EmployeurHome from "../../page/Employeur/EmployeurHome";
import GestionnaireHome from "../../page/Gestionnaire/GestionnaireHome.jsx";
import EtudiantHome from "../../page/Etudiant/EtudiantHome.jsx";
import ProfesseurHome from "../../page/Professeur/ProfesseurHome.jsx";
import AddOffer from "../Offer/AddOffer.jsx";
import SideBarLayout from "../Design/Layouts/SideBarLayout.jsx";
import AccountEtudiant from "../../page/Etudiant/AccountEtudiant.jsx";
import AccountEmployeur from "../../page/Employeur/AccountEmployeur.jsx";
import AccountGestionnaire from "../../page/Gestionnaire/AccountGestionnaire.jsx";
import EditOffer from "../Offer/EditOffer.jsx";
import OfferDetails from "../Offer/OfferDetails.jsx";
import GestionnaireCvs from "../../page/Gestionnaire/GestionnaireCvs.jsx";
import ViewOffers from "../../page/Etudiant/ViewOffers.jsx";
import EmployeurOffers from "../../page/Employeur/EmployeurOffers.jsx";
import GestionnaireOffers from "../../page/Gestionnaire/GestionnaireOffers.jsx";

export default function AppRoutes({
                                      user,
                                      error,
                                      message,
                                      setError,
                                      setUser,
                                      addEtudiant,
                                      addProfesseur,
                                      addEmployeur,
                                      setMessage,
                                      openDrawer
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
                            addEmployeur={addEmployeur}
                            error={error}
                            message={message}
                            setError={setError}
                            setMessage={setMessage}
                        />
                    }
                />

                <Route path="/" element={<Navigate to="/login" />} />
            </Route>

            <Route element={<SideBarLayout user={user} openDrawer={openDrawer} />}>
                <Route path="/home" element={<MainContainer setError={setError} />} />
                <Route path="/logout" element={<Logout setUser={setUser} />} />
                <Route path="/etudiant" element={<EtudiantHome user={user} />} />
                <Route path="/etudiant/account" element={<AccountEtudiant user={user}/>}/>
                <Route path="/etudiant/offers" element={<ViewOffers user={user}/>}/>
                <Route path="/employeur/account" element={<AccountEmployeur/>}/>
                <Route path="/gestionnaire/account" element={<AccountGestionnaire />}/>
                <Route path="/employeur" element={<EmployeurHome  user={user}/>} />
                <Route path="/employeur/offers" element={<EmployeurOffers />} />
                <Route path="/employeur/offres/nouvelle" element={<AddOffer />} />
                <Route path="/employeur/offres/modifier" element={<EditOffer />} />
                <Route path="/professeur" element={<ProfesseurHome />} />
                <Route path="/gestionnaire" element={<GestionnaireHome  user={user}/>} />
                <Route path="/gestionnaire/offers" element={<GestionnaireOffers />} />
                <Route path="/gestionnaire/cvs" element={<GestionnaireCvs />} />
                <Route path="/gestionnaire/offres/details" element={<OfferDetails/>} />
                <Route path="/error" element={<ErrorPage error={error} />} />
            </Route>

        </Routes>
    );
}
