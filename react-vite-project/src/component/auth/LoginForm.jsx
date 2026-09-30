import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import fetcher from "../../utils/fetcher";
import LanguageSwitch from "../../locales/LanguageSwitch.jsx";
import "../../css/AddEtudiant.css";
import "../../css/InscriptionHome.css";
import "../../css/Login.css";

const LoginForm = ({ user, setError }) => {
    const navigate = useNavigate();
    const { t } = useTranslation();

    const [formData, setFormData] = useState({
        email: "",
        password: ""
    });

    const [warnings, setWarnings] = useState({
        email: "",
        password: ""
    });

    const [erreurHTTP, setErreurHTTP] = useState({
        message: ""
    });

    const validateEmail = (email) => {
        const emailRegex = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;
        return emailRegex.test(email.trim());
    };

    const validatePassword = (password) => {
        return password.trim().length > 0;
    };

    const validateUser = () => {
        let isFormValid = true;
        let updatedWarnings = { ...warnings };

        if (!validateEmail(formData.email)) {
            updatedWarnings.email = "courriel invalide";
            isFormValid = false;
        } else {
            updatedWarnings.email = "";
        }

        if (!validatePassword(formData.password)) {
            updatedWarnings.password = "mot de passe invalide";
            isFormValid = false;
        } else {
            updatedWarnings.password = "";
        }

        setWarnings(updatedWarnings);

        return isFormValid;
    };

    const handleChanges = (e) => {
        const { name, value } = e.target;

        setWarnings({
            ...warnings,
            [name]: ""
        });

        setErreurHTTP({
            message: ""
        });

        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        if (validateUser()) {
            fetchFunc();
        }
    };

    const fetchFunc = async () => {
        try {
            const response = await fetcher("/user/login", {
                method: "POST",
                headers: {
                    Accept: "application/json",
                    "Content-Type": "application/json;charset=UTF-8"
                },
                body: JSON.stringify({
                    email: formData.email.toLowerCase().trim(),
                    password: formData.password.trim()
                })
            });

            if (!response.ok) {

                if (response.status === 401 || response.status === 400) {
                    setErreurHTTP({
                        message: t("login.credentialsWrong")
                    });
                    return;
                }

                if (response.status === 404) {
                    setErreurHTTP({
                        message: "Service d'authentification introuvable."
                    });
                    return;
                }

                setErreurHTTP({
                    message: "Une erreur serveur est survenue."
                });

                return;
            }

            const token = await response.text();

            if (!token) {
                setErreurHTTP({
                    message: t("login.credentialsWrong")
                });
                return;
            }

            // Sauvegarde du JWT
            localStorage.setItem("token", token);

            // Récupération du profil utilisateur
            const userResponse = await fetcher("/user/me", {});

            if (!userResponse.ok) {
                setErreurHTTP({
                    message: "Impossible de récupérer votre profil."
                });
                return;
            }

            const userData = await userResponse.json();

            const role = userData.role?.name;

            // Redirection selon le rôle
            switch (role) {

                case "GESTIONNAIRE":
                    navigate("/gestionnaire");
                    break;

                case "EMPLOYEUR":
                    navigate("/employeur");
                    break;

                case "ETUDIANT":
                    navigate("/etudiant");
                    break;

                case "PROFESSEUR":
                    navigate("/professeur");
                    break;

                default:
                    navigate("/");
            }

        } catch (error) {
            console.error("Erreur d'authentification :", error);

            if (error.message === "Failed to fetch") {
                setErreurHTTP({
                    message:
                        "Impossible de se connecter au serveur backend. Vérifiez votre connexion."
                });
            } else {
                setError(error);
                navigate("/error");
            }
        }
    };

    return (
        <>
                <div className="inscription-wrapper">

                    {/* Langue */}
                    <div className="fixed top-4 right-4 z-50">
                        <LanguageSwitch />
                    </div>

                    {/* Arrière-plan */}
                    <div className="inscription-background">
                        <div className="circleDesign circleDesign-top-right" />
                        <div className="circleDesign circleDesign-bottom-left-1" />
                        <div className="circleDesign circleDesign-bottom-left-2" />
                    </div>

                    {/* Contenu */}
                    <div className="inscription-page">

                        {/* Titre */}
                        <div className="add-etudiant-page-header mt-10">

                            <h1 className="add-etudiant-title">
                                {t("login.page_title")}
                            </h1>

                            <p className="add-etudiant-subtitle">
                                {t("login.page_subtitle")}
                            </p>

                            <span className="add-etudiant-subtitle-line" />

                        </div>

                        {/* Formulaire */}
                        <form
                            className="add-etudiant-form login-form"
                            noValidate
                            onSubmit={handleSubmit}
                        >

                            <div className="add-etudiant-fields">

                                {/* Erreur serveur */}
                                {erreurHTTP.message !== "" && (
                                    <div className="form-login-error">
                                        {erreurHTTP.message}
                                    </div>
                                )}

                                {/* Courriel */}
                                <div className="login-input-field">

                                    <label htmlFor="courrielLogin">
                                        {t("login.courriel")}
                                    </label>

                                    <input
                                        type="email"
                                        required
                                        placeholder={t("login.courriel_placeholder")}
                                        value={formData.email}
                                        onChange={handleChanges}
                                        id="courrielLogin"
                                        name="email"
                                        className="add-etudiant-input"
                                    />

                                </div>

                                {/* Erreur courriel */}
                                {warnings.email !== "" && (
                                    <div className="form-login-error">
                                        {t("login.courriel.invalide")}
                                    </div>
                                )}

                                {/* Mot de passe */}
                                <div className="login-input-field">

                                    <label htmlFor="motDePasseLogin">
                                        {t("login.motDePasse")}
                                    </label>

                                    <input
                                        type="password"
                                        required
                                        placeholder={t("login.motDePasse_placeholder")}
                                        value={formData.password}
                                        onChange={handleChanges}
                                        id="motDePasseLogin"
                                        name="password"
                                        className="add-etudiant-input"
                                    />

                                </div>

                                {/* Erreur mot de passe */}
                                {warnings.password !== "" && (
                                    <div className="form-login-error">
                                        {t("login.password.invalide")}
                                    </div>
                                )}

                                {/* Bouton */}
                                <button
                                    type="submit"
                                    className="primary-submit"
                                >
                                    {t("login.submit")}
                                </button>

                                {/* Inscription */}
                                <div className="mt-10">

                                    <p className="add-etudiant-login-text">
                                        {t("login.noAccount")} :{" "}

                                        <Link
                                            to="/inscription"
                                            className="add-etudiant-login-link"
                                        >
                                            {t("login.inscription")}
                                        </Link>

                                    </p>

                                </div>

                            </div>

                        </form>

                    </div>

                </div>

        </>
    );
};

export default LoginForm;