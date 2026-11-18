package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.dto.PerfilUsuarioForm;
import br.edu.ifto.pwebII.model.entity.Administrador;
import br.edu.ifto.pwebII.model.entity.Medico;
import br.edu.ifto.pwebII.model.entity.Paciente;
import br.edu.ifto.pwebII.model.entity.PessoaFisica;
import br.edu.ifto.pwebII.model.entity.Secretaria;
import br.edu.ifto.pwebII.model.entity.Usuario;
import br.edu.ifto.pwebII.model.jdbc.repository.AdministradorRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.MedicoRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.PacienteRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.SecretariaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Transactional
@Controller
@RequestMapping("/perfil")
public class PerfilUsuarioController {

    private final AdministradorRepository administradorRepository;
    private final SecretariaRepository secretariaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;

    public PerfilUsuarioController(AdministradorRepository administradorRepository,
                                   SecretariaRepository secretariaRepository,
                                   PacienteRepository pacienteRepository,
                                   MedicoRepository medicoRepository,
                                   UsuarioRepository usuarioRepository) {
        this.administradorRepository = administradorRepository;
        this.secretariaRepository = secretariaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String perfil(@AuthenticationPrincipal UserDetails usuarioLogado, Model model) {
        PessoaFisica pessoa = buscarPerfil(usuarioLogado.getUsername());
        model.addAttribute("perfil", criarFormulario(pessoa));
        model.addAttribute("tipoPerfil", tipoPerfil(pessoa));
        return "perfil/form";
    }

    @PostMapping
    public String atualizar(@AuthenticationPrincipal UserDetails usuarioLogado,
                            @Valid @ModelAttribute("perfil") PerfilUsuarioForm perfil,
                            BindingResult resultado,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        String loginAtual = usuarioLogado.getUsername();
        PessoaFisica pessoa = buscarPerfil(loginAtual);
        validarCamposEspecificos(pessoa, perfil, resultado);
        validarDadosUnicos(pessoa, perfil, loginAtual, resultado);

        if (resultado.hasErrors()) {
            model.addAttribute("tipoPerfil", tipoPerfil(pessoa));
            return "perfil/form";
        }

        boolean emailAlterado = !loginAtual.equalsIgnoreCase(perfil.getEmail());
        pessoa.setNome(perfil.getNome());
        pessoa.setCpf(perfil.getCpf());
        pessoa.setEmail(perfil.getEmail());
        if (pessoa instanceof Paciente paciente) {
            paciente.setTelefone(perfil.getTelefone());
        }
        if (pessoa instanceof Medico medico) {
            medico.setCrm(perfil.getCrm());
        }
        salvar(pessoa);

        Usuario usuario = usuarioRepository.findByLogin(loginAtual)
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado."));
        usuario.setLogin(perfil.getEmail());
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Perfil atualizado com sucesso!");
        if (emailAlterado) {
            SecurityContextHolder.clearContext();
            return "redirect:/login";
        }
        return "redirect:/perfil";
    }

    private PessoaFisica buscarPerfil(String login) {
        return administradorRepository.findByEmailIgnoreCase(login)
                .map(pessoa -> (PessoaFisica) pessoa)
                .or(() -> secretariaRepository.findByEmailIgnoreCase(login).map(pessoa -> (PessoaFisica) pessoa))
                .or(() -> pacienteRepository.findByEmailIgnoreCase(login).map(pessoa -> (PessoaFisica) pessoa))
                .or(() -> medicoRepository.findByEmailIgnoreCase(login).map(pessoa -> (PessoaFisica) pessoa))
                .orElseThrow(() -> new IllegalStateException("Perfil não encontrado."));
    }

    private PerfilUsuarioForm criarFormulario(PessoaFisica pessoa) {
        PerfilUsuarioForm formulario = new PerfilUsuarioForm();
        formulario.setNome(pessoa.getNome());
        formulario.setCpf(pessoa.getCpf());
        formulario.setEmail(pessoa.getEmail());
        if (pessoa instanceof Paciente paciente) {
            formulario.setTelefone(paciente.getTelefone());
        }
        if (pessoa instanceof Medico medico) {
            formulario.setCrm(medico.getCrm());
        }
        return formulario;
    }

    private String tipoPerfil(PessoaFisica pessoa) {
        if (pessoa instanceof Paciente) {
            return "PACIENTE";
        }
        if (pessoa instanceof Medico) {
            return "MEDICO";
        }
        if (pessoa instanceof Secretaria) {
            return "SECRETARIA";
        }
        return "ADMINISTRADOR";
    }

    private void validarCamposEspecificos(PessoaFisica pessoa, PerfilUsuarioForm perfil, BindingResult resultado) {
        if (pessoa instanceof Paciente && (perfil.getTelefone() == null || perfil.getTelefone().isBlank())) {
            resultado.rejectValue("telefone", "perfil.telefone.obrigatorio", "O telefone é obrigatório.");
        }
        if (pessoa instanceof Medico && (perfil.getCrm() == null || perfil.getCrm().isBlank())) {
            resultado.rejectValue("crm", "perfil.crm.obrigatorio", "O CRM é obrigatório.");
        }
    }

    private void validarDadosUnicos(PessoaFisica pessoa, PerfilUsuarioForm perfil, String loginAtual,
                                    BindingResult resultado) {
        if (!resultado.hasFieldErrors("cpf") && cpfEmUsoPorOutroPerfil(perfil.getCpf(), pessoa)) {
            resultado.rejectValue("cpf", "perfil.cpf.duplicado", "Já existe uma pessoa cadastrada com este CPF.");
        }
        if (!resultado.hasFieldErrors("email") && emailEmUsoPorOutroPerfil(perfil.getEmail(), pessoa)) {
            resultado.rejectValue("email", "perfil.email.duplicado", "Já existe uma pessoa cadastrada com este e-mail.");
        }
        if (!resultado.hasFieldErrors("email") && usuarioRepository.findByLogin(perfil.getEmail())
                .filter(usuario -> !usuario.getLogin().equalsIgnoreCase(loginAtual))
                .isPresent()) {
            resultado.rejectValue("email", "perfil.email.usuario.duplicado", "Já existe um usuário cadastrado com este e-mail.");
        }
    }

    private boolean cpfEmUsoPorOutroPerfil(String cpf, PessoaFisica pessoa) {
        return administradorRepository.findByCpf(cpf).filter(existente -> ehOutroPerfil(existente, pessoa)).isPresent()
                || secretariaRepository.findByCpf(cpf).filter(existente -> ehOutroPerfil(existente, pessoa)).isPresent()
                || pacienteRepository.findByCpf(cpf).filter(existente -> ehOutroPerfil(existente, pessoa)).isPresent()
                || medicoRepository.findByCpf(cpf).filter(existente -> ehOutroPerfil(existente, pessoa)).isPresent();
    }

    private boolean emailEmUsoPorOutroPerfil(String email, PessoaFisica pessoa) {
        return administradorRepository.findByEmailIgnoreCase(email).filter(existente -> ehOutroPerfil(existente, pessoa)).isPresent()
                || secretariaRepository.findByEmailIgnoreCase(email).filter(existente -> ehOutroPerfil(existente, pessoa)).isPresent()
                || pacienteRepository.findByEmailIgnoreCase(email).filter(existente -> ehOutroPerfil(existente, pessoa)).isPresent()
                || medicoRepository.findByEmailIgnoreCase(email).filter(existente -> ehOutroPerfil(existente, pessoa)).isPresent();
    }

    private boolean ehOutroPerfil(PessoaFisica existente, PessoaFisica pessoa) {
        return existente.getClass() != pessoa.getClass() || !Objects.equals(existente.getId(), pessoa.getId());
    }

    private void salvar(PessoaFisica pessoa) {
        if (pessoa instanceof Administrador administrador) {
            administradorRepository.save(administrador);
        } else if (pessoa instanceof Secretaria secretaria) {
            secretariaRepository.save(secretaria);
        } else if (pessoa instanceof Paciente paciente) {
            pacienteRepository.save(paciente);
        } else if (pessoa instanceof Medico medico) {
            medicoRepository.save(medico);
        }
    }
}
