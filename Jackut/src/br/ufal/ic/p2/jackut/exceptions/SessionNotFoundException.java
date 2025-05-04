package br.ufal.ic.p2.jackut.exceptions;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException() {
        super("Sess�o inv�lida ou expirada.");
    }

    public SessionNotFoundException(String message) {
        super(message);
    }
}