package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.services.*;
import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.*;

/**
 * Facade para o sistema Jackut, fornecendo uma interface simplificada.
 */
public class Facade implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String SISTEMA_FILE = "sistema.dat";

    private final UsuarioService usuarioService;
    private final SessaoService sessaoService;
    private final ComunidadeService comunidadeService;
    private final MensagemService mensagemService;
    private final RelacionamentoService relacionamentoService;

    /**
     * Construtor da classe Facade.
     */
    public Facade() {
        ServiceLocator serviceLocator = carregarOuCriarServiceLocator();
        this.usuarioService = serviceLocator.getUsuarioService();
        this.sessaoService = serviceLocator.getSessaoService();
        this.comunidadeService = serviceLocator.getComunidadeService();
        this.mensagemService = serviceLocator.getMensagemService();
        this.relacionamentoService = serviceLocator.getRelacionamentoService();
    }

    private ServiceLocator carregarOuCriarServiceLocator() {
        try {
            File file = new File(SISTEMA_FILE);
            if (file.exists()) {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                ServiceLocator serviceLocator = (ServiceLocator) in.readObject();
                in.close();
                return serviceLocator;
            }
        } catch (Exception e) {
            // Se houver erro ao ler o arquivo, continua com um sistema novo
        }
        return new ServiceLocator();
    }

    /**
     * Salva o estado atual do sistema.
     */
    public void encerrarSistema() {
        try {
            FileOutputStream fileOut = new FileOutputStream(SISTEMA_FILE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            ServiceLocator serviceLocator = new ServiceLocator(
                    usuarioService, sessaoService, comunidadeService,
                    mensagemService, relacionamentoService
            );
            out.writeObject(serviceLocator);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            throw new SystemSaveException("Erro ao salvar o sistema");
        }
    }

    /**
     * Reseta o sistema.
     */
    public void zerarSistema() {
        usuarioService.zerarUsuarios();
        sessaoService.zerarSessoes();
        comunidadeService.zerarComunidades();
        mensagemService.zerarMensagens();
        relacionamentoService.zerarRelacionamentos();
    }

    /**
     * Obt�m o valor de um atributo de um usu�rio.
     */
    public String getAtributoUsuario(String login, String atributo) {
        return usuarioService.getAtributoUsuario(login, atributo);
    }

    /**
     * Cria um novo usu�rio no sistema.
     */
    public void criarUsuario(String login, String senha, String nome) {
        usuarioService.criarUsuario(login, senha, nome);
    }

    /**
     * Abre uma sess�o para um usu�rio.
     */
    public String abrirSessao(String login, String senha) {
        return sessaoService.abrirSessao(login, senha);
    }

    /**
     * Encerra uma sess�o espec�fica.
     */
    public boolean encerrarSessao(String sessionId) {
        return sessaoService.encerrarSessao(sessionId);
    }

    /**
     * Verifica se uma sess�o existe.
     */
    public boolean existeSessao(String sessionId) {
        return sessaoService.existeSessao(sessionId);
    }

    /**
     * Obt�m o login do usu�rio associado a uma sess�o.
     */
    public String getLoginDaSessao(String sessionId) {
        return sessaoService.getLoginDaSessao(sessionId);
    }

    /**
     * Edita o perfil de um usu�rio.
     */
    public void editarPerfil(String sessionId, String atributo, String valor) {
        String login = sessaoService.validarEObterLogin(sessionId);
        usuarioService.editarPerfil(login, atributo, valor);
    }

    /**
     * Verifica se dois usu�rios s�o amigos.
     */
    public boolean ehAmigo(String sessionId, String amigo) {
        try {
            String login = sessaoService.validarEObterLogin(sessionId);
            return relacionamentoService.ehAmigo(login, amigo);
        } catch (SessionNotFoundException | UserNotFoundException e) {
            // Para manter compatibilidade com o comportamento original
            return false;
        }
    }

    /**
     * Adiciona um amigo para um usu�rio.
     */
    public void adicionarAmigo(String sessionId, String amigo) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarAmigo(login, amigo);
    }

    /**
     * Obt�m a lista de amigos de um usu�rio.
     */
    public String getAmigos(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return relacionamentoService.getAmigos(login);
    }

    /**
     * Envia um recado para um usu�rio.
     */
    public void enviarRecado(String sessionId, String destinatario, String recado) {
        String remetente = sessaoService.validarEObterLogin(sessionId);
        mensagemService.enviarRecado(remetente, destinatario, recado);
    }

    /**
     * L� um recado de um usu�rio.
     */
    public String lerRecado(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return mensagemService.lerRecado(login);
    }

    /**
     * Cria uma nova comunidade.
     */
    public void criarComunidade(String sessionId, String nome, String descricao) {
        String login = sessaoService.validarEObterLogin(sessionId);
        comunidadeService.criarComunidade(login, nome, descricao);
    }

    /**
     * Obt�m a descri��o de uma comunidade.
     */
    public String getDescricaoComunidade(String nome) {
        return comunidadeService.getDescricaoComunidade(nome);
    }

    /**
     * Obt�m o dono de uma comunidade.
     */
    public String getDonoComunidade(String nome) {
        return comunidadeService.getDonoComunidade(nome);
    }

    /**
     * Obt�m os membros de uma comunidade.
     */
    public String getMembrosComunidade(String nome) {
        return comunidadeService.getMembrosComunidade(nome);
    }

    /**
     * Obt�m as comunidades de um usu�rio.
     */
    public String getComunidades(String login) {
        return comunidadeService.getComunidadesDoUsuario(login);
    }

    /**
     * Adiciona um usu�rio a uma comunidade.
     */
    public void adicionarComunidade(String sessionId, String nome) {
        String login = sessaoService.validarEObterLogin(sessionId);
        comunidadeService.adicionarUsuarioAComunidade(login, nome);
    }

    /**
     * L� uma mensagem de um usu�rio.
     */
    public String lerMensagem(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return mensagemService.lerMensagemComunidade(login);
    }

    /**
     * Envia uma mensagem para uma comunidade.
     */
    public void enviarMensagem(String sessionId, String comunidade, String mensagem) {
        String login = sessaoService.validarEObterLogin(sessionId);
        mensagemService.enviarMensagemComunidade(login, comunidade, mensagem);
    }

    /**
     * Verifica se um usu�rio � f� de outro.
     */
    public boolean ehFa(String login, String idolo) {
        return relacionamentoService.ehFa(login, idolo);
    }

    /**
     * Adiciona um �dolo para um usu�rio.
     */
    public void adicionarIdolo(String sessionId, String idolo) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarIdolo(login, idolo);
    }

    /**
     * Obt�m os f�s de um usu�rio.
     */
    public String getFas(String login) {
        return relacionamentoService.getFas(login);
    }

    /**
     * Verifica se um usu�rio � paquera de outro.
     */
    public boolean ehPaquera(String sessionId, String paquera) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return relacionamentoService.ehPaquera(login, paquera);
    }

    /**
     * Adiciona uma paquera para um usu�rio.
     */
    public void adicionarPaquera(String sessionId, String paquera) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarPaquera(login, paquera);
    }

    /**
     * Obt�m as paqueras de um usu�rio.
     */
    public String getPaqueras(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return relacionamentoService.getPaqueras(login);
    }

    /**
     * Adiciona um inimigo para um usu�rio.
     */
    public void adicionarInimigo(String sessionId, String inimigo) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarInimigo(login, inimigo);
    }

    /**
     * Remove um usu�rio do sistema.
     */
    public void removerUsuario(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        usuarioService.removerUsuario(login);
        sessaoService.encerrarSessao(sessionId);
        comunidadeService.removerUsuarioDeComunidades(login);
        mensagemService.removerMensagensDoUsuario(login);
    }
}