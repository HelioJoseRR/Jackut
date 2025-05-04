package br.ufal.ic.p2.jackut.services;

import java.io.Serial;
import java.io.Serializable;

/**
 * Localizador de serviços que facilita o acesso aos diferentes serviços do sistema.
 */
public class ServiceLocator implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UsuarioService usuarioService;
    private final SessaoService sessaoService;
    private final ComunidadeService comunidadeService;
    private final MensagemService mensagemService;
    private final RelacionamentoService relacionamentoService;

    /**
     * Cria um novo localizador de serviços com instâncias padrão.
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
     * Cria um localizador de serviços com serviços específicos.
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

    public UsuarioService getUsuarioService() {
        return usuarioService;
    }

    public SessaoService getSessaoService() {
        return sessaoService;
    }

    public ComunidadeService getComunidadeService() {
        return comunidadeService;
    }

    public MensagemService getMensagemService() {
        return mensagemService;
    }

    public RelacionamentoService getRelacionamentoService() {
        return relacionamentoService;
    }
}