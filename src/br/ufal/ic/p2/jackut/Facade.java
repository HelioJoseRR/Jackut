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
     * @return O login do usu�rio.
     */
    public String abrirSessao(String login, String senha) {
        return sistema.abrirSessao(login, senha);
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
     * @param id O ID do usu�rio.
     * @param atributo O nome do atributo a ser editado.
     * @param valor O novo valor do atributo.
     */
    public void editarPerfil(String id, String atributo, String valor) {
        sistema.editarPerfil(id, atributo, valor);
    }

    /**
     * Verifica se dois usu�rios s�o amigos.
     *
     * @param login O login do usu�rio.
     * @param amigo O login do amigo.
     * @return true se os usu�rios s�o amigos, false caso contr�rio.
     */
    public boolean ehAmigo(String login, String amigo) {
        return sistema.ehAmigo(login, amigo);
    }

    /**
     * Adiciona um amigo para um usu�rio.
     *
     * @param login O login do usu�rio.
     * @param amigo O login do amigo a ser adicionado.
     */
    public void adicionarAmigo(String login, String amigo) {
        sistema.adicionarAmigo(login, amigo);
    }

    /**
     * Obt�m a lista de amigos de um usu�rio.
     *
     * @param login O login do usu�rio.
     * @return Uma string contendo os logins dos amigos do usu�rio.
     */
    public String getAmigos(String login) {
        return sistema.getAmigos(login);
    }

    /**
     * Envia um recado para um usu�rio.
     *
     * @param id O ID do usu�rio que envia o recado.
     * @param destinatario O ID do usu�rio que recebe o recado.
     * @param recado O conte�do do recado.
     */
    public void enviarRecado(String id, String destinatario, String recado) {
        sistema.enviarRecado(id, destinatario, recado);
    }

    /**
     * L� um recado de um usu�rio.
     *
     * @param id O ID do usu�rio.
     * @return O conte�do do recado.
     */
    public String lerRecado(String id) {
        return sistema.lerRecado(id);
    }
}
