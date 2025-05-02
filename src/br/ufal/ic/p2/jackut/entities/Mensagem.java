package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;

/**
 * Representa uma mensagem enviada em uma comunidade no sistema Jackut.
 * Uma mensagem possui um remetente, conte�do e est� associada a uma comunidade.
 */
public class Mensagem implements Serializable {
    /** ID de serializa��o da classe */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Login do remetente da mensagem */
    private final String remetente;

    /** Conte�do da mensagem */
    private final String conteudo;

    /** Nome da comunidade onde a mensagem foi enviada */
    private final String comunidade;

    /**
     * Cria uma nova mensagem.
     *
     * @param remetente  Login do usu�rio que enviou a mensagem
     * @param conteudo   Conte�do da mensagem
     * @param comunidade Nome da comunidade onde a mensagem foi enviada
     */
    public Mensagem(String remetente, String conteudo, String comunidade) {
        this.remetente = remetente;
        this.conteudo = conteudo;
        this.comunidade = comunidade;
    }

    /**
     * Obt�m o remetente da mensagem.
     *
     * @return Login do usu�rio que enviou a mensagem
     */
    public String getRemetente() {
        return remetente;
    }

    /**
     * Obt�m o conte�do da mensagem.
     *
     * @return Conte�do da mensagem
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * Obt�m o nome da comunidade onde a mensagem foi enviada.
     *
     * @return Nome da comunidade
     */
    public String getComunidade() {
        return comunidade;
    }

    /**
     * Retorna uma representa��o em string da mensagem.
     *
     * @return Conte�do da mensagem
     */
    @Override
    public String toString() {
        return conteudo;
    }
}