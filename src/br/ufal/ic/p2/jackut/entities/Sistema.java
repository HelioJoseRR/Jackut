package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Classe principal que gerencia todas as entidades do sistema Jackut.
 * Controla usuários, comunidades, sessões e todas as operações relacionadas.
 */
public class Sistema implements Serializable {
    /** ID de serialização da classe */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Mapa de usuários do sistema (login -> Usuario) */
    private Map<String, Usuario> usuarios;

    /** Mapa de comunidades do sistema (nome -> Comunidade) */
    private Map<String, Comunidade> comunidades;

    /** Mapa de sessões ativas (sessionId -> login) */
    private Map<String, String> sessoes;

    /** Contador para gerar IDs de sessão únicos */
    private int nextSessionId;

    /**
     * Cria uma nova instância do sistema com estruturas vazias.
     */
    public Sistema() {
        this.usuarios = new HashMap<>();
        this.comunidades = new HashMap<>();
        this.sessoes = new HashMap<>();
        this.nextSessionId = 1;
    }

    /**
     * Reinicia o sistema, limpando todas as coleções.
     */
    public void zerarSistema() {
        this.usuarios = new HashMap<>();
        this.comunidades = new HashMap<>();
        this.sessoes = new HashMap<>();
        this.nextSessionId = 1;
    }

    /**
     * Verifica se um usuário existe no sistema.
     *
     * @param login Login do usuário
     * @return true se o usuário existe, false caso contrário
     */
    public boolean verificaUsuarioExiste(String login) {
        return this.usuarios.containsKey(login);
    }

    /**
     * Retorna um usuário pelo login.
     * Lança exceção se o usuário não existir.
     *
     * @param login Login do usuário
     * @return Objeto Usuario correspondente
     * @throws UserNotFoundException se o usuário não existir
     */
    private Usuario getUsuarioPeloLogin(String login) {
        if (!verificaUsuarioExiste(login)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }
        return usuarios.get(login);
    }

    /**
     * Obtém o login associado a uma sessão.
     *
     * @param sessionId ID da sessão
     * @return Login do usuário da sessão, ou null se a sessão não existir
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

        if(usuarioRecebe.ehInimigo(usuarioEnvia.getLogin())){
            throw new RelacionamentoException("Função inválida: " + usuarioRecebe.getNome() + " é seu inimigo.");
        }

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
        return usuario.getRelacionamento().getAmigosFormatado();
    }

    /**
     * Envia um recado para um usuário.
     */
    public void enviarRecado(String sessionId, String destinatario, String recado) {
        Usuario remetente = getUsuarioDaSessao(sessionId);
        Usuario dest = getUsuarioPeloLogin(destinatario);

        if (remetente.getLogin() == null) {
            throw new SessionNotFoundException("Sessão inválida ou expirada.");
        }

        if (remetente.getLogin().equals(destinatario)) {
            throw new MessageException("Usuário não pode enviar recado para si mesmo.");
        }

        if (dest.ehInimigo(remetente.getLogin())) {
            throw new RelacionamentoException("Função inválida: " + dest.getNome() + " é seu inimigo.");
        }

        dest.adicionarRecado(remetente.getLogin(), recado);
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
        Usuario idoloObj = getUsuarioPeloLogin(idolo);



        if (this.getUsuarioDaSessao(id).getLogin().equals(idolo)) {
            throw new RelacionamentoException("Usuário não pode ser fã de si mesmo.");
        }

        if (usuario.getIdolos().contains(idolo)) {
            throw new RelacionamentoException("Usuário já está adicionado como ídolo.");
        }

        if(idoloObj.ehInimigo(usuario.getLogin())) {
            throw new RelacionamentoException("Função inválida: " + idoloObj.getNome() + " é seu inimigo.");
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

        if (paqueraObj.ehInimigo(usuario.getLogin())) {
            throw new RelacionamentoException("Função inválida: " + paqueraObj.getNome() + " é seu inimigo.");
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

    public void adicionarInimigo(String sessionId, String inimigo){
        Usuario usuario = getUsuarioDaSessao(sessionId);

        if(!this.usuarios.containsKey(inimigo) || !this.sessoes.containsKey(sessionId)){
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        usuario.adicionarInimigo(inimigo);
    }

    public void removerUsuario(String sessionId){
        String login = getLoginDaSessao(sessionId);
        Usuario removido = usuarios.remove(login);
        String sessaoRemovida = sessoes.remove(sessionId);

        if (sessaoRemovida == null || removido == null) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        Iterator<Map.Entry<String, Comunidade>> iter = comunidades.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Comunidade> entry = iter.next();
            String nomeComunidade = entry.getKey();
            Comunidade com = entry.getValue();

            // Supondo que Comunidade.getDono() retorna o login do dono
            if (com.getDono().equals(login)) {
                // Remove a comunidade de todos os membros
                for (String membro : com.getMembrosList()) {
                    Usuario u = usuarios.get(membro);
                    if (u != null) {
                        u.removerComunidade(nomeComunidade);
                    }
                }
                // Remove a comunidade do sistema
                iter.remove();
            }
        }

        // Remove recados enviados por esse usuário dos outros usuários
        for (Usuario u : usuarios.values()) {
            Queue<Recado> recadosFiltrados = new LinkedList<>();
            for (Recado r : u.getRecados()) {
                if (!r.getRemetente().equals(login)) {
                    recadosFiltrados.add(r);
                }
            }

            u.setRecados(recadosFiltrados);
        }
    }
}