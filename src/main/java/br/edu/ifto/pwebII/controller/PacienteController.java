package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Consulta;
import br.edu.ifto.pwebII.model.entity.Paciente;
import br.edu.ifto.pwebII.model.entity.Perfil;
import br.edu.ifto.pwebII.model.entity.Usuario;
import br.edu.ifto.pwebII.model.dto.CadastroPacienteForm;
import br.edu.ifto.pwebII.model.jdbc.repository.AdministradorRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.ConsultaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.MedicoRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.PacienteRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.PerfilRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.SecretariaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Objects;

@Transactional
@Controller
public class PacienteController {

    private final PacienteRepository repository;
    private final ConsultaRepository consultaRep;
    private final AdministradorRepository administradorRepository;
    private final SecretariaRepository secretariaRepository;
    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    public PacienteController(PacienteRepository repository, ConsultaRepository consultaRep,
                              AdministradorRepository administradorRepository,
                              SecretariaRepository secretariaRepository, MedicoRepository medicoRepository,
                              UsuarioRepository usuarioRepository, PerfilRepository perfilRepository,
                              PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.consultaRep = consultaRep;
        this.administradorRepository = administradorRepository;
        this.secretariaRepository = secretariaRepository;
        this.medicoRepository = medicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/paciente/form")
    public String form(Paciente paciente){
        return "paciente/form";
    }

    @GetMapping("/pessoa/form")
    public String formCadastro(@ModelAttribute("cadastro") CadastroPacienteForm cadastro) {
        return "pessoa/form";
    }

    @GetMapping("/paciente/list")
    public String listar(Model model) {
        List<Paciente> pacientes = repository.findAll();
        model.addAttribute("paciente", pacientes);
        model.addAttribute("totalPacientes", pacientes.size());
        return "paciente/list";
    }

    @PostMapping("/paciente/save")
    public String save(@Valid @ModelAttribute Paciente paciente, BindingResult resultado,
                       RedirectAttributes redirectAttribute) {
        if (paciente.getId() != null) {
            resultado.reject("paciente.id.invalido", "Não é permitido informar um ID ao cadastrar um paciente.");
        }
        validarDadosUnicos(paciente, resultado);

        if (resultado.hasErrors()) {
            return "paciente/form";
        }

        repository.save(paciente);
        redirectAttribute.addFlashAttribute("mensagemSucesso", "Paciente cadastrado com sucesso!");
        return "redirect:/paciente/list";
    }

    @PostMapping("/pessoa/save")
    public String cadastrarVisitante(@Valid @ModelAttribute("cadastro") CadastroPacienteForm cadastro,
                                     BindingResult resultado, RedirectAttributes redirectAttributes) {
        validarDadosUnicosCadastro(cadastro, resultado);

        if (resultado.hasErrors()) {
            return "pessoa/form";
        }

        Perfil perfilPaciente = perfilRepository.findByNome("PACIENTE")
                .orElseThrow(() -> new IllegalStateException("Perfil de paciente não encontrado."));
        Usuario usuario = new Usuario(cadastro.getEmail(), passwordEncoder.encode(cadastro.getSenha()));
        usuario.adicionarPerfil(perfilPaciente);
        usuarioRepository.save(usuario);

        Paciente paciente = new Paciente();
        paciente.setNome(cadastro.getNome());
        paciente.setCpf(cadastro.getCpf());
        paciente.setEmail(cadastro.getEmail());
        paciente.setTelefone(cadastro.getTelefone());
        paciente.associarUsuario(usuario);
        repository.save(paciente);

        redirectAttributes.addFlashAttribute("mensagemSucesso",
                "Conta criada com sucesso. Faça login para continuar.");
        return "redirect:/login";
    }

    @PostMapping("/paciente/remove/{id}")
    public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Paciente paciente = repository.findById(id).orElse(null);
        if (paciente == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Paciente não encontrado.");
            return "redirect:/paciente/list";
        }

        List<Consulta> consultasDoPaciente = consultaRep.findAllByPaciente_Id(id);

        if (!consultasDoPaciente.isEmpty()){
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não é possível excluir o(a) paciente " + paciente.getNome() + ", pois ele(a) possui consultas vinculadas a ele(a).");
            return "redirect:/paciente/list";
        }

        repository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Paciente excluído com sucesso!");

        return "redirect:/paciente/list";
    }

    @GetMapping("/paciente/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Paciente paciente = repository.findById(id).orElse(null);
        if (paciente == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Paciente não encontrado.");
            return "redirect:/paciente/list";
        }

        model.addAttribute("paciente", paciente);
        return "paciente/form";
    }

    @PostMapping("/paciente/update")
    public String update(@Valid @ModelAttribute Paciente paciente, BindingResult resultado) {
        if (paciente.getId() == null || !repository.existsById(paciente.getId())) {
            resultado.reject("paciente.inexistente", "O paciente informado não existe mais.");
        }
        validarDadosUnicos(paciente, resultado);

        if (resultado.hasErrors()) {
            return "paciente/form";
        }

        repository.save(paciente);
        return "redirect:/paciente/list";
    }

    private void validarDadosUnicos(Paciente paciente, BindingResult resultado) {
        if (!resultado.hasFieldErrors("cpf") && repository.findByCpf(paciente.getCpf())
                .filter(existente -> !Objects.equals(existente.getId(), paciente.getId()))
                .isPresent()) {
            resultado.rejectValue("cpf", "paciente.cpf.duplicado", "Já existe um paciente cadastrado com este CPF.");
        }

        if (!resultado.hasFieldErrors("email") && repository.findByEmailIgnoreCase(paciente.getEmail())
                .filter(existente -> !Objects.equals(existente.getId(), paciente.getId()))
                .isPresent()) {
            resultado.rejectValue("email", "paciente.email.duplicado", "Já existe um paciente cadastrado com este e-mail.");
        }
    }

    private void validarDadosUnicosCadastro(CadastroPacienteForm cadastro, BindingResult resultado) {
        if (!resultado.hasFieldErrors("cpf") && cpfJaCadastrado(cadastro.getCpf())) {
            resultado.rejectValue("cpf", "cadastro.cpf.duplicado", "Já existe uma pessoa cadastrada com este CPF.");
        }

        if (!resultado.hasFieldErrors("email") && emailJaCadastrado(cadastro.getEmail())) {
            resultado.rejectValue("email", "cadastro.email.duplicado", "Já existe uma conta cadastrada com este e-mail.");
        }
    }

    private boolean cpfJaCadastrado(String cpf) {
        return administradorRepository.findByCpf(cpf).isPresent()
                || secretariaRepository.findByCpf(cpf).isPresent()
                || repository.findByCpf(cpf).isPresent()
                || medicoRepository.findByCpf(cpf).isPresent();
    }

    private boolean emailJaCadastrado(String email) {
        return usuarioRepository.findByLogin(email).isPresent()
                || administradorRepository.findByEmailIgnoreCase(email).isPresent()
                || secretariaRepository.findByEmailIgnoreCase(email).isPresent()
                || repository.findByEmailIgnoreCase(email).isPresent()
                || medicoRepository.findByEmailIgnoreCase(email).isPresent();
    }
}
