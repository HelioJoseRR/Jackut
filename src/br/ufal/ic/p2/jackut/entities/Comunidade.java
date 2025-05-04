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
     * Cria uma nova comunidade com nome e descrição especificados.
     *
     * @param sessionID ID da sessão do criador
     * @param nome Nome da comunidade
     * @param descricao Descrição da comunidade
     */
    public Comunidade(String sessionID, String nome, String descricao) {
        this.sessionID = sessionID;
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new ArrayList<>();
    }

    /**
     * Obtém o nome da comunidade.
     *
     * @return Nome da comunidade
     */
    public String getNome() {
        return nome;
    }

    /**
     * Obtém a descrição da comunidade.
     *
     * @return Descrição da comunidade
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Obtém o ID da sessão do criador da comunidade.
     *
     * @return ID da sessão do criador
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Adiciona um membro à comunidade.
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
     * Verifica se um usuário é membro da comunidade.
     *
     * @param login Login do usuário
     * @return true se o usuário é membro, false caso contrário
     */
    public boolean isMembro(String login) {
        return membros.contains(login);
    }

    /**
     * Obtém a lista de membros da comunidade formatada como texto.
     *
     * @return Lista de membros formatada como {membro1,membro2,...}
     */
    public String getMembros() {
        return membros.stream()
                .collect(Collectors.joining(",", "{", "}"));
    }

    /**
     * Obtém a lista de membros da comunidade.
     *
     * @return Vector contendo os logins dos membros
     */
    public List<String> getMembrosList() {
        return membros;
    }
}