import { useTranslation } from "react-i18next";
import { useEffect, useState } from "react"
import CardEtudiant from "../../component/Design/Cards/OffreStageCard.jsx";
import { getOffresEmployeur } from "../../api/employeur.jsx";
import {useOutletContext} from "react-router-dom";
import OffreDetailsDrawerContent from "../../component/Offer/OffreDetailsDrawerContent.jsx";

const EmployeurHome = ({user}) => {
    const { t } = useTranslation();
    const [offresEnAttente, setOffresEnAttente] = useState([]);

    const { openDrawer } = useOutletContext();

    const handleView = (offer) => {
        openDrawer(<OffreDetailsDrawerContent offer={offer} />);
    };

    useEffect(() => {
        getOffresEmployeur()
            .then((offres) => {
                const enAttente = (offres || []).filter(
                    (offer) => offer.statut === "EN_ATTENTE"
                );
                setOffresEnAttente(enAttente);
            })
            .catch((err) => console.error(err));
    }, []);

    return(
        <div className={"pt-5 h-full"}>
            <h1 className={"text-4xl mb-10 text-center"}>{t("home.welcome")} {user?.firstName} {user?.lastName}</h1>
            <h2 className="text-2xl font-bold text-[#043462] mb-6 text-center">{t("offre.pending_offers")}</h2>

            {offresEnAttente.length === 0 ? (
                <p className="text-gray-500 font-medium text-center">
                    {t("offre.no_pending_offers")}
                </p>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 justify-center justify-items-center w-full">
                    {offresEnAttente.map((offer) => (
                        <CardEtudiant
                            key={offer.id}
                            offer={offer}
                            onView={handleView}
                        />
                    ))}
                </div>
            )}
        </div>
    );
};

export default EmployeurHome;