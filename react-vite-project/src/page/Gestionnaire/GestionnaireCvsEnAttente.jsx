import CvCard from "../../component/Design/Cards/CvCard.jsx";
import { useCallback, useMemo, useState } from "react";
import { approuverCv, getCvsEnAttente, refuserCv } from "../../api/gestionnaire.jsx";
import SearchBar from "../../component/Design/SearchBar.jsx";
import GestionnaireItemsPage, { useGestionnaireItems } from "../../utils/GestionnaireItemsPage.jsx";
import { isNewSince } from "../../utils/isNewItem.jsx";

function GestionnaireCvsEnAttente({ user }) {
    const [searchTerm, setSearchTerm] = useState("");
    const [error, setError] = useState(null);

    const handleLoadError = useCallback((err) => {
        setError(err ? "error.generic" : null);
    }, []);

    const {
        items: cvs,
        reloadItems: chargerCvs,
        targetItemId: targetCvId,
    } = useGestionnaireItems({
        fetchItems: getCvsEnAttente,
        targetParamName: "cvId",
        targetElementPrefix: "cv",
        onError: handleLoadError,
    });

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

        return searchTerm === "" ||
            cv.etudiant?.firstName?.toLowerCase().includes(term) ||
            cv.etudiant?.lastName?.toLowerCase().includes(term) ||
            cv.fileName?.toLowerCase().includes(term);
    }), [cvs, searchTerm, targetCvId]);

    return (
        <GestionnaireItemsPage
            controls={
                <SearchBar
                    value={searchTerm}
                    onChange={setSearchTerm}
                    onClear={() => setSearchTerm("")}
                />
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
                    isNew={isNewSince(cv.uploadDate, user?.previousLoginAt)}
                />
            )}
        />
    );
}

export default GestionnaireCvsEnAttente;
