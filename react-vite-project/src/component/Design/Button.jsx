const variantClasses = {
    standard: "border-transparent bg-[#043462] text-white enabled:hover:bg-[#03284d] focus-visible:outline-[#043462]",
    success: "border-transparent bg-green-600 text-white enabled:hover:bg-green-800 focus-visible:outline-green-600",
    danger: "border-transparent bg-red-600 text-white enabled:hover:bg-red-700 focus-visible:outline-red-600",
    secondary: "border-[#043462] bg-white text-[#043462] enabled:hover:bg-blue-50 focus-visible:outline-[#043462]",
    custom: "border-transparent",
};

const sizeClasses = {
    normal: "px-4 py-2",
    small: "px-3 py-1.5 text-sm",
    icon: "size-10 shrink-0 p-2",
};

function Button({
    children,
    type = "button",
    variant = "standard",
    size = "normal",
    className = "",
    disabled = false,
    loading = false,
    ...props
}) {
    return (
        <button
            {...props}
            type={type}
            disabled={disabled || loading}
            aria-busy={loading || undefined}
            className={`inline-flex items-center justify-center gap-2 rounded-md border font-medium transition-colors enabled:cursor-pointer disabled:cursor-not-allowed disabled:opacity-50 focus-visible:outline-2 focus-visible:outline-offset-2 ${Object.hasOwn(variantClasses, variant) ? variantClasses[variant] : variantClasses.standard} ${sizeClasses[size] || sizeClasses.normal} ${className}`}
        >
            {loading && <span className="loading loading-spinner loading-sm" aria-hidden="true" />}
            {children}
        </button>
    );
}

export default Button;