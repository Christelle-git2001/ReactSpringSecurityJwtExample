function Drawer({ id, content }) {
    return (
        <div className="drawer drawer-end fixed inset-0 z-50 pointer-events-none ">
            <input id={id} type="checkbox" className="drawer-toggle" />

            <div className="drawer-side pointer-events-auto">
                <label htmlFor={id} className="drawer-overlay"></label>
                <div className="min-h-full w-80 p-4 shadow-xl bg-[#D9B8FF]">
                    {content}
                </div>
            </div>
        </div>
    );
}

export default Drawer;
