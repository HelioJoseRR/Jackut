package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Representa uma comunidade no sistema Jackut.
 * Uma comunidade possui um nome, descri��o e um conjunto de membros.
 * O primeiro membro a ser adicionado torna-se automaticamente o dono da comunidade.
 */
public class Comunidade implements Serializable {
    /** ID de serializa��o da classe */
    @Serial
    private static final long serialVersionUID = 1L;

    /** ID da sess�o do criador da comunidade */
    private final String sessionID;

    /** Nome da comunidade */
    private final String nome;

    /** Descri��o da comunidade */
    private final String descricao;

    /** Login do dono da comunidade */
    private String dono;

    /** Lista de membros da comunidade */
    private Vector<String> membros;

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
        this.membros = new Vector<>();
        this.dono = "";
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
            if (membros.isEmpty()) {
                dono = login;
            }
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
    public Vector<String> getMembrosList() {
        return membros;
    }

    /**
     * Obt�m o dono da comunidade.
     *
     * @return Login do dono da comunidade
     */
    public String getDono() {
        return dono;
    }
}