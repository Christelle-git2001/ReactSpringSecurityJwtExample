import { useTranslation } from "react-i18next";

const OffreCard = ({ offre, onDetails }) => {
    const { t } = useTranslation();

    return (
        <div className="card w-96 bg-base-100 card-md shadow-sm">
            <div className="card-body">
                <p className="text-sm opacity-70">{offre.employeur?.businessName}</p>
                <h2 className="card-title">{offre.title}</h2>
                <p className="line-clamp-3">{offre.description}</p>
                <div className="justify-end card-actions">
                    <button className="btn btn-accent" onClick={() => onDetails(offre)}>
                        {t("etudiant.offers.details_button")}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default OffreCard;