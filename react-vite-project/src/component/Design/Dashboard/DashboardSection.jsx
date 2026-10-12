import React from "react";

const DashboardSection = ({ title, children }) => (
    <section className="rounded-xl border border-gray-100 bg-white shadow-sm p-5">
        <h2 className="font-bold text-[#043462]">{title}</h2>
        <div className="my-4 h-px w-full bg-[#043462]" />
        {children}
    </section>
);

export default DashboardSection;
