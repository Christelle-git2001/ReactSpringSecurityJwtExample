const ProfileField = ({label,result}) => {
    return (
        <div className="flex flex-col p-4 bg-[radial-gradient(circle_at_top_left,#00CCCB33,transparent_70%)] border-1 rounded-xl">
            <span className="text-xs uppercase font-semibold text-base-content/60 tracking-wider">{label}</span>
            <span className="text-lg font-medium text-base-content mt-1">{result}</span>
        </div>
    )
};export default ProfileField