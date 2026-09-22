import React from "react";
import '../../css/Header.css';
import { Link } from 'react-router-dom';
import LanguageSwitche from "../../locales/LanguageSwitch.jsx";

function Header({user}) {
    // Function to format role for display (remove ROLE_ prefix and capitalize)
    const formatRole = (roleString) => {
        if (!roleString) return '';
        // Remove ROLE_ prefix if present
        const roleName = roleString.replace('ROLE_', '');
        // Capitalize first letter, lowercase the rest
        return roleName.charAt(0).toUpperCase() + roleName.slice(1).toLowerCase();
    };

    const isGestionnaire = () => {
        console.log(user)
        return user && user.role &&
            (user.role.toString() === 'GESTIONNAIRE');
    }
    const isPrepose = () => {
        console.log(user)
        return user && user.role &&
            (user.role.toString() === 'GESTIONNAIRE' || user.role.toString() === 'PREPOSE');
    }
    const isEmprunteur = () => {
        console.log(user)
        return user && user.role &&
            (user.role.toString() === 'GESTIONNAIRE' || user.role.toString() === 'EMPRUNTEUR');
    }

    return (
        <header className="header">
            <h1>My App</h1>
            <nav>
                <ul className="nav-links">
                    <li><Link to="/">Accueil</Link></li>
                    <li><Link to="/about">À propos</Link></li>
                    {isEmprunteur() && <li><Link to="/emprunteur">Emprunteur</Link></li>}
                    {isPrepose() && <li><Link to="/prepose">Prepose</Link></li>}
                    {isGestionnaire() && <li><Link to="/gestionnaire">Gestionnaire</Link></li>}
                    {!user?.isLoggedIn && <li><Link to="/inscription">Inscription</Link></li>}
                    <li>{user?.isLoggedIn ? <Link to="/logout">Logout</Link> : <Link to="/login">Login</Link>}</li>
                </ul>
                {user?.isLoggedIn && (
                    <div className="user-info">
                        <p className="para-align">
                            Bonjour <span className="user-name">{user.firstName} {user.lastName}</span>
                            {user.role && (
                                <span className="user-role"> - {formatRole(user.role.toString())}</span>
                            )}
                        </p>
                    </div>
                )}
            </nav>
            <LanguageSwitche />
        </header>
    );
}

export default Header;
