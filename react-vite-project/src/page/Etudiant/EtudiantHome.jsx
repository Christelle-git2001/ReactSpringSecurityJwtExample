import CvOnLandingPage from "../../component/CV/CvOnLandingPage.jsx";
import {useTranslation} from "react-i18next";
import EtudiantOffers from "./EtudiantOffers.jsx";

const EtudiantHome = ({user}) => {
    const { t } = useTranslation();

    return(
      <div className={"h-full px-4 sm:px-6 lg:px-8 py-6"}>
        <CvOnLandingPage />
        <h1 className={"welcome-message"}>{t("home.welcome")} {user?.firstName} {user?.lastName} <span aria-hidden="true">👋</span></h1>
        <div className="w-full">
            <EtudiantOffers user={user} showFilters={false} />
        </div>
    </div>
  );
}
export default EtudiantHome;