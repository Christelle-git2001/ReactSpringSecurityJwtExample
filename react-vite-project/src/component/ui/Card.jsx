function Card({ offer }) {
    return (
        <div className="card w-96 bg-base-100 shadow-sm">
            <div className="card-body">

                {/* Statut */}
                <span className="badge badge-xs badge-warning">
                    En attente
                </span>

                {/* Titre + salaire */}
               <div className="flex justify-between">
                   <h2 className="text-3xl font-bold">
                       {offer.title}
                   </h2>

                   <span className="text-xl font-semibold">
                       {offer.salary} $ / heure
                   </span>
               </div>

                {/* Informations de l'offre */}
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

                {/* Bouton */}
                <button
                    className="btn btn-primary btn-block mt-4"
                >
                    Voir l'offre
                </button>

            </div>
        </div>
    );
}

export default Card;