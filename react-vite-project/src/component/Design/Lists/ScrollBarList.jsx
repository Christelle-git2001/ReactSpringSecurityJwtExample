import {useTranslation} from "react-i18next";
import SearchBar from "../Bars/SearchBar.jsx";
import {useState} from "react";

const ScrollBarList = ({title , emptyMessage, liste = [], renderItem}) => {
    const { t } = useTranslation();
    const [filtreValue, setFiltreValue] = useState("")
    const messageVide = emptyMessage || t("general.liste.empty");
    const filtreActif = (event) => {
        setFiltreValue(event)
    }
    return (
        <div className="overflow-y-auto flex flex-col ms-5 me-5 h-full">
            {title && (
                <h2 className="text-center rounded-3xl text-xl m-2 font-semibold">{title}</h2>
            )}
            <SearchBar onChange={filtreActif} value={filtreValue}/>
            {liste.length > 0 ? (
                <div className="flex flex-col gap-2 mt-2">
                    {liste.filter((item) => {
                        return Object.values(item).some((valeur) =>
                            String(valeur).toLowerCase().includes(String(filtreValue).toLowerCase())
                        );
                    }).map((item, index) => (
                        renderItem ? renderItem(item, index) : <div key={index}>{item.toString()}</div>
                    ))}
                </div>
            ) : (
                <p className="p-4 text-center text-gray-500">{messageVide}</p>
            )}
        </div>
    )
}; export default ScrollBarList;