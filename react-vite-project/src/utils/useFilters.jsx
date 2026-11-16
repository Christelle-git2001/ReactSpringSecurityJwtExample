import { useState, useMemo } from "react";
import { getDepartmentKey } from "./departementConverter.js";

/**
 * Hook de filtrage générique
 * @param {Array} items Liste d'éléments à filtrer (offres, candidatures, cvs, etc.)
 * @param {Object} config Configuration d'extraction des données
 */
export const useFilters = (items = [], config = {}, controls = {}) => {
    const {
        getDomain = (item) => item?.domain || item?.etudiantDepartment || item?.etudiant?.department,
        getTown = (item) => item?.town || item?.ville || item?.location,
        getStatus = (item) => item?.statut || item?.status,
        getSearchableText = (item) => {
            if (!item) return "";
            const offerTitle = item.title || item.offreTitle || "";
            const description = item.description || "";
            const studentFirstName = item.etudiantFirstName || item.etudiant?.firstName || "";
            const studentLastName = item.etudiantLastName || item.etudiant?.lastName || "";

            return `${offerTitle} ${description} ${studentFirstName} ${studentLastName}`;
        },
    } = config;

    const [localSearchTerm, setLocalSearchTerm] = useState("");
    const searchTerm = controls.searchTerm ?? localSearchTerm;
    const setSearchTerm = controls.setSearchTerm ?? setLocalSearchTerm;
    const [localSelectedStatus, setLocalSelectedStatus] = useState("TOUS");
    const selectedStatus = controls.selectedStatus ?? localSelectedStatus;
    const setSelectedStatus = controls.setSelectedStatus ?? setLocalSelectedStatus;
    const [selectedDomain, setSelectedDomain] = useState("TOUS");
    const [localSelectedTown, setLocalSelectedTown] = useState("TOUS");
    const selectedTown = controls.selectedTown ?? localSelectedTown;
    const setSelectedTown = controls.setSelectedTown ?? setLocalSelectedTown;

    const availableDomains = useMemo(() => {
        const rawDomains = items.map(getDomain).filter(Boolean);
        return Array.from(
            new Set(
                rawDomains.map((d) => {
                    const key = getDepartmentKey(d);
                    return key ? key : String(d).trim();
                })
            )
        );
    }, [items, getDomain]);

    const availableTowns = useMemo(() => {
        const rawTowns = items.map(getTown).filter(Boolean);
        return Array.from(new Set(rawTowns.map((t) => String(t).trim())));
    }, [items, getTown]);

    const hasActiveFilters = useMemo(() => {
        return (
            searchTerm.trim() !== "" ||
            selectedStatus !== "TOUS" ||
            selectedDomain !== "TOUS" ||
            selectedTown !== "TOUS"
        );
    }, [searchTerm, selectedStatus, selectedDomain, selectedTown]);

    const itemsFiltres = useMemo(() => {
        const term = searchTerm.trim().toLowerCase();

        return items.filter((item) => {
            if (selectedStatus !== "TOUS") {
                const status = getStatus(item);
                if (status && status !== selectedStatus) return false;
            }

            if (selectedDomain !== "TOUS") {
                const domain = getDomain(item);
                const domainKey = getDepartmentKey(domain) || domain;
                if (domainKey !== selectedDomain) return false;
            }

            if (selectedTown !== "TOUS") {
                const town = getTown(item);
                if (town !== selectedTown) return false;
            }

            if (term) {
                const searchableText = getSearchableText(item)?.toLowerCase() || "";
                if (!searchableText.includes(term)) return false;
            }

            return true;
        });
    }, [items, searchTerm, selectedStatus, selectedDomain, selectedTown, getDomain, getStatus, getTown, getSearchableText]);

    const resetFilters = () => {
        setSearchTerm("");
        setSelectedStatus("TOUS");
        setSelectedDomain("TOUS");
        setSelectedTown("TOUS");
    };

    return {
        searchTerm,
        setSearchTerm,
        selectedStatus,
        setSelectedStatus,
        selectedDomain,
        setSelectedDomain,
        selectedTown,
        setSelectedTown,
        availableDomains,
        availableTowns,
        hasActiveFilters,
        itemsFiltres,
        resetFilters,
    };
};