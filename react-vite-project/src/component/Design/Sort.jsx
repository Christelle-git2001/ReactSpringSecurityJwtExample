import { useTranslation } from "react-i18next";
import { FiArrowDown, FiArrowUp } from "react-icons/fi";

export default function Sort({ sortOrder = "desc", onSortChange, className = "", newestLabel, oldestLabel, title }) {
    const { t } = useTranslation();
    const newest = newestLabel ?? t("sort.newest_first");
    const oldest = oldestLabel ?? t("sort.oldest_first");

    return (
        <button
            type="button"
            onClick={() => onSortChange(sortOrder === "desc" ? "asc" : "desc")}
            className={`inline-flex cursor-pointer items-center gap-2 rounded-lg px-3 py-2 text-xs font-medium hover:bg-blue-50 focus-visible:outline-2 focus-visible:outline-blue-600 ${className}`}
            title={title ?? t("sort.by_date")}
            aria-label={sortOrder === "desc" ? oldest : newest}
        >
            {sortOrder === "desc"
                ? <FiArrowDown className="size-4" aria-hidden="true" />
                : <FiArrowUp className="size-4" aria-hidden="true" />}
            {sortOrder === "desc" ? newest : oldest}
        </button>
    );
}