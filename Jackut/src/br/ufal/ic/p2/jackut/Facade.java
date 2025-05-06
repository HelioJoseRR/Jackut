package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.services.*;
import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.*;

/**
 * Facade para o sistema Jackut, fornecendo uma interface simplificada.
 * Implementa o padr�o de projeto Facade, ocultando a complexidade do sistema
 * e fornecendo um ponto �nico de acesso para todas as funcionalidades.
 * Tamb�m gerencia a persist�ncia do sistema atrav�s de serializa��o.
 */
public class Facade implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Nome do arquivo para persist�ncia do sistema */
    private static final String SISTEMA_FILE = "sistema.dat";

    /** Servi�os que comp�em o sistema */
    private final UsuarioService usuarioService;
    private final SessaoService sessaoService;
    private final ComunidadeService comunidadeService;
    private final MensagemService mensagemService;
    private final RelacionamentoService relacionamentoService;

    /**
     * Construtor da classe Facade.
     * Carrega o estado anterior do sistema, se existir, ou cria um novo.
     */
    public Facade() {
        ServiceLocator serviceLocator = carregarOuCriarServiceLocator();
        this.usuarioService = serviceLocator.getUsuarioService();
        this.sessaoService = serviceLocator.getSessaoService();
        this.comunidadeService = serviceLocator.getComunidadeService();
        this.mensagemService = serviceLocator.getMensagemService();
        this.relacionamentoService = serviceLocator.getRelacionamentoService();
    }

    /**
     * Carrega o ServiceLocator de um arquivo serializado ou cria um novo.
     *
     * @return ServiceLocator carregado ou rec�m-criado
     */
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
     * Salva o estado atual do sistema em um arquivo serializado.
     *
     * @throws SystemSaveException Se ocorrer um erro ao salvar o sistema
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
     * Reseta o sistema, removendo todos os dados.
     */
    public void zerarSistema() {
        usuarioService.zerarUsuarios(); // relacionamentos s�o zerados com o usuario
        sessaoService.zerarSessoes();
        comunidadeService.zerarComunidades();
        mensagemService.zerarMensagens();
    }

    /**
     * Obt�m o valor de um atributo de um usu�rio.
     *
     * @param login Login do usu�rio
     * @param atributo Nome do atributo
     * @return Valor do atributo
     * @throws UserNotFoundException Se o usu�rio n�o existir
     * @throws ProfileAttributeException Se o atributo n�o estiver preenchido
     */
    public String getAtributoUsuario(String login, String atributo) {
        return usuarioService.getAtributoUsuario(login, atributo);
    }

    /**
     * Cria um novo usu�rio no sistema.
     *
     * @param login Login �nico do usu�rio
     * @param senha Senha para autentica��o
     * @param nome Nome completo do usu�rio
     * @throws InvalidUserDataException Se o login j� existir ou se os dados forem inv�lidos
     */
    public void criarUsuario(String login, String senha, String nome) {
        usuarioService.criarUsuario(login, senha, nome);
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
        return sessaoService.abrirSessao(login, senha);
    }

    /**
     * Encerra uma sess�o espec�fica.
     *
     * @param sessionId ID da sess�o a ser encerrada
     * @return true se a sess�o foi encerrada, false se n�o existia
     */
    public boolean encerrarSessao(String sessionId) {
        return sessaoService.encerrarSessao(sessionId);
    }

    /**
     * Verifica se uma sess�o existe.
     *
     * @param sessionId ID da sess�o
     * @return true se a sess�o existir, false caso contr�rio
     */
    public boolean existeSessao(String sessionId) {
        return sessaoService.existeSessao(sessionId);
    }

    /**
     * Obt�m o login do usu�rio associado a uma sess�o.
     *
     * @param sessionId ID da sess�o
     * @return Login do usu�rio ou null se a sess�o n�o existir
     */
    public String getLoginDaSessao(String sessionId) {
        return sessaoService.getLoginDaSessao(sessionId);
    }

    /**
     * Edita um atributo personalizado do perfil de um usu�rio.
     *
     * @param sessionId ID da sess�o ou login do usu�rio
     * @param atributo Nome do atributo
     * @param valor Novo valor do atributo
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public void editarPerfil(String sessionId, String atributo, String valor) {
        String login = sessaoService.validarEObterLogin(sessionId);
        usuarioService.editarPerfil(login, atributo, valor);
    }

    /**
     * Verifica se dois usu�rios s�o amigos.
     *
     * @param sessionId ID da sess�o ou login do primeiro usu�rio
     * @param amigo Login do poss�vel amigo
     * @return true se forem amigos, false caso contr�rio
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
     * Adiciona um usu�rio como amigo de outro.
     *
     * @param sessionId ID da sess�o ou login do usu�rio que est� adicionando
     * @param amigo Login do usu�rio a ser adicionado como amigo
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se algum dos usu�rios n�o existir
     * @throws FriendshipException Se o usu�rio tentar adicionar a si mesmo ou se j� forem amigos
     * @throws RelacionamentoException Se o usu�rio a ser adicionado for inimigo
     */
    public void adicionarAmigo(String sessionId, String amigo) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarAmigo(login, amigo);
    }

    /**
     * Obt�m a lista de amigos de um usu�rio formatada como string.
     *
     * @param sessionId ID da sess�o ou login do usu�rio
     * @return String formatada com a lista de amigos: "{amigo1,amigo2,...}"
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public String getAmigos(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return relacionamentoService.getAmigos(login);
    }

    /**
     * Envia um recado de um usu�rio para outro.
     *
     * @param sessionId ID da sess�o ou login do usu�rio remetente
     * @param destinatario Login do usu�rio destinat�rio
     * @param recado Conte�do do recado
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se algum dos usu�rios n�o existir
     * @throws MessageException Se o usu�rio tentar enviar recado para si mesmo
     * @throws RelacionamentoException Se o destinat�rio for inimigo do remetente
     */
    public void enviarRecado(String sessionId, String destinatario, String recado) {
        String remetente = sessaoService.validarEObterLogin(sessionId);
        mensagemService.enviarRecado(remetente, destinatario, recado);
    }

    /**
     * L� o pr�ximo recado dispon�vel para um usu�rio.
     *
     * @param sessionId ID da sess�o ou login do usu�rio
     * @return Conte�do do recado
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se o usu�rio n�o existir
     * @throws MessageException Se n�o houver recados
     */
    public String lerRecado(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return mensagemService.lerRecado(login);
    }

    /**
     * Cria uma nova comunidade.
     *
     * @param sessionId ID da sess�o ou login do usu�rio que est� criando
     * @param nome Nome �nico da comunidade
     * @param descricao Descri��o da comunidade
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se o usu�rio n�o existir
     * @throws CommunityException Se j� existir uma comunidade com o mesmo nome
     */
    public void criarComunidade(String sessionId, String nome, String descricao) {
        String login = sessaoService.validarEObterLogin(sessionId);
        comunidadeService.criarComunidade(login, nome, descricao);
    }

    /**
     * Obt�m a descri��o de uma comunidade.
     *
     * @param nome Nome da comunidade
     * @return Descri��o da comunidade
     * @throws CommunityException Se a comunidade n�o existir
     */
    public String getDescricaoComunidade(String nome) {
        return comunidadeService.getDescricaoComunidade(nome);
    }

    /**
     * Obt�m o login do dono de uma comunidade.
     *
     * @param nome Nome da comunidade
     * @return Login do dono da comunidade
     * @throws CommunityException Se a comunidade n�o existir
     */
    public String getDonoComunidade(String nome) {
        return comunidadeService.getDonoComunidade(nome);
    }

    /**
     * Obt�m a lista de membros de uma comunidade formatada como string.
     *
     * @param nome Nome da comunidade
     * @return String formatada com a lista de membros: "{membro1,membro2,...}"
     * @throws CommunityException Se a comunidade n�o existir
     */
    public String getMembrosComunidade(String nome) {
        return comunidadeService.getMembrosComunidade(nome);
    }

    /**
     * Obt�m a lista de comunidades de um usu�rio formatada como string.
     *
     * @param login Login do usu�rio
     * @return String formatada com a lista de comunidades: "{comunidade1,comunidade2,...}"
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public String getComunidades(String login) {
        return comunidadeService.getComunidadesDoUsuario(login);
    }

    /**
     * Adiciona um usu�rio a uma comunidade.
     *
     * @param sessionId ID da sess�o ou login do usu�rio a ser adicionado
     * @param nome Nome da comunidade
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se o usu�rio n�o existir
     * @throws CommunityException Se a comunidade n�o existir ou se o usu�rio j� for membro
     */
    public void adicionarComunidade(String sessionId, String nome) {
        String login = sessaoService.validarEObterLogin(sessionId);
        comunidadeService.adicionarUsuarioAComunidade(login, nome);
    }

    /**
     * L� a pr�xima mensagem de comunidade dispon�vel para um usu�rio.
     *
     * @param sessionId ID da sess�o ou login do usu�rio
     * @return Conte�do da mensagem formatada
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se o usu�rio n�o existir
     * @throws MessageException Se n�o houver mensagens
     */
    public String lerMensagem(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return mensagemService.lerMensagemComunidade(login);
    }

    /**
     * Envia uma mensagem para todos os membros de uma comunidade.
     *
     * @param sessionId ID da sess�o ou login do usu�rio remetente
     * @param comunidade Nome da comunidade
     * @param mensagem Conte�do da mensagem
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se o usu�rio n�o existir
     * @throws CommunityException Se a comunidade n�o existir
     */
    public void enviarMensagem(String sessionId, String comunidade, String mensagem) {
        String login = sessaoService.validarEObterLogin(sessionId);
        mensagemService.enviarMensagemComunidade(login, comunidade, mensagem);
    }

    /**
     * Verifica se um usu�rio � f� de outro.
     *
     * @param login Login do poss�vel f�
     * @param idolo Login do poss�vel �dolo
     * @return true se o primeiro for f� do segundo, false caso contr�rio
     */
    public boolean ehFa(String login, String idolo) {
        return relacionamentoService.ehFa(login, idolo);
    }

    /**
     * Adiciona um usu�rio como �dolo de outro.
     *
     * @param sessionId ID da sess�o ou login do usu�rio que est� adicionando (f�)
     * @param idolo Login do usu�rio a ser adicionado como �dolo
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se algum dos usu�rios n�o existir
     * @throws RelacionamentoException Se o usu�rio tentar ser f� de si mesmo, se j� for f� ou se o �dolo for inimigo
     */
    public void adicionarIdolo(String sessionId, String idolo) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarIdolo(login, idolo);
    }

    /**
     * Obt�m a lista de f�s de um usu�rio formatada como string.
     *
     * @param login Login do usu�rio
     * @return String formatada com a lista de f�s: "{fa1,fa2,...}"
     */
    public String getFas(String login) {
        return relacionamentoService.getFas(login);
    }

    /**
     * Verifica se um usu�rio tem outro como paquera.
     *
     * @param sessionId ID da sess�o ou login do primeiro usu�rio
     * @param paquera Login do poss�vel paquera
     * @return true se o segundo for paquera do primeiro, false caso contr�rio
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se algum dos usu�rios n�o existir
     */
    public boolean ehPaquera(String sessionId, String paquera) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return relacionamentoService.ehPaquera(login, paquera);
    }

    /**
     * Adiciona um usu�rio como paquera de outro.
     * Se ambos se adicionarem como paquera, o sistema envia recados autom�ticos.
     *
     * @param sessionId ID da sess�o ou login do usu�rio que est� adicionando
     * @param paquera Login do usu�rio a ser adicionado como paquera
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se algum dos usu�rios n�o existir
     * @throws RelacionamentoException Se o usu�rio tentar adicionar a si mesmo, se j� for paquera ou se for inimigo
     */
    public void adicionarPaquera(String sessionId, String paquera) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarPaquera(login, paquera);
    }

    /**
     * Obt�m a lista de paqueras de um usu�rio formatada como string.
     *
     * @param sessionId ID da sess�o ou login do usu�rio
     * @return String formatada com a lista de paqueras: "{paquera1,paquera2,...}"
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public String getPaqueras(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return relacionamentoService.getPaqueras(login);
    }

    /**
     * Adiciona um usu�rio como inimigo de outro.
     *
     * @param sessionId ID da sess�o ou login do usu�rio que est� adicionando
     * @param inimigo Login do usu�rio a ser adicionado como inimigo
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se algum dos usu�rios n�o existir
     * @throws RelacionamentoException Se o usu�rio tentar adicionar a si mesmo ou se j� for inimigo
     */
    public void adicionarInimigo(String sessionId, String inimigo) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarInimigo(login, inimigo);
    }

    /**
     * Remove um usu�rio do sistema.
     * Tamb�m remove suas sess�es, comunidades e mensagens.
     *
     * @param sessionId ID da sess�o ou login do usu�rio a ser removido
     * @throws SessionNotFoundException Se a sess�o n�o existir
     * @throws UserNotFoundException Se o usu�rio n�o existir
     */
    public void removerUsuario(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        usuarioService.removerUsuario(login);
        sessaoService.encerrarSessao(sessionId);
        comunidadeService.removerUsuarioDeComunidades(login);
        mensagemService.removerMensagensDoUsuario(login);
    }
}