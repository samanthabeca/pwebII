package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Departamento;
import br.edu.ifto.pwebII.model.jdbc.repository.DepartamentoRepository;
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
@RequestMapping("departamento")
public class DepartamentoController {

    private final DepartamentoRepository repository;

    // O Spring injetará o repositório automaticamente
    @Autowired
    public DepartamentoController(DepartamentoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/form")
    public String form(Departamento departamento){
        return "departamento/form";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("departamento", repository.departamentos());
        return "departamento/list";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Departamento departamento) {
        repository.save(departamento);
        return "redirect:/departamento/list";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        int qtdFuncionarios = repository.contarFuncionarios(id);

        if (qtdFuncionarios > 0) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não é possível excluir o departamento. Existem " + qtdFuncionarios + " funcionário(s) associado(s).");
        } else {
            repository.remove(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Departamento excluído com sucesso!");
        }

        return "redirect:/departamento/list";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("departamento", repository.departamento(id));
        return new ModelAndView("departamento/form", model);
    }

    @PostMapping("/update")
    public ModelAndView update(Departamento departamento) {
        repository.update(departamento);
        return new ModelAndView("redirect:/departamento/list");
    }
}