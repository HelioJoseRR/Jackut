package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * A classe Sistema representa o sistema principal do Jackut.
 */
public class Sistema implements Serializable {
    private Map<String, Usuario> usuarios;
    private Map<String, Comunidade> comunidades;
    private Map<String, String> sessoes; // Map de sessionId para login
    private int nextSessionId;
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Sistema.
     */
    public Sistema() {
        this.usuarios = new HashMap<>();
        this.comunidades = new HashMap<>();
        this.sessoes = new HashMap<>();
        this.nextSessionId = 1;
    }

    /**
     * Reseta o sistema.
     */
    public void zerarSistema() {
        this.usuarios = new HashMap<>();
        this.comunidades = new HashMap<>();
        this.sessoes = new HashMap<>();
        this.nextSessionId = 1;
    }

    /**
     * Verifica se um usuário existe no sistema.
     */
    public boolean verificaUsuarioExiste(String login) {
        return this.usuarios.containsKey(login);
    }

    /**
     * Retorna um usuário pelo login.
     */
    private Usuario getUsuarioPeloLogin(String login) {
        if (!verificaUsuarioExiste(login)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        return usuarios.get(login);
    }

    /**
     * Obtém o login associado a uma sessão.
     */
    public String getLoginDaSessao(String sessionId) {
        return sessoes.get(sessionId);
    }

    /**
     * Verifica se uma sessão existe.
     */
    public boolean existeSessao(String sessionId) {
        return sessoes.containsKey(sessionId);
    }

    /**
     * Encerra uma sessão.
     */
    public boolean encerrarSessao(String sessionId) {
        if (existeSessao(sessionId)) {
            sessoes.remove(sessionId);
            return true;
        }
        return false;
    }

    /**
     * Obtém o usuário associado a uma sessão.
     */
    private Usuario getUsuarioDaSessao(String sessionId) {
        String login = getLoginDaSessao(sessionId);

        if (login == null) {
            if (verificaUsuarioExiste(sessionId)) {
                return getUsuarioPeloLogin(sessionId);
            }

            throw new SessionNotFoundException("Usuário não cadastrado.");
        }

        return getUsuarioPeloLogin(login);
    }

    /**
     * Cria um novo usuário no sistema.
     */
    public void criarUsuario(String login, String senha, String nome) {
        if (login == null) {
            throw new InvalidUserDataException("Login inválido.");
        }
        if (senha == null) {
            throw new InvalidUserDataException("Senha inválida.");
        }
        if (verificaUsuarioExiste(login)) {
            throw new InvalidUserDataException("Conta com esse nome já existe.");
        }

        usuarios.put(login, new Usuario(login, senha, nome));
    }

    /**
     * Cria uma nova sessão para um usuário.
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
     * Obtém o valor de um atributo de um usuário.
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
     * Edita o perfil de um usuário.
     */
    public void editarPerfil(String sessionId, String atributo, String valor) {
        Usuario usuario = getUsuarioDaSessao(sessionId);
        usuario.setAtributo(atributo, valor);
    }

    /**
     * Verifica se dois usuários são amigos.
     */
    public boolean ehAmigo(String sessionId, String amigo) {
        Usuario usuario = getUsuarioDaSessao(sessionId);
        return usuario.getAmigos().contains(amigo);
    }

    /**
     * Adiciona um amigo para um usuário.
     */
    public void adicionarAmigo(String sessionId, String amigo) {
        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Usuário não cadastrado.");
        }

        if (login.equals(amigo)) {
            throw new FriendshipException("Usuário não pode adicionar a si mesmo como amigo.");
        }

        Usuario usuarioEnvia = getUsuarioPeloLogin(login);
        Usuario usuarioRecebe = getUsuarioPeloLogin(amigo);

        if (usuarioEnvia.getConvitesAmizade().contains(amigo)) {
            // Aceitar convite pendente (ambos já enviaram convites)
            usuarioRecebe.adicionarAmigo(login);
            usuarioEnvia.adicionarAmigo(amigo);
            usuarioRecebe.removerConviteAmizade(login);
            usuarioEnvia.removerConviteAmizade(amigo);
            return;
        }

        if (usuarioRecebe.getConvitesAmizade().contains(login)) {
            throw new FriendshipException("Usuário já está adicionado como amigo, esperando aceitação do convite.");
        }

        if (usuarioRecebe.getAmigos().contains(login)) {
            throw new FriendshipException("Usuário já está adicionado como amigo.");
        }

        usuarioRecebe.adicionarConviteAmizade(login);
    }

    /**
     * Obtém a lista de amigos de um usuário.
     */
    public String getAmigos(String sessionId) {
        Usuario usuario = getUsuarioDaSessao(sessionId);
        return usuario.getAmigosFormatado();
    }

    /**
     * Envia um recado para um usuário.
     */
    public void enviarRecado(String sessionId, String destinatario, String recado) {
        String login = getLoginDaSessao(sessionId);

        if (login == null) {
            throw new SessionNotFoundException("Sessão inválida ou expirada.");
        }

        if (login.equals(destinatario)) {
            throw new MessageException("Usuário não pode enviar recado para si mesmo.");
        }

        Usuario recebeRecado = getUsuarioPeloLogin(destinatario);
        recebeRecado.adicionarRecado(login, recado);
    }

    /**
     * Lê um recado de um usuário.
     */
    public String lerRecado(String sessionId) {
        Usuario usuario = getUsuarioDaSessao(sessionId);

        if (usuario.getRecados().isEmpty()) {
            throw new MessageException("Não há recados.");
        }

        Recado recado = usuario.getRecados().poll();

        return recado.getConteudo();
    }

    /**
     * Cria uma nova comunidade no sistema.
     */
    public void criarComunidade(String sessionId, String nome, String descricao) {
        if (comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade com esse nome já existe.");
        }

        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Usuário não cadastrado.");
        }

        Comunidade comunidade = new Comunidade(sessionId, nome, descricao);
        comunidade.addMembro(login);
        comunidades.put(nome, comunidade);

        this.usuarios.get(login).addComunidade(nome);
    }

    /**
     * Obtém a descrição de uma comunidade.
     */
    public String getDescricaoComunidade(String nome) {
        if (!comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade não existe.");
        }
        return comunidades.get(nome).getDescricao();
    }

    /**
     * Obtém o dono de uma comunidade.
     */
    public String getDonoComunidade(String nome) {
        if (!comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade não existe.");
        }
        return getLoginDaSessao(comunidades.get(nome).getSessionID());
    }

    /**
     * Obtém os membros de uma comunidade.
     */
    public String getMembrosComunidade(String nome) {
        if (!comunidades.containsKey(nome)) {
            throw new CommunityException("Comunidade não existe.");
        }
        return comunidades.get(nome).getMembros();
    }

    /**
     * Obtém as comunidades de um usuário.
     */
    public String getComunidades(String login) {
        if (login == null || login.isEmpty() || !verificaUsuarioExiste(login)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        return usuarios.get(login).getComunidadesCadastradas();
    }

    /**
     * Adiciona um usuário a uma comunidade.
     */
    public void adicionarComunidade(String sessionId, String comunidade) {
        if (!existeSessao(sessionId)) {
            throw new SessionNotFoundException("Usuário não cadastrado.");
        }

        String login = getLoginDaSessao(sessionId);

        if (!comunidades.containsKey(comunidade)) {
            throw new CommunityException("Comunidade não existe.");
        }

        Comunidade comunidadeObj = comunidades.get(comunidade);

        if (comunidadeObj.isMembro(login)) {
            throw new CommunityException("Usuario já faz parte dessa comunidade.");
        }

        Usuario usuario = usuarios.get(login);

        comunidadeObj.addMembro(login);
        usuario.addComunidade(comunidade);
    }

    /**
     * Lê uma mensagem de um usuário.
     */
    public String lerMensagem(String sessionId) {
        if (!existeSessao(sessionId)) {
            throw new SessionNotFoundException("Sessão inválida ou expirada.");
        }

        Usuario usuario = getUsuarioPeloLogin(getLoginDaSessao(sessionId));

        if (usuario.getMensagens().isEmpty()) {
            throw new MessageException("Não há mensagens.");
        }

        return usuario.getMensagens().poll().toString();
    }

    /**
     * Envia uma mensagem para uma comunidade.
     */
    public void enviarMensagem(String sessionId, String comunidade, String mensagem) {
        if (!existeSessao(sessionId)) {
            throw new SessionNotFoundException("Usuário não cadastrado.");
        }

        if (!comunidades.containsKey(comunidade)) {
            throw new CommunityException("Comunidade não existe.");
        }

        String login = getLoginDaSessao(sessionId);

        Mensagem novaMensagem = new Mensagem(login, mensagem, comunidade);
        
        for(Usuario usuario : usuarios.values()) {
            if(usuario.getComunidadesCadastradas().contains(comunidade)) {
                usuario.adicionarMensagem(novaMensagem);
            }
        }
    }

    public boolean ehFa(String login, String idolo){
        Usuario usuario = getUsuarioPeloLogin(login);

        return usuario.getIdolos().contains(idolo);
    }

    public void adicionarIdolo(String id, String idolo){
        Usuario usuario = this.getUsuarioDaSessao(id);

        if (usuario == null || !this.sessoes.containsValue(idolo)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        if (this.getUsuarioDaSessao(id).getLogin().equals(idolo)) {
            throw new RelacionamentoException("Usuário não pode ser fã de si mesmo.");
        }

        if (usuario.getIdolos().contains(idolo)) {
            throw new RelacionamentoException("Usuário já está adicionado como ídolo.");
        }

        usuario.getIdolos().add(idolo);

        return;
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
        this.usuarios.get(login).adicionarRecado("jackut", recado + " é seu paquera - Recado do Jackut.");
    }

    public void adicionarPaquera(String sessionId, String paquera){
        Usuario usuario = getUsuarioDaSessao(sessionId);
        Usuario paqueraObj = getUsuarioDaSessao(paquera);

        if(usuario.getPaqueras().contains(paquera)){
            throw new RelacionamentoException("Usuário já está adicionado como paquera.");
        }

        if(usuario.getLogin().equals(paquera)){
            throw new RelacionamentoException("Usuário não pode ser paquera de si mesmo.");
        }

        usuario.adicionarPaquera(paquera);

        if (usuario.getPaqueras().contains(paquera) && paqueraObj.getPaqueras().contains(usuario.getLogin())) {
            adicionarRecadoJackut(usuario.getLogin(), paqueraObj.getNome());
            adicionarRecadoJackut(paqueraObj.getLogin(), usuario.getNome());
        }

        return;
    }

    public String getPaqueras(String sessionId){
        Usuario usuario = getUsuarioDaSessao(sessionId);

        return usuario.getPaqueras();
    }
}