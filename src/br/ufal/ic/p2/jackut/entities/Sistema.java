package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Sistema implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Map<String, Usuario> usuarios;
    private Map<String, String> sessoes;
    private Map<String, List<Comunicacao>> mensagemRecado;
    private Map<String, Comunidade> comunidades;
    private Map<String, Set<String>> donoParaComunidades;
    private int nextSessionId;

    public Sistema() {
        this.usuarios = new HashMap<>();
        this.comunidades = new HashMap<>();
        this.sessoes = new HashMap<>();
        this.mensagemRecado = new HashMap<>();
        this.donoParaComunidades = new HashMap<>();
        this.nextSessionId = 1;
    }

    /**
     * Reinicia o sistema, limpando todas as cole��es.
     */
    public void zerarSistema() {
        this.usuarios = new HashMap<>();
        this.comunidades = new HashMap<>();
        this.sessoes = new HashMap<>();
        this.mensagemRecado = new HashMap<>();
        this.donoParaComunidades = new HashMap<>();
        this.nextSessionId = 1;
    }

    /**
     * Verifica se um usu�rio existe no sistema.
     *
     * @param login Login do usu�rio
     * @return true se o usu�rio existe, false caso contr�rio
     */
    public boolean verificaUsuarioExiste(String login) {
        return this.usuarios.containsKey(login);
    }

    /**
     * Retorna um usu�rio pelo login.
     * Lan�a exce��o se o usu�rio n�o existir.
     *
     * @param login Login do usu�rio
     * @return Objeto Usuario correspondente
     * @throws UserNotFoundException se o usu�rio n�o existir
     */
    private Usuario getUsuarioPeloLogin(String login) {
        if (!verificaUsuarioExiste(login)) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }
        return usuarios.get(login);
    }

    /**
     * Obt�m o login associado a uma sess�o.
     *
     * @param sessionId ID da sess�o
     * @return Login do usu�rio da sess�o, ou null se a sess�o n�o existir
     */
    public String getLoginDaSessao(String sessionId) {
        return sessoes.get(sessionId);
    }

    /**
     * Verifica se uma sess�o existe.
     */
    public boolean existeSessao(String sessionId) {
        return sessoes.containsKey(sessionId);
    }

    /**
     * Encerra uma sess�o.
     */
    public boolean encerrarSessao(String sessionId) {
        if (existeSessao(sessionId)) {
            sessoes.remove(sessionId);
            return true;
        }
        return false;
    }

    /**
     * Obt�m o usu�rio associado a uma sess�o.
     */
    private Usuario getUsuarioDaSessao(String sessionId) {
        String login = getLoginDaSessao(sessionId);

        if (login == null) {
            if (verificaUsuarioExiste(sessionId)) {
                return getUsuarioPeloLogin(sessionId);
            }

            throw new SessionNotFoundException("Usu�rio n�o cadastrado.");
        }

        return getUsuarioPeloLogin(login);
    }

    /**
     * Cria um novo usu�rio no sistema.
     */
    public void criarUsuario(String login, String senha, String nome) {
        if (login == null) {
            throw new InvalidUserDataException("Login inv�lido.");
        }
        if (senha == null) {
            throw new InvalidUserDataException("Senha inv�lida.");
        }
        if (verificaUsuarioExiste(login)) {
            throw new InvalidUserDataException("Conta com esse nome j� existe.");
        }

        donoParaComunidades.putIfAbsent(login, new HashSet<>());

        usuarios.put(login, new Usuario(login, senha, nome));
    }

    /**
     * Cria uma nova sess�o para um usu�rio.
     */
    public String criarSessao(String login, String senha) {
        if (!usuarios.containsKey(login)) {
            throw new AuthenticationException();
        }

        Usuario usuario = usuarios.get(login);

        if (!usuario.isPasswordValid(senha)) {
            throw new AuthenticationException();
        }

        String sessionId = String.valueOf(nextSessionId++);
        sessoes.put(sessionId, login);

        return sessionId;
    }

    /**
     * Obt�m o valor de um atributo de um usu�rio.
     */
    public String getAtributoUsuario(String login, String atributo) {
        Usuario usuario = getUsuarioPeloLogin(login);
        switch (atributo) {
            case "nome":
                return usuario.getNome();
            case "login":
                return usuario.getLogin();
            default:
                return usuario.getAtributo(atributo);
        }
    }

    /**
     * Edita o perfil de um usu�rio.
     */
    public void editarPerfil(String sessionId, String atributo, String valor) {
        Usuario usuario = getUsuarioDaSessao(sessionId);
        usuario.setAtributo(atributo, valor);
    }

    /**
     * Verifica se dois usu�rios s�o amigos.
     */
    public boolean ehAmigo(String sessionId, String amigo) {
        Usuario usuario = getUsuarioDaSessao(sessionId);
        return usuario.getAmigos().contains(amigo);
    }

    /**
     * Adiciona um amigo para um usu�rio.
     */
    public void adicionarAmigo(String sessionId, String amigo) {
        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Usu�rio n�o cadastrado.");
        }

        if (login.equals(amigo)) {
            throw new FriendshipException("Usu�rio n�o pode adicionar a si mesmo como amigo.");
        }

        Usuario usuarioEnvia = getUsuarioPeloLogin(login);
        Usuario usuarioRecebe = getUsuarioPeloLogin(amigo);

        if(usuarioRecebe.ehInimigo(usuarioEnvia.getLogin())){
            throw new RelacionamentoException("Fun��o inv�lida: " + usuarioRecebe.getNome() + " � seu inimigo.");
        }

        if (usuarioEnvia.getConvitesAmizade().contains(amigo)) {
            // Aceitar convite pendente (ambos j� enviaram convites)
            usuarioRecebe.adicionarAmigo(login);
            usuarioEnvia.adicionarAmigo(amigo);
            usuarioRecebe.removerConviteAmizade(login);
            usuarioEnvia.removerConviteAmizade(amigo);
            return;
        }

        if (usuarioRecebe.getConvitesAmizade().contains(login)) {
            throw new FriendshipException("Usu�rio j� est� adicionado como amigo, esperando aceita��o do convite.");
        }

        if (usuarioRecebe.getAmigos().contains(login)) {
            throw new FriendshipException("Usu�rio j� est� adicionado como amigo.");
        }

        usuarioRecebe.adicionarConviteAmizade(login);
    }

    /**
     * Obt�m a lista de amigos de um usu�rio.
     */
    public String getAmigos(String sessionId) {
        Usuario usuario = getUsuarioDaSessao(sessionId);
        return usuario.getRelacionamento().getAmigosFormatado();
    }

    /**
     * Envia um recado para um usu�rio.
     */
    public void enviarRecado(String sessionId, String destinatario, String recado) {
        Usuario remetente = getUsuarioDaSessao(sessionId);
        Usuario dest = getUsuarioPeloLogin(destinatario);

        if (remetente.getLogin() == null) {
            throw new SessionNotFoundException("Sess�o inv�lida ou expirada.");
        }

        if (remetente.getLogin().equals(destinatario)) {
            throw new MessageException("Usu�rio n�o pode enviar recado para si mesmo.");
        }

        if (dest.ehInimigo(remetente.getLogin())) {
            throw new RelacionamentoException("Fun��o inv�lida: " + dest.getNome() + " � seu inimigo.");
        }

        mensagemRecado.putIfAbsent(dest.getLogin(), new LinkedList<>());
        mensagemRecado.get(dest.getLogin()).add(Comunicacao.criarRecado(remetente.getLogin(), dest.getLogin(), recado));
    }

    /**
     * L� um recado de um usu�rio.
     */
    public String lerRecado(String sessionId) {
        Usuario usuario = getUsuarioDaSessao(sessionId);
        mensagemRecado.putIfAbsent(usuario.getLogin(), new LinkedList<>());

        List<Comunicacao> recadosUsuario = getMensagensDoTipo(usuario.getLogin(), "recado");

        if (recadosUsuario.isEmpty()) {
            throw new MessageException("N�o h� recados.");
        }

        Comunicacao recado = recadosUsuario.remove(0);
        mensagemRecado.get(usuario.getLogin()).remove(recado);

        return recado.getConteudo();
    }

    /**
     * Filtra mensagens por tipo para um usu�rio espec�fico.
     *
     * @param login Login do usu�rio
     * @param tipo Tipo de mensagem ("recado" ou "comunidade")
     * @return Lista de mensagens do tipo especificado
     */
    private List<Comunicacao> getMensagensDoTipo(String login, String tipo) {
        List<Comunicacao> mensagensDoTipo = new ArrayList<>();
        List<Comunicacao> todasMensagens = mensagemRecado.getOrDefault(login, new ArrayList<>());

        for (Comunicacao mensagem : todasMensagens) {
            if (tipo.equals(mensagem.getTipo())) {
                mensagensDoTipo.add(mensagem);
            }
        }

        return mensagensDoTipo;
    }

    /**
     * Cria uma nova comunidade no sistema.
     */
    public void criarComunidade(String sessionId, String nome, String descricao) {
        if (comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade com esse nome j� existe.");
        }

        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Usu�rio n�o cadastrado.");
        }

        Comunidade comunidade = new Comunidade(sessionId, nome, descricao);
        comunidade.addMembro(login);
        comunidades.put(nome, comunidade);
        donoParaComunidades.get(login).add(nome);

        this.usuarios.get(login).addComunidade(nome);
    }

    /**
     * Obt�m a descri��o de uma comunidade.
     */
    public String getDescricaoComunidade(String nome) {
        if (!comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade n�o existe.");
        }
        return comunidades.get(nome).getDescricao();
    }

    /**
     * Obt�m o dono de uma comunidade.
     */
    public String getDonoComunidade(String nome) {
        if (!comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade n�o existe.");
        }
        return getLoginDaSessao(comunidades.get(nome).getSessionID());
    }

    /**
     * Obt�m os membros de uma comunidade.
     */
    public String getMembrosComunidade(String nome) {
        if (!comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade n�o existe.");
        }

        return comunidades.get(nome).getMembros();
    }

    /**
     * Obt�m as comunidades de um usu�rio.
     */
    public String getComunidades(String login) {
        if (login == null || login.isEmpty() || !verificaUsuarioExiste(login)) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        return usuarios.get(login).getComunidadesCadastradas();
    }

    /**
     * Adiciona um usu�rio a uma comunidade.
     */
    public void adicionarComunidade(String sessionId, String comunidade) {
        if (!existeSessao(sessionId)) {
            throw new SessionNotFoundException("Usu�rio n�o cadastrado.");
        }

        String login = getLoginDaSessao(sessionId);

        if (!comunidades.containsKey(comunidade)) {
            throw new CommunityException("Comunidade n�o existe.");
        }

        Comunidade comunidadeObj = comunidades.get(comunidade);

        if (comunidadeObj.isMembro(login)) {
            throw new CommunityException("Usuario j� faz parte dessa comunidade.");
        }

        Usuario usuario = usuarios.get(login);

        comunidadeObj.addMembro(login);
        usuario.addComunidade(comunidade);
    }

    /**
     * L� uma mensagem de um usu�rio.
     */
    public String lerMensagem(String sessionId) {
        if (!existeSessao(sessionId)) {
            throw new SessionNotFoundException("Sess�o inv�lida ou expirada.");
        }

        String login = getLoginDaSessao(sessionId);
        mensagemRecado.putIfAbsent(login, new ArrayList<>());

        List<Comunicacao> mensagensComunidade = getMensagensDoTipo(login, "comunidade");

        if (mensagensComunidade.isEmpty()) {
            throw new MessageException("N�o h� mensagens.");
        }

        Comunicacao mensagem = mensagensComunidade.remove(0);
        mensagemRecado.get(login).remove(mensagem);

        return mensagem.toString();
    }

    /**
     * Envia uma mensagem para uma comunidade.
     */
    public void enviarMensagem(String sessionId, String comunidade, String mensagem) {
        if (!existeSessao(sessionId)) {
            throw new SessionNotFoundException("Usu�rio n�o cadastrado.");
        }

        if (!comunidades.containsKey(comunidade)) {
            throw new CommunityException("Comunidade n�o existe.");
        }

        String login = getLoginDaSessao(sessionId);

        Comunicacao novaMensagem = Comunicacao.criarMensagemComunidade(login, comunidade, mensagem);

        List<String> membros = comunidades.get(comunidade).getMembrosList();

        for (String membro : membros) {
            mensagemRecado.putIfAbsent(membro, new ArrayList<>());
            mensagemRecado.get(membro).add(novaMensagem);
        }
    }

    public boolean ehFa(String login, String idolo){
        Usuario usuario = getUsuarioPeloLogin(login);

        return usuario.getIdolos().contains(idolo);
    }

    public void adicionarIdolo(String id, String idolo){
        Usuario usuario = this.getUsuarioDaSessao(id);
        Usuario idoloObj = getUsuarioPeloLogin(idolo);

        if (usuario.getIdolos().contains(idolo)) {
            throw new RelacionamentoException("Usu�rio j� est� adicionado como �dolo.");
        }

        if(idoloObj.ehInimigo(usuario.getLogin())) {
            throw new RelacionamentoException("Fun��o inv�lida: " + idoloObj.getNome() + " � seu inimigo.");
        }

        usuario.adicionarIdolo(idolo);
    }

    public String getFas(String login) {
        List<String> fasList = new ArrayList<>();

        for (Usuario usuario : usuarios.values()) {
            if (!usuario.getLogin().equals(login) && usuario.getIdolos().contains(login)) {
                fasList.add(usuario.getLogin());
            }
        }

        return "{" + String.join(",", fasList) + "}";
    }

    public boolean ehPaquera(String sessionId, String paquera){
        Usuario usuario = getUsuarioDaSessao(sessionId);
        return usuario.ehPaquera(paquera);
    }

    public void adicionarRecadoJackut(String login, String recado){
        this.mensagemRecado.putIfAbsent(login, new ArrayList<>());
        this.mensagemRecado.get(login).add(Comunicacao.criarRecado("jackut", login, recado + " � seu paquera - Recado do Jackut."));
    }

    public void adicionarPaquera(String sessionId, String paquera){
        Usuario usuario = getUsuarioDaSessao(sessionId);
        Usuario paqueraObj = getUsuarioDaSessao(paquera);

        if(usuario.getPaqueras().contains(paquera)){
            throw new RelacionamentoException("Usu�rio j� est� adicionado como paquera.");
        }

        if(usuario.getLogin().equals(paquera)){
            throw new RelacionamentoException("Usu�rio n�o pode ser paquera de si mesmo.");
        }

        if (paqueraObj.ehInimigo(usuario.getLogin())) {
            throw new RelacionamentoException("Fun��o inv�lida: " + paqueraObj.getNome() + " � seu inimigo.");
        }

        usuario.adicionarPaquera(paquera);

        if (usuario.getPaqueras().contains(paquera) && paqueraObj.getPaqueras().contains(usuario.getLogin())) {
            adicionarRecadoJackut(usuario.getLogin(), paqueraObj.getNome());
            adicionarRecadoJackut(paqueraObj.getLogin(), usuario.getNome());
        }
    }

    public String getPaqueras(String sessionId){
        Usuario usuario = getUsuarioDaSessao(sessionId);

        return usuario.getPaqueras();
    }

    public void adicionarInimigo(String sessionId, String inimigo){
        Usuario usuario = getUsuarioDaSessao(sessionId);

        if(!this.usuarios.containsKey(inimigo) || !this.sessoes.containsKey(sessionId)){
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        usuario.adicionarInimigo(inimigo);
    }

    public void removerUsuario(String sessionId) {
        String login = getLoginDaSessao(sessionId);
        Usuario removido = usuarios.remove(login);
        sessoes.remove(sessionId);

        if (removido == null) {
            throw new UserNotFoundException("Usu�rio n�o cadastrado.");
        }

        // Remover comunidades onde o usu�rio � DONO
        Set<String> comunidadesDoDono = donoParaComunidades.getOrDefault(login, new HashSet<>());
        Iterator<String> iterator = comunidadesDoDono.iterator();
        while (iterator.hasNext()) {
            String comunidadeNome = iterator.next();
            Comunidade comunidade = comunidades.get(comunidadeNome);
            if (comunidade != null) {
                // Remove a comunidade de todos os membros
                for (String membro : comunidade.getMembrosList()) {
                    Usuario usuarioMembro = usuarios.get(membro);
                    if (usuarioMembro != null) {
                        usuarioMembro.removerComunidadeCadastrada(comunidadeNome);
                    }
                }
                comunidades.remove(comunidadeNome); // Remove do mapa de comunidades
            }
            iterator.remove(); // Remove do conjunto do dono
        }

        donoParaComunidades.remove(login);

        // Remover mensagens enviadas pelo usu�rio
        for (List<Comunicacao> listaMensagens : mensagemRecado.values()) {
            listaMensagens.removeIf(mensagem -> mensagem.getRemetente().equals(login));
        }
        mensagemRecado.remove(login); // Remove as mensagens recebidas pelo usu�rio
    }
}