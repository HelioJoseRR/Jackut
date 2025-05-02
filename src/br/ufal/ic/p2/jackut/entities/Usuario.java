package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.ProfileAttributeException;
import br.ufal.ic.p2.jackut.exceptions.RelacionamentoException;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Representa um usu�rio no sistema Jackut.
 * Um usu�rio possui login, senha, nome, atributos personalizados e
 * mant�m listas de relacionamentos, recados, mensagens e comunidades.
 */
public class Usuario implements Serializable {
    /** ID de serializa��o da classe */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Login do usu�rio (identificador �nico) */
    private final String login;

    /** Senha do usu�rio */
    private final String senha;

    /** Nome do usu�rio */
    private final String nome;

    /** Atributos personalizados do perfil */
    private final Map<String, String> atributos;

    /** Fila de recados recebidos */
    private Queue<Recado> recados;

    /** Lista de comunidades das quais o usu�rio participa */
    private final List<String> comunidadesCadastradas;

    /** Fila de mensagens recebidas */
    private final Queue<Mensagem> mensagens;

    /** Gerenciador de relacionamentos do usu�rio */
    private final Relacionamento relacionamentos;

    /**
     * Cria um novo usu�rio com as informa��es b�sicas.
     *
     * @param login Login do usu�rio
     * @param senha Senha do usu�rio
     * @param nome  Nome do usu�rio
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
     * Verifica se a senha fornecida corresponde � senha do usu�rio.
     *
     * @param password Senha a ser verificada
     * @return true se a senha estiver correta, false caso contr�rio
     */
    public boolean isPasswordValid(String password) {
        return this.senha.equals(password);
    }

    /**
     * Obt�m o login do usu�rio.
     *
     * @return Login do usu�rio
     */
    public String getLogin() {
        return login;
    }

    /**
     * Obt�m o nome do usu�rio.
     *
     * @return Nome do usu�rio
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define um atributo personalizado no perfil do usu�rio.
     *
     * @param atributo Nome do atributo
     * @param valor    Valor do atributo
     */
    public void setAtributo(String atributo, String valor) {
        this.atributos.put(atributo, valor);
    }

    /**
     * Obt�m o valor de um atributo personalizado do perfil.
     * Lan�a exce��o se o atributo n�o existir.
     *
     * @param atributo Nome do atributo
     * @return Valor do atributo
     * @throws ProfileAttributeException se o atributo n�o estiver definido
     */
    public String getAtributo(String atributo) {
        if (!this.atributos.containsKey(atributo)) {
            throw new ProfileAttributeException("Atributo n�o preenchido.");
        }
        return this.atributos.get(atributo);
    }

    /**
     * Obt�m os convites de amizade pendentes.
     *
     * @return Conjunto de logins dos usu�rios que enviaram convites
     */
    public Set<String> getConvitesAmizade() {
        return relacionamentos.getConvitesAmizade();
    }

    /**
     * Adiciona um convite de amizade � lista de convites pendentes.
     *
     * @param id Login do usu�rio que enviou o convite
     */
    public void adicionarConviteAmizade(String id) {
        this.relacionamentos.adicionarConviteAmizade(id);
    }

    /**
     * Remove um convite de amizade da lista de convites pendentes.
     *
     * @param id Login do usu�rio cujo convite ser� removido
     */
    public void removerConviteAmizade(String id) {
        this.relacionamentos.removerConviteAmizade(id);
    }

    /**
     * Obt�m a lista de amigos do usu�rio.
     *
     * @return Conjunto de logins dos amigos
     */
    public Set<String> getAmigos() {
        return relacionamentos.getAmigos();
    }

    /**
     * Adiciona um amigo � lista de amigos do usu�rio.
     *
     * @param amigo Login do amigo a ser adicionado
     */
    public void adicionarAmigo(String amigo) {
        this.relacionamentos.adicionarAmigo(amigo);
    }

    /**
     * Adiciona um recado � fila de recados do usu�rio.
     *
     * @param remetente Login do usu�rio que enviou o recado
     * @param conteudo  Conte�do do recado
     */
    public void adicionarRecado(String remetente, String conteudo) {
        Recado recado = new Recado(remetente, this.login, conteudo);
        recados.add(recado);
    }

    /**
     * Obt�m a fila de recados do usu�rio.
     *
     * @return Fila de recados
     */
    public Queue<Recado> getRecados() {
        return this.recados;
    }

    /**
     * Adiciona uma comunidade � lista de comunidades do usu�rio.
     *
     * @param comunidade Nome da comunidade a ser adicionada
     */
    public void addComunidade(String comunidade) {
        this.comunidadesCadastradas.add(comunidade);
    }

    /**
     * Obt�m as comunidades cadastradas do usu�rio, formatadas.
     *
     * @return String formatada com as comunidades: {comunidade1,comunidade2,...}
     */
    public String getComunidadesCadastradas() {
        return this.comunidadesCadastradas.stream()
                .collect(Collectors.joining(",", "{", "}"));
    }

    /**
     * Obt�m as mensagens do usu�rio.
     *
     * @return Fila de mensagens
     */
    public Queue<Mensagem> getMensagens() {
        return this.mensagens;
    }

    /**
     * Adiciona uma mensagem � fila de mensagens do usu�rio.
     *
     * @param mensagem Mensagem a ser adicionada
     */
    public void adicionarMensagem(Mensagem mensagem) {
        this.mensagens.add(mensagem);
    }

    /**
     * Obt�m a lista de �dolos (usu�rios dos quais � f�).
     *
     * @return Conjunto de logins dos �dolos
     */
    public Set<String> getIdolos() {
        return this.relacionamentos.getIdolos();
    }

    /**
     * Adiciona um �dolo � lista de �dolos do usu�rio.
     *
     * @param idolo Login do �dolo a ser adicionado
     */
    public void adicionarIdolo(String idolo) {
        this.relacionamentos.adicionarIdolo(idolo);
    }

    /**
     * Verifica se um usu�rio est� na lista de paqueras.
     *
     * @param paquera Login do usu�rio a verificar
     * @return true se o usu�rio est� na lista de paqueras, false caso contr�rio
     */
    public boolean ehPaquera(String paquera) {
        return this.relacionamentos.ehPaquera(paquera);
    }

    /**
     * Adiciona um usu�rio � lista de paqueras.
     *
     * @param paquera Login do usu�rio a ser adicionado como paquera
     */
    public void adicionarPaquera(String paquera) {
        this.relacionamentos.adicionarPaquera(paquera);
    }

    /**
     * Formata a lista de paqueras para exibi��o.
     *
     * @return String formatada com as paqueras: {paquera1,paquera2,...}
     */
    public String getPaqueras() {
        return this.relacionamentos.getPaqueras();
    }

    /**
     * Obt�m o objeto de relacionamentos do usu�rio.
     *
     * @return Objeto Relacionamento do usu�rio
     */
    public Relacionamento getRelacionamento() {
        return this.relacionamentos;
    }

    /**
     * Adiciona um usu�rio � lista de inimigos.
     * Lan�a exce��o se o usu�rio tentar adicionar a si mesmo como inimigo.
     *
     * @param inimigo Login do usu�rio a ser adicionado como inimigo
     * @throws RelacionamentoException se o usu�rio tentar adicionar a si mesmo como inimigo
     */
    public void adicionarInimigo(String inimigo) {
        if (this.login.equals(inimigo)) {
            throw new RelacionamentoException("Usu�rio n�o pode ser inimigo de si mesmo.");
        }
        this.relacionamentos.adicionarInimigo(inimigo);
    }

    /**
     * Verifica se um usu�rio est� na lista de inimigos.
     *
     * @param inimigo Login do usu�rio a verificar
     * @return true se o usu�rio est� na lista de inimigos, false caso contr�rio
     */
    public boolean ehInimigo(String inimigo) {
        return this.relacionamentos.getInimigos().contains(inimigo);
    }

    /**
     * Remove uma comunidade da lista de comunidades do usu�rio.
     *
     * @param comunidade Nome da comunidade a ser removida
     */
    public void removerComunidade(String comunidade) {
        this.comunidadesCadastradas.remove(comunidade);
    }

    /**
     * Substitui a fila de recados do usu�rio.
     *
     * @param recados Nova fila de recados
     */
    public void setRecados(Queue<Recado> recados) {
        this.recados = recados;
    }
}