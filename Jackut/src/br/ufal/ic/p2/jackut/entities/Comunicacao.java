package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;

public class Comunicacao implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String remetente;
    private final String destinatario;
    private final String conteudo;
    private final String tipo;

    public Comunicacao(String remetente, String destinatario, String conteudo, String tipo) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.conteudo = conteudo;
        this.tipo = tipo;
    }

    public String getRemetente() {
        return remetente;
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getTipo() {
        return tipo;
    }
}
