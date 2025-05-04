package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exce��o lan�ada quando dados de usu�rio s�o inv�lidos.
 */

public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException() {
        super("Usu�rio n�o cadastrado.");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
