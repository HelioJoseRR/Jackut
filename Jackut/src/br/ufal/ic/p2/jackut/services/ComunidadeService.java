package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.Comunidade;
import br.ufal.ic.p2.jackut.entities.Usuario;
import br.ufal.ic.p2.jackut.exceptions.CommunityException;
import br.ufal.ic.p2.jackut.exceptions.UserNotFoundException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * Servi�o respons�vel por gerenciar as opera��es relacionadas �s comunidades.
 * Permite criar, consultar e gerenciar membros de comunidades.
 */
public class ComunidadeService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Reposit�rio central de dados */
    private final DataRepository repository;

    /** Servi�o de usu�rios */
    private final UsuarioService usuarioService;

    /**
     * Construtor que inicializa o servi�o com as depend�ncias necess�rias.
     *
     * @param repository Reposit�rio central de dados
     * @param usuarioService Servi�o de usu�rios
     */
    public ComunidadeService(DataRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    /**
     * Cria uma nova comunidade.
     *
     * @param login Login do usu�rio que est� criando a comunidade
     * @param nome Nome �nico da comunidade
     * @param descricao Descri��o da comunidade
     * @throws CommunityException Se j� existir uma comunidade com o mesmo nome
     */
    public void criarComunidade(String login, String nome, String descricao) {
        if (repository.existeComunidade(nome)) {
            throw new CommunityException("Comunidade com esse nome j� existe.");
        }

        Usuario usuario = usuarioService.getUsuario(login);

        // Passa o login do dono ao criar a comunidade
        Comunidade comunidade = new Comunidade(login, login, nome, descricao);
        adicionarMembroComunidade(comunidade, login);

        repository.adicionarComunidade(comunidade);
        repository.adicionarComunidadeAoDono(login, nome);

        adicionarComunidadeAoUsuario(usuario, nome);
    }

    /**
     * Obt�m uma comunidade pelo nome.
     *
     * @param nome Nome da comunidade
     * @return A comunidade encontrada
     * @throws CommunityException Se a comunidade n�o existir
     */
    public Comunidade getComunidade(String nome) {
        Comunidade comunidade = repository.getComunidade(nome);

        if (comunidade == null) {
            throw new CommunityException("Comunidade n�o existe.");
        }

        return comunidade;
    }

    /**
     * Obt�m a descri��o de uma comunidade.
     *
     * @param nome Nome da comunidade
     * @return Descri��o da comunidade
     * @throws CommunityException Se a comunidade n�o existir
     */
    public String getDescricaoComunidade(String nome) {
        return getComunidade(nome).getDescricao();
    }

    /**
     * Obt�m o login do dono de uma comunidade.
     *
     * @param nome Nome da comunidade
     * @return Login do dono da comunidade
     * @throws CommunityException Se a comunidade n�o existir
     */
    public String getDonoComunidade(String nome) {
        Comunidade comunidade = getComunidade(nome);
        return comunidade.getDonoComunidade();
    }

    /**
     * Obt�m a lista de membros de uma comunidade formatada como string.
     *
     * @param nome Nome da comunidade
     * @return String formatada com a lista de membros: "{membro1,membro2,...}"
     * @throws CommunityException Se a comunidade n�o existir
     */
    public String getMembrosComunidade(String nome) {
        Comunidade comunidade = getComunidade(nome);
        return "{" + String.join(",", comunidade.getMembros()) + "}";
    }

    /**
     * Obt�m a lista de comunidades de um usu�rio formatada como string.
     *
     * @param login Login do usu�rio
     * @return String formatada com a lista de comunidades: "{comunidade1,comunidade2,...}"
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public String getComunidadesDoUsuario(String login) {
        if (login == null || login.isEmpty() || !usuarioService.existeUsuario(login)) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        Usuario usuario = usuarioService.getUsuario(login);
        return "{" + String.join(",", usuario.getComunidadesCadastradas()) + "}";
    }

    /**
     * Adiciona um usu�rio a uma comunidade.
     *
     * @param login Login do usu�rio a ser adicionado
     * @param nomeComunidade Nome da comunidade
     * @throws CommunityException Se a comunidade n�o existir ou se o usu�rio j� for membro
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public void adicionarUsuarioAComunidade(String login, String nomeComunidade) {
        Comunidade comunidade = getComunidade(nomeComunidade);
        Usuario usuario = usuarioService.getUsuario(login);

        if (isMembro(comunidade, login)) {
            throw new CommunityException("Usuario j� faz parte dessa comunidade.");
        }

        adicionarMembroComunidade(comunidade, login);
        adicionarComunidadeAoUsuario(usuario, nomeComunidade);
    }

    /**
     * Remove um usu�rio de todas as comunidades que ele � dono.
     * Tamb�m remove essas comunidades de todos os membros.
     *
     * @param login Login do usu�rio
     */
    public void removerUsuarioDeComunidades(String login) {
        // Remove comunidades onde o usu�rio � dono
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

    /**
     * Remove todas as comunidades do sistema.
     */
    public void zerarComunidades() {
        repository.getComunidades().clear();
        repository.getDonoParaComunidades().clear();
    }

    /**
     * Verifica se um usu�rio � membro de uma comunidade.
     *
     * @param comunidade A comunidade a verificar
     * @param login Login do usu�rio
     * @return true se o usu�rio for membro, false caso contr�rio
     */
    private boolean isMembro(Comunidade comunidade, String login) {
        return comunidade.getMembros().contains(login);
    }

    /**
     * Adiciona um usu�rio como membro de uma comunidade.
     *
     * @param comunidade A comunidade
     * @param login Login do usu�rio
     */
    private void adicionarMembroComunidade(Comunidade comunidade, String login) {
        if (!comunidade.getMembros().contains(login)) {
            comunidade.getMembros().add(login);
        }
    }

    /**
     * Adiciona uma comunidade � lista de comunidades de um usu�rio.
     *
     * @param usuario O usu�rio
     * @param nomeComunidade Nome da comunidade
     */
    private void adicionarComunidadeAoUsuario(Usuario usuario, String nomeComunidade) {
        if (!usuario.getComunidadesCadastradas().contains(nomeComunidade)) {
            usuario.getComunidadesCadastradas().add(nomeComunidade);
        }
    }

    /**
     * Remove uma comunidade da lista de comunidades de um usu�rio.
     *
     * @param usuario O usu�rio
     * @param nome Nome da comunidade
     */
    private void removerComunidadeCadastrada(Usuario usuario, String nome) {
        usuario.getComunidadesCadastradas().removeIf(com -> com.equals(nome));
    }
}