import {useTranslation} from "react-i18next";
import {toneClasses} from "../../../utils/tones.jsx";

const DashboardMetricSection = ({title,tabs = [], activeTabId, onTabChange,children, className= "", tone}) => {
    const { t, i18n } = useTranslation();
    const styles = toneClasses[tone] || toneClasses.purple;

    const hasTabs = Boolean(onTabChange) && tabs.length > 0;

    return (
        <section className={`flex flex-col gap-4 rounded-xl border p-4 ${styles.border} ${styles.gradient} ${className} `}>
            {hasTabs ? (
                <div role="tablist" className="tabs tabs-lift">
                    {tabs.map((tab) => {
                        const isActive = tab.id === activeTabId;
                        return (
                            <a
                                key={tab.id}
                                role="tab"
                                onClick={() => onTabChange(tab.id)}
                                className={`tab cursor-pointer ${isActive ? "tab-active" : ""} font-bold`}
                            >
                                {tab.label}
                            </a>
                        );
                    })}
                </div>
            ) : (
                title && <h2 className="text-lg font-bold">{title}</h2>
            )}

            <div className="grid grid-cols-12 gap-3 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
                {children}
            </div>
        </section>
    );
};export default DashboardMetricSection;