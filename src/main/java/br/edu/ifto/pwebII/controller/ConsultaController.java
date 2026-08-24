package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Consulta;
import br.edu.ifto.pwebII.model.entity.Medico;
import br.edu.ifto.pwebII.model.entity.Paciente;
import br.edu.ifto.pwebII.model.jdbc.repository.ConsultaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.MedicoRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.PacienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Transactional
@Controller
@RequestMapping("consulta")
public class ConsultaController {

    private final ConsultaRepository consultaRep;
    private final PacienteRepository pacienteRep;
    private final MedicoRepository medicoRep;

    @Autowired
    public ConsultaController(ConsultaRepository consultaRep, PacienteRepository pacienteRep, MedicoRepository medicoRep) {
        this.consultaRep = consultaRep;
        this.pacienteRep = pacienteRep;
        this.medicoRep = medicoRep;
    }

    @GetMapping("/form")
    public ModelAndView form(Consulta consulta, ModelMap model){
        model.addAttribute("paciente", pacienteRep.pacientes());
        model.addAttribute("medico", medicoRep.medicos());

        return new ModelAndView("consulta/form", model);
    }

    @GetMapping("/list")
    public ModelAndView listar(ModelMap model) {
        model.addAttribute("consulta", consultaRep.consultas());
        return new ModelAndView("consulta/list", model);
    }

    @PostMapping("/save")
    public String save(Consulta consulta, RedirectAttributes redirect) {
        consultaRep.save(consulta);
        return "redirect:/consulta/list";
    }

    /**
     * @param id
     * @return
     * @PathVariable é utilizado quando o valor da variável é passada diretamente na URL
     */
    @PostMapping("/remove/{id}")
    public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        boolean removido = consultaRep.remove(id);

        if (removido) {
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Consulta excluída com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Não foi possível excluir a consulta");
        }

        return "redirect:/consulta/list";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model) {
        Consulta consulta = consultaRep.consulta(id);

        if (consulta != null && consulta.getPaciente() == null && consulta.getMedico() == null) {
            consulta.setPaciente(new Paciente());
            consulta.setMedico(new Medico());
        }

        model.addAttribute("consulta", consulta);
        model.addAttribute("paciente", pacienteRep.pacientes());
        model.addAttribute("medico", medicoRep.medicos());

        return new ModelAndView("consulta/form", model);
    }

    @PostMapping("/update")
    public String update(Consulta consulta, RedirectAttributes redirect) {
        consultaRep.update(consulta);
        return "redirect:/consulta/list";
    }

    @GetMapping("/paciente/{id}")
    public ModelAndView listByPaciente(@PathVariable("id") Long id, ModelAndView model) {
        // 1. Busca no banco as consultas desse paciente
        List<Consulta> listaFiltrada = consultaRep.consultasPorPaciente(id);
        Paciente paciente = pacienteRep.paciente(id);

//        // LINHA DE TESTE: veja o que aparece no console do STS / IntelliJ / Eclipse
//        System.out.println(">>> Consultas encontradas no banco: " + listaFiltrada.size());

        // 2. Envia a lista e o paciente para a página
        model.addObject("consulta", listaFiltrada);
        model.addObject("paciente", paciente); // Envia o paciente para a view
        // 3. Abre a página de listagem de consultas
        model.setViewName("consulta/list");

        return model;
    }

    @GetMapping("/medico/{id}")
    public ModelAndView listByMedico(@PathVariable("id") Long id, ModelAndView model) {
        // 1. Busca no banco as consultas desse medico
        List<Consulta> listaFiltrada = consultaRep.consultasPorMedico(id);
        Medico medico = medicoRep.medico(id);

        // 2. Envia a lista e o medico para a página
        model.addObject("consulta", listaFiltrada);
        model.addObject("medico", medico); // Envia o médico para a view
        // 3. Abre a página de listagem de consultas
        model.setViewName("consulta/list");

        return model;
    }

    @GetMapping("/{id}")
    public ModelAndView show(@PathVariable("id") Long id, ModelAndView model) {
        Consulta consulta = consultaRep.consulta(id);

        model.addObject("consulta", consulta);
        model.setViewName("consulta/detail"); // Nome do arquivo HTML de detalhes

        return model;
    }
}