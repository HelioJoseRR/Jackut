package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Repositório central de dados do sistema.
 * Armazena todas as entidades e gerencia o acesso a elas.
 */
public class DataRepository implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Map<String, Usuario> usuarios;
    private Map<String, String> sessoes;
    private Map<String, Comunidade> comunidades;
    private Map<String, List<Comunicacao>> mensagens;
    private Map<String, Set<String>> donoParaComunidades;
    private int nextSessionId;

    public DataRepository() {
        this.usuarios = new HashMap<>();
        this.sessoes = new HashMap<>();
        this.comunidades = new HashMap<>();
        this.mensagens = new HashMap<>();
        this.donoParaComunidades = new HashMap<>();
        this.nextSessionId = 1;
    }

    // Métodos para usuários
    public Map<String, Usuario> getUsuarios() {
        return usuarios;
    }

    public void adicionarUsuario(Usuario usuario) {
        usuarios.put(usuario.getLogin(), usuario);
        donoParaComunidades.putIfAbsent(usuario.getLogin(), new HashSet<>());
    }

    public Usuario getUsuario(String login) {
        return usuarios.get(login);
    }

    public boolean existeUsuario(String login) {
        return usuarios.containsKey(login);
    }

    public void removerUsuario(String login) {
        usuarios.remove(login);
    }

    // Métodos para sessões
    public Map<String, String> getSessoes() {
        return sessoes;
    }

    public String criarSessao(String login) {
        String sessionId = String.valueOf(nextSessionId++);
        sessoes.put(sessionId, login);
        return sessionId;
    }

    public String getLoginDaSessao(String sessionId) {
        return sessoes.get(sessionId);
    }

    public boolean existeSessao(String sessionId) {
        return sessoes.containsKey(sessionId);
    }

    public void removerSessao(String sessionId) {
        sessoes.remove(sessionId);
    }

    // Métodos para comunidades
    public Map<String, Comunidade> getComunidades() {
        return comunidades;
    }

    public void adicionarComunidade(Comunidade comunidade) {
        comunidades.put(comunidade.getNome(), comunidade);
    }

    public Comunidade getComunidade(String nome) {
        return comunidades.get(nome);
    }

    public boolean existeComunidade(String nome) {
        return comunidades.containsKey(nome);
    }

    public void removerComunidade(String nome) {
        comunidades.remove(nome);
    }

    // Métodos para mensagens
    public Map<String, List<Comunicacao>> getMensagens() {
        return mensagens;
    }

    public List<Comunicacao> getMensagensDoUsuario(String login) {
        return mensagens.getOrDefault(login, new ArrayList<>());
    }

    public void adicionarMensagem(String destinatario, Comunicacao mensagem) {
        mensagens.putIfAbsent(destinatario, new ArrayList<>());
        mensagens.get(destinatario).add(mensagem);
    }

    // Métodos para relação dono-comunidade
    public Map<String, Set<String>> getDonoParaComunidades() {
        return donoParaComunidades;
    }

    public Set<String> getComunidadesDonoUsuario(String login) {
        return donoParaComunidades.getOrDefault(login, new HashSet<>());
    }

    public void adicionarComunidadeAoDono(String login, String comunidade) {
        donoParaComunidades.putIfAbsent(login, new HashSet<>());
        donoParaComunidades.get(login).add(comunidade);
    }

    public void removerComunidadeDoDono(String login, String comunidade) {
        if (donoParaComunidades.containsKey(login)) {
            donoParaComunidades.get(login).remove(comunidade);
        }
    }

    // Método para zerar o sistema
    public void zerarTudo() {
        usuarios.clear();
        sessoes.clear();
        comunidades.clear();
        mensagens.clear();
        donoParaComunidades.clear();
        nextSessionId = 1;
    }
}