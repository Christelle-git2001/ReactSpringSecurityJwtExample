import React, { useState } from "react";
import { FiChevronDown } from "react-icons/fi";

const ExpandableList = ({
    items = [],
    limit = 6,
    renderItem,
    getKey = (item) => item.id,
    expandLabel = "Voir plus",
    collapseLabel = "Voir moins",
}) => {
    const [expanded, setExpanded] = useState(false);
    const hasMore = items.length > limit;
    const visibleItems = expanded ? items : items.slice(0, limit);

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
                    {expanded ? collapseLabel : expandLabel}
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
