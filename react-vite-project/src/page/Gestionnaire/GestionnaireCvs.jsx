import CvCard from "../../component/CV/CvCard.jsx";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { approuverCv, getTousLesCvs, refuserCv } from "../../api/gestionnaire.jsx";
import FilterBar from "../../component/Design/FilterBar.jsx";

function GestionnaireCvs() {
    const { t } = useTranslation();
    const [selectedStatus, setSelectedStatus] = useState("EN_ATTENTE");
    const [searchTerm, setSearchTerm] = useState("");
    const [cvs, setCvs] = useState([]);
    const [error, setError] = useState(null);

    const chargerCvs = async () => {
        try {
            const data = await getTousLesCvs();
            setCvs(data);
            setError(null);
        } catch (err) {
            setError("error.generic");
        }
    };

    useEffect(() => {
        chargerCvs();
    }, []);

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

    const cvsFiltres = cvs.filter((cv) => {
        const matchesStatus = selectedStatus === "TOUS" || cv.statut === selectedStatus;
        const matchesSearch = searchTerm === "" ||
            (cv.nom && cv.nom.toLowerCase().includes(searchTerm.toLowerCase())) ||
            (cv.prenom && cv.prenom.toLowerCase().includes(searchTerm.toLowerCase()));

        return matchesStatus && matchesSearch;
    });

    return (
        <div className="w-full max-w-7xl mx-auto px-4 flex flex-col items-center">
            <div className="w-full max-w-4xl space-y-4 mb-6">
                <FilterBar
                    selectedStatus={selectedStatus}
                    onStatusChange={setSelectedStatus}
                    selectedDomain="TOUS"
                    onDomainChange={() => {}}
                    availableDomains={[]}
                    statusLabels={{
                        ACCEPTEE: t("status.cvs_acceptes"),
                        REFUSEE: t("status.cvs_refuses"),
                    }}
                />
            </div>

            {error && (
                <div className="w-full max-w-4xl mb-4 rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
                    {t(error)}
                </div>
            )}

            <div className="w-full">
                {cvsFiltres.length === 0 ? (
                    <div className="items-center flex justify-center w-full py-8">
                        <p className="text-gray-500 font-medium">
                            {cvs.length === 0
                                ? t("cv.none_pending")
                                : t("offre.no_matching_offers")}
                        </p>
                    </div>
                ) : (
                    <div className="space-y-4 w-full max-w-4xl mx-auto">
                        {cvsFiltres.map((cv) => (
                            <CvCard
                                key={cv.id}
                                cv={cv}
                                onApprove={handleApprove}
                                onReject={handleReject}
                            />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default GestionnaireCvs;