package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.RelacionamentoException;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Relacionamento implements Serializable {
    private Map<String, Set<String>> relacionamentos;
    private Set<String> convitesAmizade;
    @Serial
    private static final long serialVersionUID = 1L;

    public Relacionamento() {
        this.convitesAmizade = new LinkedHashSet<>();
        this.relacionamentos = new HashMap<>();
        this.relacionamentos.put("amigo", new LinkedHashSet<>());
        this.relacionamentos.put("fa", new LinkedHashSet<>());
        this.relacionamentos.put("paquera", new LinkedHashSet<>());
        this.relacionamentos.put("inimigo", new LinkedHashSet<>());
    }

    /**
     * Obtém os convites de amizade do usuário.
     */
    public Set<String> getConvitesAmizade() {
        return convitesAmizade;
    }

    /**
     * Adiciona um convite de amizade.
     */
    public void adicionarConviteAmizade(String id) {
        this.convitesAmizade.add(id);
    }

    /**
     * Remove um convite de amizade.
     */
    public void removerConviteAmizade(String id) {
        this.convitesAmizade.remove(id);
    }

    /**
     * Obtém a lista de amigos do usuário.
     */
    public Set<String> getAmigos() {
        return this.relacionamentos.get("amigo");
    }

    /**
     * Adiciona um amigo ao usuário.
     */
    public void adicionarAmigo(String amigo) {
        this.relacionamentos.get("amigo").add(amigo);
    }

    /**
     * Formata a lista de amigos para exibição.
     */
    public String getAmigosFormatado() {
        String amigosStr = String.join(",", this.relacionamentos.get("amigo"));
        return "{" + amigosStr + "}";
    }

    public Set<String> getIdolos() {
        return this.relacionamentos.get("fa");
    }

    public void adicionarIdolo(String idolo){
        this.relacionamentos.get("fa").add(idolo);
    }

    public boolean ehPaquera(String paquera){
        return this.relacionamentos.get("paquera").contains(paquera);
    }

    public void adicionarPaquera(String paquera){
        this.relacionamentos.get("paquera").add(paquera);
    }

    public String getPaqueras() {
        String paquerasStr = String.join(",", this.relacionamentos.get("paquera"));
        return "{" + paquerasStr + "}";
    }

    public void adicionarInimigo(String inimigo){
        if(this.relacionamentos.get("inimigo").contains(inimigo)){
            throw new RelacionamentoException("Usuário já está adicionado como inimigo.");
        }

        this.relacionamentos.get("inimigo").add(inimigo);
    }

    public Set<String> getInimigos() {
        return this.relacionamentos.get("inimigo");
    }
}
