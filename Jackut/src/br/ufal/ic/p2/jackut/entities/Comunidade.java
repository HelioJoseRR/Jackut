package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Comunidade implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String sessionID;
    private final String donoComunidade;
    private final String nome;
    private final String descricao;
    private List<String> membros;

    public Comunidade(String sessionID, String donoComunidade, String nome, String descricao) {
        this.sessionID = sessionID;
        this.donoComunidade = donoComunidade;
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDonoComunidade() {
        return donoComunidade;
    }

    public List<String> getMembros() {
        return membros;
    }
}
