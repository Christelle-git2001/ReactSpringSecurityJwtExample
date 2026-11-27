import React from "react";
import { FiChevronRight } from "react-icons/fi";
import {useTranslation} from "react-i18next";
import Button from "../Button.jsx";
import {toneClasses} from "../../../utils/tones.jsx";


function ListItem({
    icon,
    title,
    subtitle,
    meta,
    detailRows = [],
    detailsGridClassName = "sm:grid-cols-2 lg:grid-cols-4",
    badge,
    badgeClassName,
    tone = "blue",
    variant = "card",
    actionLabel,
    actionIcon,
    actionRole = "details",
    actionClassName,
    secondaryAction,
    extraActions,
    footer,
    showChevron = true,
    wrapText = false,
    onClick,
    isSelected = false,
    isClickableCard = false,
    ariaLabel,
}) {
    const { t } = useTranslation();
    const styles = toneClasses[tone] || toneClasses.blue;
    const isRow = variant === "row";
    const isCompact = variant === "compact";
    const selectionStyle = isSelected
        ? "border-5 border-green-600"
        : styles.border;

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

    if (isCompact){
        return (
            <div className="text-gray-700 border-b pb-2">
                {detailRows.length > 0 && (
                    <dl className="grid grid-cols-2 gap-2">
                        {detailRows.map(({ label, value }, index) => (
                            <div key={`${label}-${index}`} className="min-w-0 pe-2 ps-2">
                                <dt className="font-bold text-[#043462]">{label}</dt>
                                <dd>{value}</dd>
                            </div>
                        ))}
                    </dl>
                )}
                {onClick && (
                    <div className="flex justify-end me-2 mt-2">
                        <Button className="w-full" onClick={onClick} size="small" role="details">{t("etudiant.card_professeur.voir_detail")}</Button>
                    </div>
                )}
            </div>
        )
    }

    return (
        <div onClick={isClickableCard ? onClick : undefined}
             className={`${isClickableCard ? "cursor-pointer" : ""} rounded-xl border ${styles.border} bg-base-100 p-4 shadow-sm ${styles.gradient} ${isSelected ? selectionStyle:""}`}>
            <div className="flex items-start justify-between gap-3">
                <div className="flex min-w-0 flex-1 items-start gap-3">
                    {icon && (
                        <div className={`size-11 shrink-0 rounded-xl flex items-center justify-center ${styles.icon}`}>
                            {icon}
                        </div>
                    )}
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
                            <dl className={`mt-3 grid gap-2 text-sm text-gray-700 ${detailsGridClassName}`}>
                                {detailRows.map(({ label, value },index) => (
                                    <div key={`${label}-${index}`} className="min-w-0">
                                        <dt className="font-bold text-[#043462]">{label}</dt>
                                        <dd className="truncate font-medium">{value}</dd>
                                    </div>
                                ))}
                            </dl>
                        )}
                    </div>
                </div>

                {(secondaryAction || (onClick && !isClickableCard)) && (
                    <div className="flex shrink-0 flex-col items-stretch gap-2 sm:flex-row sm:items-center">
                        {secondaryAction && (
                            <button
                                type="button"
                                onClick={(e) => { e.stopPropagation(); secondaryAction.onClick(); }}
                                disabled={secondaryAction.disabled}
                                className={`btn btn-sm shrink-0 border-none rounded-lg px-4 ${secondaryAction.className || `bg-white/80 ${styles.action}`}`}
                            >
                                {secondaryAction.icon}
                                {secondaryAction.label}
                            </button>
                        )}

                        {onClick && !isClickableCard && (
                            <div className="flex shrink-0 flex-col items-end gap-2">
                                <Button
                                    role={actionRole}
                                    icon={actionIcon ?? Boolean(actionLabel)}
                                    size="small"
                                    type="button"
                                    onClick={onClick}
                                    className={`btn btn-sm shrink-0 border-none ${actionLabel ? "rounded-lg px-4" : "btn-circle"} ${actionClassName || `bg-white/80 ${styles.action}`}`}
                                    aria-label={ariaLabel}
                                >
                                    {actionLabel || <FiChevronRight className="size-4" />}
                                </Button>
                                {extraActions}
                            </div>
                        )}
                    </div>
                )}
            </div>
            {footer && <div className="mt-3 flex justify-end text-right text-xs italic text-gray-500">{footer}</div>}
        </div>
    );
}

export default ListItem;