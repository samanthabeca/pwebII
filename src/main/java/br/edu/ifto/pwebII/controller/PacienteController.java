package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Paciente;
import br.edu.ifto.pwebII.model.jdbc.repository.PacienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Transactional
@Controller
@RequestMapping("paciente")
public class PacienteController {

    private final PacienteRepository repository;

    // O Spring injetará o repositório automaticamente
    @Autowired
    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/form")
    public String form(Paciente paciente){
        return "paciente/form";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("paciente", repository.pacientes());
        return "paciente/list";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Paciente paciente) {
        repository.save(paciente);
        return "redirect:/paciente/list";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean removido = repository.remove(id);

        if (removido) {
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Paciente excluído com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não é possível excluir o paciente.");
        }

        return "redirect:/paciente/list";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("paciente", repository.paciente(id));
        return new ModelAndView("paciente/form", model);
    }

    @PostMapping("/update")
    public ModelAndView update(Paciente paciente) {
        repository.update(paciente);
        return new ModelAndView("redirect:/paciente/list");
    }
}