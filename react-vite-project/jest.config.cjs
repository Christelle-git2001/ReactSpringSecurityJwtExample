module.exports = {
    testEnvironment: "jsdom",
    setupFilesAfterEnv: ["<rootDir>/src/setupTests.js"],
    transform: {
        "^.+\\.(js|jsx)$": "babel-jest",
    },
    moduleNameMapper: {
        "\\.(css|less|scss|sass)$": "identity-obj-proxy",
    },
    transformIgnorePatterns: [
        "/node_modules/(?!(@?react-i18next|i18next)/)",
    ],
};