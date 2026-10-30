import {useLocation} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {useState} from "react";

function ConvocationEntrevue() {
    const location = useLocation();
    const { t } = useTranslation();
    const [loading, setLoading] = useState(false);

    return(
        <div>
        </div>
    )
}
export default ConvocationEntrevue;