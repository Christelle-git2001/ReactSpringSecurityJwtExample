import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import fetcher from "../utils/fetcher";

export default function useAuth() {
    const [user, setUser] = useState({});
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const token = localStorage.getItem("token");

    useEffect(() => {
        if (!token) return;

        try {
            fetcher("user/me", {})
                .then(async (res) => {
                    if (!res.ok) {
                        switch (res.status) {
                            case 401:
                                localStorage.clear();
                                setUser(null);
                                return;
                            case 403:
                                throw new Error("Forbidden");
                            case 404:
                                throw new Error("Nothing here 404");
                        }
                    }

                    const data = await res.json();
                    const newUser = { ...data, isLoggedIn: true };
                    setUser(newUser);
                })
                .catch((err) => {
                    setError(err);
                    navigate("/error");
                });
        } catch (err) {
            if (!error) {
                setError(err);
                navigate("/error");
            }
        }
    }, [token]);

    return { user, setUser, error, setError };
}
