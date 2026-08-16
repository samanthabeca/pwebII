package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Departamento;
import br.edu.ifto.pwebII.model.entity.Funcionario;
import br.edu.ifto.pwebII.model.jdbc.repository.DepartamentoRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.FuncionarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Transactional
@Controller
@RequestMapping("funcionario")
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRep;
    private final DepartamentoRepository departamentoRep;

    @Autowired
    public FuncionarioController(FuncionarioRepository funcionarioRep, DepartamentoRepository departamentoRep) {
        this.funcionarioRep = funcionarioRep;
        this.departamentoRep = departamentoRep;
    }

    @GetMapping("/form")
    public ModelAndView form(Funcionario funcionario, ModelMap model){
        model.addAttribute("departamento", departamentoRep.departamentos());
        return new ModelAndView("funcionario/form", model);
    }

    @GetMapping("/list")
    public ModelAndView listar(ModelMap model) {
        model.addAttribute("funcionario", funcionarioRep.funcionarios());
        return new ModelAndView("funcionario/list", model);
    }

    @PostMapping("/save")
    public String save(Funcionario funcionario, RedirectAttributes redirect) {
        if (funcionario.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
            // Salário é menor ou igual a 0.0
            redirect.addFlashAttribute("mensagemErro", "O salário deve ser maior que R$ 0,00.");
            return "redirect:/funcionario/list";
        }

        funcionarioRep.save(funcionario);
        return "redirect:/funcionario/list";
    }

    /**
     * @param id
     * @return
     * @PathVariable é utilizado quando o valor da variável é passada diretamente na URL
     */
    @PostMapping("/remove/{id}")
    public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        boolean removido = funcionarioRep.remove(id);

        if (removido) {
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Funcionário(a) excluído(a) com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Não foi possível excluir o(a) funcionário(a).");
        }

        return "redirect:/funcionario/list";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        Funcionario funcionario = funcionarioRep.funcionario(id);

        if (funcionario != null && funcionario.getDepartamento() == null) {
            funcionario.setDepartamento(new Departamento());
        }

        model.addAttribute("funcionario", funcionario);
        model.addAttribute("departamento", departamentoRep.departamentos());

        return new ModelAndView("funcionario/form", model);
    }

    @PostMapping("/update")
    public String update(Funcionario funcionario, RedirectAttributes redirect) {
        if (funcionario.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
            // Salário é menor ou igual a 0.0
            redirect.addFlashAttribute("mensagemErro", "O salário deve ser maior que R$ 0,00.");
            return "redirect:/funcionario/list";
        }

        funcionarioRep.update(funcionario);
        return "redirect:/funcionario/list";
    }
}