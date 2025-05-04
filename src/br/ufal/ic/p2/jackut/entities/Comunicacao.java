package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;

public class Comunicacao implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String remetente;
    private final String destinatario;
    private final String conteudo;
    private final String tipo;

    /**
     * Cria uma nova mensagem.
     *
     * @param remetente    Login do usuário que enviou a mensagem
     * @param destinatario Login do usuário que recebeu a mensagem ou nome da comunidade
     * @param conteudo     Conteúdo da mensagem
     * @param tipo         Tipo da mensagem ("recado" ou "comunidade")
     */
    public Comunicacao(String remetente, String destinatario, String conteudo, String tipo) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.conteudo = conteudo;
        this.tipo = tipo;
    }

    /**
     * Cria um novo recado (mensagem direta entre usuários).
     *
     * @param remetente    Login do usuário que enviou o recado
     * @param destinatario Login do usuário que recebeu o recado
     * @param conteudo     Conteúdo do recado
     * @return Nova instância de Mensagem do tipo recado
     */
    public static Comunicacao criarRecado(String remetente, String destinatario, String conteudo) {
        return new Comunicacao(remetente, destinatario, conteudo, "recado");
    }

    /**
     * Cria uma nova mensagem de comunidade.
     *
     * @param remetente  Login do usuário que enviou a mensagem
     * @param comunidade Nome da comunidade onde a mensagem foi enviada
     * @param conteudo   Conteúdo da mensagem
     * @return Nova instância de Mensagem do tipo comunidade
     */
    public static Comunicacao criarMensagemComunidade(String remetente, String comunidade, String conteudo) {
        return new Comunicacao(remetente, comunidade, conteudo, "comunidade");
    }

    /**
     * Obtém o remetente da mensagem.
     *
     * @return Login do usuário que enviou a mensagem
     */
    public String getRemetente() {
        return remetente;
    }

    /**
     * Obtém o destinatário da mensagem.
     *
     * @return Login do usuário que recebeu a mensagem ou nome da comunidade
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * Obtém o conteúdo da mensagem.
     *
     * @return Conteúdo da mensagem
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * Obtém o tipo da mensagem.
     *
     * @return Tipo da mensagem ("recado" ou "comunidade")
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Verifica se a mensagem é um recado.
     *
     * @return true se for um recado, false caso contrário
     */
    public boolean isRecado() {
        return "recado".equals(tipo);
    }

    /**
     * Verifica se a mensagem é uma mensagem de comunidade.
     *
     * @return true se for uma mensagem de comunidade, false caso contrário
     */
    public boolean isMensagemComunidade() {
        return "comunidade".equals(tipo);
    }

    /**
     * Retorna uma representação em string da mensagem.
     *
     * @return Para recados: "remetente: conteudo", para mensagens de comunidade: conteudo
     */
    @Override
    public String toString() {
        if (isRecado()) {
            return remetente + ": " + conteudo;
        }
        return conteudo;
    }
}