export const COMMON_USER_FIELDS = [
    { name: "firstName", labelKey: "first_name" },
    { name: "lastName", labelKey: "last_name" },
    {
        name: "email",
        labelKey: "email",
        type: "email",
        pattern: "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$",
        fullWidth: true
    },
    {
        name: "phone",
        labelKey: "phone",
        type: "tel",
        pattern: "[0-9]{3}-?[0-9]{3}-?[0-9]{4}",
        maxLength: "12"
    },
    { name: "password", labelKey: "password", type: "password", fullWidth: true },
    { name: "passwordConfirmation", labelKey: "confirm_password", type: "password", fullWidth: true }, // 👈 Changé de "confirmPassword" à "passwordConfirmation"
];