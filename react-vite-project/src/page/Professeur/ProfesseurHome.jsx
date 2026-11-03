import React, {useCallback, useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import { useNavigate, useSearchParams} from "react-router-dom";
import {getEtudiants} from "../../api/professeur.jsx";
import WelcomeMessage from "../../component/Design/WelcomeMessage.jsx";
import EtudiantListItem from "../../component/Design/Lists/EtudiantListItem.jsx";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import {getNotifications} from "../../api/notification.jsx";
import FilterBar from "../../component/Design/Bars/FilterBar.jsx";

const ProfesseurHome = ({user}) => {
    const [error, setError] = useState();
    const navigate = useNavigate();
    const { t, i18n } = useTranslation();
    const [isInitialLoading,setIsInitialLoading] = useState(false);
    const [listeEtudiantACharge, setListeEtudiantACharge] = useState([]);
    const [nouveauEtudiantACharge, setNouveauxEtudiantACharge] = useState([]);
    const [filtreValue, setFiltreValue] = useState("");
    const [onlyNew, setOnlyNew] = useState(false);
    const [searchParams] = useSearchParams();


    const initListeEtudiant = useCallback(async () => {
        if (!user) {
            navigate("/login");
            return;
        }
        try {
            setIsInitialLoading(true);
            const etudiants = await getEtudiants();
            setListeEtudiantACharge(etudiants || []);
        } catch (e) {
            setError(t("generic.error"));
        } finally {
            setIsInitialLoading(false);
        }
    }, [user, navigate, t]);


    const fetchNouveauxEtudiants = useCallback(async (etudiants) => {
        if (!user?.id || etudiants.length === 0) return;

        try {
            const notifications = await getNotifications(user.id);
            if (!Array.isArray(notifications)) return;
            const etudiantIds = notifications
                .filter((n) => n.notificationType === "PROFESSEUR_ASSIGNE_ETUDIANT" && !n.estLu)
                .flatMap((n) => {
                    const rawIds = n.variables?.ETUDIANT_IDS;
                    if (!rawIds) return [];
                    return rawIds.split(',').map((id) => Number(id.trim()));
                });

            const uniqueIds = [...new Set(etudiantIds)];
            const nouveaux = etudiants.filter((e) => uniqueIds.includes(Number(e.id)));
            setNouveauxEtudiantACharge(nouveaux);
        } catch (e) {
            setError(t("generic.error"));
        }
    }, [user?.id, t]);

    const filtreActif = (event) => {
        setFiltreValue(event)
    }

    const trierEtudiantsNouveauxEnPremier = (a, b, nouveauxIds) => {
        const isANew = nouveauxIds.some((n) => Number(n.id) === Number(a.id));
        const isBNew = nouveauxIds.some((n) => Number(n.id) === Number(b.id));

        if (isANew && !isBNew) return -1;
        if (!isANew && isBNew) return 1;
        return 0;
    };

    useEffect(() => {
        initListeEtudiant();
    }, [initListeEtudiant]);

    useEffect(() => {
        if (listeEtudiantACharge.length > 0) {
            fetchNouveauxEtudiants(listeEtudiantACharge);
        }
    }, [listeEtudiantACharge, fetchNouveauxEtudiants]);

    useEffect(() => {
        const filterParam = searchParams.get("filter");
        if (filterParam === "new") {
            setOnlyNew(true);
        }
    }, [searchParams]);

    return(
    <>
       <WelcomeMessage user={user}/>
        {error && (
            <div className="bg-red-500">
                <p>{error}</p>
            </div>
        )}
        <div className="w-full mt-5">
            <SearchBar onChange={filtreActif} value={filtreValue}/>
            <FilterBar
                onlyNew={onlyNew}
                onOnlyNewChange={setOnlyNew}
            />
            {isInitialLoading ? (
                    <div className="flex items-center justify-center p-12 border rounded-xl bg-gray-50 mt-5">
                        <span className="loading loading-spinner loading-md text-[#043462]" />
                    </div>
                ) : (
                <details open className="collapse collapse-arrow bg-transparent mt-5">

                    <summary className="collapse-title text-xl font-bold text-[#043462] px-0">
                        {t("professeur.etudiant_section_title")} ({listeEtudiantACharge.length})
                    </summary>

                    <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                        <div className="my-4 h-px w-full bg-[#043462]" aria-hidden="true" />
                        <div className="w-full px-2 sm:px-4 pb-8 space-y-8">
                            <div className="grid grid-cols-12 gap-4 w-full">
                                {listeEtudiantACharge
                                    .filter((etudiant) => {
                                        return Object.values(etudiant).some((valeur) =>
                                            String(valeur).toLowerCase().includes(String(filtreValue).toLowerCase())
                                        );
                                    })
                                    .filter((etudiant) => {
                                        if (!onlyNew) return true;
                                        return nouveauEtudiantACharge.some((n) => Number(n.id) === Number(etudiant.id));
                                    })
                                    .sort((a, b) => trierEtudiantsNouveauxEnPremier(a, b, nouveauEtudiantACharge))
                                    .map((etudiant) => {
                                        const isNew = nouveauEtudiantACharge.some((n) => Number(n.id) === Number(etudiant.id));

                                        return (
                                            <div key={etudiant.id} className="col-span-6">
                                                <EtudiantListItem etudiant={etudiant} isNew={isNew} />
                                            </div>
                                        );
                                    })}
                            </div>
                        </div>
                    </div>
                </details>
            )}

        </div>
    </>
  );
}
export default ProfesseurHome;