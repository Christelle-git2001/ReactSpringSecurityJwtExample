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

function DashboardSummaryCard({ icon, value, label, tone = "blue", onClick, ariaLabel, actionLabel }) {
    const styles = toneClasses[tone] || toneClasses.blue;

    return (
        <div className={`rounded-xl border ${styles.border} bg-white shadow-sm p-5 flex items-center justify-between`}>
            <div className="flex items-center gap-4">
                <div className={`size-14 rounded-xl flex items-center justify-center ${styles.icon}`}>
                    {icon}
                </div>

                <div>
                    <p className="text-3xl font-bold text-[#043462]">{value}</p>
                    <p className="text-sm font-semibold text-gray-600">{label}</p>
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
