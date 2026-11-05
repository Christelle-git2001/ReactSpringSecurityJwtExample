import React from "react";

const ProfileSection = ({ title, children, id }) => {
    return (
        <div id={id}>
            <h2 className="text-2xl font-bold text-[#043462] border-b pb-4 mb-6 pt-4">
                {title}
            </h2>
            <div className="grid grid-cols-12 gap-y-4">
                {children}
            </div>
        </div>
    );
};

export default ProfileSection;