import React, { useEffect, useState } from "react";
import { useOutletContext } from "react-router-dom";
import Card from "./../component/Design/Card.jsx";
import { fetchOffresEmployeur } from "../utils/offerService.js";
import { useTranslation } from "react-i18next";

const EmployeurHome = ({ user }) => {
    const { t } = useTranslation();
    const { openDrawer } = useOutletContext();

    const [offres, setOffres] = useState([]);
    const [selectedOffer, setSelectedOffer] = useState(null);

    const handleView = (offer) => {
        setSelectedOffer(offer);

        openDrawer(
            <div className="space-y-4">
                <h2 className="text-xl font-bold">{offer.title}</h2>

                <p>
                    <strong>{t("offre.description")} :</strong><br />
                    {offer.description}
                </p>

                <ul className="mt-4 text-sm space-y-1">
                    <li><strong>{t("offre.domaine")} :</strong> {offer.domain}</li>
                    <li><strong>{t("offre.date_debut")} :</strong> {offer.startDate}</li>
                    <li><strong>{t("offre.date_fin")} :</strong> {offer.endDate}</li>
                    <li><strong>{t("offre.document")} :</strong> {offer.fileName || t("offre.aucun")}</li>
                </ul>
            </div>
        );
    };

    useEffect(() => {
        fetchOffresEmployeur(user?.id).then(res => {
            setOffres(res);
            setSelectedOffer(res[0]);
        });
    }, [user]);

    return (
        <>
            <h1>Page accueil Employeur</h1>

            {selectedOffer && (
                <Card
                    offer={selectedOffer}
                    onView={handleView}
                    onEdit={() => {}}
                />
            )}

            <div className="mt-6 flex gap-4 flex-wrap">
                {offres.map((offer) => (
                    <Card
                        key={offer.id}
                        offer={offer}
                        onView={handleView}
                        onEdit={() => {}}
                    />
                ))}
            </div>
        </>
    );
};

export default EmployeurHome;
