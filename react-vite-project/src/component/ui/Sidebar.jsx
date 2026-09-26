import { Link } from "react-router-dom";

const Sidebar = () => {
    return (
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
    );
};

export default Sidebar;