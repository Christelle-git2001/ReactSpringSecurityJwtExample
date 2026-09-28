import { createContext, useContext, useEffect, useState } from "react";
import fetcher from "../utils/fetcher";
import { useNavigate } from "react-router-dom";

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const token = localStorage.getItem("token");

    useEffect(() => {
        if (!token) return;

        fetcher("/user/me")
            .then(async (res) => {
                if (!res.ok) {
                    localStorage.clear();
                    setUser(null);
                    return;
                }

                const data = await res.json();
                setUser({ ...data, isLoggedIn: true });
            })
            .catch((err) => {
                setError(err);
                navigate("/error");
            });
    }, [token]);

    return (
        <AuthContext.Provider value={{ user, setUser, error, setError }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
