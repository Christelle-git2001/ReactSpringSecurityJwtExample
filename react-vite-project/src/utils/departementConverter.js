export const DEPARTMENT_MAP = {
    "Techniques de l'informatique": "INFORMATIQUE",
    "Techniques de la gestion": "GESTION",
    "Techniques de travail social": "TRAVAIL_SOCIAL",
    "Techniques d'éducation à l'enfance": "EDUCATION_ENFANCE",
    "Techniques de soins infirmiers": "SOINS_INFIRMIERS",
    "Technologie du génie civil": "GENIE_CIVIL",
    "Technologie du génie électrique": "GENIE_ELECTRIQUE",
    "Technologie du génie physique": "GENIE_PHYSIQUE",
    "Technologie de l'architecture": "ARCHITECTURE",
    "Technologie de l'estimation et de l'évaluation en bâtiment": "ESTIMATION_EVALUATION",
};

export const getDepartmentKey = (deptValue) => {
    if (!deptValue) return "";
    return DEPARTMENT_MAP[deptValue] || deptValue;
};