package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.RelacionamentoException;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Relacionamento implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Map<String, Set<String>> relacionamentos;
    private final Set<String> convitesAmizade;

    public Relacionamento() {
        this.convitesAmizade = new LinkedHashSet<>();
        this.relacionamentos = new HashMap<>();
        this.relacionamentos.put("amigo", new LinkedHashSet<>());
        this.relacionamentos.put("fa", new LinkedHashSet<>());
        this.relacionamentos.put("paquera", new LinkedHashSet<>());
        this.relacionamentos.put("inimigo", new LinkedHashSet<>());
    }

    /**
     * Obtém os convites de amizade pendentes do usuário.
     *
     * @return Conjunto com os logins dos usuários que enviaram convites
     */
    public Set<String> getConvitesAmizade() {
        return convitesAmizade;
    }

    /**
     * Adiciona um convite de amizade à lista de convites pendentes.
     *
     * @param id Login do usuário que enviou o convite
     */
    public void adicionarConviteAmizade(String id) {
        this.convitesAmizade.add(id);
    }

    /**
     * Remove um convite de amizade da lista de convites pendentes.
     *
     * @param id Login do usuário cujo convite será removido
     */
    public void removerConviteAmizade(String id) {
        this.convitesAmizade.remove(id);
    }

    /**
     * Obtém a lista de amigos do usuário.
     *
     * @return Conjunto com os logins dos amigos
     */
    public Set<String> getAmigos() {
        return this.relacionamentos.get("amigo");
    }

    /**
     * Adiciona um amigo à lista de amigos do usuário.
     *
     * @param amigo Login do amigo a ser adicionado
     */
    public void adicionarAmigo(String amigo) {
        this.relacionamentos.get("amigo").add(amigo);
    }

    /**
     * Formata a lista de amigos para exibição.
     *
     * @return String formatada com os amigos: {amigo1,amigo2,...}
     */
    public String getAmigosFormatado() {
        String amigosStr = String.join(",", this.relacionamentos.get("amigo"));
        return "{" + amigosStr + "}";
    }

    /**
     * Obtém a lista de ídolos (usuários dos quais é fã).
     *
     * @return Conjunto com os logins dos ídolos
     */
    public Set<String> getIdolos() {
        return this.relacionamentos.get("fa");
    }

    /**
     * Adiciona um ídolo à lista de ídolos do usuário.
     *
     * @param idolo Login do ídolo a ser adicionado
     */
    public void adicionarIdolo(String idolo) {
        this.relacionamentos.get("fa").add(idolo);
    }

    /**
     * Verifica se um usuário está na lista de paqueras.
     *
     * @param paquera Login do usuário a verificar
     * @return true se o usuário está na lista de paqueras, false caso contrário
     */
    public boolean ehPaquera(String paquera) {
        return this.relacionamentos.get("paquera").contains(paquera);
    }

    /**
     * Adiciona um usuário à lista de paqueras.
     *
     * @param paquera Login do usuário a ser adicionado como paquera
     */
    public void adicionarPaquera(String paquera) {
        this.relacionamentos.get("paquera").add(paquera);
    }

    /**
     * Formata a lista de paqueras para exibição.
     *
     * @return String formatada com as paqueras: {paquera1,paquera2,...}
     */
    public String getPaqueras() {
        String paquerasStr = String.join(",", this.relacionamentos.get("paquera"));
        return "{" + paquerasStr + "}";
    }

    /**
     * Adiciona um usuário à lista de inimigos.
     * Lança exceção se o usuário já estiver na lista.
     *
     * @param inimigo Login do usuário a ser adicionado como inimigo
     * @throws RelacionamentoException se o usuário já estiver na lista de inimigos
     */
    public void adicionarInimigo(String inimigo) {
        if (this.relacionamentos.get("inimigo").contains(inimigo)) {
            throw new RelacionamentoException("Usuário já está adicionado como inimigo.");
        }
        this.relacionamentos.get("inimigo").add(inimigo);
    }

    /**
     * Obtém a lista de inimigos do usuário.
     *
     * @return Conjunto com os logins dos inimigos
     */
    public Set<String> getInimigos() {
        return this.relacionamentos.get("inimigo");
    }
}