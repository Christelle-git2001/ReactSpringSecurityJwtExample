import CvCard from "../../component/CV/CvCard.jsx";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { approuverCv, getTousLesCvs, refuserCv } from "../../api/http.jsx";
import FilterBar from "../../component/Design/FilterBar.jsx";

function GestionnaireCvs() {
    const { t } = useTranslation();
    const [selectedStatus, setSelectedStatus] = useState("TOUS");
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

    const handleApprove = async (id, comment) => {
        try {
            await approuverCv(id, comment);
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
        if (selectedStatus === "TOUS") {
            return true;
        }

        return cv.statut === selectedStatus;
    });

    return (
        <div className="w-full max-w-7xl mx-auto px-4">
            <div className="w-full max-w-4xl mx-auto p-4 md:p-8">
                <div className="w-full rounded-xl bg-white p-6 shadow-md md:p-8">
                    <h2 className="mb-6 text-xl font-bold text-[#043462]">
                        {t("cv.pending_title")}
                    </h2>
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
                    {error && (
                        <div className="mb-4 rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
                            {t(error)}
                        </div>
                    )}

                    {cvsFiltres.length === 0 ?  (
                        <p className="text-center font-medium text-gray-500">
                            {t("cv.none_pending")}
                        </p>
                    ) : (
                        <div className="space-y-4">
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
        </div>
    );
}

export default GestionnaireCvs;
