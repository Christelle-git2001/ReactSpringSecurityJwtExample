import { fetchJson } from "./http.jsx";

export async function poserQuestionChatbot(question) {
    return fetchJson("/chatbot/questions", {
        method: "POST",
        body: JSON.stringify({ question })
    });
}
