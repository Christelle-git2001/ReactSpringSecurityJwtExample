import "./App.css";
import PageLayout from "./components/PageLayout.jsx";
import React, {useEffect, useState} from "react";
import {Route, Routes, useNavigate} from "react-router-dom";
import MainContainer from "./components/MainContainer.jsx";
import About from "./components/About.jsx";
import LoginForm from "./components/auth/LoginForm.jsx";
import fetcher from "./utils/fetcher.js";
import ErrorPage from "./components/ErrorPage.jsx";
import Logout from "./components/auth/Logout.jsx";
import EmprunteurHome from "./components/page/EmprunteurHome.jsx";
import PreposeHome from "./components/page/PreposeHome.jsx";
import GestionnaireHome from "./components/page/GestionnaireHome.jsx";
import AddEtudiant from "./components/AddEtudiant.jsx";
import { inscrireEtudiant } from "./api.jsx";
function App() {
  const [user, setUser] = useState({})
  const [error, setError] = useState(null)
  const [message, setMessage] = useState("")
  const navigate = useNavigate();

  let token = localStorage.getItem('token')

  async function addEtudiant(etudiant) {
    try {
      await inscrireEtudiant(etudiant);
      setMessage("Étudiant ajouté avec succès.");
      setError(null);
      setMessage("");
      navigate('/login');
      return true;
    } catch (error) {
      setError(error.message || "Une erreur inattendue s'est produite.");
      setMessage("");
      return false;
    }
  }

  useEffect(() => {
      if (token) {
        try {
          fetcher('user/me', {})
            .then(async (res) => {
                if (!res.ok) {
                  switch (res.status) {
                    case 401:
                      localStorage.clear();
                      setUser(null);
                    case 403:
                      throw new Error("Forbidden")
                    case 404:
                      throw new Error("Nothing here 404");
                  }
                }
                const data = await res.json();
                let newUser = {...data, isLoggedIn: true}
                setUser(newUser)
              }
            ).catch(async (err) => {
              setError(err)
              navigate('/error')
          })

        } catch (err) {
          if (!error) {
            setError(err)
            navigate('/error')
          }
        }
      }
    }, [token]
  );

  return (
    <div>
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

    </div>
  );
}

export default App;
