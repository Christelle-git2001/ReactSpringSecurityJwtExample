import { useState, useEffect } from "react";
import fetcher from "../../utils/fetcher.js";
import {getOffresEnAttente} from "../../api/gestionnaire.jsx";
import { useOutletContext, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Card from "../../component/Design/Cards/Card.jsx";

const GestionnaireHome = () => {
    const { t } = useTranslation();
    const [offres, setOffres] = useState([]);
    const { openDrawer } = useOutletContext();
    const navigate = useNavigate();

    useEffect(() => {
        getOffresEnAttente().then((res) => {setOffres(res);})
            .catch((err) => console.log(err));
    }, []);

    const handleView = (offer) => {
        navigate(`/gestionnaire/offres/details`, { state: { offer } });
    };



  return (
    <>
      <h1>Page accueil gestionnaire</h1>

        <div className="mt-6 flex flex-col gap-6 flex-1 items-center">
            {!offres || offres.length === 0 ? (
                <div className=" items-center flex justify-center w-full">
                    <p className="text-gray-500 font-medium">
                        {t("offre.no_offers")}
                    </p>
                </div>
            ) : (
                offres.map((offer) => (
                    <Card
                        key={offer.id}
                        offer={offer}
                        onView={handleView}
                    />
                ))
            )}
        </div>

    </>
  );
}
export default GestionnaireHome;