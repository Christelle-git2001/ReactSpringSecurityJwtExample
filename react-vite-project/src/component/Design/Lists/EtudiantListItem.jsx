import EtudiantInfoCard from "../Cards/EtudiantInfoCard.jsx";

const EtudiantListItem = ({etudiant, isNew,}) => {

    return (
          <EtudiantInfoCard etudiant={etudiant} isNew={isNew}/>
    )
};export default EtudiantListItem;