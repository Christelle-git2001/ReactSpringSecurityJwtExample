import { useState } from "react";
import fetcher from "../../utils/fetcher.js";

const GestionnaireHome = () => {
  const [message, setMessage] = useState("");

  const handleAccessGestionnaireEndpoint = () => {
    setMessage("");
    fetcher("/user/gestionnaire/demo", { method: "GET" })
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(`Erreur API (${response.status})`);
        }
        const data = await response.text();
        setMessage(data);
      })
      .catch((error) => {
        setMessage(error.message);
      });
  };

  return(
    <>
      <h1>Page accueil gestionnaire</h1>
      <button style={{ width: 'fit-content' }} onClick={handleAccessGestionnaireEndpoint}>
        Accéder à l'endpoint gestionnaire
      </button>
      {message && <p>{message}</p>}
    </>
  );
}
export default GestionnaireHome;