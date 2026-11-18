package com.lacouf.rsbjwt.exception.chatbot;

public class AccesChatbotRefuseException extends Exception {
    public AccesChatbotRefuseException() { super("chatbot.errors.access_denied"); }
}