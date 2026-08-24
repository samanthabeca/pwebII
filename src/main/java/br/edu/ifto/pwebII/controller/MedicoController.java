package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Consulta;
import br.edu.ifto.pwebII.model.entity.Medico;
import br.edu.ifto.pwebII.model.jdbc.repository.ConsultaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.MedicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Transactional
@Controller
@RequestMapping("medico")
public class MedicoController {

    private final MedicoRepository repository;
    private final ConsultaRepository consultaRep;

    // O Spring injetará o repositório automaticamente
    @Autowired
    public MedicoController(MedicoRepository repository, ConsultaRepository consultaRep) {
        this.repository = repository;
        this.consultaRep = consultaRep;
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
    public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        List<Consulta> consultasDoMedico = consultaRep.consultasPorMedico(id);

        if (!consultasDoMedico.isEmpty()){
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não é possível excluir o(a) médico(a) " + repository.medico(id).getNome() + ", pois ele(a) possui consultas vinculadas a ele(a).");
            return "redirect:/medico/list";
        }

        repository.remove(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Médico excluído com sucesso!");

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