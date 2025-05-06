package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.Comunicacao;
import br.ufal.ic.p2.jackut.entities.Usuario;
import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Servi�o respons�vel por gerenciar as mensagens entre usu�rios e comunidades.
 * Permite enviar e ler recados e mensagens de comunidade.
 */
public class MensagemService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Reposit�rio central de dados */
    private final DataRepository repository;

    /** Servi�o de usu�rios */
    private final UsuarioService usuarioService;

    /** Servi�o de comunidades */
    private final ComunidadeService comunidadeService;

    /**
     * Construtor que inicializa o servi�o com as depend�ncias necess�rias.
     *
     * @param repository Reposit�rio central de dados
     * @param usuarioService Servi�o de usu�rios
     * @param comunidadeService Servi�o de comunidades
     */
    public MensagemService(DataRepository repository, UsuarioService usuarioService, ComunidadeService comunidadeService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.comunidadeService = comunidadeService;
    }

    /**
     * Envia um recado de um usu�rio para outro.
     *
     * @param remetente Login do usu�rio remetente
     * @param destinatario Login do usu�rio destinat�rio
     * @param recado Conte�do do recado
     * @throws MessageException Se o usu�rio tentar enviar recado para si mesmo
     * @throws RelacionamentoException Se o destinat�rio for inimigo do remetente
     * @throws UserNotFoundException Se algum dos usu�rios n�o existir
     */
    public void enviarRecado(String remetente, String destinatario, String recado)
            throws MessageException, RelacionamentoException, UserNotFoundException {
        Usuario usuarioRemetente = usuarioService.getUsuario(remetente);
        Usuario usuarioDestinatario = usuarioService.getUsuario(destinatario);

        if (remetente.equals(destinatario)) {
            throw new MessageException("Usu�rio n�o pode enviar recado para si mesmo.");
        }

        if (ehInimigo(destinatario, remetente)) {
            throw new RelacionamentoException("Fun��o inv�lida: " + usuarioDestinatario.getNome() + " � seu inimigo.");
        }

        Comunicacao novaMensagem = criarRecado(remetente, destinatario, recado);
        repository.adicionarMensagem(destinatario, novaMensagem);
    }

    /**
     * L� o pr�ximo recado dispon�vel para um usu�rio.
     *
     * @param login Login do usu�rio
     * @return Conte�do do recado
     * @throws MessageException Se n�o houver recados
     */
    public String lerRecado(String login) throws MessageException {
        List<Comunicacao> recados = getMensagensDoTipo(login, "recado");

        if (recados.isEmpty()) {
            throw new MessageException("N�o h� recados.");
        }

        Comunicacao recado = recados.remove(0);
        repository.getMensagensDoUsuario(login).remove(recado);

        return recado.getConteudo();
    }

    /**
     * Envia uma mensagem para todos os membros de uma comunidade.
     *
     * @param login Login do usu�rio remetente
     * @param comunidade Nome da comunidade
     * @param mensagem Conte�do da mensagem
     * @throws CommunityException Se a comunidade n�o existir
     */
    public void enviarMensagemComunidade(String login, String comunidade, String mensagem)
            throws CommunityException {
        if (!repository.existeComunidade(comunidade)) {
            throw new CommunityException("Comunidade n�o existe.");
        }

        Comunicacao novaMensagem = criarMensagemComunidade(login, comunidade, mensagem);

        List<String> membros = repository.getComunidade(comunidade).getMembros();

        for (String membro : membros) {
            repository.adicionarMensagem(membro, novaMensagem);
        }
    }

    /**
     * L� a pr�xima mensagem de comunidade dispon�vel para um usu�rio.
     *
     * @param login Login do usu�rio
     * @return Conte�do da mensagem formatada
     * @throws MessageException Se n�o houver mensagens
     */
    public String lerMensagemComunidade(String login) throws MessageException {
        List<Comunicacao> mensagens = getMensagensDoTipo(login, "comunidade");

        if (mensagens.isEmpty()) {
            throw new MessageException("N�o h� mensagens.");
        }

        Comunicacao mensagem = mensagens.remove(0);
        repository.getMensagensDoUsuario(login).remove(mensagem);

        return formatarMensagem(mensagem);
    }

    /**
     * Adiciona um recado autom�tico do sistema Jackut para um usu�rio.
     * Usado para notificar sobre paqueras m�tuas.
     *
     * @param login Login do usu�rio destinat�rio
     * @param recado Conte�do base do recado
     */
    public void adicionarRecadoJackut(String login, String recado) {
        Comunicacao mensagem = criarRecado("jackut", login, recado + " � seu paquera - Recado do Jackut.");
        repository.adicionarMensagem(login, mensagem);
    }

    /**
     * Obt�m todas as mensagens de um determinado tipo para um usu�rio.
     *
     * @param login Login do usu�rio
     * @param tipo Tipo da mensagem ("recado" ou "comunidade")
     * @return Lista de mensagens do tipo especificado
     */
    private List<Comunicacao> getMensagensDoTipo(String login, String tipo) {
        List<Comunicacao> mensagensDoTipo = new ArrayList<>();
        List<Comunicacao> todasMensagens = repository.getMensagensDoUsuario(login);

        for (Comunicacao mensagem : todasMensagens) {
            if (tipo.equals(mensagem.getTipo())) {
                mensagensDoTipo.add(mensagem);
            }
        }

        return mensagensDoTipo;
    }

    /**
     * Remove todas as mensagens enviadas ou recebidas por um usu�rio.
     *
     * @param login Login do usu�rio
     */
    public void removerMensagensDoUsuario(String login) {
        // Remove mensagens enviadas pelo usu�rio
        for (List<Comunicacao> listaMensagens : repository.getMensagens().values()) {
            listaMensagens.removeIf(mensagem -> mensagem.getRemetente().equals(login));
        }

        // Remove mensagens recebidas pelo usu�rio
        repository.getMensagens().remove(login);
    }

    /**
     * Remove todas as mensagens do sistema.
     */
    public void zerarMensagens() {
        repository.getMensagens().clear();
    }

    /**
     * Cria um novo objeto de comunica��o do tipo recado.
     *
     * @param remetente Login do usu�rio remetente
     * @param destinatario Login do usu�rio destinat�rio
     * @param conteudo Conte�do do recado
     * @return Objeto Comunicacao criado
     */
    private Comunicacao criarRecado(String remetente, String destinatario, String conteudo) {
        return new Comunicacao(remetente, destinatario, conteudo, "recado");
    }

    /**
     * Cria um novo objeto de comunica��o do tipo mensagem de comunidade.
     *
     * @param remetente Login do usu�rio remetente
     * @param comunidade Nome da comunidade
     * @param conteudo Conte�do da mensagem
     * @return Objeto Comunicacao criado
     */
    private Comunicacao criarMensagemComunidade(String remetente, String comunidade, String conteudo) {
        return new Comunicacao(remetente, comunidade, conteudo, "comunidade");
    }

    /**
     * Formata uma mensagem para exibi��o.
     *
     * @param mensagem Objeto Comunicacao a ser formatado
     * @return String formatada da mensagem
     */
    private String formatarMensagem(Comunicacao mensagem) {
        if (mensagem.getTipo().equals("recado")) {
            return mensagem.getRemetente() + ": " + mensagem.getConteudo();
        }
        return mensagem.getConteudo();
    }

    /**
     * Verifica se um usu�rio � inimigo de outro.
     *
     * @param login Login do primeiro usu�rio
     * @param outroUsuario Login do segundo usu�rio
     * @return true se o primeiro usu�rio tiver o segundo como inimigo
     * @throws UserNotFoundException Se algum dos usu�rios n�o existir
     */
    private boolean ehInimigo(String login, String outroUsuario) throws UserNotFoundException {
        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getInimigos().contains(outroUsuario);
    }
}