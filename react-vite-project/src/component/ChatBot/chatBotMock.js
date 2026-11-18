const DONNEES = {
    etudiants: [
        { nom: "Sebastien Castonguay", dept: "Informatique", statut: "EN_RECHERCHE", professeur: null, cv: "APPROUVE" },
        { nom: "Adrien Agreste",       dept: "Informatique", statut: "CONTRAT_CREE", professeur: "Toru Shirazu", cv: "APPROUVE" },
        { nom: "Nicolas Sarkozy",      dept: "Informatique", statut: "CONTRAT_CREE", professeur: "Toru Shirazu", cv: "APPROUVE" },
        { nom: "Juzo Suzuya",          dept: "Informatique", statut: "STAGE_TROUVE",  professeur: null, cv: "APPROUVE" },
        { nom: "Christelle Nouga",     dept: "Informatique", statut: "EN_RECHERCHE", professeur: null, cv: "EN_ATTENTE" },
        { nom: "Marc Disparu",         dept: "Travail Social", statut: "EN_RECHERCHE", professeur: null, cv: "REFUSE" },
        { nom: "Jacob Frye",           dept: "Informatique", statut: "EN_RECHERCHE", professeur: null, cv: null },
    ],
    offres: [
        { titre: "Développeur Web Junior",        entreprise: "Malinois inc", statut: "ACCEPTEE", candidatures: 3 },
        { titre: "Technicien Support Informatique", entreprise: "Malinois inc", statut: "ACCEPTEE", candidatures: 2 },
        { titre: "Technician Réseaux",             entreprise: "Malinois inc", statut: "ACCEPTEE", candidatures: 0 },
    ],
    contrats: [
        { etudiant: "Adrien Agreste",  sigGest: false, sigEmpl: false, sigEtud: false },
        { etudiant: "Nicolas Sarkozy", sigGest: true,  sigEmpl: false, sigEtud: false },
    ],
};

// Normalise le texte pour la comparaison
const norm = (s) =>
    s.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");

function liste(items) {
    return items.map(i => `• ${i}`).join("\n");
}

export function getMockReponse(question) {
    const q = norm(question);

    // INSCRIPTIONS
    if (q.includes("combien") && q.includes("etudiant")) {
        return `Il y a **${DONNEES.etudiants.length} étudiants inscrits** dans la session courante.`;
    }
    if ((q.includes("quels") || q.includes("liste") || q.includes("noms")) && q.includes("etudiant")) {
        return `Étudiants inscrits :\n${liste(DONNEES.etudiants.map(e => `${e.nom} (${e.dept})`))}`;
    }
    if (q.includes("informatique") && q.includes("etudiant")) {
        const info = DONNEES.etudiants.filter(e => norm(e.dept) === "informatique");
        return `**${info.length} étudiants** en Informatique :\n${liste(info.map(e => e.nom))}`;
    }
    if (q.includes("repartition") && q.includes("discipline")) {
        const par = {};
        DONNEES.etudiants.forEach(e => { par[e.dept] = (par[e.dept] || 0) + 1; });
        return `Répartition par département :\n${Object.entries(par).map(([k,v]) => `• ${k} : ${v}`).join("\n")}`;
    }

    // CV
    if (q.includes("cv") && (q.includes("aucun") || q.includes("pas") || q.includes("soumis"))) {
        const sans = DONNEES.etudiants.filter(e => !e.cv);
        return sans.length
            ? `**${sans.length} étudiant(s)** sans CV :\n${liste(sans.map(e => e.nom))}`
            : "Tous les étudiants ont soumis un CV.";
    }
    if (q.includes("cv") && q.includes("attente")) {
        const att = DONNEES.etudiants.filter(e => e.cv === "EN_ATTENTE");
        return att.length
            ? `**${att.length} CV en attente** de validation :\n${liste(att.map(e => e.nom))}`
            : "Aucun CV en attente.";
    }
    if (q.includes("cv") && q.includes("refus")) {
        const ref = DONNEES.etudiants.filter(e => e.cv === "REFUSE");
        return ref.length
            ? `CV refusés :\n${liste(ref.map(e => e.nom))}`
            : "Aucun CV refusé.";
    }
    if (q.includes("cv") && q.includes("approuv")) {
        const app = DONNEES.etudiants.filter(e => e.cv === "APPROUVE");
        const pct = Math.round(app.length / DONNEES.etudiants.length * 100);
        return `**${app.length} CV approuvés** sur ${DONNEES.etudiants.length} (${pct} %).`;
    }

    // OFFRES
    if (q.includes("offre") && q.includes("valid")) {
        const val = DONNEES.offres.filter(o => o.statut === "ACCEPTEE");
        return `**${val.length} offres validées** :\n${liste(val.map(o => `${o.titre} — ${o.entreprise}`))}`;
    }
    if (q.includes("offre") && q.includes("aucune candidature")) {
        const vides = DONNEES.offres.filter(o => o.candidatures === 0);
        return vides.length
            ? `Offres sans candidature :\n${liste(vides.map(o => o.titre))}`
            : "Toutes les offres ont au moins une candidature.";
    }
    if (q.includes("offre") && (q.includes("plus") || q.includes("candidature"))) {
        const tri = [...DONNEES.offres].sort((a, b) => b.candidatures - a.candidatures);
        return `Offres par nombre de candidatures :\n${liste(tri.map(o => `${o.titre} : ${o.candidatures} candidature(s)`))}`;
    }
    if (q.includes("entreprise")) {
        const ents = [...new Set(DONNEES.offres.map(o => o.entreprise))];
        return `Entreprises ayant déposé des offres :\n${liste(ents)}`;
    }

    // CONTRATS
    if (q.includes("contrat") && q.includes("cree")) {
        return `**${DONNEES.contrats.length} contrat(s) créé(s)** :\n${liste(DONNEES.contrats.map(c => c.etudiant))}`;
    }
    if (q.includes("contrat") && q.includes("signature")) {
        const enCours = DONNEES.contrats.filter(c => !(c.sigGest && c.sigEmpl && c.sigEtud));
        return enCours.length
            ? `Contrats en attente de signature :\n${liste(enCours.map(c =>
                `${c.etudiant} — Gestionnaire: ${c.sigGest ? "✓" : "✗"}  Employeur: ${c.sigEmpl ? "✓" : "✗"}  Étudiant: ${c.sigEtud ? "✓" : "✗"}`
            ))}`
            : "Tous les contrats sont signés.";
    }
    if (q.includes("ma signature") || (q.includes("contrat") && q.includes("gestionnaire"))) {
        const moi = DONNEES.contrats.filter(c => !c.sigGest);
        return moi.length
            ? `Contrats qui attendent **votre signature** :\n${liste(moi.map(c => c.etudiant))}`
            : "Vous n'avez aucun contrat à signer.";
    }

    // STAGE TROUVÉ
    if (q.includes("trouve") && q.includes("stage")) {
        const trouves = DONNEES.etudiants.filter(e =>
            ["STAGE_TROUVE","CONTRAT_CREE"].includes(e.statut));
        const pct = Math.round(trouves.length / DONNEES.etudiants.length * 100);
        return `**${trouves.length} étudiants** ont trouvé un stage (${pct} %) :\n${liste(trouves.map(e => e.nom))}`;
    }
    if (q.includes("recherche") || (q.includes("pas") && q.includes("stage"))) {
        const rech = DONNEES.etudiants.filter(e => e.statut === "EN_RECHERCHE");
        return `**${rech.length} étudiants** encore en recherche :\n${liste(rech.map(e => e.nom))}`;
    }

    // PROFESSEURS
    if (q.includes("professeur") && (q.includes("pas") || q.includes("aucun"))) {
        const sans = DONNEES.etudiants.filter(e => !e.professeur);
        return sans.length
            ? `Étudiants sans professeur assigné :\n${liste(sans.map(e => e.nom))}`
            : "Tous les étudiants ont un professeur.";
    }
    if (q.includes("professeur")) {
        const par = {};
        DONNEES.etudiants.forEach(e => {
            const p = e.professeur || "Non assigné";
            par[p] = (par[p] || []).concat(e.nom);
        });
        return `Répartition par professeur :\n${Object.entries(par)
            .map(([p, es]) => `• ${p} : ${es.join(", ")}`).join("\n")}`;
    }

    // ── AIDE ───────────────────────────────────────────────────────────────
    if (q.includes("aide") || q.includes("help") || q.includes("que") || q.includes("quoi")) {
        return `Je peux répondre à vos questions sur :\n• Les **étudiants inscrits**\n• La **gestion des CV**\n• Les **offres de stage**\n• Les **contrats et signatures**\n• La **recherche de stage**\n• L'**assignation des professeurs**\n\nExemples :\n"Combien d'étudiants sont inscrits ?"\n"Quels CV attendent une validation ?"\n"Quels contrats attendent ma signature ?"`;
    }

    return "Je n'ai pas trouvé de réponse précise. Essayez de reformuler, ou tapez **aide** pour voir ce que je peux faire.";
}