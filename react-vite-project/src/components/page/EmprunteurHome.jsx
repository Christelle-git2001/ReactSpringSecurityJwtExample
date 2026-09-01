import { useState } from "react";
import fetcher from "../../utils/fetcher.js";

const EmprunteurHome = () => {
  const [message, setMessage] = useState("");

  const handleAccessGestionnaireEndpoint = () => {
    setMessage("");
    fetcher("/user/gestionnaire/demo", { method: "GET" })
      .then(async (response) => {
        if (response.status === 403) {
          throw new Error("Accès refusé: endpoint réservé au gestionnaire (403).");
        }
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
      <h1>Page accueil emprunteur</h1>
      <button style={{ width: 'fit-content' }} onClick={handleAccessGestionnaireEndpoint}>
        Accéder à l'endpoint gestionnaire
      </button>
      {message && <p>{message}</p>}
    </>
  );
}
export default EmprunteurHome;