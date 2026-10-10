import SideBarIcon from "./Icons/SideBarIcon.jsx";

function Drawer({ id, content }) {
    return (
        <div className="drawer drawer-end fixed inset-0 z-50 pointer-events-none ">
            <input id={id} type="checkbox" className="drawer-toggle" />

            <div className="drawer-side pointer-events-auto">
                <label htmlFor={id} className="drawer-overlay"></label>
                <label
                    htmlFor={id}
                    aria-label="Fermer"
                    data-tip="Fermer"
                    className="btn btn-square btn-ghost fixed bottom-6 right-80 z-[60] tooltip tooltip-left bg-[#043462] text-white hover:bg-[#00CCCB] hover:text-white"
                >
                    <SideBarIcon />
                </label>
                <div className="min-h-full w-96 p-4 shadow-xl bg-[#D9B8FF]">
                    {content}
                </div>
            </div>
        </div>
    );
}

export default Drawer;
