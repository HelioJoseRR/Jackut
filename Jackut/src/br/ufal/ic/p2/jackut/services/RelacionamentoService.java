package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.Relacionamento;
import br.ufal.ic.p2.jackut.entities.Usuario;
import br.ufal.ic.p2.jackut.exceptions.FriendshipException;
import br.ufal.ic.p2.jackut.exceptions.RelacionamentoException;
import br.ufal.ic.p2.jackut.exceptions.UserNotFoundException;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Servi�o respons�vel por gerenciar os relacionamentos entre usu�rios.
 * Permite adicionar e consultar amigos, �dolos, paqueras e inimigos.
 */
public class RelacionamentoService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Reposit�rio central de dados */
    private final DataRepository repository;

    /** Servi�o de usu�rios */
    private final UsuarioService usuarioService;

    /** Servi�o de mensagens */
    private final MensagemService mensagemService;

    /**
     * Construtor que inicializa o servi�o com as depend�ncias necess�rias.
     *
     * @param repository Reposit�rio central de dados
     * @param usuarioService Servi�o de usu�rios
     * @param mensagemService Servi�o de mensagens
     */
    public RelacionamentoService(DataRepository repository, UsuarioService usuarioService, MensagemService mensagemService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.mensagemService = mensagemService;
    }

    /**
     * Verifica se um usu�rio � amigo de outro.
     *
     * @param login Login do primeiro usu�rio
     * @param amigo Login do poss�vel amigo
     * @return true se forem amigos, false caso contr�rio
     */
    public boolean ehAmigo(String login, String amigo) {
        if (!usuarioService.existeUsuario(login) || !usuarioService.existeUsuario(amigo)) {
            return false;
        }

        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getAmigos().contains(amigo);
    }

    /**
     * Adiciona um usu�rio como amigo de outro.
     * Se ambos enviaram convites, a amizade � estabelecida automaticamente.
     *
     * @param login Login do usu�rio que est� adicionando
     * @param amigo Login do usu�rio a ser adicionado como amigo
     * @throws UserNotFoundException Se algum dos usu�rios n�o existir
     * @throws FriendshipException Se o usu�rio tentar adicionar a si mesmo ou se j� forem amigos
     * @throws RelacionamentoException Se o usu�rio a ser adicionado for inimigo
     */
    public void adicionarAmigo(String login, String amigo) {
        if (login == null || login.isEmpty()) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        if (login.equals(amigo)) {
            throw new FriendshipException("Usu�rio n�o pode adicionar a si mesmo como amigo.");
        }

        if (!usuarioService.existeUsuario(login) || !usuarioService.existeUsuario(amigo)) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        Usuario usuarioEnvia = usuarioService.getUsuario(login);
        Usuario usuarioRecebe = usuarioService.getUsuario(amigo);

        if (ehInimigo(amigo, login)) {
            throw new RelacionamentoException("Fun��o inv�lida: " + usuarioRecebe.getNome() + " � seu inimigo.");
        }

        Set<String> convitesAmizadeEnvia = usuarioEnvia.getRelacionamentos().getConvitesAmizade();
        Set<String> convitesAmizadeRecebe = usuarioRecebe.getRelacionamentos().getConvitesAmizade();
        Set<String> amigosEnvia = usuarioEnvia.getRelacionamentos().getAmigos();
        Set<String> amigosRecebe = usuarioRecebe.getRelacionamentos().getAmigos();

        if (convitesAmizadeEnvia.contains(amigo)) {
            // Aceitar convite pendente (ambos j� enviaram convites)
            amigosRecebe.add(login);
            amigosEnvia.add(amigo);
            convitesAmizadeRecebe.remove(login);
            convitesAmizadeEnvia.remove(amigo);
            return;
        }

        if (convitesAmizadeRecebe.contains(login)) {
            throw new FriendshipException("Usu�rio j� est� adicionado como amigo, esperando aceita��o do convite.");
        }

        if (amigosRecebe.contains(login)) {
            throw new FriendshipException("Usu�rio j� est� adicionado como amigo.");
        }

        convitesAmizadeRecebe.add(login);
    }

    /**
     * Obt�m a lista de amigos de um usu�rio formatada como string.
     *
     * @param login Login do usu�rio
     * @return String formatada com a lista de amigos: "{amigo1,amigo2,...}"
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public String getAmigos(String login) {
        if (!usuarioService.existeUsuario(login)) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        Usuario usuario = usuarioService.getUsuario(login);
        Set<String> amigos = usuario.getRelacionamentos().getAmigos();
        return "{" + String.join(",", amigos) + "}";
    }

    /**
     * Verifica se um usu�rio � f� de outro.
     *
     * @param login Login do poss�vel f�
     * @param idolo Login do poss�vel �dolo
     * @return true se o primeiro for f� do segundo, false caso contr�rio
     */
    public boolean ehFa(String login, String idolo) {
        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getIdolos().contains(idolo);
    }

    /**
     * Adiciona um usu�rio como �dolo de outro.
     *
     * @param login Login do usu�rio que est� adicionando (f�)
     * @param idolo Login do usu�rio a ser adicionado como �dolo
     * @throws RelacionamentoException Se o usu�rio tentar ser f� de si mesmo, se j� for f� ou se o �dolo for inimigo
     */
    public void adicionarIdolo(String login, String idolo) {
        Usuario usuario = usuarioService.getUsuario(login);
        Usuario idoloObj = usuarioService.getUsuario(idolo);

        if (login.equals(idolo)) {
            throw new RelacionamentoException("Usu�rio n�o pode ser f� de si mesmo.");
        }

        if (usuario.getRelacionamentos().getIdolos().contains(idolo)) {
            throw new RelacionamentoException("Usu�rio j� est� adicionado como �dolo.");
        }

        if (ehInimigo(idolo, login)) {
            throw new RelacionamentoException("Fun��o inv�lida: " + idoloObj.getNome() + " � seu inimigo.");
        }

        usuario.getRelacionamentos().getIdolos().add(idolo);
    }

    /**
     * Obt�m a lista de f�s de um usu�rio formatada como string.
     *
     * @param login Login do usu�rio
     * @return String formatada com a lista de f�s: "{fa1,fa2,...}"
     */
    public String getFas(String login) {
        List<String> fasList = new ArrayList<>();

        for (Usuario usuario : repository.getUsuarios().values()) {
            if (!usuario.getLogin().equals(login) && usuario.getRelacionamentos().getIdolos().contains(login)) {
                fasList.add(usuario.getLogin());
            }
        }

        return "{" + String.join(",", fasList) + "}";
    }

    /**
     * Verifica se um usu�rio tem outro como paquera.
     *
     * @param login Login do primeiro usu�rio
     * @param paquera Login do poss�vel paquera
     * @return true se o segundo for paquera do primeiro, false caso contr�rio
     */
    public boolean ehPaquera(String login, String paquera) {
        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getPaqueras().contains(paquera);
    }

    /**
     * Adiciona um usu�rio como paquera de outro.
     * Se ambos se adicionarem como paquera, o sistema envia recados autom�ticos.
     *
     * @param login Login do usu�rio que est� adicionando
     * @param paquera Login do usu�rio a ser adicionado como paquera
     * @throws RelacionamentoException Se o usu�rio tentar adicionar a si mesmo, se j� for paquera ou se for inimigo
     */
    public void adicionarPaquera(String login, String paquera) {
        Usuario usuario = usuarioService.getUsuario(login);
        Usuario paqueraObj = usuarioService.getUsuario(paquera);

        if (usuario.getRelacionamentos().getPaqueras().contains(paquera)) {
            throw new RelacionamentoException("Usu�rio j� est� adicionado como paquera.");
        }

        if (login.equals(paquera)) {
            throw new RelacionamentoException("Usu�rio n�o pode ser paquera de si mesmo.");
        }

        if (ehInimigo(paquera, login)) {
            throw new RelacionamentoException("Fun��o inv�lida: " + paqueraObj.getNome() + " � seu inimigo.");
        }

        usuario.getRelacionamentos().getPaqueras().add(paquera);

        if (usuario.getRelacionamentos().getPaqueras().contains(paquera) &&
                paqueraObj.getRelacionamentos().getPaqueras().contains(login)) {
            mensagemService.adicionarRecadoJackut(login, paqueraObj.getNome());
            mensagemService.adicionarRecadoJackut(paqueraObj.getLogin(), usuario.getNome());
        }
    }

    /**
     * Obt�m a lista de paqueras de um usu�rio formatada como string.
     *
     * @param login Login do usu�rio
     * @return String formatada com a lista de paqueras: "{paquera1,paquera2,...}"
     */
    public String getPaqueras(String login) {
        Usuario usuario = usuarioService.getUsuario(login);
        Set<String> paqueras = usuario.getRelacionamentos().getPaqueras();
        return "{" + String.join(",", paqueras) + "}";
    }

    /**
     * Adiciona um usu�rio como inimigo de outro.
     *
     * @param login Login do usu�rio que est� adicionando
     * @param inimigo Login do usu�rio a ser adicionado como inimigo
     * @throws UserNotFoundException Se o usu�rio a ser adicionado n�o existir
     * @throws RelacionamentoException Se o usu�rio tentar adicionar a si mesmo ou se j� for inimigo
     */
    public void adicionarInimigo(String login, String inimigo) {
        if (!usuarioService.existeUsuario(inimigo)) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        if (login.equals(inimigo)) {
            throw new RelacionamentoException("Usu�rio n�o pode ser inimigo de si mesmo.");
        }

        Usuario usuario = usuarioService.getUsuario(login);
        Set<String> inimigos = usuario.getRelacionamentos().getInimigos();

        if (inimigos.contains(inimigo)) {
            throw new RelacionamentoException("Usu�rio j� est� adicionado como inimigo.");
        }

        inimigos.add(inimigo);
    }

    /**
     * Verifica se um usu�rio tem outro como inimigo.
     *
     * @param login Login do primeiro usu�rio
     * @param inimigo Login do poss�vel inimigo
     * @return true se o segundo for inimigo do primeiro, false caso contr�rio
     */
    public boolean ehInimigo(String login, String inimigo) {
        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getInimigos().contains(inimigo);
    }

    /**
     * Adiciona um convite de amizade para um usu�rio.
     *
     * @param login Login do usu�rio que receber� o convite
     * @param id Login do usu�rio que enviou o convite
     */
    public void adicionarConviteAmizade(String login, String id) {
        Usuario usuario = usuarioService.getUsuario(login);
        usuario.getRelacionamentos().getConvitesAmizade().add(id);
    }

    /**
     * Remove um convite de amizade de um usu�rio.
     *
     * @param login Login do usu�rio que tem o convite
     * @param id Login do usu�rio que enviou o convite
     */
    public void removerConviteAmizade(String login, String id) {
        Usuario usuario = usuarioService.getUsuario(login);
        usuario.getRelacionamentos().getConvitesAmizade().remove(id);
    }
}