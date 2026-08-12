package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Pessoa;
import br.edu.ifto.pwebII.model.jdbc.dao.PessoaDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("pessoa")
public class PessoasController {

    PessoaDAO dao;

    public PessoasController(){
        dao = new PessoaDAO();
    }

    /**
     * @param pessoa necessário devido utilizar no form.html o th:object que faz referência ao objeto esperado no controller.
     * @return
     */
    @GetMapping("/form")
    public String form(Pessoa pessoa){
        return "/pessoa/form";
    }

    @GetMapping("/list")
    public ModelAndView listar(ModelMap model) {
        model.addAttribute("pessoa", dao.buscarPessoas());
        return new ModelAndView("/pessoa/list", model);
    }

    @PostMapping("/save")
    public ModelAndView save(Pessoa pessoa){
        dao.save(pessoa);
        return new ModelAndView("redirect:/pessoa/list");
    }

    /**
     * @param id
     * @return
     * @PathVariable é utilizado quando o valor da variável é passada diretamente na URL
     */
    @PostMapping("/remove/{id}")
    public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        boolean removido = dao.remove(id);

        if (removido) {
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Pessoa excluída com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Não foi possível excluir a pessoa.");
        }

        return "redirect:/pessoa/list";
    }

    /**
     * @param id
     * @return
     * @PathVariable é utilizado quando o valor da variável é passada diretamente na URL
     */
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("pessoa", dao.buscarPessoa(id));
        return new ModelAndView("/pessoa/form", model);
    }

    @PostMapping("/update")
    public ModelAndView update(Pessoa pessoa) {
        dao.update(pessoa);
        return new ModelAndView("redirect:/pessoa/list");
    }
}