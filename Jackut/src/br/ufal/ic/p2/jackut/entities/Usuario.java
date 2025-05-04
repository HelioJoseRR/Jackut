package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Usuario implements Serializable {
    @Serial
    private static long serialVersionUID = 1L;

    private String login;
    private String senha;
    private String nome;
    private Map<String, String> atributos;
    private List<String> comunidadesCadastradas;
    private Relacionamento relacionamentos;

    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.atributos = new HashMap<>();
        this.comunidadesCadastradas = new ArrayList<>();
        this.relacionamentos = new Relacionamento();
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public String getNome() {
        return nome;
    }

    public Map<String, String> getAtributos() {
        return atributos;
    }

    public List<String> getComunidadesCadastradas() {
        return comunidadesCadastradas;
    }

    public Relacionamento getRelacionamentos() {
        return relacionamentos;
    }
}
