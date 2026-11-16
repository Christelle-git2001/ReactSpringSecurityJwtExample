import React, { useState, useRef, useEffect } from "react";
import { FaRobot, FaTimes, FaCommentDots, FaPaperPlane } from "react-icons/fa";

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

export default function ChatBot({ titre, sousTitre, suggestions, initialMessage, onSend, t }) {
    const [ouvert, setOuvert] = useState(false);
    const [messages, setMessages] = useState([
        { role: "bot", texte: initialMessage },
    ]);
    const [saisie, setSaisie] = useState("");
    const [chargement, setChargement] = useState(false);
    const [suggestionsMontrees, setSuggestionsMontrees] = useState(true);

    const messagesRef = useRef(null);
    const inputRef = useRef(null);

    useEffect(() => {
        if (messagesRef.current) {
            messagesRef.current.scrollTop = messagesRef.current.scrollHeight;
        }
    }, [messages, chargement]);

    useEffect(() => {
        if (ouvert) {
            const timeout = setTimeout(() => inputRef.current?.focus(), 150);
            return () => clearTimeout(timeout);
        }
    }, [ouvert]);

    useEffect(() => {
        setMessages(prev => {
            if (prev.length === 1 && prev[0].role === "bot") {
                return [{ role: "bot", texte: initialMessage }];
            }
            return prev;
        });
    }, [initialMessage]);

    const envoyer = async (texteForce) => {
        const question = (texteForce ?? saisie).trim();
        if (!question || chargement) return;

        setSaisie("");
        setSuggestionsMontrees(false);
        setMessages(prev => [...prev, { role: "user", texte: question }]);
        setChargement(true);

        try {
            const reponse = await onSend(question);
            setMessages(prev => [...prev, { role: "bot", texte: reponse }]);
        } catch (error) {
            console.error("Erreur chatbot :", error);
            setMessages(prev => [
                ...prev,
                { role: "bot", texte: error.message || "Une erreur est survenue. Veuillez réessayer." }
            ]);
        } finally {
            setChargement(false);
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            envoyer();
        }
    };

    return (
        <>
            <button
                onClick={() => setOuvert(o => !o)}
                className="fixed bottom-6 right-6 z-50 origin-bottom-right w-14 h-14 rounded-full bg-[#043462] text-white shadow-xl
                           flex items-center justify-center hover:bg-[#0a4a80]
                           transition-all duration-200 hover:scale-110 active:scale-95"
                aria-label={ouvert ? "Fermer l'assistant" : "Ouvrir l'assistant"}
            >
                {ouvert ? <FaTimes size={20} /> : <FaCommentDots size={22} />}
            </button>

            <div className={`fixed bottom-24 right-6 z-50 w-80 bg-white rounded-2xl shadow-2xl
                             border border-gray-200 flex flex-col overflow-hidden
                             transition-all duration-300 origin-bottom-right
                             ${ouvert ? "scale-100 opacity-100 pointer-events-auto" : "scale-90 opacity-0 pointer-events-none"}`}
                 style={{ height: 460 }}>

                <div className="bg-[#043462] px-4 py-3 flex items-center gap-3 shrink-0">
                    <div className="w-9 h-9 rounded-full bg-[#00CCCB] flex items-center justify-center shrink-0">
                        <FaRobot size={16} className="text-[#043462]" />
                    </div>
                    <div className="min-w-0">
                        <p className="text-white font-semibold text-sm leading-tight">{titre}</p>
                        <p className="text-[#99F0EF] text-xs">{sousTitre}</p>
                    </div>
                    <button
                        onClick={() => setOuvert(false)}
                        className="ml-auto text-white/60 hover:text-white transition-colors"
                        aria-label="Fermer"
                    >
                        <FaTimes size={14} />
                    </button>
                </div>

                <div ref={messagesRef} className="flex-1 overflow-y-auto px-3 py-3 space-y-3 bg-gray-50">
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

                    {chargement && (
                        <div className="flex justify-start">
                            <div className="w-6 h-6 rounded-full bg-[#00CCCB] flex items-center justify-center mr-2">
                                <FaRobot size={11} className="text-[#043462]" />
                            </div>
                            <div className="bg-white border border-gray-200 rounded-2xl rounded-bl-sm px-3 py-2 text-xs text-gray-500">
                                {t ? t("chatbot.thinking") : "L'assistant réfléchit..."}
                            </div>
                        </div>
                    )}

                    {suggestionsMontrees && !chargement && suggestions && suggestions.length > 0 && (
                        <div className="pt-1 space-y-1.5">
                            {suggestions.map((s, i) => (
                                <button key={i}
                                        onClick={() => envoyer(s.query)}
                                        className="w-full text-left text-xs px-3 py-1.5 rounded-xl
                                                   border border-[#043462]/20 text-[#043462] bg-white
                                                   hover:bg-[#043462] hover:text-white
                                                   transition-colors duration-150">
                                    {s.label}
                                </button>
                            ))}
                        </div>
                    )}
                </div>

                <div className="p-3 border-t border-gray-100 bg-white flex gap-2 shrink-0">
                    <textarea
                        ref={inputRef}
                        rows={1}
                        value={saisie}
                        onChange={e => setSaisie(e.target.value)}
                        onKeyDown={handleKeyDown}
                        placeholder={t ? t("chatbot.placeholder") : "Posez votre question..."}
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