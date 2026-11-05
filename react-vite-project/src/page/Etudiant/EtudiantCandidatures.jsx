import EtudiantHome from "./EtudiantHome.jsx";

// Preserve existing links and notification state while showing the shared dashboard.
export default function EtudiantCandidatures({ user }) {
    return <EtudiantHome user={user} />;
}
