import React, { useEffect, useState } from "react";
import { useOutletContext, useNavigate } from "react-router-dom";
import Card from "./../component/Design/Card.jsx";
import { fetchOffresEmployeur } from "../utils/offerService.js";
import { useTranslation } from "react-i18next";

const EmployeurHome = ({ user }) => {
    const { t } = useTranslation();
    const { openDrawer } = useOutletContext();
    const navigate = useNavigate();

    const [offres, setOffres] = useState([]);

    const handleView = (offer) => {
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

    const handleEdit = (offer) => {
        navigate("/employeur/offres/modifier", {
            state: { offer }
        });
    };

    useEffect(() => {
        fetchOffresEmployeur(user?.id).then(res => {
            setOffres(res);
        });
    }, [user]);

    return (
        <>
            <h1>Page accueil Employeur</h1>

            <div className="mt-6 flex gap-4 flex-wrap">
                {offres.map((offer) => (
                    <Card
                        key={offer.id}
                        offer={offer}
                        onView={handleView}
                        onEdit={handleEdit}
                    />
                ))}
            </div>
        </>
    );
};

export default EmployeurHome;
