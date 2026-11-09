import { FiLock } from "react-icons/fi";
import { useTranslation } from "react-i18next";

export default function ActionsDesactivees() {
    const { t } = useTranslation();
    return (
        <div role="note" className="flex items-center justify-center gap-3 rounded-lg border border-slate-300 bg-slate-100 px-4 py-3 text-center text-[#043462]">
            <FiLock className="size-5 shrink-0" aria-hidden="true" />
            <div>
                <p className="text-sm font-bold">{t("candidature.actions_disabled")}</p>
                <p className="mt-1 text-xs">{t("candidature.actions_disabled_description")}</p>
            </div>
        </div>
    );
}