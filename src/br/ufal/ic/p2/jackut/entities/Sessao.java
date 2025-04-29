package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.CommunityException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A classe Sessao representa o gerenciador de sessões de usuários no sistema Jackut.
 * Implementa a interface Serializable para permitir a serialização dos objetos.
 */
public class Sessao implements Serializable {
    private Map<String, String> sessoes;
    private int nextSessionId;
    private Map<String, Comunidade> comunidades;
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Sessao.
     * Inicializa as coleções de sessões e o ID da próxima sessão.
     */
    public Sessao() {
        this.sessoes = new HashMap<>();
        this.comunidades = new HashMap<>();
        this.nextSessionId = 1;
    }

    /**
     * Cria uma nova sessão para um usuário.
     *
     * @param login O login do usuário.
     * @return O ID da sessão criada.
     */
    public String criarSessao(String login) {
        String sessionId = String.valueOf(this.nextSessionId);
        this.sessoes.put(sessionId, login);
        this.nextSessionId++;
        return sessionId;
    }

    /**
     * Obtém o login associado a uma sessão.
     *
     * @param sessionId O ID da sessão.
     * @return O login do usuário associado à sessão, ou null se a sessão não existir.
     */
    public String getLoginDaSessao(String sessionId) {
        return this.sessoes.get(sessionId);
    }

    /**
     * Verifica se uma sessão existe.
     *
     * @param sessionId O ID da sessão.
     * @return true se a sessão existir, false caso contrário.
     */
    public boolean existeSessao(String sessionId) {
        return this.sessoes.containsKey(sessionId);
    }

    /**
     * Encerra uma sessão.
     *
     * @param sessionId O ID da sessão a ser encerrada.
     * @return true se a sessão foi encerrada com sucesso, false se a sessão não existia.
     */
    public boolean encerrarSessao(String sessionId) {
        if (existeSessao(sessionId)) {
            this.sessoes.remove(sessionId);
            return true;
        }
        return false;
    }

    /**
     * Remove todas as sessões.
     */
    public void limparSessoes() {
        this.sessoes.clear();
        this.nextSessionId = 1;
    }

    public void criarComunidade(String id, String nome, String descricao) {
        if (this.comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade com esse nome já existe.");
        }

        Comunidade comunidade = new Comunidade(id, nome, descricao);
        comunidade.addMembro(sessoes.get(id));
        this.comunidades.put(nome, comunidade);
    }

    public String getDescricaoComunidade(String nome) throws CommunityException {
        if (!this.comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade não existe.");
        }

        return this.comunidades.get(nome).getDescricao();
    }

    public String getDonoComunidade(String nome) throws CommunityException{
        if (!this.comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade não existe.");
        }

        return sessoes.get(this.comunidades.get(nome).getSessionID());
    }

    public String getMembrosComunidade(String nomeComunidade) throws CommunityException{
        if (!this.comunidades.containsKey(nomeComunidade)){
            throw new CommunityException("Comunidade não existe.");
        }

        return this.comunidades.get(nomeComunidade).getMembros();
    }
}
