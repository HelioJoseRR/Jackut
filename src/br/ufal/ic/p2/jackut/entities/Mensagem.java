package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;

/**
 * Representa uma mensagem enviada em uma comunidade no sistema Jackut.
 * Uma mensagem possui um remetente, conteúdo e está associada a uma comunidade.
 */
public class Mensagem implements Serializable {
    /** ID de serialização da classe */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Login do remetente da mensagem */
    private final String remetente;

    /** Conteúdo da mensagem */
    private final String conteudo;

    /** Nome da comunidade onde a mensagem foi enviada */
    private final String comunidade;

    /**
     * Cria uma nova mensagem.
     *
     * @param remetente  Login do usuário que enviou a mensagem
     * @param conteudo   Conteúdo da mensagem
     * @param comunidade Nome da comunidade onde a mensagem foi enviada
     */
    public Mensagem(String remetente, String conteudo, String comunidade) {
        this.remetente = remetente;
        this.conteudo = conteudo;
        this.comunidade = comunidade;
    }

    /**
     * Obtém o remetente da mensagem.
     *
     * @return Login do usuário que enviou a mensagem
     */
    public String getRemetente() {
        return remetente;
    }

    /**
     * Obtém o conteúdo da mensagem.
     *
     * @return Conteúdo da mensagem
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * Obtém o nome da comunidade onde a mensagem foi enviada.
     *
     * @return Nome da comunidade
     */
    public String getComunidade() {
        return comunidade;
    }

    /**
     * Retorna uma representação em string da mensagem.
     *
     * @return Conteúdo da mensagem
     */
    @Override
    public String toString() {
        return conteudo;
    }
}