import { useTranslation } from "react-i18next";
import CardEtudiant from "../../component/ui/CardEtudiant.jsx";
import {FiUser} from "react-icons/fi";

const EtudiantHome = ({user}) => {
  const { t } = useTranslation();
  return(
    <div className={"pt-5 h-full"}>
      <h1 className={"text-4xl mb-10 text-center"}>{t("home.welcome")} {user?.firstName} {user?.lastName}</h1>
      <div className="grid grid-cols-12">
        <div className="xl:col-span-3 md:col-span-6 sm:col-span-12 ">
            <CardEtudiant
                title={t("nav.account")}
                description={t("etudiant.card.account")}
                Icon={FiUser}
                linkTo="/etudiant/account"
            />
        </div>
      </div>
    </div>
  );
}
export default EtudiantHome;