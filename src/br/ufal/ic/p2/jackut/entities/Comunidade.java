package br.ufal.ic.p2.jackut.entities;

import java.util.Vector;
import java.io.Serializable;
import java.util.stream.Collectors;

public class Comunidade implements Serializable {
    private String sessionID;
    private String nome;
    private String descricao;
    private Vector<String> membros;
    private static final long serialVersionUID = 1L;

    public Comunidade(String sessionID, String nome, String descricao) {
        this.sessionID = sessionID;
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new Vector<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void addMembro(String nome){
        if(!membros.contains(nome)){
            membros.add(nome);
        }
    }

    public String getMembros(){
        return membros.stream().collect(Collectors.joining(",", "{", "}"));
    }
}
