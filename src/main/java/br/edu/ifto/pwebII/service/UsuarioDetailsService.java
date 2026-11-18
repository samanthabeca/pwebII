package br.edu.ifto.pwebII.service;

import br.edu.ifto.pwebII.model.entity.Perfil;
import br.edu.ifto.pwebII.model.entity.Usuario;
import br.edu.ifto.pwebII.model.jdbc.repository.AdministradorRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.MedicoRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.PacienteRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.SecretariaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;
    private final AdministradorRepository administradorRepository;
    private final SecretariaRepository secretariaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public UsuarioDetailsService(UsuarioRepository repository, AdministradorRepository administradorRepository,
                                 SecretariaRepository secretariaRepository, PacienteRepository pacienteRepository,
                                 MedicoRepository medicoRepository) {
        this.repository = repository;
        this.administradorRepository = administradorRepository;
        this.secretariaRepository = secretariaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = repository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Perfil perfil : usuario.getPerfis()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + perfil.getNome()));
        }

        UserDetails userDetails = User.withUsername(usuario.getLogin())
                .password(usuario.getPassword())
                .authorities(authorities)
                .disabled(!usuario.isAtivo())
                .build();

        String nome = administradorRepository.findByEmailIgnoreCase(usuario.getLogin())
                .map(administrador -> administrador.getNome())
                .or(() -> secretariaRepository.findByEmailIgnoreCase(usuario.getLogin())
                        .map(secretaria -> secretaria.getNome()))
                .or(() -> pacienteRepository.findByEmailIgnoreCase(usuario.getLogin())
                        .map(paciente -> paciente.getNome()))
                .or(() -> medicoRepository.findByEmailIgnoreCase(usuario.getLogin())
                        .map(medico -> medico.getNome()))
                .orElse(usuario.getLogin());

        return new UsuarioAutenticado(nome, userDetails);
    }
}
