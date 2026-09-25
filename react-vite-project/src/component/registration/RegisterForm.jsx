import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import "../../css/AddEtudiant.css";

function RegisterForm({
                          translationPrefix,
                          fields = [],
                          onAdd,
                          error,
                          message
                      }) {
    const { t } = useTranslation();

    const handleSubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const data = {};

        fields.forEach((field) => {
            data[field.name] = formData.get(field.name);
        });

        const success = await onAdd(data);
        if (success) e.target.reset();
    };

    const renderInput = (field) => {
        const commonProps = {
            name: field.name,
            required: field.required !== false,
            className: "add-etudiant-input",
            pattern: field.pattern,
            minLength: field.minLength,
            maxLength: field.maxLength,
        };

        if (field.type === "select") {
            return (
                <select {...commonProps}>
                    <option value="">{t(`${translationPrefix}.${field.placeholderKey}`)}</option>
                    {field.options?.map((option) => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>
            );
        }

        return <input type={field.type || "text"} {...commonProps} />;
    };

    return (
        <div className="add-etudiant-page">
            <div className="add-etudiant-page-header">
                <h1 className="add-etudiant-title">{t(`${translationPrefix}.page_title`)}</h1>
                <p className="add-etudiant-subtitle">{t(`${translationPrefix}.page_subtitle`)}</p>
                <span className="add-etudiant-subtitle-line" />
            </div>

            <form onSubmit={handleSubmit} className="add-etudiant-form">
                <div className="add-etudiant-fields">
                    {fields.map((field) => (
                        <div
                            key={field.name}
                            className={field.fullWidth ? "" : "add-etudiant-row-item"}
                        >
                            <label className="add-etudiant-label">
                                {t(`${translationPrefix}.${field.labelKey}`)}
                            </label>
                            {field.loading ? (
                                <p>{t(`${translationPrefix}.${field.loadingKey}`)}</p>
                            ) : (
                                renderInput(field)
                            )}
                        </div>
                    ))}
                </div>

                {error && <p className="error-message">{t(error)}</p>}
                {message && <p className="success-message">{t(message)}</p>}

                <input
                    type="submit"
                    value={t(`${translationPrefix}.submit`)}
                    className="primary-submit"
                />

                <p className="add-etudiant-login-text">
                    {t(`${translationPrefix}.already_registered`)}{" "}
                    <Link to="/login" className="add-etudiant-login-link">
                        {t(`${translationPrefix}.login`)}
                    </Link>
                </p>
            </form>
        </div>
    );
}

export default RegisterForm;