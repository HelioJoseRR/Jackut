package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.CommunityException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A classe Sessao representa o gerenciador de sess�es de usu�rios no sistema Jackut.
 * Implementa a interface Serializable para permitir a serializa��o dos objetos.
 */
public class Sessao implements Serializable {
    private Map<String, String> sessoes;
    private int nextSessionId;
    private Map<String, Comunidade> comunidades;
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Sessao.
     * Inicializa as cole��es de sess�es e o ID da pr�xima sess�o.
     */
    public Sessao() {
        this.sessoes = new HashMap<>();
        this.comunidades = new HashMap<>();
        this.nextSessionId = 1;
    }

    /**
     * Cria uma nova sess�o para um usu�rio.
     *
     * @param login O login do usu�rio.
     * @return O ID da sess�o criada.
     */
    public String criarSessao(String login) {
        String sessionId = String.valueOf(this.nextSessionId);
        this.sessoes.put(sessionId, login);
        this.nextSessionId++;
        return sessionId;
    }

    /**
     * Obt�m o login associado a uma sess�o.
     *
     * @param sessionId O ID da sess�o.
     * @return O login do usu�rio associado � sess�o, ou null se a sess�o n�o existir.
     */
    public String getLoginDaSessao(String sessionId) {
        return this.sessoes.get(sessionId);
    }

    /**
     * Verifica se uma sess�o existe.
     *
     * @param sessionId O ID da sess�o.
     * @return true se a sess�o existir, false caso contr�rio.
     */
    public boolean existeSessao(String sessionId) {
        return this.sessoes.containsKey(sessionId);
    }

    /**
     * Encerra uma sess�o.
     *
     * @param sessionId O ID da sess�o a ser encerrada.
     * @return true se a sess�o foi encerrada com sucesso, false se a sess�o n�o existia.
     */
    public boolean encerrarSessao(String sessionId) {
        if (existeSessao(sessionId)) {
            this.sessoes.remove(sessionId);
            return true;
        }
        return false;
    }

    /**
     * Remove todas as sess�es.
     */
    public void limparSessoes() {
        this.sessoes.clear();
        this.nextSessionId = 1;
    }

    public void criarComunidade(String id, String nome, String descricao) {
        if (this.comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade com esse nome j� existe.");
        }

        Comunidade comunidade = new Comunidade(id, nome, descricao);
        comunidade.addMembro(sessoes.get(id));
        this.comunidades.put(nome, comunidade);
    }

    public String getDescricaoComunidade(String nome) throws CommunityException {
        if (!this.comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade n�o existe.");
        }

        return this.comunidades.get(nome).getDescricao();
    }

    public String getDonoComunidade(String nome) throws CommunityException{
        if (!this.comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade n�o existe.");
        }

        return sessoes.get(this.comunidades.get(nome).getSessionID());
    }

    public String getMembrosComunidade(String nomeComunidade) throws CommunityException{
        if (!this.comunidades.containsKey(nomeComunidade)){
            throw new CommunityException("Comunidade n�o existe.");
        }

        return this.comunidades.get(nomeComunidade).getMembros();
    }
}
