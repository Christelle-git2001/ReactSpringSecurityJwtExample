import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import fetcher from "../../utils/fetcher";
import "../../css/Login.css"
import {useTranslation} from "react-i18next";


const LoginForm = ({user, setUser, setError}) => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [warnings, setWarnings] = useState({
    email: '',
    password: '',
  });

  const [erreurHTTP, setErreurHTTP] = useState({
        message: '',
      }
  )
  const { t } = useTranslation();

  const validateUser = () => {
    let isValid = true;
    let updatedWarnings = {...warnings};

    if (!validateEmail()) {
      updatedWarnings.email = "courriel invalide";
      isValid = false;
    } else {
      updatedWarnings.email = "";
    }

    if (!validatePassword()) {
      updatedWarnings.password = "mot de passe invalide";
      isValid = false;
    } else {
      updatedWarnings.password = "";
    }

    setWarnings(updatedWarnings);
    return isValid;
  };

  const validateEmail = () => {
    const emailRegex = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;
    return emailRegex.test(formData.email.trim());
  }

  const validatePassword = () => {
    return formData.password.trim().length > 0;
  }

  const handleChanges = (e) => {
    const {name, value} = e.target;
    setWarnings({...warnings, [name]: ""});
    setFormData({...formData, [name]: value.trim()});
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateUser()) {
      fetchFunc();
    }
  }

  const fetchFunc = async () => {
    try {
      const response = await fetcher('/user/login', {
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json;charset=UTF-8",
        },
        body: JSON.stringify({
          email: formData.email.toLowerCase(),
          password: formData.password
        }),
      });
      if (!response.ok) {
        switch (response.status) {
          case 404:
            throw new Error("Server not Found")
          case 401:
            setErreurHTTP({message: t("login.credentialsWrong") })
            throw new Error("AUTH_FAILED");
          default:
            throw new Error("Not ok")
        }
      }
      const token = await response.text();
      console.log("Token reçu :", token);
      localStorage.setItem('token', token);
      // Fetch user info to get role
      const userResponse = await fetcher('/user/me', {});
      if (!userResponse.ok) {
        throw new Error("Failed to fetch user info ");
      }
      const userData = await userResponse.json();

      // Navigate to role-specific page
      const role = userData.role.name;
      console.log(role)
      if (role === "GESTIONNAIRE") {
        navigate("/gestionnaire");
      } else if (role === "EMPLOYEUR") {
        navigate("/employeur");
      }else if (role === "ETUDIANT") {
        navigate("/etudiant");
      }else if (role === "PROFESSEUR") {
        navigate("/professeur");
      } else {
        navigate("/");
      }
    } catch(error) {
      if (error.message !== "AUTH_FAILED") {
        setError(error)
        navigate('/error')
      }
    }
  }

  return (
    <>
      {user?.isLoggedIn ? (
            user.role === "ROLE_GESTIONNAIRE" ? navigate("/gestionnaire") :
              navigate("/")
      ) : (
          <div>
            <div className="add-etudiant-page-header mt-10">
              <h1 className="add-etudiant-title">{t('login.page_title')}</h1>
              <p className="add-etudiant-subtitle">{t('login.page_subtitle')}</p>
              <span className="add-etudiant-subtitle-line" />
            </div>
            <div className="add-etudiant-page grid grid-cols-1 lg:grid-cols-[1fr_1.4fr] ">
              <div className="title-login-section" >
                <div className="add-etudiant-slogan">
                  <div className="circleDesign circleDesign-top-right" />
                  <div className="circleDesign circleDesign-bottom-left-1" />
                  <div className="circleDesign circleDesign-bottom-left-2" />
                  <h1 className="add-etudiant-slogan-title">{t('add_etudiant.slogan_message_1')}<br />{t('add_etudiant.slogan_message_2')}<br /><span
                      className="add-etudiant-slogan-name">{t('add_etudiant.slogan_message_3')}</span><span className="add-etudiant-slogan-line" /></h1>
                </div>
              </div>
              <div className={"form-login-section "}>
                <form className={"form-login"} noValidate onSubmit={handleSubmit}>
                  <div className={"input-login-section"}>
                    {erreurHTTP.message !== "" && (
                        <div className={"form-login-error"}>
                          {erreurHTTP.message}
                        </div>
                    )}
                    <div className={"login-input-field"}>
                      <label htmlFor="courrielLogin">{t("login.courriel")}</label>
                      <input type="email" placeholder={t("login.courriel_placeholder")} value={formData.email} onChange={handleChanges}  id="courrielLogin" name="email" className="w-full rounded-md border border-gray-300 px-3 py-2"/>
                    </div>
                    {warnings.email !== "" && (
                        <div className={"form-login-error"}>
                          {t("login.courriel.invalide")}
                        </div>
                    )}
                    <div className={"login-input-field"}>
                      <label htmlFor="motDePasseLogin">{t("login.motDePasse")}</label>
                      <input type="password" placeholder={t("login.motDePasse_placeholder")} value={formData.password} onChange={handleChanges} id="motDePasseLogin" name="password" className="w-full rounded-md border border-gray-300 px-3 py-2"/>
                    </div>
                    {warnings.password !== "" && (
                        <div className={"form-login-error"}>
                          {t("login.password.invalide")}
                        </div>
                    )}
                    <button type="submit" className="login-button-submit">
                      {t('login.submit')}
                    </button>
                    <div className="mt-10">
                      <p>{t("login.noAccount")} : <Link to="/inscription" className="add-etudiant-login-link">{t("login.inscription")}</Link></p>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
      )}
    </>
  )
}

export default LoginForm;
