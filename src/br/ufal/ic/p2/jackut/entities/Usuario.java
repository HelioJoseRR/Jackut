package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.ProfileAttributeException;

import java.io.Serializable;
import java.util.*;

/**
 * A classe Usuario representa um usu�rio no sistema Jackut.
 * Implementa a interface Serializable para permitir a serializa��o dos objetos.
 */
public class Usuario implements Serializable {
    private String login;
    private String senha;
    private String nome;
    private Map<String, String> atributos;
    private Set<String> convitesAmizade;
    private Set<String> amigos;
    private Queue<String> recados;
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Usuario.
     *
     * @param login O login do usu�rio.
     * @param senha A senha do usu�rio.
     * @param nome O nome do usu�rio.
     */
    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.atributos = new HashMap<>();
        this.amigos = new LinkedHashSet<>();
        this.convitesAmizade = new LinkedHashSet<>();
        this.recados = new LinkedList<>();
    }

    /**
     * Obt�m o login do usu�rio.
     *
     * @return O login do usu�rio.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Obt�m o nome do usu�rio.
     *
     * @return O nome do usu�rio.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define um atributo do usu�rio.
     *
     * @param atributo O nome do atributo.
     * @param valor O valor do atributo.
     */
    public void setAtributo(String atributo, String valor) {
        this.atributos.put(atributo, valor);
    }

    /**
     * Obt�m o valor de um atributo do usu�rio.
     *
     * @param atributo O nome do atributo.
     * @return O valor do atributo.
     * @throws ProfileAttributeException Se o atributo n�o estiver preenchido.
     */
    public String getAtributo(String atributo) {
        if (!this.hasAtributo(atributo)) {
            throw new ProfileAttributeException("Atributo n�o preenchido.");
        }

        return this.atributos.get(atributo);
    }

    /**
     * Define o login do usu�rio.
     *
     * @param login O novo login do usu�rio.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Define a senha do usu�rio.
     *
     * @param senha A nova senha do usu�rio.
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * Define o nome do usu�rio.
     *
     * @param nome O novo nome do usu�rio.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obt�m os convites de amizade do usu�rio.
     *
     * @return Um conjunto de IDs de convites de amizade.
     */
    public Set<String> getConvitesAmizade() {
        return convitesAmizade;
    }

    /**
     * Remove um convite de amizade.
     *
     * @param id O ID do convite de amizade a ser removido.
     */
    public void removerConviteAmizade(String id) {
        this.convitesAmizade.remove(id);
    }

    /**
     * Adiciona um convite de amizade.
     *
     * @param id O ID do convite de amizade a ser adicionado.
     */
    public void adicionarConviteAmizade(String id) {
        this.convitesAmizade.add(id);
    }

    /**
     * Obt�m a lista de amigos do usu�rio.
     *
     * @return Um conjunto de IDs de amigos.
     */
    public Set<String> getAmigos() {
        return amigos;
    }

    /**
     * Verifica se a senha fornecida � v�lida.
     *
     * @param password A senha a ser verificada.
     * @return true se a senha for v�lida, false caso contr�rio.
     */
    public boolean isPasswordValid(String password) {
        return this.senha.equals(password);
    }

    /**
     * Verifica se o usu�rio possui um determinado atributo.
     *
     * @param atributo O nome do atributo.
     * @return true se o atributo existir, false caso contr�rio.
     */
    public boolean hasAtributo(String atributo) {
        return this.atributos.containsKey(atributo);
    }

    /**
     * Adiciona um amigo ao usu�rio.
     *
     * @param amigo O ID do amigo a ser adicionado.
     */
    public void adicionarAmigo(String amigo) {
        this.amigos.add(amigo);
    }

    /**
     * Adiciona um recado ao usu�rio.
     *
     * @param recado O recado a ser adicionado.
     */
    public void adicionarRecado(String recado) {
        recados.add(recado);
    }

    /**
     * Obt�m a fila de recados do usu�rio.
     *
     * @return A fila de recados.
     */
    public Queue<String> getRecados() {
        return this.recados;
    }
}
