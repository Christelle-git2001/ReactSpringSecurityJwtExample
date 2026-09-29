import React, { useEffect, useState } from "react";
import { useOutletContext, useNavigate } from "react-router-dom";
import Card from "./../component/Design/Card.jsx";
import { fetchOffresEmployeur } from "../utils/offerService.js";
import { useTranslation } from "react-i18next";
import { getSecteursEmployeur } from "../api/http.jsx";
import SearchBar from "../component/Design/SearchBar.jsx";

const EmployeurHome = ({ user }) => {
    const { t } = useTranslation();
    const { openDrawer } = useOutletContext();
    const navigate = useNavigate();

    const [offres, setOffres] = useState([]);

    const handleView = (offer) => {
        openDrawer(
            <div className="space-y-4">
                <div className="bg-base-100 p-4 rounded-lg shadow-md">
                    <h2 className="text-xl font-bold">{offer.title}</h2>
                    <ul className="mt-4 text-sm space-y-1">
                        <li><strong>{t("offre.domaine")} :</strong> {offer.domain}</li>
                        <li><strong>{t("offre.date_debut")} :</strong> {offer.startDate}</li>
                        <li><strong>{t("offre.date_fin")} :</strong> {offer.endDate}</li>
                        <li><strong>{t("offre.document")} :</strong> {offer.fileName || t("offre.aucun")}</li>
                    </ul>
                    <p className="mt-2">
                        <strong>{t("offre.description")} :</strong><br />
                        {offer.description}
                    </p>
                </div>
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
        <SearchBar className="self-center mb-6" />
        <div className="flex flex-col md:flex-row
                gap-6 md:gap-16
                items-start">

            {/* Colonne des offres */}
            <div className="mt-6 flex flex-col gap-6 flex-1">
                {offres.map((offer) => (
                    <Card
                        key={offer.id}
                        offer={offer}
                        onView={handleView}
                        onEdit={handleEdit}
                    />
                ))}
            </div>

            {/* Bloc candidatures */}
            <div className="
                mt-6 bg-white p-6 rounded-lg shadow-md
                w-full md:w-[420px] lg:w-[500px]
                h-auto md:h-[85vh] lg:h-[90vh]
                overflow-y-auto
            ">
                <h3 className="font-semibold text-sm mb-4">Candidatures</h3>
                <p className="text-xs text-gray-600">Aucune candidature pour le moment</p>
            </div>

        </div>
        </>
    );
};

export default EmployeurHome;
