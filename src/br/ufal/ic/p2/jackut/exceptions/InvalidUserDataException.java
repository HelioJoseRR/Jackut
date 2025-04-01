package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exce��o lan�ada quando h� problemas com os dados de usu�rio.
 */
public class InvalidUserDataException extends JackutException {
    private static final long serialVersionUID = 1L;

    public InvalidUserDataException(String message) {
        super(message);
    }
}