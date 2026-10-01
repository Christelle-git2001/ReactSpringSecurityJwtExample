import { Link } from "react-router-dom";

const Sidebar = ({ title, name, links }) => {
    return (
        <aside className="w-56 shrink-0 rounded-xl bg-white p-5 shadow">

            <div className="mb-8 border-b border-gray-200 pb-5">
                <h2 className="font-bold text-[#043462]">
                    {title}
                </h2>

                <p className="mt-1 text-sm text-gray-500">
                    {name}
                </p>
            </div>

            <nav className="space-y-2">

                {links.map((link) => (
                    <Link
                        key={link.label}
                        to={link.path}
                        className="block rounded-lg px-4 py-3 text-gray-700 hover:bg-gray-100"
                    >
                        {link.label}
                    </Link>
                ))}

            </nav>

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