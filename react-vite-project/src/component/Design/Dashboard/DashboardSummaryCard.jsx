import React from "react";
import {toneClasses} from "../../../utils/tones.jsx";


function DashboardSummaryCard({ icon, value, label, tone = "blue", onClick, ariaLabel, actionLabel, className = "" }) {
    const styles = toneClasses[tone] || toneClasses.blue;

    return (
        <div className={`rounded-xl border ${styles.border} bg-white shadow-sm p-4 flex items-center justify-between ${className}`}>
            <div className="flex items-center gap-3">
                <div className={`shrink-0 size-11 rounded-xl flex items-center justify-center ${styles.icon}`}>
                    {typeof icon === "function" ? icon({ className: "size-5" }) : icon}
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
