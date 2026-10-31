function PageSeparator() {
    return (
        <div
            className="col-span-full flex items-center gap-6 my-6"
            aria-hidden="true"
        >
            <div className="h-px bg-[#00CCCB]/40 flex-1" />
            <div className="flex gap-3">
                <span className="size-2 rounded-full bg-[#00CCCB]/40" />
                <span className="size-2 rounded-full bg-[#00CCCB]/40" />
                <span className="size-2 rounded-full bg-[#00CCCB]/40" />
            </div>
            <div className="h-px bg-[#00CCCB]/40 flex-1" />
        </div>
    );
}

export default PageSeparator;