import {Role} from "../enum/Role.jsx";
import {FiFileText, FiTrendingUp, FiUser, FiUserCheck, FiUsers, FiUserX} from "react-icons/fi";
import {FaBriefcase, FaFileContract, FaPercent, FaRegFilePdf, FaUsers, FaUsersSlash} from "react-icons/fa";
import {IoIdCard} from "react-icons/io5";
import {MdFactCheck} from "react-icons/md";
import {TbContract} from "react-icons/tb";
import {FaFileCircleCheck, FaFileCircleQuestion} from "react-icons/fa6";

export const metricConfig = [
    {
        id: "etudiants_gestionnaire",
        dataKey : "etudiants",
        title: "inscription.etudiant",
        role: [Role.GESTIONNAIRE],
        tone: "blue",
        cards: [
            { id: "cvVerification", label: "dashboard.pending_cvs_count", key: "cvVerification", icon: FiFileText },
            { id: "etudiantInscrits", label: "dashboard.gestionnaire.etudiants_inscrits", key: "etudiantInscrits", icon: FiUser },
            { id: "aucunStageTrouve", label: "dashboard.gestionnaire.aucun_stage_trouve", key: "aucunStageTrouve", icon: FiUserX },
            { id: "stageTrouve", label: "dashboard.gestionnaire.stage_trouve", key: "stageTrouve", icon: FiUserCheck },
            { id: "tauxPlacement", label: "dashboard.gestionnaire.taux_placement", key: "tauxPlacement", icon: FiTrendingUp },
        ]

    },
    {
        id: "professeur_gestionnaire",
        dataKey : "professeurs",
        title: "inscription.professeur",
        role: [Role.GESTIONNAIRE],
        tone: "purple",
        cards: [
            { id: "professeurEleve", label: "dashboard.gestionnaire.professeur_eleves", key: "professeurEleve", icon: FaUsers },
            { id: "professeurSansEleve", label: "dashboard.gestionnaire.professeur_sans_eleves", key: "professeurSansEleve", icon: FaUsersSlash },
            { id: "moyenneEleve", label: "dashboard.gestionnaire.professeur_moyenne", key: "moyenneEleve", icon: FiUsers },
        ]

    },
    {
        id: "employeur_gestionnaire",
        dataKey: "employeurs",
        title: "inscription.employeur",
        role: [Role.GESTIONNAIRE],
        tone: "green_background",
        cards: [
            { id: "offreVerification", label: "dashboard.gestionnaire.offreVerification", key: "offreVerification", icon: FaBriefcase },
            { id: "employeursInscrits", label: "dashboard.gestionnaire.employeur_inscrit", key: "employeursInscrits", icon: IoIdCard },
            { id: "offresActives", label: "dashboard.gestionnaire.offre_active", key: "offresActives", icon: MdFactCheck  },
            { id: "offreSansStagiaire", label: "dashboard.gestionnaire.offre_sans_stagiaire", key: "offreSansStagiaire", icon: FaUsersSlash },
            { id: "tauxOffres", label: "dashboard.gestionnaire.taux_offres", key: "tauxOffres", icon: FaPercent  },
        ]
    },
    {
        id: "contrat_gestionnaire",
        dataKey: "contrats",
        title: "dashboard.contracts_title",
        role: [Role.GESTIONNAIRE],
        tone: "cyan_background",
        cards: [
            { id: "nombreContratASigner", label: "dashboard.gestionnaire.nombre_contrat_a_signer", key: "nombreContratASigner", icon: TbContract  },
            { id: "contratSigne", label: "dashboard.gestionnaire.contrat_signe", key: "contratSigne", icon: FaFileContract  },
            { id: "nombreContratGenerer", label: "dashboard.gestionnaire.nombre_contrat_generer", key: "nombreContratGenerer", icon: FaRegFilePdf   },
            { id: "contratBloque", label: "dashboard.gestionnaire.contrat_bloque", key: "contratBloque", icon: FaFileCircleQuestion  },
            { id: "tauxFinalisation", label: "dashboard.gestionnaire.taux_finalisation", key: "tauxFinalisation", icon: FaFileCircleCheck   },
        ]
    }
]