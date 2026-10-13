import React from "react";

const toneClasses = {
    blue: {
        border: "border-blue-100",
        icon: "bg-blue-100 text-blue-600",
        action: "bg-blue-50 text-blue-600",
    },
    green: {
        border: "border-green-100",
        icon: "bg-green-100 text-green-600",
        action: "bg-green-50 text-green-600",
    },
};

function DashboardSummaryCard({ icon, value, label, tone = "blue", onClick, ariaLabel, actionLabel, className = "" }) {
    const styles = toneClasses[tone] || toneClasses.blue;

    return (
        <div className={`rounded-xl border ${styles.border} bg-white shadow-sm p-4 flex items-center justify-between ${className}`}>
            <div className="flex items-center gap-3">
                <div className={`size-11 rounded-xl flex items-center justify-center ${styles.icon}`}>
                    {icon}
                </div>

                <div>
                    <p className="text-2xl font-bold leading-tight text-[#043462]">{value}</p>
                    <p className="text-xs font-semibold text-gray-600">{label}</p>
                </div>
            </div>

            {onClick && (
                <button
                    type="button"
                    onClick={onClick}
                    className={`btn btn-sm shrink-0 rounded-lg border-none px-4 ${styles.action}`}
                    aria-label={ariaLabel}
                >
                    {actionLabel}
                </button>
            )}
        </div>
    );
}

export default DashboardSummaryCard;
