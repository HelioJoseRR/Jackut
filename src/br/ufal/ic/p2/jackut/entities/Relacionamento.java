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
     * Obt�m os convites de amizade pendentes do usu�rio.
     *
     * @return Conjunto com os logins dos usu�rios que enviaram convites
     */
    public Set<String> getConvitesAmizade() {
        return convitesAmizade;
    }

    /**
     * Adiciona um convite de amizade � lista de convites pendentes.
     *
     * @param id Login do usu�rio que enviou o convite
     */
    public void adicionarConviteAmizade(String id) {
        this.convitesAmizade.add(id);
    }

    /**
     * Remove um convite de amizade da lista de convites pendentes.
     *
     * @param id Login do usu�rio cujo convite ser� removido
     */
    public void removerConviteAmizade(String id) {
        this.convitesAmizade.remove(id);
    }

    /**
     * Obt�m a lista de amigos do usu�rio.
     *
     * @return Conjunto com os logins dos amigos
     */
    public Set<String> getAmigos() {
        return this.relacionamentos.get("amigo");
    }

    /**
     * Adiciona um amigo � lista de amigos do usu�rio.
     *
     * @param amigo Login do amigo a ser adicionado
     */
    public void adicionarAmigo(String amigo) {
        this.relacionamentos.get("amigo").add(amigo);
    }

    /**
     * Formata a lista de amigos para exibi��o.
     *
     * @return String formatada com os amigos: {amigo1,amigo2,...}
     */
    public String getAmigosFormatado() {
        String amigosStr = String.join(",", this.relacionamentos.get("amigo"));
        return "{" + amigosStr + "}";
    }

    /**
     * Obt�m a lista de �dolos (usu�rios dos quais � f�).
     *
     * @return Conjunto com os logins dos �dolos
     */
    public Set<String> getIdolos() {
        return this.relacionamentos.get("fa");
    }

    /**
     * Adiciona um �dolo � lista de �dolos do usu�rio.
     *
     * @param idolo Login do �dolo a ser adicionado
     */
    public void adicionarIdolo(String idolo) {
        this.relacionamentos.get("fa").add(idolo);
    }

    /**
     * Verifica se um usu�rio est� na lista de paqueras.
     *
     * @param paquera Login do usu�rio a verificar
     * @return true se o usu�rio est� na lista de paqueras, false caso contr�rio
     */
    public boolean ehPaquera(String paquera) {
        return this.relacionamentos.get("paquera").contains(paquera);
    }

    /**
     * Adiciona um usu�rio � lista de paqueras.
     *
     * @param paquera Login do usu�rio a ser adicionado como paquera
     */
    public void adicionarPaquera(String paquera) {
        this.relacionamentos.get("paquera").add(paquera);
    }

    /**
     * Formata a lista de paqueras para exibi��o.
     *
     * @return String formatada com as paqueras: {paquera1,paquera2,...}
     */
    public String getPaqueras() {
        String paquerasStr = String.join(",", this.relacionamentos.get("paquera"));
        return "{" + paquerasStr + "}";
    }

    /**
     * Adiciona um usu�rio � lista de inimigos.
     * Lan�a exce��o se o usu�rio j� estiver na lista.
     *
     * @param inimigo Login do usu�rio a ser adicionado como inimigo
     * @throws RelacionamentoException se o usu�rio j� estiver na lista de inimigos
     */
    public void adicionarInimigo(String inimigo) {
        if (this.relacionamentos.get("inimigo").contains(inimigo)) {
            throw new RelacionamentoException("Usu�rio j� est� adicionado como inimigo.");
        }
        this.relacionamentos.get("inimigo").add(inimigo);
    }

    /**
     * Obt�m a lista de inimigos do usu�rio.
     *
     * @return Conjunto com os logins dos inimigos
     */
    public Set<String> getInimigos() {
        return this.relacionamentos.get("inimigo");
    }
}