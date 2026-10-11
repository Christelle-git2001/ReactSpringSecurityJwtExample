import { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useSearchParams } from "react-router-dom";

export function useGestionnaireItems({
    fetchItems,
    targetParamName,
    targetElementPrefix,
    onError,
}) {
    const location = useLocation();
    const [searchParams] = useSearchParams();
    const targetItemId = Number(location.state?.targetItemId || searchParams.get(targetParamName));
    const [items, setItems] = useState([]);

    const reloadItems = useCallback(async () => {
        try {
            const data = await fetchItems();
            setItems(data || []);
            onError?.(null);
        } catch (err) {
            onError?.(err);
        }
    }, [fetchItems, onError]);

    useEffect(() => {
        reloadItems();
    }, [reloadItems]);

    useEffect(() => {
        if (!targetItemId || items.length === 0) return;

        const targetElement = document.getElementById(`${targetElementPrefix}-${targetItemId}`);
        targetElement?.scrollIntoView({ behavior: "smooth", block: "center" });
    }, [targetElementPrefix, targetItemId, items]);

    return {
        items,
        reloadItems,
        targetItemId,
    };
}

function GestionnaireItemsPage({
    controls,
    errorMessage,
    items,
    displayedItems,
    targetItemId,
    targetElementPrefix,
    emptyItemsMessageKey,
    emptyFilteredMessageKey,
    beforeList,
    listClassName,
    renderItem,
}) {
    const { t } = useTranslation();

    return (
        <div className="w-full max-w-7xl mx-auto px-4 flex flex-col items-center">
            {controls && (
                <div className="w-full max-w-4xl space-y-4 mb-6">
                    {controls}
                </div>
            )}

            {errorMessage && (
                <div className="w-full max-w-4xl mb-4 rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
                    {t(errorMessage)}
                </div>
            )}

            <div className="w-full">
                {beforeList}

                {displayedItems.length === 0 ? (
                    <div className="items-center flex justify-center w-full py-8">
                        <p className="text-gray-500 font-medium">
                            {items.length === 0
                                ? t(emptyItemsMessageKey)
                                : t(emptyFilteredMessageKey)}
                        </p>
                    </div>
                ) : (
                    <div className={listClassName}>
                        {displayedItems.map((item) => {
                            const isTarget = item.id === targetItemId;

                            return (
                                <div
                                    key={item.id}
                                    id={`${targetElementPrefix}-${item.id}`}
                                    className={isTarget ? "scroll-mt-24 rounded-xl ring-2 ring-[#00CCCB] ring-offset-4" : "scroll-mt-24"}
                                >
                                    {renderItem(item)}
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>
        </div>
    );
}

export default GestionnaireItemsPage;