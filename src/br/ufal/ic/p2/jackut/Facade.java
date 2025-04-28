package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.entities.*;
import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.*;

/**
 * A classe Facade fornece uma interface simplificada para interagir com o sistema Jackut.
 * Implementa a interface Serializable para permitir a serialização dos objetos.
 */
public class Facade implements Serializable {
    private Sistema sistema;
    private static final long serialVersionUID = 1L;
    private static final String SISTEMA_FILE = "sistema.dat";

    /**
     * Construtor da classe Facade.
     * Inicializa o sistema lendo os dados do arquivo "sistema.dat".
     */
    public Facade() {
        this.sistema = new Sistema(); // Inicialização padrão para evitar NullPointerException
        this.readSistema();
    }

    /**
     * Salva o estado atual do sistema no arquivo "sistema.dat".
     */
    public void saveSistema() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(SISTEMA_FILE);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

            objectOutputStream.writeObject(this.sistema);

        } catch (IOException e) {
            throw new SystemSaveException("Erro ao salvar o sistema");
        }
    }

    /**
     * Lê o estado do sistema a partir do arquivo "sistema.dat".
     */
    public void readSistema() {
        File file = new File(SISTEMA_FILE);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            sistema = (Sistema) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new SystemSaveException("Erro ao ler o sistema");
        }
    }

    /**
     * Reseta o sistema, limpando as coleções de usuários e sessões.
     */
    public void zerarSistema() {
        this.sistema.zerarSistema();
    }

    /**
     * Obtém o valor de um atributo de um usuário.
     *
     * @param login O login do usuário.
     * @param atributo O nome do atributo.
     * @return O valor do atributo.
     */
    public String getAtributoUsuario(String login, String atributo) {
        return sistema.getAtributoUsuario(login, atributo);
    }

    /**
     * Cria um novo usuário no sistema.
     *
     * @param login O login do usuário.
     * @param senha A senha do usuário.
     * @param nome O nome do usuário.
     */
    public void criarUsuario(String login, String senha, String nome) {
        sistema.criarUsuario(login, senha, nome);
    }

    /**
     * Abre uma sessão para um usuário.
     *
     * @param login O login do usuário.
     * @param senha A senha do usuário.
     * @return O ID da sessão criada.
     */
    public String abrirSessao(String login, String senha) {
        // Primeiro autentica o usuário
        String loginAutenticado = sistema.abrirSessao(login, senha);
        // Em seguida, cria uma sessão para o usuário autenticado
        return sistema.getGerenciadorSessoes().criarSessao(loginAutenticado);
    }

    /**
     * Encerra uma sessão específica.
     *
     * @param sessionId O ID da sessão a ser encerrada.
     * @return true se a sessão foi encerrada com sucesso, false caso contrário.
     */
    public boolean encerrarSessao(String sessionId) {
        return sistema.getGerenciadorSessoes().encerrarSessao(sessionId);
    }

    /**
     * Verifica se uma sessão existe.
     *
     * @param sessionId O ID da sessão.
     * @return true se a sessão existir, false caso contrário.
     */
    public boolean existeSessao(String sessionId) {
        return sistema.getGerenciadorSessoes().existeSessao(sessionId);
    }

    /**
     * Obtém o login do usuário associado a uma sessão.
     *
     * @param sessionId O ID da sessão.
     * @return O login do usuário associado à sessão, ou null se a sessão não existir.
     */
    public String getLoginDaSessao(String sessionId) {
        return sistema.getGerenciadorSessoes().getLoginDaSessao(sessionId);
    }

    /**
     * Salva o estado atual do sistema e encerra a aplicação.
     */
    public void encerrarSistema() {
        this.saveSistema();
    }

    /**
     * Edita o perfil de um usuário.
     *
     * @param sessionId O ID da sessão do usuário.
     * @param atributo O nome do atributo a ser editado.
     * @param valor O novo valor do atributo.
     * @throws SessionNotFoundException Se a sessão não for encontrada.
     */
    public void editarPerfil(String sessionId, String atributo, String valor) {
        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Usuário não cadastrado.");
        }
        sistema.editarPerfil(login, atributo, valor);
    }

    /**
     * Verifica se dois usuários são amigos.
     *
     * @param sessionId O ID da sessão do usuário.
     * @param amigo O login do amigo.
     * @return true se os usuários são amigos, false caso contrário.
     * @throws SessionNotFoundException Se a sessão não for encontrada.
     */
    public boolean ehAmigo(String sessionId, String amigo) {
        String login = getLoginDaSessao(sessionId);

        // Verifica se o sessionId é um login válido usando o método público
        if (login == null && sistema.verificarUsuarioExiste(sessionId) != null) {
            login = sessionId;
        }
        else if (login == null) {
            throw new SessionNotFoundException("Sessão inválida ou expirada.");
        }

        return sistema.ehAmigo(login, amigo);
    }

    /**
     * Adiciona um amigo para um usuário.
     *
     * @param sessionId O ID da sessão do usuário.
     * @param amigo O login do amigo a ser adicionado.
     * @throws SessionNotFoundException Se a sessão não for encontrada.
     */
    public void adicionarAmigo(String sessionId, String amigo) {
        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Usuário não cadastrado.");
        }
        sistema.adicionarAmigo(login, amigo);
    }

    /**
     * Obtém a lista de amigos de um usuário.
     *
     * @param sessionId O ID da sessão do usuário.
     * @return Uma string contendo os logins dos amigos do usuário.
     * @throws SessionNotFoundException Se a sessão não for encontrada.
     */
    public String getAmigos(String sessionId) {
        String login = getLoginDaSessao(sessionId);

        if (login == null && sistema.verificarUsuarioExiste(sessionId) != null) {
            login = sessionId;
        }
        else if (login == null) {
            throw new SessionNotFoundException("Sessão inválida ou expirada.");
        }

        return sistema.getAmigos(login);
    }

    /**
     * Envia um recado para um usuário.
     *
     * @param sessionId O ID da sessão do usuário que envia o recado.
     * @param destinatario O login do usuário que recebe o recado.
     * @param recado O conteúdo do recado.
     * @throws SessionNotFoundException Se a sessão não for encontrada.
     */
    public void enviarRecado(String sessionId, String destinatario, String recado) {
        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Sessão inválida ou expirada.");
        }
        sistema.enviarRecado(login, destinatario, recado);
    }

    /**
     * Lê um recado de um usuário.
     *
     * @param sessionId O ID da sessão do usuário.
     * @return O conteúdo do recado.
     * @throws SessionNotFoundException Se a sessão não for encontrada.
     */
    public String lerRecado(String sessionId) {
        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Sessão inválida ou expirada.");
        }
        return sistema.lerRecado(login);
    }
}