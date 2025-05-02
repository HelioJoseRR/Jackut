package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A classe Recado representa um recado enviado entre usu�rios no sistema Jackut.
 */
public class Recado implements Serializable {
    private String remetente;
    private String destinatario;
    private String conteudo;
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Recado.
     *
     * @param remetente    O login do usu�rio que enviou o recado
     * @param destinatario O login do usu�rio que recebeu o recado
     * @param conteudo     O conte�do do recado
     */
    public Recado(String remetente, String destinatario, String conteudo) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.conteudo = conteudo;
    }

    /**
     * Obt�m o remetente do recado.
     *
     * @return O login do usu�rio que enviou o recado
     */
    public String getRemetente() {
        return remetente;
    }

    /**
     * Obt�m o destinat�rio do recado.
     *
     * @return O login do usu�rio que recebeu o recado
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * Obt�m o conte�do do recado.
     *
     * @return O conte�do do recado
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * Retorna uma representa��o em string do recado.
     *
     * @return Uma string formatada com as informa��es do recado
     */
    @Override
    public String toString() {
        return remetente + ": " + conteudo;
    }
}