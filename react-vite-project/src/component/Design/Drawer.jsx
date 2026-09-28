import React from "react";

function Drawer({ id, content, children }) {
    return (
        <div className="drawer drawer-end">
            <input id={id} type="checkbox" className="drawer-toggle" />
            <div className="drawer-content">
                {children}
            </div>
            <div className="drawer-side z-50">
                <label htmlFor={id} aria-label="close sidebar" className="drawer-overlay"></label>
                <div className="bg-base-200 min-h-full w-80 p-4">
                    {content}
                </div>
            </div>
        </div>
    );
}

export default Drawer;