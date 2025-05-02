package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.ProfileAttributeException;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A classe Usuario representa um usu�rio no sistema Jackut.
 */
public class Usuario implements Serializable {
    private String login;
    private String senha;
    private String nome;
    private Map<String, String> atributos;
    private Set<String> convitesAmizade;
    private Queue<Recado> recados;
    private List<String> comunidadesCadastradas;
    private Queue<Mensagem> mensagens;
    private Map<String, Set<String>> relacionamentos;
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Usuario.
     */
    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.atributos = new HashMap<>();
        this.convitesAmizade = new LinkedHashSet<>();
        this.recados = new LinkedList<>();
        this.comunidadesCadastradas = new ArrayList<>();
        this.mensagens = new LinkedList<>();
        this.relacionamentos = new HashMap<>();
    }

    /**
     * Verifica se a senha fornecida � v�lida.
     */
    public boolean isPasswordValid(String password) {
        return this.senha.equals(password);
    }

    /**
     * Obt�m o login do usu�rio.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Obt�m o nome do usu�rio.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define um atributo do usu�rio.
     */
    public void setAtributo(String atributo, String valor) {
        this.atributos.put(atributo, valor);
    }

    /**
     * Obt�m o valor de um atributo do usu�rio.
     */
    public String getAtributo(String atributo) {
        if (!this.atributos.containsKey(atributo)) {
            throw new ProfileAttributeException("Atributo n�o preenchido.");
        }
        return this.atributos.get(atributo);
    }

    /**
     * Obt�m os convites de amizade do usu�rio.
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
     * Obt�m a lista de amigos do usu�rio.
     */
    public Set<String> getAmigos() {
        this.relacionamentos.computeIfAbsent("amigo", k -> new LinkedHashSet<>());
        return this.relacionamentos.get("amigo");
    }

    /**
     * Adiciona um amigo ao usu�rio.
     */
    public void adicionarAmigo(String amigo) {
        this.relacionamentos.computeIfAbsent("amigo", k -> new LinkedHashSet<>());

        this.relacionamentos.get("amigo").add(amigo);
    }

    /**
     * Formata a lista de amigos para exibi��o.
     */
    public String getAmigosFormatado() {
        String amigosStr = String.join(",", this.relacionamentos.get("amigo"));
        return "{" + amigosStr + "}";
    }

    /**
     * Adiciona um recado ao usu�rio.
     *
     * @param remetente O login do usu�rio que enviou o recado
     * @param conteudo O conte�do do recado
     */
    public void adicionarRecado(String remetente, String conteudo) {
        Recado recado = new Recado(remetente, this.login, conteudo);
        recados.add(recado);
    }

    /**
     * Obt�m a fila de recados do usu�rio.
     */
    public Queue<Recado> getRecados() {
        return this.recados;
    }

    /**
     * Adiciona uma comunidade ao usu�rio.
     */
    public void addComunidade(String comunidade) {
        this.comunidadesCadastradas.add(comunidade);
    }

    /**
     * Obt�m as comunidades cadastradas do usu�rio, formatadas.
     */
    public String getComunidadesCadastradas() {
        return this.comunidadesCadastradas.stream()
                .collect(Collectors.joining(",", "{", "}"));
    }

    /**
     * Obt�m as mensagens do usu�rio.
     */
    public Queue<Mensagem> getMensagens() {
        return this.mensagens;
    }

    /**
     * Adiciona uma mensagem ao usu�rio.
     *
     * @param mensagem A mensagem a ser adicionada
     */
    public void adicionarMensagem(Mensagem mensagem) {
        this.mensagens.add(mensagem);
    }

    public Set<String> getIdolos() {
        this.relacionamentos.computeIfAbsent("fa", k -> new LinkedHashSet<>());

        return this.relacionamentos.get("fa");
    }

    public void adicionarIdolo(String idolo){
        this.relacionamentos.computeIfAbsent("fa", k -> new LinkedHashSet<>());

        this.relacionamentos.get("fa").add(idolo);
    }

    public boolean ehPaquera(String paquera){
        this.relacionamentos.computeIfAbsent("paquera", k -> new LinkedHashSet<>());

        return this.relacionamentos.get("paquera").contains(paquera);
    }

    public void adicionarPaquera(String paquera){
        this.relacionamentos.computeIfAbsent("paquera", k -> new LinkedHashSet<>());
        this.relacionamentos.get("paquera").add(paquera);
    }

    public String getPaqueras() {
        this.relacionamentos.computeIfAbsent("paquera", k -> new LinkedHashSet<>());
        String paquerasStr = String.join(",", this.relacionamentos.get("paquera"));
        return "{" + paquerasStr + "}";
    }
}