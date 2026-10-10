import { useTranslation } from "react-i18next";
import {useEffect, useState} from "react";
import CvCard from "../../component/Design/Cards/CvCard.jsx";
import OffreStageCard from "../../component/Design/Cards/OffreStageCard.jsx";
import {approuverCv, getOffresEnAttente, getTousLesCvs, refuserCv} from "../../api/gestionnaire.jsx";
import {useNavigate} from "react-router-dom";

const GestionnaireHome = ({user}) => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [cvs, setCvs] = useState([]);
    const [offres, setOffres] = useState([]);
    const [error, setError] = useState(null);

    useEffect(() => {
        chargerCvs();
        getOffresEnAttente().then((res) => {setOffres(res);})
            .catch((err) => console.log(err));
    }, []);

    const handleView = (offer) => {
        navigate(`/gestionnaire/offres/details`, { state: { offer, from:"/gestionnaire" } });
    };

    const chargerCvs = async () => {
        try {
            const data = await getTousLesCvs();
            setCvs(data);
            setError(null);
        } catch (err) {
            setError("error.generic");
        }
    };

    const handleApprove = async (id) => {
        try {
            await approuverCv(id);
            await chargerCvs();
        } catch (err) {
            setError("error.generic");
        }
    };

    const handleReject = async (id, comment) => {
        try {
            await refuserCv(id, comment);
            await chargerCvs();
        } catch (err) {
            setError("error.generic");
        }
    };

    return(
        <div className={"pt-5 h-full"}>
            <h1 className={"text-4xl mb-10 text-center"}>{t("home.welcome")} {user?.firstName} {user?.lastName}</h1>
            <h2 className="text-2xl font-bold mb-4 text-center">{t("cv.pending_cvs_title")}</h2>

            <div className="space-y-4 w-full max-w-4xl mx-auto mb-10">
                {cvs.length === 0 && !error && (
                    <p className="text-center opacity-70">{t("cv.none_pending")}</p>
                )}
                {cvs.map((cv) => (
                    <CvCard key={cv.id} cv={cv} onApprove={handleApprove} onReject={handleReject} />
                ))}
            </div>

            <h2 className="text-2xl font-bold mb-4 text-center">{t("offre.pending_offers")}</h2>
            {offres.length === 0 && (
                <p className="text-center opacity-70">{t("offre.no_offers")}</p>
            )}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 justify-items-center w-full">
                {offres.map((offer) => (
                    <OffreStageCard key={offer.id} offer={offer} onView={handleView} />
                ))}
            </div>

        </div>
    );
}
export default GestionnaireHome;