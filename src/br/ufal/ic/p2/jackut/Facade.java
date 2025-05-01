// Refactored Facade.java
package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.entities.*;
import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.*;

/**
 * A classe Facade fornece uma interface simplificada para interagir com o sistema Jackut.
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
        this.sistema = new Sistema();
        try {
            File file = new File(SISTEMA_FILE);
            if (file.exists()) {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                Sistema sistemaLido = (Sistema) in.readObject();
                if (sistemaLido != null) {
                    this.sistema = sistemaLido;
                }
                in.close();
            }
        } catch (Exception e) {
            // Se houver erro ao ler o arquivo, continua com um sistema novo
        }
    }

    /**
     * Salva o estado atual do sistema no arquivo "sistema.dat".
     */
    public void encerrarSistema() {
        try {
            FileOutputStream fileOut = new FileOutputStream(SISTEMA_FILE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.sistema);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            throw new SystemSaveException("Erro ao salvar o sistema");
        }
    }

    /**
     * Reseta o sistema, limpando todas as cole��es.
     */
    public void zerarSistema() {
        this.sistema = new Sistema();
    }

    /**
     * Obt�m o valor de um atributo de um usu�rio.
     */
    public String getAtributoUsuario(String login, String atributo) {
        return sistema.getAtributoUsuario(login, atributo);
    }

    /**
     * Cria um novo usu�rio no sistema.
     */
    public void criarUsuario(String login, String senha, String nome) {
        sistema.criarUsuario(login, senha, nome);
    }

    /**
     * Abre uma sess�o para um usu�rio.
     */
    public String abrirSessao(String login, String senha) {
        return sistema.criarSessao(login, senha);
    }

    /**
     * Encerra uma sess�o espec�fica.
     */
    public boolean encerrarSessao(String sessionId) {
        return sistema.encerrarSessao(sessionId);
    }

    /**
     * Verifica se uma sess�o existe.
     */
    public boolean existeSessao(String sessionId) {
        return sistema.existeSessao(sessionId);
    }

    /**
     * Obt�m o login do usu�rio associado a uma sess�o.
     */
    public String getLoginDaSessao(String sessionId) {
        return sistema.getLoginDaSessao(sessionId);
    }

    /**
     * Edita o perfil de um usu�rio.
     */
    public void editarPerfil(String sessionId, String atributo, String valor) {
        sistema.editarPerfil(sessionId, atributo, valor);
    }

    /**
     * Verifica se dois usu�rios s�o amigos.
     */
    public boolean ehAmigo(String sessionId, String amigo) {
        return sistema.ehAmigo(sessionId, amigo);
    }

    /**
     * Adiciona um amigo para um usu�rio.
     */
    public void adicionarAmigo(String sessionId, String amigo) {
        sistema.adicionarAmigo(sessionId, amigo);
    }

    /**
     * Obt�m a lista de amigos de um usu�rio.
     */
    public String getAmigos(String sessionId) {
        return sistema.getAmigos(sessionId);
    }

    /**
     * Envia um recado para um usu�rio.
     */
    public void enviarRecado(String sessionId, String destinatario, String recado) {
        sistema.enviarRecado(sessionId, destinatario, recado);
    }

    /**
     * L� um recado de um usu�rio.
     */
    public String lerRecado(String sessionId) {
        return sistema.lerRecado(sessionId);
    }

    /**
     * Cria uma nova comunidade.
     */
    public void criarComunidade(String sessionId, String nome, String descricao) {
        sistema.criarComunidade(sessionId, nome, descricao);
    }

    /**
     * Obt�m a descri��o de uma comunidade.
     */
    public String getDescricaoComunidade(String nome) {
        return sistema.getDescricaoComunidade(nome);
    }

    /**
     * Obt�m o dono de uma comunidade.
     */
    public String getDonoComunidade(String nome) {
        return sistema.getDonoComunidade(nome);
    }

    /**
     * Obt�m os membros de uma comunidade.
     */
    public String getMembrosComunidade(String nome) {
        return sistema.getMembrosComunidade(nome);
    }

    /**
     * Obt�m as comunidades de um usu�rio.
     */
    public String getComunidades(String login) {
        return sistema.getComunidades(login);
    }

    /**
     * Adiciona um usu�rio a uma comunidade.
     */
    public void adicionarComunidade(String sessionId, String nome) {
        sistema.adicionarComunidade(sessionId, nome);
    }

    /**
     * L� uma mensagem de um usu�rio.
     */
    public String lerMensagem(String sessionId) {
        return sistema.lerMensagem(sessionId);
    }

    /**
     * Envia uma mensagem para uma comunidade.
     */
    public void enviarMensagem(String sessionId, String comunidade, String mensagem) {
        sistema.enviarMensagem(sessionId, comunidade, mensagem);
    }
}