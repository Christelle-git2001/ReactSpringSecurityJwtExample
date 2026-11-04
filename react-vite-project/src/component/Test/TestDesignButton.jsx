import React, { useState } from "react";
import Button from "../Design/Button.jsx";

export default function TestDesignButton() {
    const [loading, setLoading] = useState(false);

    const roles = [
        { role: "approbation", label: "Approuver" },
        { role: "refus", label: "Refuser" },
        { role: "annulation", label: "Annuler" },
        { role: "televerser", label: "Téléverser CV" },
        { role: "details", label: "Voir détails" },
        { role: "postuler", label: "Postuler" },
        { role: "entrevue", label: "Planifier entrevue" },
        { role: "signature", label: "Signer le contrat" },
        { role: "retour", label: "Retour" },
        { role: "soumettre", label: "Soumettre" },
        { role: "neutre", label: "Neutre / Défaut" }
    ];

    const toggleLoading = () => {
        setLoading(true);
        setTimeout(() => setLoading(false), 2000);
    };

    return (
        <div className="p-8 max-w-5xl mx-auto flex flex-col gap-8">
            <div className="flex justify-between items-center border-b pb-4">
                <div>
                    <h1 className="text-2xl font-bold text-[#043462]">Galerie de validation des Boutons</h1>
                    <p className="text-sm text-gray-600">Test des rôles métiers et rétrocompatibilité avec les anciens variants.</p>
                </div>
                <Button role="entrevue" onClick={toggleLoading}>
                    {loading ? "Chargement..." : "Simuler Loading (2s)"}
                </Button>
            </div>

            <section className="flex flex-col gap-4">
                <h2 className="text-lg font-semibold text-gray-800">1. Nouveaux Rôles Métiers (`role="..."`)</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                    {roles.map(({ role, label }) => (
                        <div key={role} className="p-4 border border-gray-200 rounded-lg flex flex-col gap-3 bg-white shadow-sm">
                            <span className="text-xs font-mono text-gray-500 font-bold uppercase">{role}</span>
                            <div className="flex flex-wrap gap-2 items-center">
                                <Button role={role} loading={loading}>
                                    {label}
                                </Button>
                            </div>
                        </div>
                    ))}
                </div>
            </section>

            <section className="flex flex-col gap-4">
                <h2 className="text-lg font-semibold text-gray-800">2. Tailles & États (`size` & `disabled`)</h2>
                <div className="p-4 border border-gray-200 rounded-lg bg-white flex flex-wrap gap-4 items-center">
                    <Button role="approbation" size="small">Petit (small)</Button>
                    <Button role="approbation" size="normal">Normal</Button>
                    <Button role="approbation" disabled>Désactivé</Button>
                    <Button role="approbation" loading>Chargement</Button>
                </div>
            </section>

            <section className="flex flex-col gap-4">
                <h2 className="text-lg font-semibold text-gray-800">3. Rétrocompatibilité Anciens Variants (`variant="..."`)</h2>
                <div className="p-4 border border-gray-200 rounded-lg bg-white flex flex-wrap gap-4 items-center">
                    <Button variant="standard">Standard</Button>
                    <Button variant="success">Success</Button>
                    <Button variant="danger">Danger</Button>
                    <Button variant="secondary">Secondary</Button>
                </div>
            </section>
        </div>
    );
}