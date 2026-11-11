import EtudiantInfoCard from "../Cards/EtudiantInfoCard.jsx";

const EtudiantListItem = ({etudiant, isNew,entrevueEtudiantSelectedSetter}) => {
    return (
          <EtudiantInfoCard etudiant={etudiant} isNew={isNew} entrevueEtudiantSelectedSetter={entrevueEtudiantSelectedSetter}/>
    )
};export default EtudiantListItem;