function Button({ children, onClick, type = "button", className = "", disabled = false }) {
    return (
        <button
            type={type}
            onClick={onClick}
            disabled={disabled}
            className={`rounded-md bg-[#0ee1cc] px-4 py-2 font-medium text-white transition-colors hover:bg-[#043462] ${className}`}
        >
            {children}
        </button>
    );
}

export default Button;