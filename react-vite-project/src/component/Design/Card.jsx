function Card({ offer, onView, onEdit }) {

    const statusConfig = {
        EN_ATTENTE: {
            label: "En attente",
            className: "badge-warning"
        },
        ACCEPTEE: {
            label: "Acceptée",
            className: "badge-success"
        },
        REFUSEE: {
            label: "Refusée",
            className: "badge-error"
        }
    };

    const status = statusConfig[offer.status] || statusConfig.EN_ATTENTE;

    return (
        <div className="card w-96 bg-base-100 shadow-sm">
            <div className="card-body">

                {/* Statut */}
                <span className={`badge badge-xs ${status.className}`}>
                    {status.label}
                </span>

                {/* Titre + salaire */}
                <div className="flex justify-between gap-4">
                    <h2 className="text-2xl font-bold">
                        {offer.title}
                    </h2>

                    <span className="whitespace-nowrap text-lg font-semibold">
                        {offer.salary} $ / heure
                    </span>
                </div>

                {/* Informations */}
                <ul className="mt-6 flex flex-col gap-2 text-xs">

                    <li>
                        <span className="me-2">💻</span>
                        <strong>Domaine :</strong> {offer.domain}
                    </li>

                    <li>
                        <span className="me-2">📅</span>
                        <strong>Date de début :</strong> {offer.startDate}
                    </li>

                    <li>
                        <span className="me-2">📅</span>
                        <strong>Date de fin :</strong> {offer.endDate}
                    </li>

                    <li>
                        <span className="me-2">📄</span>
                        <strong>Document :</strong> {offer.fileName}
                    </li>

                </ul>

                {/* Boutons */}
                <div className="mt-6 flex gap-2">

                    <button
                        type="button"
                        onClick={() => onView(offer)}
                        className="btn btn-primary flex-1"
                    >
                        Voir détails
                    </button>

                    <button
                        type="button"
                        onClick={() => onEdit(offer)}
                        className="btn btn-outline flex-1"
                    >
                        Modifier
                    </button>

                </div>

            </div>
        </div>
    );
}

export default Card;