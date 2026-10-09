import { useTranslation } from "react-i18next";
import CvOnLandingPage from "../../component/CV/CvOnLandingPage.jsx";

const EtudiantHome = ({}) => {
  const { t } = useTranslation();
  return(
    <div className={"pt-5 h-full"}>
        <CvOnLandingPage />
    </div>
  );
}
export default EtudiantHome;