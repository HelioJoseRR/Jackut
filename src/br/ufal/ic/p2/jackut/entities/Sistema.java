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
     * Reinicia o sistema, limpando todas as coleções.
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

        donoParaComunidades.putIfAbsent(login, new HashSet<>());

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

        mensagemRecado.putIfAbsent(dest.getLogin(), new LinkedList<>());
        mensagemRecado.get(dest.getLogin()).add(Comunicacao.criarRecado(remetente.getLogin(), dest.getLogin(), recado));
    }

    /**
     * Lê um recado de um usuário.
     */
    public String lerRecado(String sessionId) {
        Usuario usuario = getUsuarioDaSessao(sessionId);
        mensagemRecado.putIfAbsent(usuario.getLogin(), new LinkedList<>());

        List<Comunicacao> recadosUsuario = getMensagensDoTipo(usuario.getLogin(), "recado");

        if (recadosUsuario.isEmpty()) {
            throw new MessageException("Não há recados.");
        }

        Comunicacao recado = recadosUsuario.remove(0);
        mensagemRecado.get(usuario.getLogin()).remove(recado);

        return recado.getConteudo();
    }

    /**
     * Filtra mensagens por tipo para um usuário específico.
     *
     * @param login Login do usuário
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
            throw new CommunityException("Comunidade com esse nome já existe.");
        }

        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Usuário não cadastrado.");
        }

        Comunidade comunidade = new Comunidade(sessionId, nome, descricao);
        comunidade.addMembro(login);
        comunidades.put(nome, comunidade);
        donoParaComunidades.get(login).add(nome);

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

        String login = getLoginDaSessao(sessionId);
        mensagemRecado.putIfAbsent(login, new ArrayList<>());

        List<Comunicacao> mensagensComunidade = getMensagensDoTipo(login, "comunidade");

        if (mensagensComunidade.isEmpty()) {
            throw new MessageException("Não há mensagens.");
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
            throw new SessionNotFoundException("Usuário não cadastrado.");
        }

        if (!comunidades.containsKey(comunidade)) {
            throw new CommunityException("Comunidade não existe.");
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
            throw new RelacionamentoException("Usuário já está adicionado como ídolo.");
        }

        if(idoloObj.ehInimigo(usuario.getLogin())) {
            throw new RelacionamentoException("Função inválida: " + idoloObj.getNome() + " é seu inimigo.");
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
        this.mensagemRecado.get(login).add(Comunicacao.criarRecado("jackut", login, recado + " é seu paquera - Recado do Jackut."));
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

    public void removerUsuario(String sessionId) {
        String login = getLoginDaSessao(sessionId);
        Usuario removido = usuarios.remove(login);
        sessoes.remove(sessionId);

        if (removido == null) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        // Remover comunidades onde o usuário é DONO
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

        // Remover mensagens enviadas pelo usuário
        for (List<Comunicacao> listaMensagens : mensagemRecado.values()) {
            listaMensagens.removeIf(mensagem -> mensagem.getRemetente().equals(login));
        }
        mensagemRecado.remove(login); // Remove as mensagens recebidas pelo usuário
    }
}