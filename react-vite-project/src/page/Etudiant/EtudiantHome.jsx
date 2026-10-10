import CvOnLandingPage from "../../component/CV/CvOnLandingPage.jsx";
import {useTranslation} from "react-i18next";
import ViewOffers from "./ViewOffers.jsx";

const EtudiantHome = ({user}) => {
    const { t } = useTranslation();

    return(
      <div className={"h-full px-4 sm:px-6 lg:px-8 py-6"}>
        <CvOnLandingPage />
        <h1 className={"text-4xl mb-10 text-center"}>{t("home.welcome")} {user?.firstName} {user?.lastName}</h1>
        <div className="w-full">
            <ViewOffers user={user} showFilters={false} />
        </div>
    </div>
  );
}
export default EtudiantHome;