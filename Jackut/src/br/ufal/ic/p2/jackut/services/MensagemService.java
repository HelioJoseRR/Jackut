package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.Comunicacao;
import br.ufal.ic.p2.jackut.entities.Usuario;
import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MensagemService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final DataRepository repository;
    private final UsuarioService usuarioService;
    private final ComunidadeService comunidadeService;

    public MensagemService(DataRepository repository, UsuarioService usuarioService, ComunidadeService comunidadeService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.comunidadeService = comunidadeService;
    }

    public void enviarRecado(String remetente, String destinatario, String recado) {
        Usuario usuarioRemetente = usuarioService.getUsuario(remetente);
        Usuario usuarioDestinatario = usuarioService.getUsuario(destinatario);

        if (remetente.equals(destinatario)) {
            throw new MessageException("Usuário não pode enviar recado para si mesmo.");
        }

        if (ehInimigo(destinatario, remetente)) {
            throw new RelacionamentoException("Função inválida: " + usuarioDestinatario.getNome() + " é seu inimigo.");
        }

        Comunicacao novaMensagem = criarRecado(remetente, destinatario, recado);
        repository.adicionarMensagem(destinatario, novaMensagem);
    }

    public String lerRecado(String login) {
        List<Comunicacao> recados = getMensagensDoTipo(login, "recado");

        if (recados.isEmpty()) {
            throw new MessageException("Não há recados.");
        }

        Comunicacao recado = recados.remove(0);
        repository.getMensagensDoUsuario(login).remove(recado);

        return recado.getConteudo();
    }

    public void enviarMensagemComunidade(String login, String comunidade, String mensagem) {
        if (!repository.existeComunidade(comunidade)) {
            throw new CommunityException("Comunidade não existe.");
        }

        Comunicacao novaMensagem = criarMensagemComunidade(login, comunidade, mensagem);

        List<String> membros = repository.getComunidade(comunidade).getMembros();

        for (String membro : membros) {
            repository.adicionarMensagem(membro, novaMensagem);
        }
    }

    public String lerMensagemComunidade(String login) {
        List<Comunicacao> mensagens = getMensagensDoTipo(login, "comunidade");

        if (mensagens.isEmpty()) {
            throw new MessageException("Não há mensagens.");
        }

        Comunicacao mensagem = mensagens.remove(0);
        repository.getMensagensDoUsuario(login).remove(mensagem);

        return formatarMensagem(mensagem);
    }

    public void adicionarRecadoJackut(String login, String recado) {
        Comunicacao mensagem = criarRecado("jackut", login, recado + " é seu paquera - Recado do Jackut.");
        repository.adicionarMensagem(login, mensagem);
    }

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

    public void removerMensagensDoUsuario(String login) {
        // Remove mensagens enviadas pelo usuário
        for (List<Comunicacao> listaMensagens : repository.getMensagens().values()) {
            listaMensagens.removeIf(mensagem -> mensagem.getRemetente().equals(login));
        }

        // Remove mensagens recebidas pelo usuário
        repository.getMensagens().remove(login);
    }

    public void zerarMensagens() {
        repository.getMensagens().clear();
    }

    // Métodos auxiliares
    private Comunicacao criarRecado(String remetente, String destinatario, String conteudo) {
        return new Comunicacao(remetente, destinatario, conteudo, "recado");
    }

    private Comunicacao criarMensagemComunidade(String remetente, String comunidade, String conteudo) {
        return new Comunicacao(remetente, comunidade, conteudo, "comunidade");
    }

    private String formatarMensagem(Comunicacao mensagem) {
        if (mensagem.getTipo().equals("recado")) {
            return mensagem.getRemetente() + ": " + mensagem.getConteudo();
        }
        return mensagem.getConteudo();
    }

    private boolean ehInimigo(String login, String outroUsuario) {
        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getInimigos().contains(outroUsuario);
    }
}
