package br.ufal.ic.p2.jackut.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A classe Mensagem representa uma mensagem enviada em uma comunidade no sistema Jackut.
 */
public class Mensagem implements Serializable {
    private String remetente;
    private String conteudo;
    private String comunidade;
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Mensagem.
     *
     * @param remetente  O login do usu�rio que enviou a mensagem
     * @param conteudo   O conte�do da mensagem
     * @param comunidade O nome da comunidade onde a mensagem foi enviada
     */
    public Mensagem(String remetente, String conteudo, String comunidade) {
        this.remetente = remetente;
        this.conteudo = conteudo;
        this.comunidade = comunidade;
    }

    /**
     * Obt�m o remetente da mensagem.
     *
     * @return O login do usu�rio que enviou a mensagem
     */
    public String getRemetente() {
        return remetente;
    }

    /**
     * Obt�m o conte�do da mensagem.
     *
     * @return O conte�do da mensagem
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * Obt�m o nome da comunidade onde a mensagem foi enviada.
     *
     * @return O nome da comunidade
     */
    public String getComunidade() {
        return comunidade;
    }

    /**
     * Retorna uma representa��o em string da mensagem.
     *
     * @return Uma string formatada com as informa��es da mensagem
     */
    @Override
    public String toString() {
        return conteudo;
    }
}