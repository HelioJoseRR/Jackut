package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.io.Serializable;
import java.util.stream.Collectors;

/**
 * A classe Comunidade representa uma comunidade no sistema Jackut.
 */
public class Comunidade implements Serializable {
    private String sessionID;
    private String nome;
    private String descricao;
    private Vector<String> membros;
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Comunidade.
     */
    public Comunidade(String sessionID, String nome, String descricao) {
        this.sessionID = sessionID;
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new Vector<>();
    }

    /**
     * Obtém o nome da comunidade.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Obtém a descrição da comunidade.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Obtém o ID da sessão do criador da comunidade.
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Adiciona um membro à comunidade.
     */
    public void addMembro(String nome) {
        if (!membros.contains(nome)) {
            membros.add(nome);
        }
    }

    /**
     * Verifica se um usuário é membro da comunidade.
     *
     * @param login O login do usuário
     * @return true se o usuário é membro, false caso contrário
     */
    public boolean isMembro(String login) {
        return membros.contains(login);
    }

    /**
     * Obtém os membros da comunidade, formatados.
     */
    public String getMembros() {
        return membros.stream().collect(Collectors.joining(",", "{", "}"));
    }


}