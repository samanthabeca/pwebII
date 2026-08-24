package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Medico;
import br.edu.ifto.pwebII.model.jdbc.repository.MedicoRepository;
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
@RequestMapping("medico")
public class MedicoController {

    private final MedicoRepository repository;

    // O Spring injetará o repositório automaticamente
    @Autowired
    public MedicoController(MedicoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/form")
    public String form(Medico medico){
        return "medico/form";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("medico", repository.medicos());
        return "medico/list";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Medico medico) {
        repository.save(medico);
        return "redirect:/medico/list";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean removido = repository.remove(id);

        if (removido) {
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Médico excluído com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não é possível excluir o médico.");
        }

        return "redirect:/medico/list";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("medico", repository.medico(id));
        return new ModelAndView("medico/form", model);
    }

    @PostMapping("/update")
    public ModelAndView update(Medico medico) {
        repository.update(medico);
        return new ModelAndView("redirect:/medico/list");
    }
}