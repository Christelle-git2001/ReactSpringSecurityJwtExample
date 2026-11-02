import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { obtenirToutesConvocations } from "../../api/etudiant.jsx";

function ListeConvocations() {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    const [convocations, setConvocations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const visibles = convocations.filter((c) => c.statut !== "REFUSEE");

    useEffect(() => {
        obtenirToutesConvocations()
            .then((data) => setConvocations(data ?? []))
            .catch(() => setError(true))
            .finally(() => setLoading(false));
    }, []);

    if (loading) {
        return <div className="p-8 text-center"><span className="loading loading-spinner" /></div>;
    }

    if (error) {
        return <p role="alert" className="p-8 text-center text-red-700">{t("error.generic")}</p>;
    }

    return (
        <div className="w-full max-w-3xl mx-auto p-4 md:p-8">
            <h1 className="text-2xl font-bold text-[#043462] mb-4">
                {t("convocation.list_title")}
            </h1>

            {visibles.length === 0 ? (
                <p className="text-center text-gray-500 py-8">
                    {t("convocation.none")}
                </p>
            ) : (
                <ul className="flex flex-col gap-3">
                    {visibles.map((c) => (
                        <li key={c.id}>
                            <button
                                type="button"
                                onClick={() => navigate("/etudiant/entrevues/details", {
                                    state: { id: c.id, from: "/etudiant/entrevues" },
                                })}
                                className="w-full text-left flex items-center justify-between gap-4 rounded-xl bg-white p-4 shadow-sm hover:bg-gray-50"
                            >
                                <div className="min-w-0">
                                    <span className="font-semibold text-[#043462] truncate">{c.offreTitle}</span>
                                    <span>{t(" avec ")}</span>
                                    <span className="font-bold">{c.employeur?.businessName}</span>
                                    <p className="text-sm text-gray-500">
                                        {c.dateHeureEntrevue
                                            ? new Date(c.dateHeureEntrevue).toLocaleString(i18n.language, {
                                                dateStyle: "medium",
                                                timeStyle: "short",
                                            })
                                            : "—"
                                        }
                                    </p>
                                </div>
                                {c.statut === "CONFIRMEE" && (
                                    <span className="badge badge-success text-white">
                                        {t("convocation.scheduled")}
                                    </span>
                                    )
                                }
                            </button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default ListeConvocations;