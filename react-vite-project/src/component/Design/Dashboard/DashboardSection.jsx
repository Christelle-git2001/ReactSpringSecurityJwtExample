import React, { useState } from "react";
import { useTranslation } from "react-i18next";

const DashboardSection = ({ title, headerAction, children, className = "", collapsible = false }) => {
    const { t } = useTranslation();
    const [expanded, setExpanded] = useState(true);
    const toggleLabel = t(expanded ? "sidebar.collapse" : "sidebar.expand");
    const Container = collapsible ? "details" : "section";
    const Header = collapsible ? "summary" : "div";

    return (
        <Container
            className={`rounded-xl border border-gray-100 bg-white shadow-sm p-5 ${collapsible ? "collapse collapse-arrow" : ""} ${className}`}
            {...(collapsible ? {
                open: expanded,
                onToggle: event => setExpanded(event.currentTarget.open),
            } : {})}
        >
            <Header className={`flex items-center justify-between gap-3 ${collapsible ? "collapse-title relative min-h-0 py-0 pl-0 pr-10" : ""}`}>
                <h2 className="font-bold text-[#043462]">{title}</h2>
                <div className="flex shrink-0 flex-wrap items-center justify-end gap-3">
                    {headerAction && (
                        <div>
                            {headerAction}
                        </div>
                    )}
                    {collapsible && (
                        <span
                            aria-label={`${toggleLabel} : ${title}`}
                            data-tip={toggleLabel}
                            className="tooltip tooltip-left absolute right-0 top-1/2 h-8 w-8 -translate-y-1/2 text-[#043462]"
                        />
                    )}
                </div>
            </Header>
            <div className={collapsible ? "collapse-content px-0 pb-0" : ""}>
                <div className="my-4 h-px w-full bg-[#043462]" />
                {children}
            </div>
        </Container>
    );
};

export default DashboardSection;