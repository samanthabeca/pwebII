package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Secretaria;
import br.edu.ifto.pwebII.model.jdbc.repository.SecretariaRepository;
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
@RequestMapping("secretaria")
public class SecretariaController {

    private final SecretariaRepository repository;

    @Autowired
    public SecretariaController(SecretariaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/form")
    public String form(Secretaria secretaria) {
        return "secretaria/form";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        List<Secretaria> secretarias = repository.findAll();
        model.addAttribute("secretaria", secretarias);
        model.addAttribute("totalSecretarias", secretarias.size());
        return "secretaria/list";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Secretaria secretaria, BindingResult resultado,
                       RedirectAttributes redirectAttributes) {
        if (secretaria.getId() != null) {
            resultado.reject("secretaria.id.invalido", "Não é permitido informar um ID ao cadastrar uma secretária.");
        }
        validarDadosUnicos(secretaria, resultado);

        if (resultado.hasErrors()) {
            return "secretaria/form";
        }

        repository.save(secretaria);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Secretária cadastrada com sucesso!");
        return "redirect:/secretaria/list";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Secretaria secretaria = repository.findById(id).orElse(null);
        if (secretaria == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Secretária não encontrada.");
            return "redirect:/secretaria/list";
        }

        repository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Secretária excluída com sucesso!");
        return "redirect:/secretaria/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Secretaria secretaria = repository.findById(id).orElse(null);
        if (secretaria == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Secretária não encontrada.");
            return "redirect:/secretaria/list";
        }

        model.addAttribute("secretaria", secretaria);
        return "secretaria/form";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute Secretaria secretaria, BindingResult resultado) {
        if (secretaria.getId() == null || !repository.existsById(secretaria.getId())) {
            resultado.reject("secretaria.inexistente", "A secretária informada não existe mais.");
        }
        validarDadosUnicos(secretaria, resultado);

        if (resultado.hasErrors()) {
            return "secretaria/form";
        }

        repository.save(secretaria);
        return "redirect:/secretaria/list";
    }

    private void validarDadosUnicos(Secretaria secretaria, BindingResult resultado) {
        if (!resultado.hasFieldErrors("cpf") && repository.findByCpf(secretaria.getCpf())
                .filter(existente -> !Objects.equals(existente.getId(), secretaria.getId()))
                .isPresent()) {
            resultado.rejectValue("cpf", "secretaria.cpf.duplicado", "Já existe uma secretária cadastrada com este CPF.");
        }

        if (!resultado.hasFieldErrors("email") && repository.findByEmailIgnoreCase(secretaria.getEmail())
                .filter(existente -> !Objects.equals(existente.getId(), secretaria.getId()))
                .isPresent()) {
            resultado.rejectValue("email", "secretaria.email.duplicado", "Já existe uma secretária cadastrada com este e-mail.");
        }
    }
}
