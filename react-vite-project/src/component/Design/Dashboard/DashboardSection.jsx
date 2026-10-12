import React from "react";

const DashboardSection = ({ title, headerAction, children, className = "" }) => (
    <section className={`rounded-xl border border-gray-100 bg-white shadow-sm p-5 ${className}`}>
        <div className="flex items-center justify-between gap-3">
            <h2 className="font-bold text-[#043462]">{title}</h2>
            {headerAction && (
                <div className="shrink-0">
                    {headerAction}
                </div>
            )}
        </div>
        <div className="my-4 h-px w-full bg-[#043462]" />
        {children}
    </section>
);

export default DashboardSection;
