import React from "react";
import { FiX } from "react-icons/fi";
import { useTranslation } from "react-i18next";

export default function SearchBar({value, onChange, onClear, className = ""}) {
    const { t } = useTranslation();
    return (
        <div className={`flex items-center gap-2 w-full ${className}`}>
            <label className="input input-bordered flex items-center gap-2 w-full bg-base-100 shadow-sm rounded-2xl focus-within:outline-none focus-within:border-primary">
                <svg
                    className="h-[1em] opacity-50 shrink-0"
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 24 24"
                >
                    <g
                        strokeLinejoin="round"
                        strokeLinecap="round"
                        strokeWidth="2.5"
                        fill="none"
                        stroke="currentColor"
                    >
                        <circle cx="11" cy="11" r="8"></circle>
                        <path d="m21 21-4.3-4.3"></path>
                    </g>
                </svg>

                <input
                    type="search"
                    value={value}
                    onChange={(e) => onChange(e.target.value)}
                    placeholder={t("search.placeholder")}
                    className="grow focus:outline-none [&::-webkit-search-cancel-button]:appearance-none [&::-webkit-search-decoration]:appearance-none"
                />
            </label>

            {value && onClear && (
                <button
                    type="button"
                    onClick={onClear}
                    className="btn btn-square btn-ghost text-gray-500  shrink-0 focus:outline-none"
                    title={t("search.clear")}
                >
                    <FiX className="w-5 h-5" />
                </button>
            )}
        </div>
    );
}
