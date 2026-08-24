package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Consulta;
import br.edu.ifto.pwebII.model.entity.Paciente;
import br.edu.ifto.pwebII.model.jdbc.repository.ConsultaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.PacienteRepository;
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
@RequestMapping("paciente")
public class PacienteController {

    private final PacienteRepository repository;
    private final ConsultaRepository consultaRep;

    // O Spring injetará o repositório automaticamente
    @Autowired
    public PacienteController(PacienteRepository repository, ConsultaRepository consultaRep) {
        this.repository = repository;
        this.consultaRep = consultaRep;
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
    public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        List<Consulta> consultasDoPaciente = consultaRep.consultasPorPaciente(id);

        if (!consultasDoPaciente.isEmpty()){
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não é possível excluir o(a) paciente " + repository.paciente(id).getNome() + ", pois ele(a) possui consultas vinculadas a ele(a).");
            return "redirect:/paciente/list";
        }

        repository.remove(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Paciente excluído com sucesso!");

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