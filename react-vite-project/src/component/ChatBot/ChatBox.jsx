import React, { useState, useRef, useEffect } from "react";
import { FaRobot, FaTimes, FaCommentDots, FaPaperPlane } from "react-icons/fa";
import { getMockReponse } from "./chatBotMock.js";

// Rend le texte du bot : gras (**...**) et sauts de ligne
function TexteBot({ texte }) {
    const lignes = texte.split("\n");
    return (
        <span>
            {lignes.map((ligne, i) => {
                const parties = ligne.split(/\*\*(.*?)\*\*/g);
                return (
                    <React.Fragment key={i}>
                        {parties.map((p, j) =>
                            j % 2 === 1
                                ? <strong key={j}>{p}</strong>
                                : <React.Fragment key={j}>{p}</React.Fragment>
                        )}
                        {i < lignes.length - 1 && <br />}
                    </React.Fragment>
                );
            })}
        </span>
    );
}

const SUGGESTIONS = [
    "Combien d'étudiants sont inscrits ?",
    "Quels CV attendent une validation ?",
    "Quels contrats attendent ma signature ?",
    "Combien ont trouvé un stage ?",
];

function ChatBot() {
    const [ouvert, setOuvert] = useState(false);
    const [messages, setMessages] = useState([
        {
            role: "bot",
            texte: "Bonjour ! Je suis votre assistant de gestion de stages.\nPosez-moi une question ou choisissez une suggestion ci-dessous.",
        },
    ]);
    const [saisie, setSaisie] = useState("");
    const [chargement, setChargement] = useState(false);
    const [suggestionsMontrees, setSuggestionsMontrees] = useState(true);

    const messagesRef = useRef(null);
    const inputRef = useRef(null);

    // Scroll au bas à chaque nouveau message
    useEffect(() => {
        if (messagesRef.current)
            messagesRef.current.scrollTop = messagesRef.current.scrollHeight;
    }, [messages, chargement]);

    // Focus sur l'input à l'ouverture
    useEffect(() => {
        if (ouvert) setTimeout(() => inputRef.current?.focus(), 150);
    }, [ouvert]);

    const envoyer = async (texteForce) => {
        const question = (texteForce ?? saisie).trim();
        if (!question || chargement) return;

        setSaisie("");
        setSuggestionsMontrees(false);
        setMessages(prev => [...prev, { role: "user", texte: question }]);
        setChargement(true);

        const reponse = getMockReponse(question);
        setMessages(prev => [...prev, { role: "bot", texte: reponse }]);
        setChargement(false);
    };

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
                className="fixed bottom-6 right-6 z-50 orgin-bottom-right w-14 h-14 rounded-full bg-[#043462] text-white shadow-xl
                           flex items-center justify-center hover:bg-[#0a4a80]
                           transition-all duration-200 hover:scale-110 active:scale-95"
                aria-label={ouvert ? "Fermer l'assistant" : "Ouvrir l'assistant"}
            >
                {ouvert
                    ? <FaTimes size={20} />
                    : <FaCommentDots size={22} />
                }
            </button>

            {/* Fenêtre chat*/}
            <div className={`fixed bottom-24 right-6 z-50 w-80 bg-white rounded-2xl shadow-2xl
                             border border-gray-200 flex flex-col overflow-hidden
                             transition-all duration-300 origin-bottom-left
                             ${ouvert ? "scale-100 opacity-100 pointer-events-auto" : "scale-90 opacity-0 pointer-events-none"}`}
                 style={{ height: 460 }}>

                {/* En-tête */}
                <div className="bg-[#043462] px-4 py-3 flex items-center gap-3 shrink-0">
                    <div className="w-9 h-9 rounded-full bg-[#00CCCB] flex items-center justify-center shrink-0">
                        <FaRobot size={16} className="text-[#043462]" />
                    </div>
                    <div className="min-w-0">
                        <p className="text-white font-semibold text-sm leading-tight">Assistant IKey</p>
                        <p className="text-[#99F0EF] text-xs">Gestionnaire de stages</p>
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
                <div ref={messagesRef}
                     className="flex-1 overflow-y-auto px-3 py-3 space-y-3 bg-gray-50">

                    {messages.map((msg, i) => (
                        <div key={i} className={`flex ${msg.role === "user" ? "justify-end" : "justify-start"}`}>
                            {msg.role === "bot" && (
                                <div className="w-6 h-6 rounded-full bg-[#00CCCB] flex items-center justify-center shrink-0 mt-1 mr-2">
                                    <FaRobot size={11} className="text-[#043462]" />
                                </div>
                            )}
                            <div className={`max-w-[78%] rounded-2xl px-3 py-2 text-xs leading-relaxed ${
                                msg.role === "user"
                                    ? "bg-[#043462] text-white rounded-br-sm"
                                    : "bg-white text-gray-800 border border-gray-200 rounded-bl-sm shadow-sm"
                            }`}>
                                <TexteBot texte={msg.texte} />
                            </div>
                        </div>
                    ))}

                    {/* Suggestions rapides */}
                    {suggestionsMontrees && !chargement && (
                        <div className="pt-1 space-y-1.5">
                            {SUGGESTIONS.map((s, i) => (
                                <button key={i}
                                        onClick={() => envoyer(s)}
                                        className="w-full text-left text-xs px-3 py-1.5 rounded-xl
                                                   border border-[#043462]/20 text-[#043462] bg-white
                                                   hover:bg-[#043462] hover:text-white
                                                   transition-colors duration-150">
                                    {s}
                                </button>
                            ))}
                        </div>
                    )}
                </div>

                {/* Saisie */}
                <div className="p-3 border-t border-gray-100 bg-white flex gap-2 shrink-0">
                    <textarea
                        ref={inputRef}
                        rows={1}
                        value={saisie}
                        onChange={e => setSaisie(e.target.value)}
                        onKeyDown={handleKeyDown}
                        placeholder="Posez votre question…"
                        disabled={chargement}
                        className="flex-1 resize-none rounded-xl border border-gray-200 px-3 py-2
                                   text-xs focus:outline-none focus:border-[#043462]
                                   disabled:opacity-50 transition-colors"
                    />
                    <button
                        onClick={() => envoyer()}
                        disabled={!saisie.trim() || chargement}
                        className="w-9 h-9 shrink-0 rounded-xl bg-[#043462] text-white
                                   flex items-center justify-center
                                   hover:bg-[#0a4a80] disabled:opacity-40 disabled:cursor-not-allowed
                                   transition-colors"
                        aria-label="Envoyer"
                    >
                        <FaPaperPlane size={13} />
                    </button>
                </div>
            </div>
        </>
    );
}

export default ChatBot;