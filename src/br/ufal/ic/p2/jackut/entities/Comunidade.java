package br.ufal.ic.p2.jackut.entities;

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
    private Queue<Mensagem> mensagens;
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Comunidade.
     */
    public Comunidade(String sessionID, String nome, String descricao) {
        this.sessionID = sessionID;
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new Vector<>();
        this.mensagens = new LinkedList<>();
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

    /**
     * Obtém e remove a primeira mensagem da fila.
     */
    public Mensagem getFirstMensagem() {
        return this.mensagens.poll();
    }

    /**
     * Obtém todas as mensagens da comunidade.
     */
    public Queue<Mensagem> getMensagens() {
        return this.mensagens;
    }

    /**
     * Adiciona uma mensagem à comunidade.
     *
     * @param remetente O login do usuário que enviou a mensagem
     * @param conteudo O conteúdo da mensagem
     */
    public void adicionarMensagem(String remetente, String conteudo) {
        this.mensagens.add(new Mensagem(remetente, conteudo, this.nome));
    }


}