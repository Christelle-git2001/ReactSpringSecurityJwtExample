
import React, { useState, useRef, useEffect } from "react";
import {
    FaRobot,
    FaTimes,
    FaCommentDots,
    FaPaperPlane
} from "react-icons/fa";

import { poserQuestionChatbot } from "../../api/chatbot.jsx";

// Affichage du texte du bot : gras et sauts de ligne
function TexteBot({ texte }) {
    const lignes = texte.split("\n");

    return (
        <span>
            {lignes.map((ligne, i) => {
                const parties = ligne.split(/\*\*(.*?)\*\*/g);

                return (
                    <React.Fragment key={i}>
                        {parties.map((partie, j) =>
                            j % 2 === 1
                                ? <strong key={j}>{partie}</strong>
                                : <React.Fragment key={j}>{partie}</React.Fragment>
                        )}
                        {i < lignes.length - 1 && <br />}
                    </React.Fragment>
                );
            })}
        </span>
    );
}

// Suggestions adaptées aux étudiants
const SUGGESTIONS = [
    "Est-ce que mon CV a été approuvé ?",
    "Quelles offres de stage sont disponibles pour moi ?",
    "Ai-je reçu de nouvelles offres ?",
    "Qui est mon professeur assigné ?"
];

function ChatBotEtudiant() {
    const [ouvert, setOuvert] = useState(false);

    const [messages, setMessages] = useState([
        {
            role: "bot",
            texte: "Bonjour ! Je suis votre assistant étudiant. 👋\nPosez-moi une question sur votre CV, vos offres de stage ou vos candidatures."
        }
    ]);

    const [saisie, setSaisie] = useState("");
    const [chargement, setChargement] = useState(false);
    const [suggestionsMontrees, setSuggestionsMontrees] = useState(true);

    const messagesRef = useRef(null);
    const inputRef = useRef(null);

    // Défilement automatique vers le dernier message
    useEffect(() => {
        if (messagesRef.current) {
            messagesRef.current.scrollTop =
                messagesRef.current.scrollHeight;
        }
    }, [messages, chargement]);

    // Focus sur le champ de saisie à l'ouverture
    useEffect(() => {
        if (ouvert) {
            const timeout = setTimeout(
                () => inputRef.current?.focus(),
                150
            );

            return () => clearTimeout(timeout);
        }
    }, [ouvert]);

    // Envoyer une question au backend
    const envoyer = async (texteForce) => {
        const question = (texteForce ?? saisie).trim();

        if (!question || chargement) return;

        setSaisie("");
        setSuggestionsMontrees(false);

        setMessages(prev => [
            ...prev,
            { role: "user", texte: question }
        ]);

        setChargement(true);

        try {
            const resultat = await poserQuestionChatbot(question);

            setMessages(prev => [
                ...prev,
                {
                    role: "bot",
                    texte: resultat.reponse
                }
            ]);
        } catch (error) {
            console.error("Erreur chatbot :", error);

            setMessages(prev => [
                ...prev,
                {
                    role: "bot",
                    texte: error.message ||
                        "Une erreur est survenue. Veuillez réessayer."
                }
            ]);
        } finally {
            setChargement(false);
        }
    };

    // Envoyer avec la touche Entrée
    const handleKeyDown = (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            envoyer();
        }
    };

    return (
        <>
            {/* Bulle flottante */}
            <button
                onClick={() => setOuvert(o => !o)}
                className="fixed bottom-6 right-6 z-50 origin-bottom-right
                           w-14 h-14 rounded-full bg-[#043462] text-white
                           shadow-xl flex items-center justify-center
                           hover:bg-[#0a4a80] transition-all duration-200
                           hover:scale-110 active:scale-95"
                aria-label={ouvert ? "Fermer l'assistant" : "Ouvrir l'assistant"}
            >
                {ouvert
                    ? <FaTimes size={20} />
                    : <FaCommentDots size={22} />
                }
            </button>

            {/* Fenêtre du chatbot */}
            <div
                className={`fixed bottom-24 right-6 z-50 w-80
                            bg-white rounded-2xl shadow-2xl
                            border border-gray-200 flex flex-col
                            overflow-hidden transition-all duration-300
                            origin-bottom-right
                            ${ouvert
                                ? "scale-100 opacity-100 pointer-events-auto"
                                : "scale-90 opacity-0 pointer-events-none"
                            }`}
                style={{ height: 460 }}
            >

                {/* En-tête */}
                <div className="bg-[#043462] px-4 py-3 flex items-center gap-3 shrink-0">
                    <div className="w-9 h-9 rounded-full bg-[#00CCCB]
                                    flex items-center justify-center shrink-0">
                        <FaRobot size={16} className="text-[#043462]" />
                    </div>

                    <div className="min-w-0">
                        <p className="text-white font-semibold text-sm leading-tight">
                            Assistant IKey
                        </p>
                        <p className="text-[#99F0EF] text-xs">
                            Assistant étudiant
                        </p>
                    </div>

                    <button
                        onClick={() => setOuvert(false)}
                        className="ml-auto text-white/60 hover:text-white transition-colors"
                        aria-label="Fermer"
                    >
                        <FaTimes size={14} />
                    </button>
                </div>

                {/* Messages */}
                <div
                    ref={messagesRef}
                    className="flex-1 overflow-y-auto px-3 py-3 space-y-3 bg-gray-50"
                >
                    {messages.map((msg, i) => (
                        <div
                            key={i}
                            className={`flex ${
                                msg.role === "user"
                                    ? "justify-end"
                                    : "justify-start"
                            }`}
                        >
                            {msg.role === "bot" && (
                                <div className="w-6 h-6 rounded-full bg-[#00CCCB]
                                                flex items-center justify-center
                                                shrink-0 mt-1 mr-2">
                                    <FaRobot size={11} className="text-[#043462]" />
                                </div>
                            )}

                            <div
                                className={`max-w-[78%] rounded-2xl px-3 py-2
                                            text-xs leading-relaxed ${
                                    msg.role === "user"
                                        ? "bg-[#043462] text-white rounded-br-sm"
                                        : "bg-white text-gray-800 border border-gray-200 rounded-bl-sm shadow-sm"
                                }`}
                            >
                                <TexteBot texte={msg.texte} />
                            </div>
                        </div>
                    ))}

                    {/* Indicateur de chargement */}
                    {chargement && (
                        <div className="flex justify-start">
                            <div className="w-6 h-6 rounded-full bg-[#00CCCB]
                                            flex items-center justify-center mr-2">
                                <FaRobot size={11} className="text-[#043462]" />
                            </div>

                            <div className="bg-white border border-gray-200
                                            rounded-2xl rounded-bl-sm px-3 py-2
                                            text-xs text-gray-500">
                                L'assistant réfléchit...
                            </div>
                        </div>
                    )}

                    {/* Suggestions rapides */}
                    {suggestionsMontrees && !chargement && (
                        <div className="pt-1 space-y-1.5">
                            {SUGGESTIONS.map((suggestion, i) => (
                                <button
                                    key={i}
                                    onClick={() => envoyer(suggestion)}
                                    className="w-full text-left text-xs px-3 py-1.5
                                               rounded-xl border border-[#043462]/20
                                               text-[#043462] bg-white
                                               hover:bg-[#043462] hover:text-white
                                               transition-colors duration-150"
                                >
                                    {suggestion}
                                </button>
                            ))}
                        </div>
                    )}
                </div>

                {/* Zone de saisie */}
                <div className="p-3 border-t border-gray-100 bg-white
                                flex gap-2 shrink-0">
                    <textarea
                        ref={inputRef}
                        rows={1}
                        value={saisie}
                        onChange={e => setSaisie(e.target.value)}
                        onKeyDown={handleKeyDown}
                        placeholder="Posez votre question..."
                        disabled={chargement}
                        className="flex-1 resize-none rounded-xl border
                                   border-gray-200 px-3 py-2 text-xs
                                   focus:outline-none focus:border-[#043462]
                                   disabled:opacity-50 transition-colors"
                    />

                    <button
                        onClick={() => envoyer()}
                        disabled={!saisie.trim() || chargement}
                        className="w-9 h-9 shrink-0 rounded-xl bg-[#043462]
                                   text-white flex items-center justify-center
                                   hover:bg-[#0a4a80] disabled:opacity-40
                                   disabled:cursor-not-allowed transition-colors"
                        aria-label="Envoyer"
                    >
                        <FaPaperPlane size={13} />
                    </button>
                </div>
            </div>
        </>
    );
}

export default ChatBotEtudiant;
