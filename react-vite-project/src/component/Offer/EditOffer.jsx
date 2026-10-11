import { useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Button from "../Design/Button.jsx";
import { modifierOffreEmployeur } from "../../api/employeur.jsx";
import OfferForm from "./OfferForm.jsx";

function EditOffer() {
    const location = useLocation();
    const navigate = useNavigate();
    const { t } = useTranslation();

    const offer = location.state?.offer;

    if (!offer) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="mx-auto max-w-2xl rounded-xl bg-white p-6 shadow-md md:p-8">
                    <p className="mb-4 text-sm text-gray-700">
                        {t("edit_offer.no_offer")}
                    </p>

                    <Button
                        type="button"
                        onClick={() => navigate("/employeur")}
                        className="bg-[#043462] text-white hover:bg-[#03284d]"
                    >
                        {t("edit_offer.back")}
                    </Button>
                </div>
            </div>
        );
    }

    return (
        <OfferForm
            mode="edit"
            titleKey="edit_offer.title"
            submitLabelKey="edit_offer.save"
            initialValues={offer}
            currentFileName={offer.fileName}
            requireFile={false}
            onSubmit={(dto, file) =>
                modifierOffreEmployeur(offer.id, dto, file).then(() =>
                    navigate("/employeur/offers")
                )
            }
            onCancel={() => navigate("/employeur")}
        />
    );
}

export default EditOffer;