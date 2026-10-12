import Button from "./Button.jsx";

function EmptyContentMessage({ message, buttonLabel, onButtonClick, textClassName = "text-gray-500 font-medium" }) {
    return (
        <div className="text-center opacity-70">
            <p className={textClassName}>{message}</p>

            {buttonLabel && onButtonClick && (
                <Button
                    type="button"
                    onClick={onButtonClick}
                    className="mt-4 bg-[#043462] text-white hover:bg-[#03284d]"
                >
                    {buttonLabel}
                </Button>
            )}
        </div>
    );
}

export default EmptyContentMessage;