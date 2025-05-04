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
     * Obtém o valor de um atributo de um usuário.
     */
    public String getAtributoUsuario(String login, String atributo) {
        return usuarioService.getAtributoUsuario(login, atributo);
    }

    /**
     * Cria um novo usuário no sistema.
     */
    public void criarUsuario(String login, String senha, String nome) {
        usuarioService.criarUsuario(login, senha, nome);
    }

    /**
     * Abre uma sessão para um usuário.
     */
    public String abrirSessao(String login, String senha) {
        return sessaoService.abrirSessao(login, senha);
    }

    /**
     * Encerra uma sessão específica.
     */
    public boolean encerrarSessao(String sessionId) {
        return sessaoService.encerrarSessao(sessionId);
    }

    /**
     * Verifica se uma sessão existe.
     */
    public boolean existeSessao(String sessionId) {
        return sessaoService.existeSessao(sessionId);
    }

    /**
     * Obtém o login do usuário associado a uma sessão.
     */
    public String getLoginDaSessao(String sessionId) {
        return sessaoService.getLoginDaSessao(sessionId);
    }

    /**
     * Edita o perfil de um usuário.
     */
    public void editarPerfil(String sessionId, String atributo, String valor) {
        String login = sessaoService.validarEObterLogin(sessionId);
        usuarioService.editarPerfil(login, atributo, valor);
    }

    /**
     * Verifica se dois usuários são amigos.
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
     * Adiciona um amigo para um usuário.
     */
    public void adicionarAmigo(String sessionId, String amigo) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarAmigo(login, amigo);
    }

    /**
     * Obtém a lista de amigos de um usuário.
     */
    public String getAmigos(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return relacionamentoService.getAmigos(login);
    }

    /**
     * Envia um recado para um usuário.
     */
    public void enviarRecado(String sessionId, String destinatario, String recado) {
        String remetente = sessaoService.validarEObterLogin(sessionId);
        mensagemService.enviarRecado(remetente, destinatario, recado);
    }

    /**
     * Lê um recado de um usuário.
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
     * Obtém a descrição de uma comunidade.
     */
    public String getDescricaoComunidade(String nome) {
        return comunidadeService.getDescricaoComunidade(nome);
    }

    /**
     * Obtém o dono de uma comunidade.
     */
    public String getDonoComunidade(String nome) {
        return comunidadeService.getDonoComunidade(nome);
    }

    /**
     * Obtém os membros de uma comunidade.
     */
    public String getMembrosComunidade(String nome) {
        return comunidadeService.getMembrosComunidade(nome);
    }

    /**
     * Obtém as comunidades de um usuário.
     */
    public String getComunidades(String login) {
        return comunidadeService.getComunidadesDoUsuario(login);
    }

    /**
     * Adiciona um usuário a uma comunidade.
     */
    public void adicionarComunidade(String sessionId, String nome) {
        String login = sessaoService.validarEObterLogin(sessionId);
        comunidadeService.adicionarUsuarioAComunidade(login, nome);
    }

    /**
     * Lê uma mensagem de um usuário.
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
     * Verifica se um usuário é fã de outro.
     */
    public boolean ehFa(String login, String idolo) {
        return relacionamentoService.ehFa(login, idolo);
    }

    /**
     * Adiciona um ídolo para um usuário.
     */
    public void adicionarIdolo(String sessionId, String idolo) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarIdolo(login, idolo);
    }

    /**
     * Obtém os fãs de um usuário.
     */
    public String getFas(String login) {
        return relacionamentoService.getFas(login);
    }

    /**
     * Verifica se um usuário é paquera de outro.
     */
    public boolean ehPaquera(String sessionId, String paquera) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return relacionamentoService.ehPaquera(login, paquera);
    }

    /**
     * Adiciona uma paquera para um usuário.
     */
    public void adicionarPaquera(String sessionId, String paquera) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarPaquera(login, paquera);
    }

    /**
     * Obtém as paqueras de um usuário.
     */
    public String getPaqueras(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return relacionamentoService.getPaqueras(login);
    }

    /**
     * Adiciona um inimigo para um usuário.
     */
    public void adicionarInimigo(String sessionId, String inimigo) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarInimigo(login, inimigo);
    }

    /**
     * Remove um usuário do sistema.
     */
    public void removerUsuario(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        usuarioService.removerUsuario(login);
        sessaoService.encerrarSessao(sessionId);
        comunidadeService.removerUsuarioDeComunidades(login);
        mensagemService.removerMensagensDoUsuario(login);
    }
}