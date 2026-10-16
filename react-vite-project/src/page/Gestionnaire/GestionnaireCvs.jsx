import CvCard from "../../component/Design/Cards/CvCard.jsx";
import { useCallback, useMemo, useState } from "react";
import { approuverCv, getTousLesCvs, refuserCv } from "../../api/gestionnaire.jsx";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design//Bars/FilterBar.jsx";
import GestionnaireItemsPage, { useGestionnaireItems } from "../../utils/GestionnaireItemsPage.jsx";

function GestionnaireCvs() {
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedStatus, setSelectedStatus] = useState("TOUS");
    const [selectedDomain, setSelectedDomain] = useState("TOUS");
    const [error, setError] = useState(null);

    const handleLoadError = useCallback((err) => {
        setError(err ? "error.generic" : null);
    }, []);

    const {
        items: cvs,
        reloadItems: chargerCvs,
        targetItemId: targetCvId,
    } = useGestionnaireItems({
        fetchItems: getTousLesCvs,
        targetParamName: "cvId",
        targetElementPrefix: "cv",
        onError: handleLoadError,
    });

    const availableDomains = useMemo(() => {
        return [...new Set(cvs.map((cv) => cv.etudiant?.department).filter(Boolean))].sort();
    }, [cvs]);

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

    const cvsFiltres = useMemo(() => cvs.filter((cv) => {
        if (targetCvId && cv.id === targetCvId) return true;

        const term = searchTerm.toLowerCase();
        const matchesSearch = searchTerm === "" ||
            cv.etudiant?.firstName?.toLowerCase().includes(term) ||
            cv.etudiant?.lastName?.toLowerCase().includes(term) ||
            cv.fileName?.toLowerCase().includes(term);
        const matchesStatus = selectedStatus === "TOUS" || cv.statut === selectedStatus;
        const matchesDepartment = selectedDomain === "TOUS" || cv.etudiant?.department === selectedDomain;

        return matchesSearch && matchesStatus && matchesDepartment;
    }), [cvs, searchTerm, selectedDomain, selectedStatus, targetCvId]);

    return (
        <GestionnaireItemsPage
            controls={
                <>
                    <SearchBar
                        value={searchTerm}
                        onChange={setSearchTerm}
                        onClear={() => setSearchTerm("")}
                    />
                    <FilterBar
                        selectedStatus={selectedStatus}
                        onStatusChange={setSelectedStatus}
                        selectedDomain={selectedDomain}
                        onDomainChange={setSelectedDomain}
                        availableDomains={availableDomains}
                        statusLabels={{
                            TOUS: "Tous",
                            EN_ATTENTE: "En attente",
                            ACCEPTEE: "CV accepte",
                            REFUSEE: "CV refuse",
                        }}
                    />
                </>
            }
            errorMessage={error}
            items={cvs}
            displayedItems={cvsFiltres}
            targetItemId={targetCvId}
            targetElementPrefix="cv"
            emptyItemsMessageKey="cv.none_pending"
            emptyFilteredMessageKey="offre.no_matching_offers"
            listClassName="space-y-4 w-full max-w-4xl mx-auto"
            renderItem={(cv) => (
                <CvCard
                    cv={cv}
                    onApprove={handleApprove}
                    onReject={handleReject}
                />
            )}
        />
    );
}

export default GestionnaireCvs;