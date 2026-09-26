import { useState } from "react";
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

    const [formData, setFormData] = useState({});
    const [warnings, setWarnings] = useState({});

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((previous) => ({ ...previous, [name]: value }));
        setWarnings((previous) => ({ ...previous, [name]: "" }));
    };

    const validateForm = () => {
        let isValid = true;
        const newWarnings = {};

        fields.forEach((field) => {
            const value = (formData[field.name] || "").trim();

            if (field.required !== false && !value) {
                newWarnings[field.name] = t(`error.${field.name}_required`, {
                    defaultValue: t("error.required")
                });
                isValid = false;
            }

            else if (field.pattern && value) {
                const regex = new RegExp(field.pattern);
                if (!regex.test(value)) {
                    newWarnings[field.name] = t(`error.${field.name}_invalid`, {
                        defaultValue: t("error.generic")
                    });
                    isValid = false;
                }
            }

            if (field.name === "passwordConfirmation" && value) {
                if (value !== (formData["password"] || "").trim()) {
                    newWarnings[field.name] = t("error.password_mismatch");
                    isValid = false;
                }
            }
        });

        setWarnings(newWarnings);
        return isValid;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) return;

        const success = await onAdd(formData);
        if (success) {
            setFormData({});
            setWarnings({});
        }
    };

    const getFieldLabel = (labelKey) => {
        return t([`${translationPrefix}.${labelKey}`, `register.${labelKey}`]);
    };

    const renderInput = (field) => {
        const placeholderText = field.placeholderKey
            ? t([`${translationPrefix}.${field.placeholderKey}`, `register.${field.placeholderKey}`])
            : "";

        const commonProps = {
            name: field.name,
            value: formData[field.name] || "",
            onChange: handleChange,
            placeholder: placeholderText,
            className: `add-etudiant-input ${warnings[field.name] ? "input-error" : ""}`,
            minLength: field.minLength,
            maxLength: field.maxLength
        };

        if (field.type === "select") {
            return (
                <select {...commonProps}>
                    <option value="">
                        {t(`${translationPrefix}.${field.placeholderKey}`)}
                    </option>
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

            <form onSubmit={handleSubmit} className="add-etudiant-form" noValidate>
                <div className="add-etudiant-fields">
                    {fields.map((field) => (
                        <div
                            key={field.name}
                            className={field.fullWidth ? "" : "add-etudiant-row-item"}
                        >
                            <label className="add-etudiant-label">
                                {getFieldLabel(field.labelKey)}
                            </label>
                            {field.loading ? (
                                <p>{t(`${translationPrefix}.${field.loadingKey}`)}</p>
                            ) : (
                                renderInput(field)
                            )}

                            {warnings[field.name] && (
                                <div className="form-login-error">
                                    {warnings[field.name]}
                                </div>
                            )}
                        </div>
                    ))}
                </div>

                {error && <p className="error-message">{t(error)}</p>}
                {message && <p className="success-message">{t(message)}</p>}

                <input
                    type="submit"
                    value={t([`${translationPrefix}.submit`, "register.submit"])}
                    className="primary-submit"
                />

                <p className="add-etudiant-login-text">
                    {t("register.already_registered")}{" "}
                    <Link to="/login" className="add-etudiant-login-link">
                        {t("register.login")}
                    </Link>
                </p>
            </form>
        </div>
    );
}

export default RegisterForm;