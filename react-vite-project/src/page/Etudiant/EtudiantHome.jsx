import { useTranslation } from "react-i18next";
import CardEtudiant from "../../component/ui/CardEtudiant.jsx";
import {FiUser} from "react-icons/fi";

const EtudiantHome = ({user}) => {
  const { t } = useTranslation();
  return(
    <div className={"min-h-dvh"}>
      <h1 className={"text-4xl mb-10"}>{t("home.welcome")} {user?.firstName} {user?.lastName}</h1>
      <div className="grid grid-cols-12">
        <div className="xl:col-span-3 md:col-span-6 sm:col-span-12 ">
            <CardEtudiant
                title={t("nav.account")}
                description={t("etudiant.card.account")}
                Icon={FiUser}
                linkTo="/etudiant/account"
            />
        </div>

        <div className="xl:col-span-3 md:col-span-6 sm:col-span-12 ">
          Contenu 2/4
        </div>
        <div className="xl:col-span-3 md:col-span-6 sm:col-span-12 ">
        Contenu 3/4
      </div>

        <div className="xl:col-span-3 md:col-span-6 sm:col-span-12" >
          Contenu 4/4
        </div>

      </div>
    </div>
  );
}
export default EtudiantHome;