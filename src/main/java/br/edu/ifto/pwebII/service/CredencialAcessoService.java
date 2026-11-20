package br.edu.ifto.pwebII.service;

import br.edu.ifto.pwebII.model.entity.Perfil;
import br.edu.ifto.pwebII.model.entity.Pessoa;
import br.edu.ifto.pwebII.model.entity.Usuario;
import br.edu.ifto.pwebII.model.jdbc.repository.PerfilRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.UsuarioRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CredencialAcessoService {

    private static final Duration VALIDADE_LINK_SENHA = Duration.ofHours(24);

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String urlBase;
    private final String remetente;

    public CredencialAcessoService(UsuarioRepository usuarioRepository, PerfilRepository perfilRepository,
                                   PasswordEncoder passwordEncoder, JavaMailSender mailSender,
                                   @Value("${clinica.url-base}") String urlBase,
                                   @Value("${clinica.email.remetente}") String remetente) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.urlBase = urlBase.replaceAll("/$", "");
        this.remetente = remetente;
    }

    public void criarCredencial(Pessoa pessoa, String nomePerfil) {
        Perfil perfil = perfilRepository.findByNome(nomePerfil)
                .orElseThrow(() -> new IllegalStateException("Perfil " + nomePerfil + " não encontrado."));

        String token = UUID.randomUUID().toString();
        Usuario usuario = new Usuario(pessoa.getEmail(), passwordEncoder.encode(UUID.randomUUID().toString()));
        usuario.setTokenDefinicaoSenha(token);
        usuario.setExpiracaoTokenDefinicaoSenha(Instant.now().plus(VALIDADE_LINK_SENHA));
        usuario.adicionarPerfil(perfil);
        usuarioRepository.save(usuario);
        pessoa.associarUsuario(usuario);

        enviarLinkDefinicaoSenha(pessoa.getEmail(), token);
    }

    public void sincronizarUsuario(Pessoa pessoa, String emailAnterior) {
        Usuario usuario = pessoa.getUsuario();
        if (usuario == null) {
            usuario = usuarioRepository.findByLogin(emailAnterior)
                    .or(() -> usuarioRepository.findByLogin(pessoa.getEmail()))
                    .orElse(null);
            if (usuario != null) {
                pessoa.associarUsuario(usuario);
            }
        }

        if (usuario != null) {
            usuario.setLogin(pessoa.getEmail());
        }
    }

    @Transactional
    public void definirSenha(String token, String senha) {
        Usuario usuario = usuarioRepository.findByTokenDefinicaoSenha(token)
                .filter(this::tokenAindaValido)
                .orElseThrow(() -> new IllegalArgumentException("O link para definição de senha é inválido ou expirou."));

        usuario.setPassword(passwordEncoder.encode(senha));
        usuario.setTokenDefinicaoSenha(null);
        usuario.setExpiracaoTokenDefinicaoSenha(null);
        usuarioRepository.save(usuario);
    }

    private boolean tokenAindaValido(Usuario usuario) {
        return usuario.getExpiracaoTokenDefinicaoSenha() != null
                && usuario.getExpiracaoTokenDefinicaoSenha().isAfter(Instant.now());
    }

    private void enviarLinkDefinicaoSenha(String destino, String token) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(destino);
        mensagem.setSubject("Defina sua senha - Clínica Simpson");
        mensagem.setText("Olá!\n\nPara definir sua senha de acesso à Clínica Simpson, acesse o link abaixo:\n"
                + urlBase + "/definir-senha?token=" + token
                + "\n\nEste link expira em 24 horas. Se você não solicitou este cadastro, ignore esta mensagem.");
        mailSender.send(mensagem);
    }
}
