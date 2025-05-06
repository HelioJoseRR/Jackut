package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Classe que representa um usu�rio no sistema Jackut.
 * Armazena informa��es b�sicas do usu�rio, seus atributos personalizados,
 * comunidades das quais participa e seus relacionamentos com outros usu�rios.
 */
public class Usuario implements Serializable {
    @Serial
    private static long serialVersionUID = 1L;

    /** Login �nico do usu�rio, usado como identificador */
    private String login;

    /** Senha do usu�rio para autentica��o */
    private String senha;

    /** Nome completo do usu�rio */
    private String nome;

    /** Mapa de atributos personalizados do usu�rio (chave-valor) */
    private Map<String, String> atributos;

    /** Lista de comunidades das quais o usu�rio � membro */
    private List<String> comunidadesCadastradas;

    /** Objeto que gerencia os relacionamentos do usu�rio com outros usu�rios */
    private Relacionamento relacionamentos;

    /**
     * Construtor que inicializa um novo usu�rio com seus dados b�sicos.
     *
     * @param login Login �nico do usu�rio
     * @param senha Senha para autentica��o
     * @param nome Nome completo do usu�rio
     */
    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.atributos = new HashMap<>();
        this.comunidadesCadastradas = new ArrayList<>();
        this.relacionamentos = new Relacionamento();
    }

    /**
     * @return O login do usu�rio
     */
    public String getLogin() {
        return login;
    }

    /**
     * @return A senha do usu�rio
     */
    public String getSenha() {
        return senha;
    }

    /**
     * @return O nome completo do usu�rio
     */
    public String getNome() {
        return nome;
    }

    /**
     * @return Mapa de atributos personalizados do usu�rio
     */
    public Map<String, String> getAtributos() {
        return atributos;
    }

    /**
     * @return Lista de comunidades das quais o usu�rio � membro
     */
    public List<String> getComunidadesCadastradas() {
        return comunidadesCadastradas;
    }

    /**
     * @return Objeto que gerencia os relacionamentos do usu�rio
     */
    public Relacionamento getRelacionamentos() {
        return relacionamentos;
    }
}