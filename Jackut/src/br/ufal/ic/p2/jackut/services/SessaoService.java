package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.exceptions.AuthenticationException;
import br.ufal.ic.p2.jackut.exceptions.SessionNotFoundException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Serviço responsável por gerenciar sessões de usuários.
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
     * Abre uma sessão para um usuário.
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
     * Encerra uma sessão específica.
     */
    public boolean encerrarSessao(String sessionId) {
        if (repository.existeSessao(sessionId)) {
            repository.removerSessao(sessionId);
            return true;
        }
        return false;
    }

    /**
     * Verifica se uma sessão existe.
     */
    public boolean existeSessao(String sessionId) {
        return repository.existeSessao(sessionId);
    }

    /**
     * Obtém o login do usuário associado a uma sessão.
     */
    public String getLoginDaSessao(String sessionId) {
        return repository.getLoginDaSessao(sessionId);
    }

    /**
     * Valida uma sessão e retorna o login associado.
     */
    public String validarEObterLogin(String sessionId) {
        // Verifica se o sessionId é null ou vazio
        if (sessionId == null || sessionId.isEmpty()) {
            throw new SessionNotFoundException("Usuário não cadastrado.");
        }

        String login = getLoginDaSessao(sessionId);

        // Se não encontrou o login na sessão, verifica se o próprio sessionId é um login válido
        // (compatibilidade com o comportamento original)
        if (login == null) {
            if (usuarioService.existeUsuario(sessionId)) {
                return sessionId;
            }
            throw new SessionNotFoundException("Usuário não cadastrado.");
        }

        return login;
    }

    /**
     * Zera todas as sessões.
     */
    public void zerarSessoes() {
        repository.getSessoes().clear();
    }
}