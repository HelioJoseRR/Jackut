package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.exceptions.AuthenticationException;
import br.ufal.ic.p2.jackut.exceptions.SessionNotFoundException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Servi�o respons�vel por gerenciar sess�es de usu�rios.
 * Permite abrir e encerrar sess�es, al�m de validar credenciais.
 */
public class SessaoService implements Serializable {
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
    public SessaoService(DataRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    /**
     * Abre uma sess�o para um usu�rio ap�s validar suas credenciais.
     *
     * @param login Login do usu�rio
     * @param senha Senha do usu�rio
     * @return ID da sess�o criada
     * @throws AuthenticationException Se as credenciais forem inv�lidas
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
     *
     * @param sessionId ID da sess�o a ser encerrada
     * @return true se a sess�o foi encerrada, false se n�o existia
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
     *
     * @param sessionId ID da sess�o
     * @return true se a sess�o existir, false caso contr�rio
     */
    public boolean existeSessao(String sessionId) {
        return repository.existeSessao(sessionId);
    }

    /**
     * Obt�m o login do usu�rio associado a uma sess�o.
     *
     * @param sessionId ID da sess�o
     * @return Login do usu�rio ou null se a sess�o n�o existir
     */
    public String getLoginDaSessao(String sessionId) {
        return repository.getLoginDaSessao(sessionId);
    }

    /**
     * Valida uma sess�o e retorna o login associado.
     * Tamb�m suporta o caso em que o pr�prio sessionId � um login v�lido.
     *
     * @param sessionId ID da sess�o ou login do usu�rio
     * @return Login do usu�rio
     * @throws SessionNotFoundException Se a sess�o n�o existir e o sessionId n�o for um login v�lido
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
     * Remove todas as sess�es do sistema.
     */
    public void zerarSessoes() {
        repository.getSessoes().clear();
    }
}