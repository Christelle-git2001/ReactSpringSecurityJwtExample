import {Role} from "../enum/Role.jsx";
import {FiFileText, FiTrendingUp, FiUser, FiUserCheck, FiUsers, FiUserX} from "react-icons/fi";
import {FaUsers, FaUserSlash} from "react-icons/fa";

export const metricConfig = [
    {
        id: "etudiants_gestionnaire",
        title: "inscription.etudiant",
        role: [Role.GESTIONNAIRE],
        tones: "blue",
        cards: [
            { id: "etudiantInscrits", label: "dashboard.gestionnaire.etudiants_inscrits", key: "etudiantInscrits", icon: FiUser },
            { id: "aucunStageTrouve", label: "dashboard.gestionnaire.aucun_stage_trouve", key: "aucunStageTrouve", icon: FiUserX },
            { id: "stageTrouve", label: "dashboard.gestionnaire.stage_trouve", key: "stageTrouve", icon: FiUserCheck },
            { id: "tauxPlacement", label: "dashboard.gestionnaire.taux_placement", key: "tauxPlacement", icon: FiTrendingUp },
            { id: "cvVerification", label: "dashboard.pending_cvs_count", key: "cvVerification", icon: FiFileText },
        ]

    },
    {
        id: "professeur_gestionnaire",
        title: "inscription.professeur",
        role: [Role.GESTIONNAIRE],
        tones: "purple",
        cards: [
            { id: "professeurEleve", label: "dashboard.gestionnaire.professeur_eleves", key: "professeurEleve", icon: FaUsers },
            { id: "professeurSansEleve", label: "dashboard.gestionnaire.professeur_sans_eleves", key: "professeurSansEleve", icon: FaUserSlash },
            { id: "moyenneEleve", label: "dashboard.gestionnaire.professeur_moyenne", key: "moyenneEleve", icon: FiUsers },
        ]

    }
]

export const gestionnaireStatsMock = {
    etudiantInscrits: 120,
    aucunStageTrouve: 30,
    stageTrouve: 90,
    tauxPlacement: "75%",
    cvVerification: 14,
};