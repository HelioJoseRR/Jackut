package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exce��o lan�ada quando h� problemas com credenciais de login.
 */
public class AuthenticationException extends JackutException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException() {
        super("Login ou senha inv�lidos.");
    }

    public AuthenticationException(String message) {
        super(message);
    }
}
