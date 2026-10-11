import React, { useEffect, useState } from "react";
import CvOnLandingPage from "../../component/CV/CvOnLandingPage.jsx";
import { useTranslation } from "react-i18next";
import EtudiantOffers from "./EtudiantOffers.jsx";
import FilterBar from "../../component/Design/FilterBar.jsx";
import { getOffresEtudiant } from "../../api/etudiant.jsx"; // <-- tu dois avoir cette API
import { useOffreFilters } from "../../utils/useOffreFilters.jsx";

const EtudiantHome = ({ user }) => {
    const { t } = useTranslation();

    const [offres, setOffres] = useState([]);

    // Charger les offres étudiant
    useEffect(() => {
        getOffresEtudiant()
            .then((res) => setOffres(res))
            .catch((err) => console.error(err));
    }, []);

    // Filtrage uniquement par lieu
    const {
        selectedTown,
        setSelectedTown,
        availableTowns,
        offresFiltrees,
    } = useOffreFilters(offres, { filterByTownOnly: true });

    return (
        <div className="w-full mx-auto px-6 sm:px-8 lg:px-10 py-6 flex flex-col items-center">

            {/* CV */}
            <div className="w-full max-w-7xl mb-6">
                <CvOnLandingPage />
            </div>

            {/* TITRE */}
            <div className="w-full max-w-7xl">
                <h1 className="welcome-message text-left">
                    {t("home.welcome")} {user?.firstName} {user?.lastName}
                </h1>
            </div>

            {/* FILTRE PAR LIEU */}
            <div className="w-full max-w-7xl space-y-4 mb-6 mt-6">
                <FilterBar
                    selectedTown={selectedTown}
                    onTownChange={setSelectedTown}
                    availableTowns={availableTowns}
                    selectedStatus={undefined}
                    selectedDomain={undefined}
                    className="bg-transparent border-none shadow-none"
                />
            </div>

            {/* OFFRES ÉTUDIANT — collapse style employeur */}
            <div className="w-full max-w-7xl">
                <div className="collapse collapse-arrow bg-transparent">
                    <input type="checkbox" className="peer" defaultChecked />

                    <div className="collapse-title text-xl font-bold text-[#043462] px-0">
                        {t("nav.view_offers")}
                    </div>

                    <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                        <EtudiantOffers
                            user={user}
                            filteredOffers={offresFiltrees}
                        />
                    </div>
                </div>
            </div>

        </div>
    );
};

export default EtudiantHome;
