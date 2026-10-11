import { useNavigate } from "react-router-dom";
import { creerOffreEmployeur } from "../../api/employeur.jsx";
import OfferForm from "./OfferForm.jsx";

function AddOffer() {
    const navigate = useNavigate();

    return (
        <OfferForm
            mode="add"
            titleKey="add_offer.title"
            submitLabelKey="add_offer.submit"
            requireFile={true}
            onSubmit={(dto, file) =>
                creerOffreEmployeur(dto, file).then(() => navigate("/employeur/offers"))
            }
            onCancel={() => navigate("/employeur")}
        />
    );
}

export default AddOffer;