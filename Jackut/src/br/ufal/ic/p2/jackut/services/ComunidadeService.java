package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.Comunidade;
import br.ufal.ic.p2.jackut.entities.Usuario;
import br.ufal.ic.p2.jackut.exceptions.CommunityException;
import br.ufal.ic.p2.jackut.exceptions.UserNotFoundException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class ComunidadeService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final DataRepository repository;
    private final UsuarioService usuarioService;

    public ComunidadeService(DataRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    public void criarComunidade(String login, String nome, String descricao) {
        if (repository.existeComunidade(nome)) {
            throw new CommunityException("Comunidade com esse nome já existe.");
        }

        Usuario usuario = usuarioService.getUsuario(login);

        // Passa o login do dono ao criar a comunidade
        Comunidade comunidade = new Comunidade(login, login, nome, descricao);
        adicionarMembroComunidade(comunidade, login);

        repository.adicionarComunidade(comunidade);
        repository.adicionarComunidadeAoDono(login, nome);

        adicionarComunidadeAoUsuario(usuario, nome);
    }

    public Comunidade getComunidade(String nome) {
        Comunidade comunidade = repository.getComunidade(nome);

        if (comunidade == null) {
            throw new CommunityException("Comunidade não existe.");
        }

        return comunidade;
    }

    public String getDescricaoComunidade(String nome) {
        return getComunidade(nome).getDescricao();
    }

    public String getDonoComunidade(String nome) {
        Comunidade comunidade = getComunidade(nome);
        return comunidade.getDonoComunidade();
    }

    public String getMembrosComunidade(String nome) {
        Comunidade comunidade = getComunidade(nome);
        return "{" + String.join(",", comunidade.getMembros()) + "}";
    }

    public String getComunidadesDoUsuario(String login) {
        if (login == null || login.isEmpty() || !usuarioService.existeUsuario(login)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        Usuario usuario = usuarioService.getUsuario(login);
        return "{" + String.join(",", usuario.getComunidadesCadastradas()) + "}";
    }

    public void adicionarUsuarioAComunidade(String login, String nomeComunidade) {
        Comunidade comunidade = getComunidade(nomeComunidade);
        Usuario usuario = usuarioService.getUsuario(login);

        if (isMembro(comunidade, login)) {
            throw new CommunityException("Usuario já faz parte dessa comunidade.");
        }

        adicionarMembroComunidade(comunidade, login);
        adicionarComunidadeAoUsuario(usuario, nomeComunidade);
    }

    public void removerUsuarioDeComunidades(String login) {
        // Remove comunidades onde o usuário é dono
        Set<String> comunidadesDoDono = repository.getComunidadesDonoUsuario(login);

        for (String nomeComunidade : comunidadesDoDono) {
            Comunidade comunidade = repository.getComunidade(nomeComunidade);

            if (comunidade != null) {
                // Remove a comunidade de todos os membros
                for (String membro : comunidade.getMembros()) {
                    Usuario usuarioMembro = repository.getUsuario(membro);
                    if (usuarioMembro != null) {
                        removerComunidadeCadastrada(usuarioMembro, nomeComunidade);
                    }
                }

                repository.removerComunidade(nomeComunidade);
            }
        }

        repository.getDonoParaComunidades().remove(login);
    }

    public void zerarComunidades() {
        repository.getComunidades().clear();
        repository.getDonoParaComunidades().clear();
    }

    // Métodos auxiliares
    private boolean isMembro(Comunidade comunidade, String login) {
        return comunidade.getMembros().contains(login);
    }

    private void adicionarMembroComunidade(Comunidade comunidade, String login) {
        if (!comunidade.getMembros().contains(login)) {
            comunidade.getMembros().add(login);
        }
    }

    private void adicionarComunidadeAoUsuario(Usuario usuario, String nomeComunidade) {
        if (!usuario.getComunidadesCadastradas().contains(nomeComunidade)) {
            usuario.getComunidadesCadastradas().add(nomeComunidade);
        }
    }

    private void removerComunidadeCadastrada(Usuario usuario, String nome) {
        usuario.getComunidadesCadastradas().removeIf(com -> com.equals(nome));
    }
}
