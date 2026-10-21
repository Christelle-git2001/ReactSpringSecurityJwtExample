import { useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { modifierOffreEmployeur } from "../../api/employeur.jsx";
import OfferForm from "./OfferForm.jsx";
import EmptyContentMessage from "../Design/EmptyContentMessage.jsx";

function EditOffer() {
    const location = useLocation();
    const navigate = useNavigate();
    const { t } = useTranslation();

    const offer = location.state?.offer;

    if (!offer) {
        return (
            <EmptyContentMessage
                message={t("edit_offer.no_offer")}
                buttonLabel={t("edit_offer.back")}
                onButtonClick={() => navigate("/employeur")}
            />
        );
    }

    const isTargeted = Boolean(offer.isTarget);
    const initialValues = {
        ...offer,
        employeurId: offer.employeur?.id || offer.employeurId || "",
        etudiantId: offer.etudiant?.id || offer.etudiantId || "",
    };

    return (
        <OfferForm
            mode="edit"
            titleKey="edit_offer.title"
            submitLabelKey="edit_offer.save"
            initialValues={initialValues}
            currentFileName={offer.fileName}
            requireFile={false}
            targeted={isTargeted}
            onSubmit={(dto, file) =>
                modifierOffreEmployeur(offer.id, dto, file).then(() =>
                    navigate("/employeur")
                )
            }
            onCancel={() => navigate("/employeur")}
        />
    );
}

export default EditOffer;