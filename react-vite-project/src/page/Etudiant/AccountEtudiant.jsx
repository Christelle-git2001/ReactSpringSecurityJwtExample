import { useTranslation } from "react-i18next";
import ProfileField from "../../component/ui/ProfileField.jsx";
import {getDepartmentKey} from "../../utils/departementConverter.js";
const AccountEtudiant = ({user}) => {
    const { t } = useTranslation();
    return (
       <div className={"bg-teal-200/30 p-10"}>
           <div className="max-w-4xl mx-auto p-6 bg-base-100 rounded-2xl shadow-sm border border-base-200">
               <h2 className="text-2xl font-bold text-[#043462] border-b pb-4 mb-6">
                   Renseignements personnels
               </h2>
               <div className="grid grid-cols-12">
                   <div className={"col-span-5"}>
                        <ProfileField label={t("register.first_name")} result={user?.firstName}/>
                   </div>
                   <div className="col-span-2 flex justify-center">
                       <div className="divider divider-horizontal m-0"></div>
                   </div>
                   <div className={"col-span-5"}>
                        <ProfileField label={t("register.last_name")} result={user?.lastName}/>
                   </div>
                   <div className="col-span-12">
                       <div className="divider"></div>
                   </div>
                   <div className={"col-span-12"}>
                       <ProfileField label={t("register.email")} result={user?.email}/>
                   </div>
                   <div className="col-span-12">
                       <div className="divider"></div>
                   </div>
                   <div className={"col-span-12"}>
                       <ProfileField label={t("register.phone")} result={user?.phoneNumber}/>
                   </div>
               </div>
               <h2 className="text-2xl font-bold text-[#043462] border-b p-4 mb-6">
                   Renseignements Scolaire
               </h2>
               <div className="grid grid-cols-12">
                   <div className={"col-span-5"}>
                       <ProfileField label={t("add_etudiant.matricule")} result={user?.matricule}/>
                   </div>
                   <div className="col-span-2 flex justify-center">
                       <div className="divider divider-horizontal m-0"></div>
                   </div>
                   <div className={"col-span-5"}>
                       <ProfileField label={t("add_etudiant.department")} result={t("departement." + getDepartmentKey(user.department))}/>
                   </div>
               </div>
           </div>
        </div>
   )
};export default AccountEtudiant