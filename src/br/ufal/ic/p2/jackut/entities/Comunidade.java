package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Comunidade implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String sessionID;
    private final String nome;
    private final String descricao;
    private List<String> membros;

    /**
     * Cria uma nova comunidade com nome e descri��o especificados.
     *
     * @param sessionID ID da sess�o do criador
     * @param nome Nome da comunidade
     * @param descricao Descri��o da comunidade
     */
    public Comunidade(String sessionID, String nome, String descricao) {
        this.sessionID = sessionID;
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new ArrayList<>();
    }

    /**
     * Obt�m o nome da comunidade.
     *
     * @return Nome da comunidade
     */
    public String getNome() {
        return nome;
    }

    /**
     * Obt�m a descri��o da comunidade.
     *
     * @return Descri��o da comunidade
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Obt�m o ID da sess�o do criador da comunidade.
     *
     * @return ID da sess�o do criador
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Adiciona um membro � comunidade.
     * O primeiro membro adicionado torna-se o dono da comunidade.
     *
     * @param login Login do membro a ser adicionado
     */
    public void addMembro(String login) {
        if (!membros.contains(login)) {
            membros.add(login);
        }
    }

    /**
     * Verifica se um usu�rio � membro da comunidade.
     *
     * @param login Login do usu�rio
     * @return true se o usu�rio � membro, false caso contr�rio
     */
    public boolean isMembro(String login) {
        return membros.contains(login);
    }

    /**
     * Obt�m a lista de membros da comunidade formatada como texto.
     *
     * @return Lista de membros formatada como {membro1,membro2,...}
     */
    public String getMembros() {
        return membros.stream()
                .collect(Collectors.joining(",", "{", "}"));
    }

    /**
     * Obt�m a lista de membros da comunidade.
     *
     * @return Vector contendo os logins dos membros
     */
    public List<String> getMembrosList() {
        return membros;
    }
}