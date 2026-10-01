import {useTranslation} from "react-i18next";
import {obtenirCv} from "../../api/etudiant.jsx";
import {useEffect, useState} from "react";
import {FiFileText} from "react-icons/fi";
import {IoEyeSharp} from "react-icons/io5";
import {FaDownload, FaTrashAlt} from "react-icons/fa";

function PDFVisionneuse(props) {
    return null;
}

const ListeCV = (refreshTrigger) => {
    const { t } = useTranslation();
    const [cv, setCv] = useState({
        id : null,
        fileName : "",
        contentType : "",
        fileSize : 0,
        uploadDate : ""

    });
    const [error, setError] = useState("")

    async function obtenirCVs(){
        try{
            const data = await obtenirCv();
            setCv(data)
        }catch (error){
            // TODO exception
        }
    }

    const formaterTailleFichier = (octets) => {
        if (!octets || octets === 0) return "0 KB";
        const kb = octets / 1024;

        if (kb >= 1024) {
            return `${(kb / 1024).toFixed(1)} MB`;
        }
        return `${Math.round(kb)} KB`;
    };

    const formaterDate = (isoString) => {
        if (!isoString) return "";
        const date = new Date(isoString);

        const annee = date.getFullYear();
        const mois = String(date.getMonth() + 1).padStart(2, "0");
        const jour = String(date.getDate()).padStart(2, "0");
        const heures = String(date.getHours()).padStart(2, "0");
        const minutes = String(date.getMinutes()).padStart(2, "0");

        return `${annee}-${mois}-${jour} ${heures}:${minutes}`;
    };

    useEffect(() => {
        obtenirCVs();
    }, [refreshTrigger]);
    return (
        <div className=" w-full mt-5">
            {!cv ? (
                <div>
                    <div><FiFileText className="size-10 rounded-box text-red-500 bg-purple-200 border border-t-0" /></div>
                    <div>
                        <div>{cv.fileName}</div>
                            <div className="text-xs uppercase font-semibold opacity-60">{formaterDate(cv.uploadDate)}</div>
                        </div>
                        <div>
                            <div>{formaterTailleFichier(cv.fileSize)}</div>
                        </div>
                        <button className="btn btn-square btn-ghost"> {/* todo on click */}
                            <IoEyeSharp className="w-full h-full text-teal-500 hover:text-black" />
                        </button>
                        <a
                            href={null}
                            download={cv.fileName || "document.pdf"}
                            className="btn btn-ghost"
                        >
                            <FaDownload className="w-full h-full text-green-700 hover:text-black"/>  {/*TODO waiting for the end-point */}
                        </a>
                        <button className="btn btn-square btn-ghost">
                            <FaTrashAlt className="w-full h-full text-red-900 hover:text-black"/> {/*TODO waiting for the end-point */}
                        </button>
                </div>
                ) : (
                    <div>
                        <p>{t("")}</p>
                    </div>
                )}
        </div>
    )
};
export default ListeCV;