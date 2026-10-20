import React from "react";
import { FiChevronRight } from "react-icons/fi";

const toneClasses = {
    blue: {
        border: "border-base-300",
        icon: "bg-blue-100 text-blue-600",
        action: "text-blue-600 hover:bg-blue-50",
        gradient: "bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]",
    },
    green: {
        border: "border-base-300",
        icon: "bg-green-100 text-green-600",
        action: "text-green-600 hover:bg-green-50",
        gradient: "bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]",
    },
    amber: {
        border: "border-base-300",
        icon: "bg-amber-100 text-amber-600",
        action: "text-amber-600 hover:bg-amber-50",
        gradient: "bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]",
    },
    red: {
        border: "border-base-300",
        icon: "bg-red-100 text-red-600",
        action: "text-red-600 hover:bg-red-950",
        gradient: "bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]",
    },
    yellow: {
        border: "border-base-300",
        icon: "bg-yellow-100 text-yellow-600",
        action: "text-yellow-600 hover:bg-yellow-950",
        gradient: "bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]",
    },
    navy: {
        border: "border-base-300",
        icon: "bg-navy-100 text-navy-600",
        action: "text-navy-600 hover:bg-navy-50",
        gradient: "bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]",
    }
};

function ListItem({
    icon,
    title,
    subtitle,
    meta,
    detailRows = [],
    badge,
    badgeClassName,
    tone = "blue",
    variant = "card",
    actionLabel,
    actionClassName,
    showChevron = true,
    wrapText = false,
    onClick,
    ariaLabel,
}) {
    const styles = toneClasses[tone] || toneClasses.blue;
    const isRow = variant === "row";

    if (isRow) {
        return (
            <button
                type="button"
                onClick={onClick}
                className="relative w-full flex items-center justify-between rounded-lg p-3 text-left hover:bg-gray-50"
                aria-label={ariaLabel}
            >
                <div className="flex min-w-0 items-center gap-3">
                    <div className={`size-10 shrink-0 rounded-full flex items-center justify-center ${styles.icon}`}>
                        {icon}
                    </div>

                    <div className="min-w-0">
                        <p className="truncate text-sm font-semibold text-[#043462]">
                            {title}
                        </p>
                        {subtitle && (
                            <p className={`text-xs font-semibold text-[#043462] ${wrapText ? "whitespace-normal break-words" : "truncate"}`}>
                                {subtitle}
                            </p>
                        )}
                        {meta && (
                            <p className="truncate text-xs font-medium text-gray-600">
                                {meta}
                            </p>
                        )}
                    </div>
                </div>

                <div className="flex shrink-0 items-center gap-2">
                    {badge && (
                        <span className={`badge badge-xs h-auto min-h-6 whitespace-nowrap px-2 py-1 text-center leading-tight ${badgeClassName || ""}`}>
                            {badge}
                        </span>
                    )}

                    {actionLabel ? (
                        <span className={`btn btn-xs rounded-lg border-none bg-white/80 px-3 ${styles.action}`}>
                            {actionLabel}
                        </span>
                    ) : showChevron ? (
                        <span className={`grid size-8 place-items-center rounded-full bg-white/80 ${styles.action}`}>
                            <FiChevronRight className="size-4" />
                        </span>
                    ) : null}
                </div>
            </button>
        );
    }

    return (
        <div className={`rounded-xl border ${styles.border} bg-base-100 p-4 shadow-sm ${styles.gradient}`}>
            <div className="flex items-start justify-between gap-3">
                <div className="flex min-w-0 flex-1 items-start gap-3">
                    <div className={`size-11 shrink-0 rounded-xl flex items-center justify-center ${styles.icon}`}>
                        {icon}
                    </div>

                    <div className="min-w-0 flex-1">
                        <div className="flex flex-col items-start gap-1 sm:flex-row sm:items-center sm:gap-2">
                            <p className="truncate text-sm font-bold text-[#043462]">
                                {title}
                            </p>
                            {badge && (
                                <span className={`badge badge-xs h-auto min-h-6 shrink-0 whitespace-nowrap px-2 py-1 text-center leading-tight ${badgeClassName || ""}`}>
                                    {badge}
                                </span>
                            )}
                        </div>

                        {subtitle && (
                            <p className="mt-1 truncate text-xs font-semibold text-[#043462]">
                                {subtitle}
                            </p>
                        )}

                        {meta && (
                            <p className="mt-1 truncate text-xs font-medium text-gray-600">
                                {meta}
                            </p>
                        )}

                        {detailRows.length > 0 && (
                            <dl className="mt-3 grid gap-2 text-xs text-gray-700 sm:grid-cols-2 lg:grid-cols-4">
                                {detailRows.map(({ label, value }) => (
                                    <div key={label} className="min-w-0">
                                        <dt className="font-bold text-[#043462]">{label}</dt>
                                        <dd className="truncate font-medium">{value}</dd>
                                    </div>
                                ))}
                            </dl>
                        )}
                    </div>
                </div>

                {onClick && (
                    <button
                        type="button"
                        onClick={onClick}
                        className={`btn btn-sm shrink-0 border-none ${actionLabel ? "rounded-lg px-4" : "btn-circle"} ${actionClassName || `bg-white/80 ${styles.action}`}`}
                        aria-label={ariaLabel}
                    >
                        {actionLabel || <FiChevronRight className="size-4" />}
                    </button>
                )}
            </div>
        </div>
    );
}

export default ListItem;