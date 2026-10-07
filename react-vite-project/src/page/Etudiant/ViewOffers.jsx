import { useCallback, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import OffreCard from "../../component/Design/Cards/OffreCard";
import OffreDetails from "../../component/Design/OffreDetails";
import {obtenirOffres} from "../../api/etudiant.jsx";

const ViewOffers = () => {
    const { t } = useTranslation();
    const [offres, setOffres] = useState([]);
    const [etat, setEtat] = useState("chargement");
    const [selection, setSelection] = useState(null);
    const [filtres, setFiltres] = useState({ titre: "", domaine: "", salaireMin: "" });

    const charger = useCallback(async () => {
        setEtat("chargement");
        try {
            setOffres(await obtenirOffres());
            setEtat("ok");
        } catch {
            setEtat("erreur");
        }
    }, []);

    useEffect(() => { charger(); }, [charger]);

    const domaines = useMemo(() => [...new Set(offres.map((o) => o.domain))].sort(), [offres]);

    const offresFiltrees = useMemo(() => {
        const titre = filtres.titre.trim().toLowerCase();
        return offres.filter((o) =>
            (!titre || o.title.toLowerCase().includes(titre)) &&
            (!filtres.domaine || o.domain === filtres.domaine) &&
            (!filtres.salaireMin || o.salary >= Number(filtres.salaireMin))
        );
    }, [offres, filtres]);

    const changer = (e) => setFiltres({ ...filtres, [e.target.name]: e.target.value });

    const postuler = (offre) => {
        console.log("Postuler à", offre.id);
    };

    return (
            <div className="p-6">
                <h1 className="text-4xl mb-6 text-center">{t("etudiant.offers.page_title")}</h1>

                <div className="flex gap-3 mb-6 justify-center">
                    <input
                        name="titre"
                        value={filtres.titre}
                        onChange={changer}
                        placeholder={t("etudiant.offers.title_filter")}
                        className="input input-bordered"
                    />
                    <input
                        name="salaireMin"
                        type="number"
                        min="0"
                        value={filtres.salaireMin}
                        onChange={changer}
                        placeholder={t("etudiant.offers.salary_filter")}
                        className="input input-bordered"
                    />
                </div>

                {etat === "chargement" && (
                    <div className="flex justify-center"><span className="loading loading-spinner loading-lg" /></div>
                )}

                {etat === "erreur" && (
                    <div role="alert" className="alert alert-error max-w-xl mx-auto">
                        <span>{t("etudiant.offers.no_offers")}</span>
                    </div>
                )}

                {etat === "ok" && offres.length === 0 && (
                    <div role="alert" className="alert alert-error max-w-xl mx-auto">
                        <span>{t("etudiant.offers.no_offers")}</span>
                    </div>
                )}

                {etat === "ok" && offres.length > 0 && offresFiltrees.length === 0 && (
                    <div role="alert" className="alert alert-error max-w-xl mx-auto">
                        <span>{t("etudiant.offers.no_offers")}</span>
                    </div>
                )}

                <div className="flex flex-wrap gap-6 justify-center">
                    {offresFiltrees.map((o) => (
                        <OffreCard key={o.id} offre={o} onDetails={setSelection} />
                    ))}
                </div>
                <OffreDetails offre={selection} onClose={() => setSelection(null)} onPostuler={postuler} />
            </div>
    );
};

export default ViewOffers;