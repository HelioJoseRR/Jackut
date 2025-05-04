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
     * @param remetente    Login do usu�rio que enviou a mensagem
     * @param destinatario Login do usu�rio que recebeu a mensagem ou nome da comunidade
     * @param conteudo     Conte�do da mensagem
     * @param tipo         Tipo da mensagem ("recado" ou "comunidade")
     */
    public Comunicacao(String remetente, String destinatario, String conteudo, String tipo) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.conteudo = conteudo;
        this.tipo = tipo;
    }

    /**
     * Cria um novo recado (mensagem direta entre usu�rios).
     *
     * @param remetente    Login do usu�rio que enviou o recado
     * @param destinatario Login do usu�rio que recebeu o recado
     * @param conteudo     Conte�do do recado
     * @return Nova inst�ncia de Mensagem do tipo recado
     */
    public static Comunicacao criarRecado(String remetente, String destinatario, String conteudo) {
        return new Comunicacao(remetente, destinatario, conteudo, "recado");
    }

    /**
     * Cria uma nova mensagem de comunidade.
     *
     * @param remetente  Login do usu�rio que enviou a mensagem
     * @param comunidade Nome da comunidade onde a mensagem foi enviada
     * @param conteudo   Conte�do da mensagem
     * @return Nova inst�ncia de Mensagem do tipo comunidade
     */
    public static Comunicacao criarMensagemComunidade(String remetente, String comunidade, String conteudo) {
        return new Comunicacao(remetente, comunidade, conteudo, "comunidade");
    }

    /**
     * Obt�m o remetente da mensagem.
     *
     * @return Login do usu�rio que enviou a mensagem
     */
    public String getRemetente() {
        return remetente;
    }

    /**
     * Obt�m o destinat�rio da mensagem.
     *
     * @return Login do usu�rio que recebeu a mensagem ou nome da comunidade
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * Obt�m o conte�do da mensagem.
     *
     * @return Conte�do da mensagem
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * Obt�m o tipo da mensagem.
     *
     * @return Tipo da mensagem ("recado" ou "comunidade")
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Verifica se a mensagem � um recado.
     *
     * @return true se for um recado, false caso contr�rio
     */
    public boolean isRecado() {
        return "recado".equals(tipo);
    }

    /**
     * Verifica se a mensagem � uma mensagem de comunidade.
     *
     * @return true se for uma mensagem de comunidade, false caso contr�rio
     */
    public boolean isMensagemComunidade() {
        return "comunidade".equals(tipo);
    }

    /**
     * Retorna uma representa��o em string da mensagem.
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