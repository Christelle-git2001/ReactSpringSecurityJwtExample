import { useState, useMemo } from "react";
import { getDepartmentKey } from "./departementConverter.js";

export const useOffreFilters = (offres = [], cvs = []) => {
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedStatus, setSelectedStatus] = useState("TOUS");
    const [selectedDomain, setSelectedDomain] = useState("TOUS");

    const availableDomains = useMemo(() => {
        const offerDomains = offres.map((o) => o.domain).filter(Boolean);
        const cvDepartments = cvs.map((c) => c.etudiant?.department).filter(Boolean);

        const allRawDomains = [...offerDomains, ...cvDepartments];

        const uniqueDomains = Array.from(
            new Set(
                allRawDomains.map((d) => {
                    const key = getDepartmentKey(d);
                    return key ? key : d.trim();
                })
            )
        );

        return uniqueDomains;
    }, [offres, cvs]);

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

            if (selectedDomain !== "TOUS") {
                const offerKey = getDepartmentKey(offer.domain) || offer.domain;
                if (offerKey !== selectedDomain) return false;
            }

            if (term) {
                const titleMatch = offer.title?.toLowerCase().includes(term);
                const descMatch = offer.description?.toLowerCase().includes(term);
                return titleMatch || descMatch;
            }
            return true;
        });
    }, [offres, searchTerm, selectedStatus, selectedDomain]);

    const cvsFiltres = useMemo(() => {
        const term = searchTerm.trim().toLowerCase();

        return cvs.filter((cv) => {
            if (selectedDomain !== "TOUS") {
                const cvKey = getDepartmentKey(cv.etudiant?.department) || cv.etudiant?.department;
                if (cvKey !== selectedDomain) return false;
            }

            if (term) {
                const studentName = `${cv.etudiant?.firstName || ""} ${cv.etudiant?.lastName || ""}`.toLowerCase();
                const fileName = (cv.fileName || "").toLowerCase();
                return studentName.includes(term) || fileName.includes(term);
            }
            return true;
        });
    }, [cvs, searchTerm, selectedDomain]);

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
        cvsFiltres,
        resetFilters
    };
};