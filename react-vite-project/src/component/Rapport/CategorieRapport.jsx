
import React from "react";
import CarteRapport from "./CarteRapport.jsx";

function CategorieRapport({
    categorie,
    compteurs,
    onView,
    onDownload
}) {
    const Icon = categorie.Icon;

    return (
        <section className={`rapport-category ${categorie.couleur} rounded-xl p-4 md:p-5`}>
            <div className="rapport-category-layout">

                <div className="rapport-category-header">
                    <div className={`rounded-xl p-3 shrink-0 ${categorie.iconeCouleur}`}>
                        <Icon className="size-7" />
                    </div>

                    <div className="min-w-0">
                        <h2 className="font-bold text-lg text-slate-800">
                            {categorie.titre}
                        </h2>

                        <p className="text-sm text-slate-500">
                            {categorie.description}
                        </p>
                    </div>
                </div>

                <div className="rapport-category-grid">
                    {categorie.rapports.map((rapport) => (
                        <CarteRapport
                            key={rapport.id}
                            rapport={rapport}
                            nombreResultats={compteurs[rapport.id]}
                            onView={onView}
                            onDownload={onDownload}
                        />
                    ))}
                </div>

            </div>
        </section>
    );
}

export default CategorieRapport;
