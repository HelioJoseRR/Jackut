package br.ufal.ic.p2.jackut.entities;

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

    public Set<String> getConvitesAmizade() {
        return convitesAmizade;
    }

    public Set<String> getAmigos() {
        return this.relacionamentos.get("amigo");
    }

    public Set<String> getIdolos() {
        return this.relacionamentos.get("fa");
    }

    public Set<String> getPaqueras() {
        return this.relacionamentos.get("paquera");
    }

    public Set<String> getInimigos() {
        return this.relacionamentos.get("inimigo");
    }

    public Map<String, Set<String>> getRelacionamentos() {
        return relacionamentos;
    }
}
