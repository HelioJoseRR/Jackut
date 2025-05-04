package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.exceptions.AuthenticationException;
import br.ufal.ic.p2.jackut.exceptions.SessionNotFoundException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Servi�o respons�vel por gerenciar sess�es de usu�rios.
 */
public class SessaoService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final DataRepository repository;
    private final UsuarioService usuarioService;

    public SessaoService(DataRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    /**
     * Abre uma sess�o para um usu�rio.
     */
    public String abrirSessao(String login, String senha) {
        if (!usuarioService.existeUsuario(login)) {
            throw new AuthenticationException();
        }

        if (!usuarioService.validarSenha(login, senha)) {
            throw new AuthenticationException();
        }

        return repository.criarSessao(login);
    }

    /**
     * Encerra uma sess�o espec�fica.
     */
    public boolean encerrarSessao(String sessionId) {
        if (repository.existeSessao(sessionId)) {
            repository.removerSessao(sessionId);
            return true;
        }
        return false;
    }

    /**
     * Verifica se uma sess�o existe.
     */
    public boolean existeSessao(String sessionId) {
        return repository.existeSessao(sessionId);
    }

    /**
     * Obt�m o login do usu�rio associado a uma sess�o.
     */
    public String getLoginDaSessao(String sessionId) {
        return repository.getLoginDaSessao(sessionId);
    }

    /**
     * Valida uma sess�o e retorna o login associado.
     */
    public String validarEObterLogin(String sessionId) {
        // Verifica se o sessionId � null ou vazio
        if (sessionId == null || sessionId.isEmpty()) {
            throw new SessionNotFoundException("Usu�rio n�o cadastrado.");
        }

        String login = getLoginDaSessao(sessionId);

        // Se n�o encontrou o login na sess�o, verifica se o pr�prio sessionId � um login v�lido
        // (compatibilidade com o comportamento original)
        if (login == null) {
            if (usuarioService.existeUsuario(sessionId)) {
                return sessionId;
            }
            throw new SessionNotFoundException("Usu�rio n�o cadastrado.");
        }

        return login;
    }

    /**
     * Zera todas as sess�es.
     */
    public void zerarSessoes() {
        repository.getSessoes().clear();
    }
}