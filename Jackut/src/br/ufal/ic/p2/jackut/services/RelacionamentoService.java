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

public class RelacionamentoService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final DataRepository repository;
    private final UsuarioService usuarioService;
    private final MensagemService mensagemService;

    public RelacionamentoService(DataRepository repository, UsuarioService usuarioService, MensagemService mensagemService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.mensagemService = mensagemService;
    }

    public boolean ehAmigo(String login, String amigo) {
        if (!usuarioService.existeUsuario(login) || !usuarioService.existeUsuario(amigo)) {
            return false;
        }

        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getAmigos().contains(amigo);
    }

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

    public String getAmigos(String login) {
        if (!usuarioService.existeUsuario(login)) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        Usuario usuario = usuarioService.getUsuario(login);
        Set<String> amigos = usuario.getRelacionamentos().getAmigos();
        return "{" + String.join(",", amigos) + "}";
    }

    public boolean ehFa(String login, String idolo) {
        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getIdolos().contains(idolo);
    }

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

    public String getFas(String login) {
        List<String> fasList = new ArrayList<>();

        for (Usuario usuario : repository.getUsuarios().values()) {
            if (!usuario.getLogin().equals(login) && usuario.getRelacionamentos().getIdolos().contains(login)) {
                fasList.add(usuario.getLogin());
            }
        }

        return "{" + String.join(",", fasList) + "}";
    }

    public boolean ehPaquera(String login, String paquera) {
        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getPaqueras().contains(paquera);
    }

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

    public String getPaqueras(String login) {
        Usuario usuario = usuarioService.getUsuario(login);
        Set<String> paqueras = usuario.getRelacionamentos().getPaqueras();
        return "{" + String.join(",", paqueras) + "}";
    }

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

    public boolean ehInimigo(String login, String inimigo) {
        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getInimigos().contains(inimigo);
    }

    public void adicionarConviteAmizade(String login, String id) {
        Usuario usuario = usuarioService.getUsuario(login);
        usuario.getRelacionamentos().getConvitesAmizade().add(id);
    }

    public void removerConviteAmizade(String login, String id) {
        Usuario usuario = usuarioService.getUsuario(login);
        usuario.getRelacionamentos().getConvitesAmizade().remove(id);
    }

    public void zerarRelacionamentos() {
        // Os relacionamentos s�o gerenciados pelos usu�rios,
        // ent�o n�o � necess�rio fazer nada aqui al�m do que j� � feito
        // ao zerar os usu�rios.
    }
}
