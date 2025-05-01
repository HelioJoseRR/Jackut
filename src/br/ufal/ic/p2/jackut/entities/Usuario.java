package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.ProfileAttributeException;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A classe Usuario representa um usuário no sistema Jackut.
 */
public class Usuario implements Serializable {
    private String login;
    private String senha;
    private String nome;
    private Map<String, String> atributos;
    private Set<String> convitesAmizade;
    private Set<String> amigos;
    private Queue<Recado> recados;
    private List<String> comunidadesCadastradas;
    private Queue<Mensagem> mensagens;
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Usuario.
     */
    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.atributos = new HashMap<>();
        this.amigos = new LinkedHashSet<>();
        this.convitesAmizade = new LinkedHashSet<>();
        this.recados = new LinkedList<>();
        this.comunidadesCadastradas = new ArrayList<>();
        this.mensagens = new LinkedList<>();
    }

    /**
     * Verifica se a senha fornecida é válida.
     */
    public boolean isPasswordValid(String password) {
        return this.senha.equals(password);
    }

    /**
     * Obtém o login do usuário.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Obtém o nome do usuário.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define um atributo do usuário.
     */
    public void setAtributo(String atributo, String valor) {
        this.atributos.put(atributo, valor);
    }

    /**
     * Obtém o valor de um atributo do usuário.
     */
    public String getAtributo(String atributo) {
        if (!this.atributos.containsKey(atributo)) {
            throw new ProfileAttributeException("Atributo não preenchido.");
        }
        return this.atributos.get(atributo);
    }

    /**
     * Obtém os convites de amizade do usuário.
     */
    public Set<String> getConvitesAmizade() {
        return convitesAmizade;
    }

    /**
     * Adiciona um convite de amizade.
     */
    public void adicionarConviteAmizade(String id) {
        this.convitesAmizade.add(id);
    }

    /**
     * Remove um convite de amizade.
     */
    public void removerConviteAmizade(String id) {
        this.convitesAmizade.remove(id);
    }

    /**
     * Obtém a lista de amigos do usuário.
     */
    public Set<String> getAmigos() {
        return amigos;
    }

    /**
     * Adiciona um amigo ao usuário.
     */
    public void adicionarAmigo(String amigo) {
        this.amigos.add(amigo);
    }

    /**
     * Formata a lista de amigos para exibição.
     */
    public String getAmigosFormatado() {
        String amigosStr = String.join(",", amigos);
        return "{" + amigosStr + "}";
    }

    /**
     * Adiciona um recado ao usuário.
     *
     * @param remetente O login do usuário que enviou o recado
     * @param conteudo O conteúdo do recado
     */
    public void adicionarRecado(String remetente, String conteudo) {
        recados.add(new Recado(remetente, this.login, conteudo));
    }

    /**
     * Obtém a fila de recados do usuário.
     */
    public Queue<Recado> getRecados() {
        return this.recados;
    }

    /**
     * Adiciona uma comunidade ao usuário.
     */
    public void addComunidade(String comunidade) {
        this.comunidadesCadastradas.add(comunidade);
    }

    /**
     * Obtém as comunidades cadastradas do usuário, formatadas.
     */
    public String getComunidadesCadastradas() {
        return this.comunidadesCadastradas.stream()
                .collect(Collectors.joining(",", "{", "}"));
    }

    /**
     * Obtém as mensagens do usuário.
     */
    public Queue<Mensagem> getMensagens() {
        return this.mensagens;
    }

    /**
     * Adiciona uma mensagem ao usuário.
     *
     * @param mensagem A mensagem a ser adicionada
     */
    public void adicionarMensagem(Mensagem mensagem) {
        this.mensagens.add(mensagem);
    }
}