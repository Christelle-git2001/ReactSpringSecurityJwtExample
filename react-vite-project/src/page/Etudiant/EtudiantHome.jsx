import { useTranslation } from "react-i18next";
import CardEtudiant from "../../component/Design/Cards/CardEtudiant.jsx";
import {FiUser,FiBriefcase} from "react-icons/fi";
import CvOnLandingPage from "../../component/CV/CvOnLandingPage.jsx";

const EtudiantHome = ({user}) => {
  const { t } = useTranslation();
  return(
    <div className={"pt-5 h-full"}>
        <CvOnLandingPage />
    </div>
  );
}
export default EtudiantHome;