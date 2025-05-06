package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.Usuario;
import br.ufal.ic.p2.jackut.exceptions.InvalidUserDataException;
import br.ufal.ic.p2.jackut.exceptions.ProfileAttributeException;
import br.ufal.ic.p2.jackut.exceptions.UserNotFoundException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Servi�o respons�vel por gerenciar os usu�rios do sistema Jackut.
 * Fornece opera��es para criar, consultar, editar e remover usu�rios,
 * al�m de gerenciar seus atributos de perfil.
 */
public class UsuarioService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Reposit�rio central de dados do sistema */
    private final DataRepository repository;

    /**
     * Construtor que inicializa o servi�o com o reposit�rio de dados.
     *
     * @param repository Reposit�rio central de dados
     */
    public UsuarioService(DataRepository repository) {
        this.repository = repository;
    }

    /**
     * Cria um novo usu�rio no sistema ap�s validar seus dados.
     *
     * @param login Login �nico do usu�rio
     * @param senha Senha para autentica��o
     * @param nome Nome completo do usu�rio
     * @throws InvalidUserDataException Se o login j� existir ou se os dados forem inv�lidos
     */
    public void criarUsuario(String login, String senha, String nome) {
        validarDadosUsuario(login, senha);

        if (repository.existeUsuario(login)) {
            throw new InvalidUserDataException("Conta com esse nome j� existe.");
        }

        Usuario novoUsuario = new Usuario(login, senha, nome);
        repository.adicionarUsuario(novoUsuario);
    }

    /**
     * Valida os dados b�sicos de um usu�rio.
     *
     * @param login Login do usu�rio
     * @param senha Senha do usu�rio
     * @throws InvalidUserDataException Se o login ou senha forem nulos ou vazios
     */
    private void validarDadosUsuario(String login, String senha) {
        if (login == null || login.isEmpty()) {
            throw new InvalidUserDataException("Login inv�lido.");
        }

        if (senha == null || senha.isEmpty()) {
            throw new InvalidUserDataException("Senha inv�lida.");
        }
    }

    /**
     * Obt�m um usu�rio pelo login.
     *
     * @param login Login do usu�rio
     * @return Objeto Usuario correspondente
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public Usuario getUsuario(String login) {
        Usuario usuario = repository.getUsuario(login);

        if (usuario == null) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        return usuario;
    }

    /**
     * Verifica se existe um usu�rio com o login especificado.
     *
     * @param login Login do usu�rio
     * @return true se o usu�rio existir, false caso contr�rio
     */
    public boolean existeUsuario(String login) {
        return repository.existeUsuario(login);
    }

    /**
     * Obt�m o valor de um atributo de um usu�rio.
     * Suporta atributos especiais (nome, login) e atributos personalizados.
     *
     * @param login Login do usu�rio
     * @param atributo Nome do atributo
     * @return Valor do atributo
     * @throws UserNotFoundException Se o usu�rio n�o existir
     * @throws ProfileAttributeException Se o atributo personalizado n�o estiver preenchido
     */
    public String getAtributoUsuario(String login, String atributo) {
        Usuario usuario = getUsuario(login);

        switch (atributo) {
            case "nome":
                return usuario.getNome();
            case "login":
                return usuario.getLogin();
            default:
                if (!usuario.getAtributos().containsKey(atributo)) {
                    throw new ProfileAttributeException("Atributo n�o preenchido.");
                }
                return usuario.getAtributos().get(atributo);
        }
    }

    /**
     * Edita um atributo personalizado do perfil de um usu�rio.
     *
     * @param login Login do usu�rio
     * @param atributo Nome do atributo
     * @param valor Novo valor do atributo
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public void editarPerfil(String login, String atributo, String valor) {
        Usuario usuario = getUsuario(login);
        usuario.getAtributos().put(atributo, valor);
    }

    /**
     * Remove um usu�rio do sistema.
     *
     * @param login Login do usu�rio a ser removido
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public void removerUsuario(String login) {
        if (!repository.existeUsuario(login)) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        repository.removerUsuario(login);
    }

    /**
     * Valida a senha de um usu�rio.
     *
     * @param login Login do usu�rio
     * @param senha Senha a ser validada
     * @return true se a senha for v�lida, false caso contr�rio
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public boolean validarSenha(String login, String senha) {
        Usuario usuario = getUsuario(login);
        return usuario.getSenha().equals(senha);
    }

    /**
     * Remove todos os usu�rios do sistema.
     */
    public void zerarUsuarios() {
        repository.getUsuarios().clear();
    }
}