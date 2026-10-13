import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { FiChevronDown } from "react-icons/fi";

const ExpandableList = ({
    items = [],
    limit = 6,
    renderItem,
    getKey = (item) => item.id,
    expandLabel,
    collapseLabel,
}) => {
    const { t } = useTranslation();
    const [expanded, setExpanded] = useState(true);
    const hasMore = items.length > limit;
    const visibleItems = expanded ? items : [];
    const resolvedExpandLabel = expandLabel || t("dashboard.view_more");
    const resolvedCollapseLabel = collapseLabel || t("dashboard.view_less");

    return (
        <div className="space-y-3">
            {visibleItems.map((item, index) => (
                <div key={getKey(item, index)}>
                    {renderItem(item)}
                </div>
            ))}

            {hasMore && (
                <button
                    type="button"
                    aria-expanded={expanded}
                    onClick={() => setExpanded((previous) => !previous)}
                    className="flex w-full cursor-pointer items-center justify-center gap-2 py-2 text-sm font-semibold text-[#043462]"
                >
                    {expanded ? resolvedCollapseLabel : resolvedExpandLabel}
                    <FiChevronDown
                        aria-hidden="true"
                        className={`size-4 transition-transform duration-200 ${expanded ? "rotate-180" : ""}`}
                    />
                </button>
            )}
        </div>
    );
};

export default ExpandableList;
