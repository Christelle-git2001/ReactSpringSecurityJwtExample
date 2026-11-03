import EtudiantInfoCard from "../Cards/EtudiantInfoCard.jsx";

const EtudiantListItem = ({etudiant, onClick,}) => {

    return (
          <EtudiantInfoCard etudiant={etudiant}/>
    )
};export default EtudiantListItem;