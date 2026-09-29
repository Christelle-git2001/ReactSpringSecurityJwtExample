export const COMMON_USER_FIELDS = [
    { name: "firstName", labelKey: "first_name" },
    { name: "lastName", labelKey: "last_name" },
    {
        name: "email",
        labelKey: "email",
        type: "email",
        placeholderKey: "email_placeholder",
        pattern: "^[a-zA-Z0-9]+([._%+-]?[a-zA-Z0-9]+)*@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        fullWidth: true
    },
    {
        name: "phone",
        labelKey: "phone",
        type: "tel",
        placeholderKey: "phone_placeholder",
        pattern: "^[0-9]{3}-[0-9]{3}-[0-9]{4}$",
        maxLength: "12"
    },
    { name: "password", labelKey: "password", type: "password", fullWidth: true },
    { name: "passwordConfirmation", labelKey: "confirm_password", type: "password", fullWidth: true },
];