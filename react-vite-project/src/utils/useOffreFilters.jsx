import { useState, useMemo } from "react";

export const useOffreFilters = (offres = []) => {
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedStatus, setSelectedStatus] = useState("TOUS");
    const [selectedDomain, setSelectedDomain] = useState("TOUS");

    const availableDomains = useMemo(() => {
        const domains = offres.map((o) => o.domain).filter(Boolean);
        return Array.from(new Set(domains));
    }, [offres]);

    const hasActiveFilters = useMemo(() => {
        return (
            searchTerm.trim() !== "" ||
            selectedStatus !== "TOUS" ||
            selectedDomain !== "TOUS"
        );
    }, [searchTerm, selectedStatus, selectedDomain]);

    const offresFiltrees = useMemo(() => {
        const term = searchTerm.trim().toLowerCase();

        return offres.filter((offer) => {
            if (selectedStatus !== "TOUS" && offer.statut !== selectedStatus) {
                return false;
            }
            if (selectedDomain !== "TOUS" && offer.domain !== selectedDomain) {
                return false;
            }
            if (term) {
                const titleMatch = offer.title?.toLowerCase().includes(term);
                const descMatch = offer.description?.toLowerCase().includes(term);
                return titleMatch || descMatch;
            }
            return true;
        });
    }, [offres, searchTerm, selectedStatus, selectedDomain]);

    const resetFilters = () => {
        setSearchTerm("");
        setSelectedStatus("TOUS");
        setSelectedDomain("TOUS");
    };

    return {
        searchTerm,
        setSearchTerm,
        selectedStatus,
        setSelectedStatus,
        selectedDomain,
        setSelectedDomain,
        availableDomains,
        hasActiveFilters,
        offresFiltrees,
        resetFilters
    };
};