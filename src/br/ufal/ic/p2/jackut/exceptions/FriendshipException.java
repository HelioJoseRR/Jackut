package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exce��o lan�ada quando h� problemas com relacionamentos de amizade.
 */
public class FriendshipException extends JackutException {
    private static final long serialVersionUID = 1L;

    public FriendshipException(String message) {
        super(message);
    }
}
