package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Reposit�rio central de dados do sistema.
 * Armazena todas as entidades e gerencia o acesso a elas.
 */
public class DataRepository implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Mapa de usu�rios indexados por login */
    private Map<String, Usuario> usuarios;

    /** Mapa de sess�es indexadas por ID de sess�o */
    private Map<String, String> sessoes;

    /** Mapa de comunidades indexadas por nome */
    private Map<String, Comunidade> comunidades;

    /** Mapa de mensagens indexadas por login do destinat�rio */
    private Map<String, List<Comunicacao>> mensagens;

    /** Mapa que relaciona donos (login) �s suas comunidades */
    private Map<String, Set<String>> donoParaComunidades;

    /** Contador para gerar IDs de sess�o �nicos */
    private int nextSessionId;

    /**
     * Construtor que inicializa todas as estruturas de dados.
     */
    public DataRepository() {
        this.usuarios = new HashMap<>();
        this.sessoes = new HashMap<>();
        this.comunidades = new HashMap<>();
        this.mensagens = new HashMap<>();
        this.donoParaComunidades = new HashMap<>();
        this.nextSessionId = 1;
    }

    // M�todos para usu�rios

    /**
     * @return Mapa de todos os usu�rios
     */
    public Map<String, Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * Adiciona um novo usu�rio ao reposit�rio.
     *
     * @param usuario Objeto Usuario a ser adicionado
     */
    public void adicionarUsuario(Usuario usuario) {
        usuarios.put(usuario.getLogin(), usuario);
        donoParaComunidades.putIfAbsent(usuario.getLogin(), new HashSet<>());
    }

    /**
     * Obt�m um usu�rio pelo login.
     *
     * @param login Login do usu�rio
     * @return Objeto Usuario correspondente ou null se n�o existir
     */
    public Usuario getUsuario(String login) {
        return usuarios.get(login);
    }

    /**
     * Verifica se existe um usu�rio com o login especificado.
     *
     * @param login Login do usu�rio
     * @return true se o usu�rio existir, false caso contr�rio
     */
    public boolean existeUsuario(String login) {
        return usuarios.containsKey(login);
    }

    /**
     * Remove um usu�rio do reposit�rio.
     *
     * @param login Login do usu�rio a ser removido
     */
    public void removerUsuario(String login) {
        usuarios.remove(login);
    }

    // M�todos para sess�es

    /**
     * @return Mapa de todas as sess�es
     */
    public Map<String, String> getSessoes() {
        return sessoes;
    }

    /**
     * Cria uma nova sess�o para um usu�rio.
     *
     * @param login Login do usu�rio
     * @return ID da sess�o criada
     */
    public String criarSessao(String login) {
        String sessionId = String.valueOf(nextSessionId++);
        sessoes.put(sessionId, login);
        return sessionId;
    }

    /**
     * Obt�m o login associado a uma sess�o.
     *
     * @param sessionId ID da sess�o
     * @return Login do usu�rio ou null se a sess�o n�o existir
     */
    public String getLoginDaSessao(String sessionId) {
        return sessoes.get(sessionId);
    }

    /**
     * Verifica se existe uma sess�o com o ID especificado.
     *
     * @param sessionId ID da sess�o
     * @return true se a sess�o existir, false caso contr�rio
     */
    public boolean existeSessao(String sessionId) {
        return sessoes.containsKey(sessionId);
    }

    /**
     * Remove uma sess�o do reposit�rio.
     *
     * @param sessionId ID da sess�o a ser removida
     */
    public void removerSessao(String sessionId) {
        sessoes.remove(sessionId);
    }

    // M�todos para comunidades

    /**
     * @return Mapa de todas as comunidades
     */
    public Map<String, Comunidade> getComunidades() {
        return comunidades;
    }

    /**
     * Adiciona uma nova comunidade ao reposit�rio.
     *
     * @param comunidade Objeto Comunidade a ser adicionado
     */
    public void adicionarComunidade(Comunidade comunidade) {
        comunidades.put(comunidade.getNome(), comunidade);
    }

    /**
     * Obt�m uma comunidade pelo nome.
     *
     * @param nome Nome da comunidade
     * @return Objeto Comunidade correspondente ou null se n�o existir
     */
    public Comunidade getComunidade(String nome) {
        return comunidades.get(nome);
    }

    /**
     * Verifica se existe uma comunidade com o nome especificado.
     *
     * @param nome Nome da comunidade
     * @return true se a comunidade existir, false caso contr�rio
     */
    public boolean existeComunidade(String nome) {
        return comunidades.containsKey(nome);
    }

    /**
     * Remove uma comunidade do reposit�rio.
     *
     * @param nome Nome da comunidade a ser removida
     */
    public void removerComunidade(String nome) {
        comunidades.remove(nome);
    }

    // M�todos para mensagens

    /**
     * @return Mapa de todas as mensagens
     */
    public Map<String, List<Comunicacao>> getMensagens() {
        return mensagens;
    }

    /**
     * Obt�m a lista de mensagens de um usu�rio.
     *
     * @param login Login do usu�rio
     * @return Lista de mensagens ou uma lista vazia se n�o houver mensagens
     */
    public List<Comunicacao> getMensagensDoUsuario(String login) {
        return mensagens.getOrDefault(login, new ArrayList<>());
    }

    /**
     * Adiciona uma mensagem para um destinat�rio.
     *
     * @param destinatario Login do destinat�rio
     * @param mensagem Objeto Comunicacao a ser adicionado
     */
    public void adicionarMensagem(String destinatario, Comunicacao mensagem) {
        mensagens.putIfAbsent(destinatario, new ArrayList<>());
        mensagens.get(destinatario).add(mensagem);
    }

    // M�todos para rela��o dono-comunidade

    /**
     * @return Mapa que relaciona donos �s suas comunidades
     */
    public Map<String, Set<String>> getDonoParaComunidades() {
        return donoParaComunidades;
    }

    /**
     * Obt�m o conjunto de comunidades de um dono.
     *
     * @param login Login do dono
     * @return Conjunto de nomes de comunidades ou um conjunto vazio
     */
    public Set<String> getComunidadesDonoUsuario(String login) {
        return donoParaComunidades.getOrDefault(login, new HashSet<>());
    }

    /**
     * Adiciona uma comunidade � lista de comunidades de um dono.
     *
     * @param login Login do dono
     * @param comunidade Nome da comunidade
     */
    public void adicionarComunidadeAoDono(String login, String comunidade) {
        donoParaComunidades.putIfAbsent(login, new HashSet<>());
        donoParaComunidades.get(login).add(comunidade);
    }

    /**
     * Remove uma comunidade da lista de comunidades de um dono.
     *
     * @param login Login do dono
     * @param comunidade Nome da comunidade
     */
    public void removerComunidadeDoDono(String login, String comunidade) {
        if (donoParaComunidades.containsKey(login)) {
            donoParaComunidades.get(login).remove(comunidade);
        }
    }

    /**
     * Limpa todos os dados do reposit�rio.
     * Usado para reiniciar o sistema.
     */
    public void zerarTudo() {
        usuarios.clear();
        sessoes.clear();
        comunidades.clear();
        mensagens.clear();
        donoParaComunidades.clear();
        nextSessionId = 1;
    }
}