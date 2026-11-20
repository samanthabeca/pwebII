package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Administrador;
import br.edu.ifto.pwebII.model.jdbc.repository.AdministradorRepository;
import br.edu.ifto.pwebII.service.CredencialAcessoService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Transactional
@Controller
@RequestMapping("administrador")
public class AdministradorController {

    private final AdministradorRepository repository;
    private final CredencialAcessoService credencialAcessoService;

    @Autowired
    public AdministradorController(AdministradorRepository repository, CredencialAcessoService credencialAcessoService) {
        this.repository = repository;
        this.credencialAcessoService = credencialAcessoService;
    }

    @GetMapping("/form")
    public String form(Administrador administrador) {
        return "administrador/form";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        List<Administrador> administradores = repository.findAll();
        model.addAttribute("administrador", administradores);
        model.addAttribute("totalAdministradores", administradores.size());
        return "administrador/list";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Administrador administrador, BindingResult resultado,
                       RedirectAttributes redirectAttributes) {
        if (administrador.getId() != null) {
            resultado.reject("administrador.id.invalido", "Não é permitido informar um ID ao cadastrar um administrador.");
        }
        validarDadosUnicos(administrador, resultado);

        if (resultado.hasErrors()) {
            return "administrador/form";
        }

        credencialAcessoService.criarCredencial(administrador, "ADMIN");
        repository.save(administrador);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Administrador cadastrado. O link para definir a senha foi enviado por e-mail.");
        return "redirect:/administrador/list";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Administrador administrador = repository.findById(id).orElse(null);
        if (administrador == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Administrador não encontrado.");
            return "redirect:/administrador/list";
        }

        repository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Administrador excluído com sucesso!");
        return "redirect:/administrador/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Administrador administrador = repository.findById(id).orElse(null);
        if (administrador == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Administrador não encontrado.");
            return "redirect:/administrador/list";
        }

        model.addAttribute("administrador", administrador);
        return "administrador/form";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute Administrador administrador, BindingResult resultado) {
        Administrador administradorExistente = administrador.getId() == null ? null
                : repository.findById(administrador.getId()).orElse(null);
        if (administradorExistente == null) {
            resultado.reject("administrador.inexistente", "O administrador informado não existe mais.");
        }
        validarDadosUnicos(administrador, resultado);

        if (resultado.hasErrors()) {
            return "administrador/form";
        }

        String emailAnterior = administradorExistente.getEmail();
        administradorExistente.setNome(administrador.getNome());
        administradorExistente.setCpf(administrador.getCpf());
        administradorExistente.setEmail(administrador.getEmail());
        credencialAcessoService.sincronizarUsuario(administradorExistente, emailAnterior);
        repository.save(administradorExistente);
        return "redirect:/administrador/list";
    }

    private void validarDadosUnicos(Administrador administrador, BindingResult resultado) {
        if (!resultado.hasFieldErrors("cpf") && repository.findByCpf(administrador.getCpf())
                .filter(existente -> !Objects.equals(existente.getId(), administrador.getId()))
                .isPresent()) {
            resultado.rejectValue("cpf", "administrador.cpf.duplicado", "Já existe um administrador cadastrado com este CPF.");
        }

        if (!resultado.hasFieldErrors("email") && repository.findByEmailIgnoreCase(administrador.getEmail())
                .filter(existente -> !Objects.equals(existente.getId(), administrador.getId()))
                .isPresent()) {
            resultado.rejectValue("email", "administrador.email.duplicado", "Já existe um administrador cadastrado com este e-mail.");
        }
    }
}
