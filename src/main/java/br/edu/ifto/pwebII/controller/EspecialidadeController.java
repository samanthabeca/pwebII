package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Especialidade;
import br.edu.ifto.pwebII.model.jdbc.repository.EspecialidadeRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Transactional
@Controller
@RequestMapping("especialidade")
public class EspecialidadeController {

    private final EspecialidadeRepository especialidadeRep;

    public EspecialidadeController(EspecialidadeRepository especialidadeRep) {
        this.especialidadeRep = especialidadeRep;
    }

    @GetMapping("/form")
    public String form(Especialidade especialidade) {
        return "especialidade/form";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        List<Especialidade> especialidades = especialidadeRep.findAllByOrderByNomeAsc();
        model.addAttribute("especialidade", especialidades);
        model.addAttribute("totalEspecialidades", especialidades.size());
        return "especialidade/list";
    }

    @PostMapping("/save")
    public String save(@Valid Especialidade especialidade, BindingResult resultado,
                       RedirectAttributes redirectAttributes) {
        normalizarNome(especialidade);
        if (especialidade.getId_especialidade() != null) {
            resultado.reject("especialidade.id.invalido", "Não é permitido informar um ID ao cadastrar uma especialidade.");
        }
        validarNomeUnico(especialidade, resultado);

        if (resultado.hasErrors()) {
            return "especialidade/form";
        }

        especialidadeRep.save(especialidade);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Especialidade cadastrada com sucesso!");
        return "redirect:/especialidade/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Especialidade especialidade = especialidadeRep.findById(id).orElse(null);
        if (especialidade == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Especialidade não encontrada.");
            return "redirect:/especialidade/list";
        }

        model.addAttribute("especialidade", especialidade);
        return "especialidade/form";
    }

    @PostMapping("/update")
    public String update(@Valid Especialidade especialidade, BindingResult resultado,
                         RedirectAttributes redirectAttributes) {
        normalizarNome(especialidade);
        Especialidade especialidadeExistente = especialidade.getId_especialidade() == null
                ? null : especialidadeRep.findById(especialidade.getId_especialidade()).orElse(null);
        if (especialidadeExistente == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "A especialidade informada não existe mais.");
            return "redirect:/especialidade/list";
        }
        validarNomeUnico(especialidade, resultado);

        if (resultado.hasErrors()) {
            return "especialidade/form";
        }

        especialidadeExistente.setNome(especialidade.getNome());
        especialidadeRep.save(especialidadeExistente);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Especialidade atualizada com sucesso!");
        return "redirect:/especialidade/list";
    }

    private void validarNomeUnico(Especialidade especialidade, BindingResult resultado) {
        if (!resultado.hasFieldErrors("nome") && especialidadeRep.findByNomeIgnoreCase(especialidade.getNome())
                .filter(existente -> !Objects.equals(existente.getId_especialidade(), especialidade.getId_especialidade()))
                .isPresent()) {
            resultado.rejectValue("nome", "especialidade.nome.duplicado", "Já existe uma especialidade com este nome.");
        }
    }

    private void normalizarNome(Especialidade especialidade) {
        if (especialidade.getNome() == null || especialidade.getNome().isBlank()) {
            return;
        }

        Locale localidade = Locale.forLanguageTag("pt-BR");
        String nomeFormatado = java.util.Arrays.stream(especialidade.getNome().trim().split("\\s+"))
                .map(palavra -> palavra.substring(0, 1).toUpperCase(localidade)
                        + palavra.substring(1).toLowerCase(localidade))
                .collect(java.util.stream.Collectors.joining(" "));
        especialidade.setNome(nomeFormatado);
    }
}
