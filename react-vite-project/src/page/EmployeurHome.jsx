import { useState } from "react";
import Card from "../component/Design/Card.jsx";
import { useLocation, useNavigate } from "react-router-dom";

const EmployeurHome = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const offer = location.state?.offer;

    const [selectedOffer, setSelectedOffer] = useState(null);

    const handleView = (offer) => {
        setSelectedOffer(offer);
    };

    const handleEdit = (offer) => {
        navigate("/employeur/offres/modifier", {
            state: {
                offer: offer
            }
        });
    };

    return (
        <main className="min-h-screen bg-gray-100 p-8">

            <h1 className="mb-6 text-3xl font-bold text-[#043462]">
                Page accueil Employeur
            </h1>

            {offer && (
                <Card
                    offer={offer}
                    onView={handleView}
                    onEdit={handleEdit}
                />
            )}

            {/* Panneau de détails */}
            {selectedOffer && (
                <>
                    {/* Fond derrière le panneau */}
                    <div
                        className="fixed inset-0 z-40 bg-black/20"
                        onClick={() => setSelectedOffer(null)}
                    ></div>

                    {/* Panneau à droite */}
                    <div className="fixed inset-y-0 right-0 z-50 w-full max-w-md bg-base-100 p-6 shadow-2xl">

                        {/* En-tête */}
                        <div className="flex items-center justify-between border-b border-base-300 pb-4">

                            <h2 className="text-2xl font-bold text-[#043462]">
                                Détails de l'offre
                            </h2>

                            <button
                                type="button"
                                onClick={() => setSelectedOffer(null)}
                                className="btn btn-sm btn-circle btn-ghost"
                            >
                                ✕
                            </button>

                        </div>

                        {/* Détails */}
                        <div className="mt-6 space-y-5">

                            <div>
                                <p className="text-sm text-gray-500">
                                    Titre
                                </p>

                                <p className="text-xl font-bold text-[#043462]">
                                    {selectedOffer.title}
                                </p>
                            </div>

                            <div>
                                <p className="text-sm text-gray-500">
                                    Domaine
                                </p>

                                <p className="font-medium">
                                    {selectedOffer.domain}
                                </p>
                            </div>

                            <div>
                                <p className="text-sm text-gray-500">
                                    Salaire
                                </p>

                                <p className="font-medium">
                                    {selectedOffer.salary} $ / heure
                                </p>
                            </div>

                            <div>
                                <p className="text-sm text-gray-500">
                                    Date de début
                                </p>

                                <p className="font-medium">
                                    {selectedOffer.startDate}
                                </p>
                            </div>

                            <div>
                                <p className="text-sm text-gray-500">
                                    Date de fin
                                </p>

                                <p className="font-medium">
                                    {selectedOffer.endDate}
                                </p>
                            </div>

                            <div>
                                <p className="text-sm text-gray-500">
                                    Document
                                </p>

                                <p className="font-medium">
                                    📄 {selectedOffer.fileName}
                                </p>
                            </div>

                        </div>

                    </div>
                </>
            )}

        </main>
    );
};

export default EmployeurHome;