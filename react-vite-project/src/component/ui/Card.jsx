function Card({ children, className = "" }) {
    return (
        <div className={`card bg-base-100 shadow-sm ${className}`}>
            <div className="card-body">
                {children}
            </div>
        </div>
    );
}

export default Card;