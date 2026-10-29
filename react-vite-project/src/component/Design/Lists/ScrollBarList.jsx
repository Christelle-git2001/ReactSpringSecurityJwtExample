import {useTranslation} from "react-i18next";

const ScrollBarList = ({title , emptyMessage, liste = [], renderItem}) => {
    const { t } = useTranslation();
    const messageVide = emptyMessage || t("general.liste.empty");
    return (
        <div className="overflow-y-auto flex flex-col ms-5 me-5 h-full">
            {title && (
                <h2 className="text-center rounded-3xl text-xl m-2 font-semibold">{title}</h2>
            )}
            {liste.length > 0 ? (
                <div className="flex flex-col gap-2">
                    {liste.map((item, index) => (
                        renderItem ? renderItem(item, index) : <div key={index}>{item.toString()}</div>
                    ))}
                </div>
            ) : (
                <p className="p-4 text-center text-gray-500">{messageVide}</p>
            )}
        </div>
    )
}; export default ScrollBarList;