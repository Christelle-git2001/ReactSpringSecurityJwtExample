import { Link } from "react-router-dom";

const EmployeurHome = () => {
    return (
        <main className="min-h-screen bg-gray-100 px-6 py-8">

            <div className="mx-auto flex max-w-6xl gap-6">

                {/* =========================
                    BARRE LATÉRALE
                ========================== */}
                <aside className="w-56 shrink-0 rounded-xl bg-white p-5 shadow">

                    {/* Nom de l'employeur */}
                    <div className="mb-8 border-b border-gray-200 pb-5">
                        <h2 className="font-bold text-[#043462]">
                            Espace employeur
                        </h2>

                        <p className="mt-1 text-sm text-gray-500">
                            Christelle Altineus
                        </p>
                    </div>

                    {/* Navigation */}
                    <nav className="space-y-2">

                        <Link
                            to="/employeur"
                            className="block rounded-lg bg-[#043462] px-4 py-3 font-medium text-white"
                        >
                            Accueil
                        </Link>

                        <button
                            className="block w-full rounded-lg px-4 py-3 text-left text-gray-700 hover:bg-gray-100"
                        >
                            Mes offres
                        </button>

                        <button
                            className="block w-full rounded-lg px-4 py-3 text-left text-gray-700 hover:bg-gray-100"
                        >
                            Mes entrevues
                        </button>

                        <button
                            className="block w-full rounded-lg px-4 py-3 text-left text-gray-700 hover:bg-gray-100"
                        >
                            Mon compte
                        </button>

                    </nav>

                    {/* Déconnexion */}
                    <div className="mt-8 border-t border-gray-200 pt-5">
                        <Link
                            to="/logout"
                            className="block rounded-lg px-4 py-3 text-gray-600 hover:bg-gray-100"
                        >
                            Déconnexion
                        </Link>
                    </div>

                </aside>


                {/* =========================
                    CONTENU PRINCIPAL
                ========================== */}
                <div className="min-w-0 flex-1">

                    {/* Bienvenue */}
                    <section className="mb-8">
                        <h1 className="text-3xl font-bold text-[#043462]">
                            Bienvenue dans votre espace employeur
                        </h1>

                        <p className="mt-2 text-gray-600">
                            Gérez vos offres de stage et consultez vos candidatures.
                        </p>

                        <div className="mx-auto mt-3 h-px w-full max-w-[600px] bg-[#0FFFDF]"></div>
                    </section>


                    {/* =========================
                        OFFRES + CANDIDATURES
                    ========================== */}
                    <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">

                        {/* Mes offres */}
                        <section className="lg:col-span-2">

                            <div className="mb-4 flex items-center justify-between">
                                <h2 className="text-2xl font-semibold text-[#043462]">
                                    Mes offres
                                </h2>

                                <button className="rounded-md bg-[#0ee1cc] px-4 py-2 font-medium text-white transition-colors hover:bg-[#043462]">
                                    + Ajouter une offre
                                </button>
                            </div>


                            {/* =========================
                                OFFRE 1
                            ========================== */}
                            <div className="mb-4 rounded-xl bg-white p-6 shadow">

                                {/* Titre + statut */}
                                <div className="flex items-start justify-between gap-4">

                                    <div>
                                        <h3 className="text-xl font-semibold text-[#043462]">
                                            Stage développeur Web
                                        </h3>

                                        <p className="mt-1 text-gray-600">
                                            Entreprise ABC
                                        </p>
                                    </div>

                                    <span className="rounded-full bg-yellow-100 px-3 py-1 text-sm text-yellow-700">
                                        En attente
                                    </span>

                                </div>


                                {/* Informations */}
                                <div className="mt-5 grid grid-cols-1 gap-3 sm:grid-cols-2">

                                    <p className="text-gray-600">
                                        📍 Montréal
                                    </p>

                                    <p className="text-gray-600">
                                        💻 Informatique
                                    </p>

                                    <p className="text-gray-600">
                                        💰 22 $ / heure
                                    </p>

                                    <p className="text-gray-600">
                                        📅 1 octobre 2026 → 1 avril 2027
                                    </p>

                                </div>


                                {/* Action */}
                                <div className="mt-5 border-t border-gray-200 pt-4 text-right">
                                    <button className="font-medium text-[#043462] hover:text-[#0ee1cc]">
                                        Voir les détails →
                                    </button>
                                </div>

                            </div>


                            {/* =========================
                                OFFRE 2
                            ========================== */}
                            <div className="rounded-xl bg-white p-6 shadow">

                                <div className="flex items-start justify-between gap-4">

                                    <div>
                                        <h3 className="text-xl font-semibold text-[#043462]">
                                            Stage développeur mobile
                                        </h3>

                                        <p className="mt-1 text-gray-600">
                                            Entreprise XYZ
                                        </p>
                                    </div>

                                    <span className="rounded-full bg-green-100 px-3 py-1 text-sm text-green-700">
                                        Acceptée
                                    </span>

                                </div>

                                <div className="mt-5 grid grid-cols-1 gap-3 sm:grid-cols-2">

                                    <p className="text-gray-600">
                                        📍 Laval
                                    </p>

                                    <p className="text-gray-600">
                                        💻 Informatique
                                    </p>

                                    <p className="text-gray-600">
                                        💰 24 $ / heure
                                    </p>

                                    <p className="text-gray-600">
                                        📅 15 septembre 2026 → 15 mars 2027
                                    </p>

                                </div>

                                <div className="mt-5 border-t border-gray-200 pt-4 text-right">
                                    <button className="font-medium text-[#043462] hover:text-[#0ee1cc]">
                                        Voir les détails →
                                    </button>
                                </div>

                            </div>

                        </section>


                        {/* =========================
                            CANDIDATURES
                        ========================== */}
                        <section className="rounded-xl bg-white p-6 shadow">

                            <h2 className="mb-4 text-2xl font-semibold text-[#043462]">
                                Candidatures
                            </h2>

                            <div className="space-y-3">

                                <div className="rounded-lg border p-4">
                                    <p className="font-medium">
                                        Candidature #1
                                    </p>

                                    <p className="text-sm text-gray-500">
                                        Stage développeur Web
                                    </p>
                                </div>

                                <div className="rounded-lg border p-4">
                                    <p className="font-medium">
                                        Candidature #2
                                    </p>

                                    <p className="text-sm text-gray-500">
                                        Stage développeur mobile
                                    </p>
                                </div>

                            </div>

                        </section>

                    </div>

                </div>

            </div>

        </main>
    );
};

export default EmployeurHome;