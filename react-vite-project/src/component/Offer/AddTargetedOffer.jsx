import { useNavigate } from "react-router-dom";
import { creerOffreCibleeGestionnaire } from "../../api/gestionnaire.jsx";
import OfferForm from "./OfferForm.jsx";

function AddTargetedOffer() {
    const navigate = useNavigate();

    return (
        <OfferForm
            mode="add"
            targeted={true}
            titleKey="add_offer.targeted_offer"
            submitLabelKey="add_offer.submit"
            requireFile={true}
            onSubmit={(dto, file) =>
                creerOffreCibleeGestionnaire(dto, file).then(() => navigate("/gestionnaire"))
            }
            onCancel={() => navigate("/gestionnaire")}
        />
    );
}

export default AddTargetedOffer;