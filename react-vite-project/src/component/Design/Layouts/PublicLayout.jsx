import { Outlet } from "react-router-dom";

export default function PublicLayout() {
    return (
        <div className="pageLayout">
            <Outlet />
        </div>
    );
}
