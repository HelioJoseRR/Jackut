package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;

/**
 * Representa um recado enviado entre usuários no sistema Jackut.
 * Um recado possui um remetente, um destinatário e um conteúdo.
 */
public class Recado implements Serializable {
    /** ID de serialização da classe */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Login do remetente do recado */
    private final String remetente;

    /** Login do destinatário do recado */
    private final String destinatario;

    /** Conteúdo do recado */
    private final String conteudo;

    /**
     * Cria um novo recado.
     *
     * @param remetente    Login do usuário que enviou o recado
     * @param destinatario Login do usuário que recebeu o recado
     * @param conteudo     Conteúdo do recado
     */
    public Recado(String remetente, String destinatario, String conteudo) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.conteudo = conteudo;
    }

    /**
     * Obtém o remetente do recado.
     *
     * @return Login do usuário que enviou o recado
     */
    public String getRemetente() {
        return remetente;
    }

    /**
     * Obtém o destinatário do recado.
     *
     * @return Login do usuário que recebeu o recado
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * Obtém o conteúdo do recado.
     *
     * @return Conteúdo do recado
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * Retorna uma representação em string do recado.
     *
     * @return String no formato "remetente: conteúdo"
     */
    @Override
    public String toString() {
        return remetente + ": " + conteudo;
    }
}