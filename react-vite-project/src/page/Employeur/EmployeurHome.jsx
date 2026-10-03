import React, { useEffect, useState } from "react";
import { useOutletContext, useNavigate } from "react-router-dom";
import Card from "../../component/Design/Cards/Card.jsx";
import { getOffresEmployeur, obtenirOffrePDF } from "../../api/employeur.jsx";
import { useTranslation } from "react-i18next";
import SearchBar from "../../component/Design/SearchBar.jsx";
import { PDFContent } from "../../component/PDF/PDFVisioneuse.jsx";

const EmployeurHome = () => {
    const { t } = useTranslation();
    const { openDrawer } = useOutletContext();
    const navigate = useNavigate();

    const [offres, setOffres] = useState([]);

    const handleView = async (offer) => {
        let pdfBlobUrl = null;

        try {
            // 1. Récupérer le fichier PDF depuis l'API Spring Boot
            const response = await obtenirOffrePDF(offer.id);

            if (response.ok) {
                // 2. Transformer la réponse binaire en Blob
                const blob = await response.blob();
                // 3. Créer une URL temporaire utilisable par <Document file={...} />
                pdfBlobUrl = URL.createObjectURL(blob);
            }
        } catch (error) {
            console.error("Erreur lors de la récupération du PDF de l'offre:", error);
        }

        // 4. Ouvrir le drawer avec le composant PDFContent
        openDrawer(
            <div className="space-y-4">
                <div className="bg-base-100 p-4 rounded-lg shadow-md">
                    <h2 className="text-xl font-bold">{offer.title}</h2>

                    <ul className="mt-4 text-sm space-y-1">
                        <li><strong>{t("offre.domaine")} :</strong> {offer.domain}</li>
                        <li><strong>{t("offre.date_debut")} :</strong> {offer.startDate}</li>
                        <li><strong>{t("offre.date_fin")} :</strong> {offer.endDate}</li>
                    </ul>

                    <p className="mt-2 text-sm">
                        <strong>{t("offre.description")} :</strong><br />
                        {offer.description}
                    </p>

                    <div className="mt-4">
                        <h3 className="font-semibold text-sm mb-2">{t("offre.document")} :</h3>
                        {pdfBlobUrl ? (
                            <PDFContent cvUrlAAffiche={pdfBlobUrl} width={300} />
                        ) : (
                            <p className="text-xs text-gray-500">Aucun document attaché ou erreur de chargement</p>
                        )}
                    </div>
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
        getOffresEmployeur()
            .then((res) => setOffres(res))
            .catch((err) => console.error(err));
    }, []);

    return (
        <div className="w-full max-w-7xl mx-auto px-4 flex flex-col items-center">
            <SearchBar className="mb-6" />
            <div className="w-full flex flex-col md:flex-row gap-8 items-start mt-6">
                <div className="w-full md:w-2/3 flex justify-center">
                    <div className="flex flex-col gap-6 w-full max-w-xl">
                        {!offres || offres.length === 0 ? (
                            <div className="items-center flex justify-center w-full">
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
                                    onEdit={handleEdit}
                                />
                            ))
                        )}
                    </div>
                </div>
                <div className="
                    w-full md:w-1/3 bg-white p-6 rounded-lg shadow-md
                    h-auto md:h-[85vh] lg:h-[90vh]
                    overflow-y-auto sticky top-4
                ">
                    <h3 className="font-semibold text-sm mb-4">Candidatures</h3>
                    <p className="text-xs text-gray-600">Aucune candidature pour le moment</p>
                </div>
            </div>
        </div>
    );
};

export default EmployeurHome;