package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.Usuario;
import br.ufal.ic.p2.jackut.exceptions.InvalidUserDataException;
import br.ufal.ic.p2.jackut.exceptions.ProfileAttributeException;
import br.ufal.ic.p2.jackut.exceptions.UserNotFoundException;

import java.io.Serial;
import java.io.Serializable;

public class UsuarioService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final DataRepository repository;

    public UsuarioService(DataRepository repository) {
        this.repository = repository;
    }

    public void criarUsuario(String login, String senha, String nome) {
        validarDadosUsuario(login, senha);

        if (repository.existeUsuario(login)) {
            throw new InvalidUserDataException("Conta com esse nome j� existe.");
        }

        Usuario novoUsuario = new Usuario(login, senha, nome);
        repository.adicionarUsuario(novoUsuario);
    }

    private void validarDadosUsuario(String login, String senha) {
        if (login == null || login.isEmpty()) {
            throw new InvalidUserDataException("Login inv�lido.");
        }

        if (senha == null || senha.isEmpty()) {
            throw new InvalidUserDataException("Senha inv�lida.");
        }
    }

    public Usuario getUsuario(String login) {
        Usuario usuario = repository.getUsuario(login);

        if (usuario == null) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        return usuario;
    }

    public boolean existeUsuario(String login) {
        return repository.existeUsuario(login);
    }

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

    public void editarPerfil(String login, String atributo, String valor) {
        Usuario usuario = getUsuario(login);
        usuario.getAtributos().put(atributo, valor);
    }

    public void removerUsuario(String login) {
        if (!repository.existeUsuario(login)) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        repository.removerUsuario(login);
    }

    public boolean validarSenha(String login, String senha) {
        Usuario usuario = getUsuario(login);
        return usuario.getSenha().equals(senha);
    }

    public void zerarUsuarios() {
        repository.getUsuarios().clear();
    }
}
