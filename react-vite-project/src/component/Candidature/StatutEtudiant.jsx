import { useRef } from "react";
import { useTranslation } from "react-i18next";
import { FiBriefcase, FiCheck, FiChevronDown, FiClipboard, FiClock, FiSearch } from "react-icons/fi";

const STATUTS = [
    { key: "EN_RECHERCHE", icon: FiSearch, color: "text-sky-600" },
    { key: "EN_ATTENTE_REPONSE", icon: FiClock, color: "text-amber-600" },
    { key: "STAGE_TROUVE", icon: FiBriefcase, color: "text-green-700" },
];

export default function StatutEtudiant({ user, candidatures = [], loading = false, value, onChange }) {
    const { t } = useTranslation();
    const dropdownRef = useRef(null);
    const statut = value ?? candidatures[0]?.statutEtudiant ?? user?.statut ?? "EN_RECHERCHE";
    const actuel = STATUTS.find(option => option.key === statut) ?? STATUTS[0];
    const Icone = actuel.icon;

    return (
        <section aria-label={t("student_status.title")} className="rounded-2xl border border-violet-200/80 bg-gradient-to-r from-violet-100/80 via-white to-indigo-50 px-4 py-3 text-[#043462] shadow-sm">
            <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div className="flex items-center gap-3">
                    <div className="flex size-10 items-center justify-center rounded-xl bg-violet-200/80 text-violet-700 shadow-inner">
                        <FiClipboard className="size-5 shrink-0" aria-hidden="true" />
                    </div>
                    <div>
                        <h2 className="font-bold">{t("student_status.title")}</h2>
                        <p className="text-xs text-gray-600">{t(`student_status.${actuel.key}.description`)}</p>
                    </div>
                </div>
                {!onChange ? (
                    <span role="status" className="inline-flex items-center gap-2 self-start rounded-full border border-violet-200 bg-white/80 px-3 py-2 text-sm font-semibold shadow-sm">
                        <Icone className={`size-5 ${actuel.color}`} aria-hidden="true" />
                        {loading ? t("student_status.loading") : t(`student_status.${actuel.key}.label`)}
                    </span>
                ) : (
                <details ref={dropdownRef} className="dropdown dropdown-end w-full sm:w-80">
                    <summary aria-label={t("student_status.title")} className="flex cursor-pointer list-none items-center gap-3 rounded-xl border border-violet-200 bg-white/90 px-3 py-2 text-sm font-semibold shadow-sm [&::-webkit-details-marker]:hidden">
                        <Icone className={`size-5 ${actuel.color}`} aria-hidden="true" />
                        {loading ? t("student_status.loading") : t(`student_status.${actuel.key}.label`)}
                        <FiChevronDown className="ml-auto" aria-hidden="true" />
                    </summary>
                    <ul className="dropdown-content z-20 mt-2 w-full rounded-xl border border-gray-200 bg-white p-1 shadow-lg">
                        {STATUTS.map(option => {
                            const OptionIcon = option.icon;
                            const selectionne = statut === option.key;
                            return (
                                <li key={option.key}>
                                    <button type="button" disabled={loading || option.key !== "STAGE_TROUVE"}
                                            onClick={() => { onChange(option.key); dropdownRef.current?.removeAttribute("open"); }}
                                            className={`flex w-full items-center gap-3 rounded-lg px-3 py-2 text-left disabled:cursor-default ${selectionne ? "bg-blue-50" : "enabled:hover:bg-blue-50"}`}>
                                        <OptionIcon className={`size-5 shrink-0 ${option.color}`} aria-hidden="true" />
                                        <span className="min-w-0">
                                            <span className="block text-sm font-semibold">{t(`student_status.${option.key}.label`)}</span>
                                            <span className="block text-xs text-gray-600">{t(`student_status.${option.key}.description`)}</span>
                                        </span>
                                        {selectionne && <FiCheck className="ml-auto shrink-0" aria-hidden="true" />}
                                    </button>
                                </li>
                            );
                        })}
                    </ul>
                </details>
                )}
            </div>
        </section>
    );
}