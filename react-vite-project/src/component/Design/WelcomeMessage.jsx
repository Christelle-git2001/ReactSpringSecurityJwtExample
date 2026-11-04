import React from "react";
import {useTranslation} from "react-i18next";

const WelcomeMessage = ({user}) => {
    const { t } = useTranslation();
    return (
        <div className="w-full max-w-7xl">
            <h1 className="welcome-message text-left">
                {t("home.welcome")} {user?.firstName} {user?.lastName}
            </h1>
        </div>
    )
};export default WelcomeMessage;