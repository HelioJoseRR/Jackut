package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;

/**
 * Representa um recado enviado entre usu�rios no sistema Jackut.
 * Um recado possui um remetente, um destinat�rio e um conte�do.
 */
public class Recado implements Serializable {
    /** ID de serializa��o da classe */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Login do remetente do recado */
    private final String remetente;

    /** Login do destinat�rio do recado */
    private final String destinatario;

    /** Conte�do do recado */
    private final String conteudo;

    /**
     * Cria um novo recado.
     *
     * @param remetente    Login do usu�rio que enviou o recado
     * @param destinatario Login do usu�rio que recebeu o recado
     * @param conteudo     Conte�do do recado
     */
    public Recado(String remetente, String destinatario, String conteudo) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.conteudo = conteudo;
    }

    /**
     * Obt�m o remetente do recado.
     *
     * @return Login do usu�rio que enviou o recado
     */
    public String getRemetente() {
        return remetente;
    }

    /**
     * Obt�m o destinat�rio do recado.
     *
     * @return Login do usu�rio que recebeu o recado
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * Obt�m o conte�do do recado.
     *
     * @return Conte�do do recado
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * Retorna uma representa��o em string do recado.
     *
     * @return String no formato "remetente: conte�do"
     */
    @Override
    public String toString() {
        return remetente + ": " + conteudo;
    }
}