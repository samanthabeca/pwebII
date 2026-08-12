package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Departamento;
import br.edu.ifto.pwebII.model.entity.Funcionario;
import br.edu.ifto.pwebII.model.jdbc.dao.DepartamentoDAO;
import br.edu.ifto.pwebII.model.jdbc.dao.FuncionarioDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("funcionario")
public class FuncionarioController {

    FuncionarioDAO dao;
    DepartamentoDAO departamentoDAO;

    public FuncionarioController(){
        dao = new FuncionarioDAO();
        departamentoDAO = new DepartamentoDAO();
    }

    /**
     * @param funcionario necessário devido utilizar no form.html o th:object que faz referência ao objeto esperado no controller.
     * @return
     */
    @GetMapping("/form")
    public ModelAndView form(Funcionario funcionario, ModelMap model){
        model.addAttribute("departamento", departamentoDAO.buscarDepartamento());
        return new ModelAndView("/funcionario/form", model);
    }

    @GetMapping("/list")
    public ModelAndView listar(ModelMap model) {
        model.addAttribute("funcionario", dao.buscarFuncionario());
        return new ModelAndView("/funcionario/list", model);
    }

    @PostMapping("/save")
    public String save(Funcionario funcionario, RedirectAttributes redirect) {
        if (funcionario.getSalario() <= 0.0) {
            redirect.addFlashAttribute("mensagemErro", "O salário deve ser maior que R$ 0,00.");
            return "redirect:/funcionario/list";
        }

        dao.save(funcionario);
        return "redirect:/funcionario/list";
    }

    /**
     * @param id
     * @return
     * @PathVariable é utilizado quando o valor da variável é passada diretamente na URL
     */
    @PostMapping("/remove/{id}")
    public ModelAndView remove(@PathVariable("id") Long id){
        dao.remove(id);
        return new ModelAndView("redirect:/funcionario/list");
    }

    /**
     * @param id
     * @return
     * @PathVariable é utilizado quando o valor da variável é passada diretamente na URL
     */
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        Funcionario funcionario = dao.buscarFuncionario(id);

        // Garante que o objeto departamento não seja nulo para evitar erros no formulário
        if (funcionario != null && funcionario.getDepartamento() == null) {
            funcionario.setDepartamento(new Departamento());
        }

        model.addAttribute("funcionario", funcionario);
        model.addAttribute("departamento", departamentoDAO.buscarDepartamento());

        return new ModelAndView("/funcionario/form", model);
    }

    @PostMapping("/update")
    public ModelAndView update(Funcionario funcionario) {
        dao.update(funcionario);
        return new ModelAndView("redirect:/funcionario/list");
    }
}