import CvOnLandingPage from "../../component/CV/CvOnLandingPage.jsx";
import {FiBriefcase, FiUser} from "react-icons/fi";
import CardEtudiant from "../../component/Design/Cards/CardEtudiant.jsx";
import {useTranslation} from "react-i18next";

const EtudiantHome = ({user}) => {
    const { t } = useTranslation();
  return(
    <div className={"pt-5 h-full"}>
        <CvOnLandingPage />
        <h1 className={"text-4xl mb-10 text-center"}>{t("home.welcome")} {user?.firstName} {user?.lastName}</h1>
        <div className="grid grid-cols-12">
            <div className="xl:col-span-4 md:col-span-6 col-span-12 ">
                <CardEtudiant
                    title={t("nav.account")}
                    description={t("etudiant.card.account")}
                    Icon={FiUser}
                    linkTo="/etudiant/account"
                />
            </div>
            <div className="xl:col-span-4 md:col-span-6 col-span-12 ">
                <CardEtudiant
                    title={t("nav.view_offers")}
                    description={t("etudiant.card.view_offers")}
                    Icon={FiBriefcase}
                    linkTo="/etudiant/offers"
                />
            </div>
        </div>
    </div>
  );
}
export default EtudiantHome;