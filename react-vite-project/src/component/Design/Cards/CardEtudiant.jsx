import {FiArrowRight} from "react-icons/fi";
import {Link} from "react-router-dom";

const CardEtudiant = ({ title, description, Icon, linkTo }) => {
    return (
        <>
            <div className="card m-5 lg:card-side bg-base-100 shadow-sm flex justify-center items-center">
                <div className="flex items-center justify-center lg:w-full h-full bg-[#0FFFDF]/30 text-[#043462] sm:w-1/4 sm:mt-5 lg:m-3">
                    {Icon && <Icon className="w-full h-full" />}
                </div>
                <div className="card-body">
                    <h2 className="card-title justify-center w-full">{title}</h2>
                    <p>{description}</p>
                    <div className="card-actions justify-end">
                        <Link to={linkTo} className={"w-full rounded-full bg-[#0FFFDF]/30 items-center justify-center flex"}>
                                <FiArrowRight className="size-6 my-1.5 text-[#043462]"/>
                        </Link>
                    </div>
                </div>
            </div>
        </>
    )
};
export default CardEtudiant;