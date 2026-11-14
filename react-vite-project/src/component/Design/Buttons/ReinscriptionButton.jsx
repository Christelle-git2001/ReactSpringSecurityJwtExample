import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { FiRefreshCw } from "react-icons/fi";
import Button from "../Button.jsx";
import { getSessionActuelle } from "../../../api/session.jsx";
import { obtenirInscriptionsEtudiant, reinscrireEtudiant } from "../../../api/etudiant.jsx";

const ReinscriptionButton = ({ user, onReinscriptionSuccess }) => {
    const { t } = useTranslation();
    const [estInscritSessionActuelle, setEstInscritSessionActuelle] = useState(true);
    const [loading, setLoading] = useState(false);

    const rawRole = typeof user?.role === "object" ? user?.role?.name : user?.role;
    const role = rawRole?.toString().replace("ROLE_", "");
    const estEtudiant = Boolean(user?.isLoggedIn) && role === "ETUDIANT";

    useEffect(() => {
        let actif = true;

        if (!estEtudiant) {
            setEstInscritSessionActuelle(true);
            return;
        }

        async function verifierInscription() {
            try {
                const [sessionActuelle, inscriptions] = await Promise.all([
                    getSessionActuelle(),
                    obtenirInscriptionsEtudiant()
                ]);

                if (!actif) return;

                const dejaInscrit = (inscriptions || []).some(
                    (i) =>
                        i.saison?.toUpperCase() === sessionActuelle.saison?.toUpperCase() &&
                        Number(i.annee) === Number(sessionActuelle.annee)
                );

                setEstInscritSessionActuelle(dejaInscrit);
            } catch (error) {
                console.error("Erreur lors de la vérification des inscriptions :", error);
                if (actif) setEstInscritSessionActuelle(true);
            }
        }

        verifierInscription();

        return () => {
            actif = false;
        };
    }, [estEtudiant, user?.id]);

    if (!estEtudiant || estInscritSessionActuelle) {
        return null;
    }

    const handleReinscription = async () => {
        setLoading(true);
        try {
            await reinscrireEtudiant();
            setEstInscritSessionActuelle(true);

            window.dispatchEvent(new CustomEvent("userUpdated"));
            if (onReinscriptionSuccess) {
                onReinscriptionSuccess();
            }
            window.location.reload();
        } catch (error) {
            console.error("Échec de la réinscription :", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Button
            type="button"
            variant="custom"
            size="small"
            loading={loading}
            onClick={handleReinscription}
            icon={<FiRefreshCw className="w-3.5 h-3.5 shrink-0" />}
            className="rounded-full bg-[#00CCCB] text-[#043462] font-semibold shadow-sm hover:bg-[#66E5E4] focus-visible:outline-white"
        >
            {t("reinscription.button", { defaultValue: "Se réinscrire" })}
        </Button>
    );
};

export default ReinscriptionButton;