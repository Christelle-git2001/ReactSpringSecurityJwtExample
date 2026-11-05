import { useTranslation } from "react-i18next";
import ProfileField from "../../component/Design/Profile/ProfileField.jsx";
import {getDepartmentKey} from "../../utils/departementConverter.js";
import CurriculumVitaeZone from "../../component/CV/CurriculumVitaeZone.jsx";
import {useEffect} from "react";
import ProfileSection from "../../component/ProfileSection.jsx";
import ProfileRow1x2 from "../../component/Design/Profile/ProfileRow1x2.jsx";
import DividerHorizontal from "../../component/Design/Profile/DividerHorizontal.jsx";

const AccountEtudiant = ({user}) => {
    const { t } = useTranslation();
    useEffect(() => {
        if (location.hash) {
            const elementId = location.hash.replace('#', '');
            const element = document.getElementById(elementId);

            if (element) {
                setTimeout(() => {
                    element.scrollIntoView({ behavior: 'smooth' });
                }, 100);
            }
        }
    }, [location]);
    return (
        <div className="p-10 w-full">
            <div className="max-w-4xl mx-auto p-6 rounded-2xl shadow-sm border border-base-200 text-center">

                <ProfileSection title={t("account.info.personal")}>
                    <ProfileRow1x2
                        left={<ProfileField label={t("register.first_name")} result={user?.firstName}/>}
                        right={<ProfileField label={t("register.last_name")} result={user?.lastName}/>}
                    />
                    <DividerHorizontal/>
                    <div className="col-span-12">
                        <ProfileField label={t("register.email")} result={user?.email}/>
                    </div>
                    <DividerHorizontal/>
                    <div className="col-span-12">
                        <ProfileField label={t("register.phone")} result={user?.phoneNumber}/>
                    </div>
                </ProfileSection>

                <ProfileSection title={t("account.info.educational")}>
                    <ProfileRow1x2
                        left={<ProfileField label={t("add_etudiant.matricule")} result={user?.matricule}/>}
                        right={<ProfileField label={t("add_etudiant.department")} result={t("departement." + getDepartmentKey(user?.department))}/>}
                    />
                </ProfileSection>

                <ProfileSection title={t("account.info.professional")} id="televerser">
                    <div className="col-span-12">
                        <ProfileField label={"Curriculum Vitae"} result={<CurriculumVitaeZone/>}/>
                    </div>
                </ProfileSection>
            </div>
        </div>
    );
};
export default AccountEtudiant