export const validateEntrevueForm = ({ selectedCandidature, date, heure, format }) => {
    const now = new Date();
    const today = [
        now.getFullYear(),
        String(now.getMonth() + 1).padStart(2, "0"),
        String(now.getDate()).padStart(2, "0"),
    ].join("-");

    const errors = {};

    if (!selectedCandidature) {
        errors.candidature = "entrevue.errors.select_candidature";
    }

    if (!format) {
        errors.format = "entrevue.errors.select_format";
    }

    if (!date) {
        errors.date = "entrevue.errors.select_date";
    } else if (date < today) {
        errors.date = "entrevue.errors.date_before_today";
    }

    if (!heure) {
        errors.heure = "entrevue.errors.select_time";
    } else if (date === today) {
        const [currentHours, currentMinutes] = [now.getHours(), now.getMinutes()];
        const [selectedHours, selectedMinutes] = heure.split(":").map(Number);

        const currentTimeTotal = currentHours * 60 + currentMinutes;
        const selectedTimeTotal = selectedHours * 60 + selectedMinutes;

        if (selectedTimeTotal <= currentTimeTotal) {
            errors.heure = "entrevue.errors.time_in_past";
        }
    }

    return errors;
};