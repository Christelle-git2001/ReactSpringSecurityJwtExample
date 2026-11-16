import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Logout = ({ setUser }) => {
  const navigate = useNavigate();

  useEffect(() => {
    localStorage.removeItem("token");
    localStorage.removeItem("sessionCible");
    setUser(null);
    window.dispatchEvent(new CustomEvent("sessionChanged", { detail: "" }));
    navigate('/');
  }, [setUser, navigate]);

  return null;
};

export default Logout;