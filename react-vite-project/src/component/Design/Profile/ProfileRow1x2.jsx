const ProfileRow1x2 = ({ left, right }) => {
    return (
        <>
            <div className="col-span-5">{left}</div>
            <div className="col-span-2 flex justify-center">
                <div className="divider divider-horizontal m-0"></div>
            </div>
            <div className="col-span-5">{right}</div>
        </>
    )
};export default ProfileRow1x2;