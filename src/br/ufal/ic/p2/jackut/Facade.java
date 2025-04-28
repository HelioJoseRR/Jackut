package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.entities.*;
import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.*;

/**
 * A classe Facade fornece uma interface simplificada para interagir com o sistema Jackut.
 * Implementa a interface Serializable para permitir a serializa��o dos objetos.
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
        this.sistema = new Sistema(); // Inicializa��o padr�o para evitar NullPointerException
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
     * L� o estado do sistema a partir do arquivo "sistema.dat".
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
     * Reseta o sistema, limpando as cole��es de usu�rios e sess�es.
     */
    public void zerarSistema() {
        this.sistema.zerarSistema();
    }

    /**
     * Obt�m o valor de um atributo de um usu�rio.
     *
     * @param login O login do usu�rio.
     * @param atributo O nome do atributo.
     * @return O valor do atributo.
     */
    public String getAtributoUsuario(String login, String atributo) {
        return sistema.getAtributoUsuario(login, atributo);
    }

    /**
     * Cria um novo usu�rio no sistema.
     *
     * @param login O login do usu�rio.
     * @param senha A senha do usu�rio.
     * @param nome O nome do usu�rio.
     */
    public void criarUsuario(String login, String senha, String nome) {
        sistema.criarUsuario(login, senha, nome);
    }

    /**
     * Abre uma sess�o para um usu�rio.
     *
     * @param login O login do usu�rio.
     * @param senha A senha do usu�rio.
     * @return O ID da sess�o criada.
     */
    public String abrirSessao(String login, String senha) {
        // Primeiro autentica o usu�rio
        String loginAutenticado = sistema.abrirSessao(login, senha);
        // Em seguida, cria uma sess�o para o usu�rio autenticado
        return sistema.getGerenciadorSessoes().criarSessao(loginAutenticado);
    }

    /**
     * Encerra uma sess�o espec�fica.
     *
     * @param sessionId O ID da sess�o a ser encerrada.
     * @return true se a sess�o foi encerrada com sucesso, false caso contr�rio.
     */
    public boolean encerrarSessao(String sessionId) {
        return sistema.getGerenciadorSessoes().encerrarSessao(sessionId);
    }

    /**
     * Verifica se uma sess�o existe.
     *
     * @param sessionId O ID da sess�o.
     * @return true se a sess�o existir, false caso contr�rio.
     */
    public boolean existeSessao(String sessionId) {
        return sistema.getGerenciadorSessoes().existeSessao(sessionId);
    }

    /**
     * Obt�m o login do usu�rio associado a uma sess�o.
     *
     * @param sessionId O ID da sess�o.
     * @return O login do usu�rio associado � sess�o, ou null se a sess�o n�o existir.
     */
    public String getLoginDaSessao(String sessionId) {
        return sistema.getGerenciadorSessoes().getLoginDaSessao(sessionId);
    }

    /**
     * Salva o estado atual do sistema e encerra a aplica��o.
     */
    public void encerrarSistema() {
        this.saveSistema();
    }

    /**
     * Edita o perfil de um usu�rio.
     *
     * @param sessionId O ID da sess�o do usu�rio.
     * @param atributo O nome do atributo a ser editado.
     * @param valor O novo valor do atributo.
     * @throws SessionNotFoundException Se a sess�o n�o for encontrada.
     */
    public void editarPerfil(String sessionId, String atributo, String valor) {
        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Usu�rio n�o cadastrado.");
        }
        sistema.editarPerfil(login, atributo, valor);
    }

    /**
     * Verifica se dois usu�rios s�o amigos.
     *
     * @param sessionId O ID da sess�o do usu�rio.
     * @param amigo O login do amigo.
     * @return true se os usu�rios s�o amigos, false caso contr�rio.
     * @throws SessionNotFoundException Se a sess�o n�o for encontrada.
     */
    public boolean ehAmigo(String sessionId, String amigo) {
        String login = getLoginDaSessao(sessionId);

        // Verifica se o sessionId � um login v�lido usando o m�todo p�blico
        if (login == null && sistema.verificarUsuarioExiste(sessionId) != null) {
            login = sessionId;
        }
        else if (login == null) {
            throw new SessionNotFoundException("Sess�o inv�lida ou expirada.");
        }

        return sistema.ehAmigo(login, amigo);
    }

    /**
     * Adiciona um amigo para um usu�rio.
     *
     * @param sessionId O ID da sess�o do usu�rio.
     * @param amigo O login do amigo a ser adicionado.
     * @throws SessionNotFoundException Se a sess�o n�o for encontrada.
     */
    public void adicionarAmigo(String sessionId, String amigo) {
        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Usu�rio n�o cadastrado.");
        }
        sistema.adicionarAmigo(login, amigo);
    }

    /**
     * Obt�m a lista de amigos de um usu�rio.
     *
     * @param sessionId O ID da sess�o do usu�rio.
     * @return Uma string contendo os logins dos amigos do usu�rio.
     * @throws SessionNotFoundException Se a sess�o n�o for encontrada.
     */
    public String getAmigos(String sessionId) {
        String login = getLoginDaSessao(sessionId);

        if (login == null && sistema.verificarUsuarioExiste(sessionId) != null) {
            login = sessionId;
        }
        else if (login == null) {
            throw new SessionNotFoundException("Sess�o inv�lida ou expirada.");
        }

        return sistema.getAmigos(login);
    }

    /**
     * Envia um recado para um usu�rio.
     *
     * @param sessionId O ID da sess�o do usu�rio que envia o recado.
     * @param destinatario O login do usu�rio que recebe o recado.
     * @param recado O conte�do do recado.
     * @throws SessionNotFoundException Se a sess�o n�o for encontrada.
     */
    public void enviarRecado(String sessionId, String destinatario, String recado) {
        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Sess�o inv�lida ou expirada.");
        }
        sistema.enviarRecado(login, destinatario, recado);
    }

    /**
     * L� um recado de um usu�rio.
     *
     * @param sessionId O ID da sess�o do usu�rio.
     * @return O conte�do do recado.
     * @throws SessionNotFoundException Se a sess�o n�o for encontrada.
     */
    public String lerRecado(String sessionId) {
        String login = getLoginDaSessao(sessionId);
        if (login == null) {
            throw new SessionNotFoundException("Sess�o inv�lida ou expirada.");
        }
        return sistema.lerRecado(login);
    }
}