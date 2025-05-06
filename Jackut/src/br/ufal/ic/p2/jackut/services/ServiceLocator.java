package br.ufal.ic.p2.jackut.services;

import java.io.Serial;
import java.io.Serializable;

/**
 * Localizador de servi�os que facilita o acesso aos diferentes servi�os do sistema.
 * Implementa o padr�o de projeto Service Locator.
 */
public class ServiceLocator implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Servi�o de usu�rios */
    private final UsuarioService usuarioService;

    /** Servi�o de sess�es */
    private final SessaoService sessaoService;

    /** Servi�o de comunidades */
    private final ComunidadeService comunidadeService;

    /** Servi�o de mensagens */
    private final MensagemService mensagemService;

    /** Servi�o de relacionamentos */
    private final RelacionamentoService relacionamentoService;

    /**
     * Cria um novo localizador de servi�os com inst�ncias padr�o.
     * Inicializa todos os servi�os com um reposit�rio compartilhado.
     */
    public ServiceLocator() {
        DataRepository repository = new DataRepository();
        this.usuarioService = new UsuarioService(repository);
        this.sessaoService = new SessaoService(repository, usuarioService);
        this.comunidadeService = new ComunidadeService(repository, usuarioService);
        this.mensagemService = new MensagemService(repository, usuarioService, comunidadeService);
        this.relacionamentoService = new RelacionamentoService(repository, usuarioService, mensagemService);
    }

    /**
     * Cria um localizador de servi�os com servi�os espec�ficos.
     * �til para testes e inje��o de depend�ncias.
     *
     * @param usuarioService Servi�o de usu�rios
     * @param sessaoService Servi�o de sess�es
     * @param comunidadeService Servi�o de comunidades
     * @param mensagemService Servi�o de mensagens
     * @param relacionamentoService Servi�o de relacionamentos
     */
    public ServiceLocator(
            UsuarioService usuarioService,
            SessaoService sessaoService,
            ComunidadeService comunidadeService,
            MensagemService mensagemService,
            RelacionamentoService relacionamentoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        this.comunidadeService = comunidadeService;
        this.mensagemService = mensagemService;
        this.relacionamentoService = relacionamentoService;
    }

    /**
     * @return Servi�o de usu�rios
     */
    public UsuarioService getUsuarioService() {
        return usuarioService;
    }

    /**
     * @return Servi�o de sess�es
     */
    public SessaoService getSessaoService() {
        return sessaoService;
    }

    /**
     * @return Servi�o de comunidades
     */
    public ComunidadeService getComunidadeService() {
        return comunidadeService;
    }

    /**
     * @return Servi�o de mensagens
     */
    public MensagemService getMensagemService() {
        return mensagemService;
    }

    /**
     * @return Servi�o de relacionamentos
     */
    public RelacionamentoService getRelacionamentoService() {
        return relacionamentoService;
    }
}