import { useTranslation } from "react-i18next";
import CardEtudiant from "../../component/Design/Cards/CardEtudiant.jsx";
import {FiUser,FiBriefcase,FiPlusCircle} from "react-icons/fi";


const EmployeurHome = ({user}) => {
    const { t } = useTranslation();
    return(
        <div className={"pt-5 h-full"}>
            <h1 className={"text-4xl mb-10 text-center"}>{t("home.welcome")} {user?.firstName} {user?.lastName}</h1>
            <div className="grid grid-cols-12">
                <div className="xl:col-span-4 md:col-span-6 col-span-12 ">
                    <CardEtudiant
                        title={t("nav.account")}
                        description={t("etudiant.card.account")}
                        Icon={FiUser}
                        linkTo="/employeur/account"
                    />
                </div>
                <div className="xl:col-span-4 md:col-span-6 col-span-12 ">
                    <CardEtudiant
                        title={t("nav.view_offers")}
                        description={t("employeur.card.view_offers")}
                        Icon={FiBriefcase}
                        linkTo="/employeur/offers"
                    />
                </div>
                <div className="xl:col-span-4 md:col-span-6 col-span-12 ">
                    <CardEtudiant
                        title={t("nav.add_offer")}
                        description={t("employeur.card.new_offer")}
                        Icon={FiPlusCircle}
                        linkTo="/employeur/offres/nouvelle"
                    />
                </div>
            </div>
        </div>
    );
};

export default EmployeurHome;