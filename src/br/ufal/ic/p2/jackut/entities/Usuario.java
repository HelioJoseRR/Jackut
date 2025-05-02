package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.ProfileAttributeException;
import br.ufal.ic.p2.jackut.exceptions.RelacionamentoException;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Representa um usuário no sistema Jackut.
 * Um usuário possui login, senha, nome, atributos personalizados e
 * mantém listas de relacionamentos, recados, mensagens e comunidades.
 */
public class Usuario implements Serializable {
    /** ID de serialização da classe */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Login do usuário (identificador único) */
    private final String login;

    /** Senha do usuário */
    private final String senha;

    /** Nome do usuário */
    private final String nome;

    /** Atributos personalizados do perfil */
    private final Map<String, String> atributos;

    /** Fila de recados recebidos */
    private Queue<Recado> recados;

    /** Lista de comunidades das quais o usuário participa */
    private final List<String> comunidadesCadastradas;

    /** Fila de mensagens recebidas */
    private final Queue<Mensagem> mensagens;

    /** Gerenciador de relacionamentos do usuário */
    private final Relacionamento relacionamentos;

    /**
     * Cria um novo usuário com as informações básicas.
     *
     * @param login Login do usuário
     * @param senha Senha do usuário
     * @param nome  Nome do usuário
     */
    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.atributos = new HashMap<>();
        this.recados = new LinkedList<>();
        this.comunidadesCadastradas = new ArrayList<>();
        this.mensagens = new LinkedList<>();
        this.relacionamentos = new Relacionamento();
    }

    /**
     * Verifica se a senha fornecida corresponde à senha do usuário.
     *
     * @param password Senha a ser verificada
     * @return true se a senha estiver correta, false caso contrário
     */
    public boolean isPasswordValid(String password) {
        return this.senha.equals(password);
    }

    /**
     * Obtém o login do usuário.
     *
     * @return Login do usuário
     */
    public String getLogin() {
        return login;
    }

    /**
     * Obtém o nome do usuário.
     *
     * @return Nome do usuário
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define um atributo personalizado no perfil do usuário.
     *
     * @param atributo Nome do atributo
     * @param valor    Valor do atributo
     */
    public void setAtributo(String atributo, String valor) {
        this.atributos.put(atributo, valor);
    }

    /**
     * Obtém o valor de um atributo personalizado do perfil.
     * Lança exceção se o atributo não existir.
     *
     * @param atributo Nome do atributo
     * @return Valor do atributo
     * @throws ProfileAttributeException se o atributo não estiver definido
     */
    public String getAtributo(String atributo) {
        if (!this.atributos.containsKey(atributo)) {
            throw new ProfileAttributeException("Atributo não preenchido.");
        }
        return this.atributos.get(atributo);
    }

    /**
     * Obtém os convites de amizade pendentes.
     *
     * @return Conjunto de logins dos usuários que enviaram convites
     */
    public Set<String> getConvitesAmizade() {
        return relacionamentos.getConvitesAmizade();
    }

    /**
     * Adiciona um convite de amizade à lista de convites pendentes.
     *
     * @param id Login do usuário que enviou o convite
     */
    public void adicionarConviteAmizade(String id) {
        this.relacionamentos.adicionarConviteAmizade(id);
    }

    /**
     * Remove um convite de amizade da lista de convites pendentes.
     *
     * @param id Login do usuário cujo convite será removido
     */
    public void removerConviteAmizade(String id) {
        this.relacionamentos.removerConviteAmizade(id);
    }

    /**
     * Obtém a lista de amigos do usuário.
     *
     * @return Conjunto de logins dos amigos
     */
    public Set<String> getAmigos() {
        return relacionamentos.getAmigos();
    }

    /**
     * Adiciona um amigo à lista de amigos do usuário.
     *
     * @param amigo Login do amigo a ser adicionado
     */
    public void adicionarAmigo(String amigo) {
        this.relacionamentos.adicionarAmigo(amigo);
    }

    /**
     * Adiciona um recado à fila de recados do usuário.
     *
     * @param remetente Login do usuário que enviou o recado
     * @param conteudo  Conteúdo do recado
     */
    public void adicionarRecado(String remetente, String conteudo) {
        Recado recado = new Recado(remetente, this.login, conteudo);
        recados.add(recado);
    }

    /**
     * Obtém a fila de recados do usuário.
     *
     * @return Fila de recados
     */
    public Queue<Recado> getRecados() {
        return this.recados;
    }

    /**
     * Adiciona uma comunidade à lista de comunidades do usuário.
     *
     * @param comunidade Nome da comunidade a ser adicionada
     */
    public void addComunidade(String comunidade) {
        this.comunidadesCadastradas.add(comunidade);
    }

    /**
     * Obtém as comunidades cadastradas do usuário, formatadas.
     *
     * @return String formatada com as comunidades: {comunidade1,comunidade2,...}
     */
    public String getComunidadesCadastradas() {
        return this.comunidadesCadastradas.stream()
                .collect(Collectors.joining(",", "{", "}"));
    }

    /**
     * Obtém as mensagens do usuário.
     *
     * @return Fila de mensagens
     */
    public Queue<Mensagem> getMensagens() {
        return this.mensagens;
    }

    /**
     * Adiciona uma mensagem à fila de mensagens do usuário.
     *
     * @param mensagem Mensagem a ser adicionada
     */
    public void adicionarMensagem(Mensagem mensagem) {
        this.mensagens.add(mensagem);
    }

    /**
     * Obtém a lista de ídolos (usuários dos quais é fã).
     *
     * @return Conjunto de logins dos ídolos
     */
    public Set<String> getIdolos() {
        return this.relacionamentos.getIdolos();
    }

    /**
     * Adiciona um ídolo à lista de ídolos do usuário.
     *
     * @param idolo Login do ídolo a ser adicionado
     */
    public void adicionarIdolo(String idolo) {
        this.relacionamentos.adicionarIdolo(idolo);
    }

    /**
     * Verifica se um usuário está na lista de paqueras.
     *
     * @param paquera Login do usuário a verificar
     * @return true se o usuário está na lista de paqueras, false caso contrário
     */
    public boolean ehPaquera(String paquera) {
        return this.relacionamentos.ehPaquera(paquera);
    }

    /**
     * Adiciona um usuário à lista de paqueras.
     *
     * @param paquera Login do usuário a ser adicionado como paquera
     */
    public void adicionarPaquera(String paquera) {
        this.relacionamentos.adicionarPaquera(paquera);
    }

    /**
     * Formata a lista de paqueras para exibição.
     *
     * @return String formatada com as paqueras: {paquera1,paquera2,...}
     */
    public String getPaqueras() {
        return this.relacionamentos.getPaqueras();
    }

    /**
     * Obtém o objeto de relacionamentos do usuário.
     *
     * @return Objeto Relacionamento do usuário
     */
    public Relacionamento getRelacionamento() {
        return this.relacionamentos;
    }

    /**
     * Adiciona um usuário à lista de inimigos.
     * Lança exceção se o usuário tentar adicionar a si mesmo como inimigo.
     *
     * @param inimigo Login do usuário a ser adicionado como inimigo
     * @throws RelacionamentoException se o usuário tentar adicionar a si mesmo como inimigo
     */
    public void adicionarInimigo(String inimigo) {
        if (this.login.equals(inimigo)) {
            throw new RelacionamentoException("Usuário não pode ser inimigo de si mesmo.");
        }
        this.relacionamentos.adicionarInimigo(inimigo);
    }

    /**
     * Verifica se um usuário está na lista de inimigos.
     *
     * @param inimigo Login do usuário a verificar
     * @return true se o usuário está na lista de inimigos, false caso contrário
     */
    public boolean ehInimigo(String inimigo) {
        return this.relacionamentos.getInimigos().contains(inimigo);
    }

    /**
     * Remove uma comunidade da lista de comunidades do usuário.
     *
     * @param comunidade Nome da comunidade a ser removida
     */
    public void removerComunidade(String comunidade) {
        this.comunidadesCadastradas.remove(comunidade);
    }

    /**
     * Substitui a fila de recados do usuário.
     *
     * @param recados Nova fila de recados
     */
    public void setRecados(Queue<Recado> recados) {
        this.recados = recados;
    }
}