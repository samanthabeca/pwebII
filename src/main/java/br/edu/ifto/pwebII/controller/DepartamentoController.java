package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Departamento;
import br.edu.ifto.pwebII.model.jdbc.dao.DepartamentoDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("departamento")
public class DepartamentoController {

    private final DepartamentoDAO dao;

    public DepartamentoController() {
        this.dao = new DepartamentoDAO();
    }

    @GetMapping("/form")
    public String form(Departamento departamento){
        return "/departamento/form";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        // Alterado de listarTodos() para buscarDepartamento()
        model.addAttribute("departamento", dao.buscarDepartamento());
        return "departamento/list";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Departamento departamento) {
        dao.save(departamento);
        return "redirect:/departamento/list";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        int qtdFuncionarios = dao.contarFuncionarios(id);

        if (qtdFuncionarios > 0) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não é possível excluir o departamento. Existem " + qtdFuncionarios + " funcionário(s) associado(s).");
        } else {
            dao.remove(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Departamento excluído com sucesso!");
        }

        return "redirect:/departamento/list";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("departamento", dao.buscarDepartamento(id));
        return new ModelAndView("/departamento/form", model);
    }

    @PostMapping("/update")
    public ModelAndView update(Departamento departamento) {
        dao.update(departamento);
        return new ModelAndView("redirect:/departamento/list");
    }
}