export const validateOfferForm = ({ title, domain, salary, description, startDate, endDate, displayEndDate, file, requireFile = false }) => {
    const today = new Date().toISOString().split("T")[0];
    const errors = {};

    if (!title) errors.title = "add_offer.errors.title_required";
    if (!domain) errors.domain = "add_offer.errors.domain_required";
    if (!description) errors.description = "add_offer.errors.description_required";

    if (!salary) errors.salary = "add_offer.errors.salary_required";
    else if (Number(salary) < 0) errors.salary = "add_offer.errors.salary_negative";

    if (!startDate) errors.startDate = "add_offer.errors.startDate_required";
    if (!endDate) errors.endDate = "add_offer.errors.endDate_required";

    if (startDate && endDate && endDate <= startDate) {
        errors.endDate = "add_offer.errors.endDate_invalid";
    }

    if (!displayEndDate) {
        errors.displayEndDate = "add_offer.errors.display_end_date_required";
    }
    if (displayEndDate && startDate && displayEndDate > startDate) {
        errors.displayEndDate = "add_offer.errors.display_end_date_after_start_date";
    }

    if (displayEndDate && displayEndDate < today) {
        errors.displayEndDate = "add_offer.errors.display_end_date_before_today";
    }

    if (startDate && startDate < today) {
        errors.startDate = "add_offer.errors.start_date_before_today";
    }

    if (requireFile && !file) {
        errors.file = "add_offer.errors.file_required";
    }

    return errors;
};