import Card from "./../component/ui/Card.jsx";
import { useLocation } from "react-router-dom";

const EmployeurHome = () => {
    const location = useLocation();
    const offer = location.state?.offer;

    return (
        <main className="min-h-screen bg-gray-100 p-8">

            <h1 className="mb-6 text-3xl font-bold text-[#043462]">
                Page accueil Employeur
            </h1>

            {offer && (
                <Card offer={offer} />
            )}

        </main>
    );
};

export default EmployeurHome;