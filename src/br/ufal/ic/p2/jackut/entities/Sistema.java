package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A classe Sistema representa o sistema principal do Jackut.
 * Implementa a interface Serializable para permitir a serializa��o dos objetos.
 */
public class Sistema implements Serializable {
    private Map<String, Usuario> usuarios;
    private Sessao gerenciadorSessoes;
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Sistema.
     * Inicializa as cole��es de usu�rios e o gerenciador de sess�es.
     */
    public Sistema() {
        this.usuarios = new HashMap<>();
        this.gerenciadorSessoes = new Sessao();
    }

    /**
     * Reseta o sistema, limpando as cole��es de usu�rios e sess�es.
     */
    public void zerarSistema() {
        this.usuarios = new HashMap<>();
        this.gerenciadorSessoes = new Sessao();
    }

    /**
     * Verifica se um usu�rio existe no sistema.
     *
     * @param login O login do usu�rio.
     * @return O objeto Usuario correspondente.
     * @throws UserNotFoundException Se o usu�rio n�o estiver cadastrado.
     */
    public Usuario verificarUsuarioExiste(String login) {
        if (!this.usuarios.containsKey(login)) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        return this.usuarios.get(login);
    }

    /**
     * Obt�m o valor de um atributo de um usu�rio.
     *
     * @param login O login do usu�rio.
     * @param atributo O nome do atributo.
     * @return O valor do atributo.
     * @throws UserNotFoundException Se o usu�rio n�o estiver cadastrado.
     */
    public String getAtributoUsuario(String login, String atributo) {
        Usuario usuario = verificarUsuarioExiste(login);

        switch (atributo) {
            case "nome":
                return usuario.getNome();
            case "login":
                return usuario.getLogin();
            default:
                return usuario.getAtributo(atributo);
        }
    }

    /**
     * Cria um novo usu�rio no sistema.
     *
     * @param login O login do usu�rio.
     * @param senha A senha do usu�rio.
     * @param nome O nome do usu�rio.
     * @throws InvalidUserDataException Se o login ou a senha forem inv�lidos, ou se j� existir um usu�rio com o mesmo login.
     */
    public void criarUsuario(String login, String senha, String nome) {
        if (login == null) {
            throw new InvalidUserDataException("Login inv�lido.");
        }

        if (senha == null) {
            throw new InvalidUserDataException("Senha inv�lida.");
        }

        if (this.usuarios.containsKey(login)) {
            throw new InvalidUserDataException("Conta com esse nome j� existe.");
        }

        Usuario usuario = new Usuario(login, senha, nome);
        this.usuarios.put(login, usuario);
    }

    /**
     * Abre uma sess�o para um usu�rio.
     *
     * @param login O login do usu�rio.
     * @param senha A senha do usu�rio.
     * @return O login do usu�rio.
     * @throws AuthenticationException Se o login ou a senha forem inv�lidos.
     */
    public String abrirSessao(String login, String senha) {
        if (!this.usuarios.containsKey(login)) {
            throw new AuthenticationException();
        }

        Usuario usuario = this.usuarios.get(login);

        if (!usuario.isPasswordValid(senha)) {
            throw new AuthenticationException();
        }

        return login;
    }

    /**
     * Edita o perfil de um usu�rio.
     *
     * @param id O ID do usu�rio.
     * @param atributo O nome do atributo a ser editado.
     * @param valor O novo valor do atributo.
     * @throws UserNotFoundException Se o usu�rio n�o estiver cadastrado.
     */
    public void editarPerfil(String id, String atributo, String valor) {
        Usuario usuario = verificarUsuarioExiste(id);
        usuario.setAtributo(atributo, valor);
    }

    /**
     * Verifica se dois usu�rios s�o amigos.
     *
     * @param login O login do usu�rio.
     * @param amigo O login do amigo.
     * @return true se os usu�rios s�o amigos, false caso contr�rio.
     */
    public boolean ehAmigo(String login, String amigo) {
        Usuario usuario = verificarUsuarioExiste(login);
        return usuario.getAmigos().contains(amigo);
    }

    /**
     * Adiciona um amigo para um usu�rio.
     *
     * @param login O login do usu�rio.
     * @param amigo O login do amigo a ser adicionado.
     * @throws FriendshipException Se o usu�rio tentar adicionar a si mesmo, ou se o convite j� existir.
     * @throws UserNotFoundException Se o usu�rio ou o amigo n�o estiverem cadastrados.
     */
    public void adicionarAmigo(String login, String amigo) {
        if (login.equals(amigo)) {
            throw new FriendshipException("Usu�rio n�o pode adicionar a si mesmo como amigo.");
        }

        Usuario usuarioRecebeConvite = verificarUsuarioExiste(amigo);
        Usuario usuarioEnviaConvite = verificarUsuarioExiste(login);

        if (usuarioEnviaConvite.getConvitesAmizade().contains(amigo)) {
            // Aceitar convite pendente (ambos j� enviaram convites)
            usuarioRecebeConvite.adicionarAmigo(login);
            usuarioEnviaConvite.adicionarAmigo(amigo);

            usuarioRecebeConvite.removerConviteAmizade(login);
            usuarioEnviaConvite.removerConviteAmizade(amigo);
            return;
        }

        if (usuarioRecebeConvite.getConvitesAmizade().contains(login)) {
            throw new FriendshipException("Usu�rio j� est� adicionado como amigo, esperando aceita��o do convite.");
        }

        if (usuarioRecebeConvite.getAmigos().contains(login)) {
            throw new FriendshipException("Usu�rio j� est� adicionado como amigo.");
        }

        usuarioRecebeConvite.adicionarConviteAmizade(login);
    }

    /**
     * Obt�m a lista de amigos de um usu�rio.
     *
     * @param login O login do usu�rio.
     * @return Uma string contendo os logins dos amigos do usu�rio.
     */
    public String getAmigos(String login) {
        Usuario usuario = verificarUsuarioExiste(login);
        String amigos = String.join(",", usuario.getAmigos());
        return "{" + amigos + "}";
    }

    /**
     * Envia um recado para um usu�rio.
     *
     * @param id O ID do usu�rio que envia o recado.
     * @param destinatario O ID do usu�rio que recebe o recado.
     * @param recado O conte�do do recado.
     * @throws MessageException Se o usu�rio tentar enviar um recado para si mesmo.
     * @throws UserNotFoundException Se o usu�rio ou o destinat�rio n�o estiverem cadastrados.
     */
    public void enviarRecado(String id, String destinatario, String recado) {
        if (id.equals(destinatario)) {
            throw new MessageException("Usu�rio n�o pode enviar recado para si mesmo.");
        }

        Usuario enviaRecado = verificarUsuarioExiste(id);
        Usuario recebeRecado = verificarUsuarioExiste(destinatario);

        recebeRecado.adicionarRecado(recado);
    }

    /**
     * L� um recado de um usu�rio.
     *
     * @param id O ID do usu�rio.
     * @return O conte�do do recado.
     * @throws MessageException Se n�o houver recados.
     */
    public String lerRecado(String id) {
        Usuario usuario = verificarUsuarioExiste(id);

        if (usuario.getRecados().isEmpty()) {
            throw new MessageException("N�o h� recados.");
        }

        return usuario.getRecados().poll();
    }

    /**
     * Obt�m o gerenciador de sess�es.
     *
     * @return O objeto Sessao que gerencia as sess�es.
     */
    public Sessao getGerenciadorSessoes() {
        return gerenciadorSessoes;
    }
}