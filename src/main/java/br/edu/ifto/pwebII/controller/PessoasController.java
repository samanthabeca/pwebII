package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Pessoa;
import br.edu.ifto.pwebII.model.jdbc.repository.PessoaRepository;
import jakarta.transaction.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Transactional
@Controller
@RequestMapping("pessoa")
public class PessoasController {

    @Autowired
    PessoaRepository repository;

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
        model.addAttribute("pessoa", repository.pessoas());
        return new ModelAndView("/pessoa/list", model);
    }

    @PostMapping("/save")
    public ModelAndView save(Pessoa pessoa){
        repository.save(pessoa);
        return new ModelAndView("redirect:/pessoa/list");
    }

    /**
     * @param id
     * @return
     * @PathVariable é utilizado quando o valor da variável é passada diretamente na URL
     */
    @PostMapping("/remove/{id}")
    public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        boolean removido = repository.remove(id);

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
        model.addAttribute("pessoa", repository.pessoa(id));
        return new ModelAndView("/pessoa/form", model);
    }

    @PostMapping("/update")
    public ModelAndView update(Pessoa pessoa) {
        repository.update(pessoa);
        return new ModelAndView("redirect:/pessoa/list");
    }
}