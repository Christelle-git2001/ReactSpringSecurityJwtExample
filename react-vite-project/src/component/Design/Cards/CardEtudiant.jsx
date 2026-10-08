import {FiArrowRight} from "react-icons/fi";
import {Link} from "react-router-dom";

const CardEtudiant = ({ title, description, Icon, linkTo }) => {
    return (
        <div className="card m-5 lg:card-side bg-base-100 shadow-sm flex h-64">
            <div className="flex items-center justify-center lg:w-1/3 h-full bg-[#0FFFDF]/30 text-[#043462]">
                {Icon && <Icon className="w-20 h-20" />}
            </div>
            <div className="card-body flex flex-col justify-between">
                <h2 className="card-title text-center">{title}</h2>
                <p className="flex-grow">{description}</p>

                <div className="card-actions justify-end">
                    <Link
                        to={linkTo}
                        className="w-full rounded-full bg-[#0FFFDF]/30 flex items-center justify-center">
                        <FiArrowRight className="size-6 my-1.5 text-[#043462]" />
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default CardEtudiant;