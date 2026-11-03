import { useId } from "react";
import { useTranslation } from "react-i18next";

export default function MotifRefus({ id, value, onChange, error = false, errorMessage, label, placeholder,
                                       maxLength = 500, rows = 4, disabled = false, className = "" }) {
    const { t } = useTranslation();
    const generatedId = useId();
    const fieldId = id ?? generatedId;

    return (
        <div className={`flex flex-col gap-1.5 border-t border-red-200 pt-4 mt-2 ${className}`}>
            <label htmlFor={fieldId} className="text-sm font-semibold text-red-600">
                {label ?? t("refusal.reason")}
            </label>
            <textarea
                id={fieldId}
                maxLength={maxLength}
                rows={rows}
                value={value}
                onChange={(e) => onChange(e.target.value)}
                placeholder={placeholder ?? t("refusal.placeholder")}
                disabled={disabled}
                aria-required="true"
                aria-invalid={Boolean(error)}
                aria-describedby={error ? `${fieldId}-error` : undefined}
                className={`w-full rounded-lg border px-3.5 py-2.5 text-sm transition-colors outline-none focus:ring-2 ${
                    error
                        ? "border-red-500 focus:ring-red-200"
                        : "border-gray-300 focus:border-[#0ee1cc] focus:ring-[#0ee1cc]/20"
                }`}
            />
            {error && <p id={`${fieldId}-error`} role="alert" className="text-xs font-medium text-red-500">
                {errorMessage ?? t("refusal.required")}
            </p>}
        </div>
    );
}