import Button from "./Button.jsx";

function EmptyContentMessage({ message, buttonLabel, onButtonClick, textClassName = "text-gray-500 font-medium" }) {
    return (
        <div className="text-center opacity-70">
            <p className={textClassName}>{message}</p>

            {buttonLabel && onButtonClick && (
                <Button
                    role="retour"
                    type="button"
                    onClick={onButtonClick}
                    className="mt-4"
                >
                    {buttonLabel}
                </Button>
            )}
        </div>
    );
}

export default EmptyContentMessage;