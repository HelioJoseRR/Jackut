package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A classe Recado representa um recado enviado entre usuários no sistema Jackut.
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
     * @param remetente    O login do usuário que enviou o recado
     * @param destinatario O login do usuário que recebeu o recado
     * @param conteudo     O conteúdo do recado
     */
    public Recado(String remetente, String destinatario, String conteudo) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.conteudo = conteudo;
    }

    /**
     * Obtém o remetente do recado.
     *
     * @return O login do usuário que enviou o recado
     */
    public String getRemetente() {
        return remetente;
    }

    /**
     * Obtém o destinatário do recado.
     *
     * @return O login do usuário que recebeu o recado
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * Obtém o conteúdo do recado.
     *
     * @return O conteúdo do recado
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * Retorna uma representação em string do recado.
     *
     * @return Uma string formatada com as informações do recado
     */
    @Override
    public String toString() {
        return remetente + ": " + conteudo;
    }
}