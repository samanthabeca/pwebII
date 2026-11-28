package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.dto.CadastroDisponibilidadeForm;
import br.edu.ifto.pwebII.model.dto.CadastroDisponibilidadePeriodoForm;
import br.edu.ifto.pwebII.model.entity.Medico;
import br.edu.ifto.pwebII.service.AgendaService;
import br.edu.ifto.pwebII.service.RegraAgendaException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("agenda")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @GetMapping("/form")
    public String form(@ModelAttribute("cadastroDisponibilidade") CadastroDisponibilidadeForm cadastroDisponibilidade,
                       ModelMap model) {
        carregarMedicosPermitidos(cadastroDisponibilidade, model);
        return "agenda/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("cadastroDisponibilidade") CadastroDisponibilidadeForm cadastroDisponibilidade,
                       BindingResult resultado, ModelMap model, RedirectAttributes redirectAttributes) {
        if (!resultado.hasErrors()) {
            try {
                agendaService.cadastrarDisponibilidade(cadastroDisponibilidade, autenticacao());
                redirectAttributes.addFlashAttribute("mensagemSucesso", "Horário disponível cadastrado com sucesso!");
                return "redirect:/agenda/form";
            } catch (RegraAgendaException e) {
                resultado.reject("agenda.regra", e.getMessage());
            } catch (DataIntegrityViolationException e) {
                resultado.reject("agenda.duplicada", "Já existe um horário cadastrado para este médico nessa data e horário.");
            }
        }

        carregarMedicosPermitidos(cadastroDisponibilidade, model);
        return "agenda/form";
    }

    @GetMapping("/periodo/form")
    public String formPorPeriodo(@ModelAttribute("cadastroDisponibilidadePeriodo") CadastroDisponibilidadePeriodoForm cadastroDisponibilidade,
                                 ModelMap model) {
        carregarMedicosPermitidos(cadastroDisponibilidade, model);
        model.addAttribute("horariosPadrao", agendaService.horariosPadrao());
        return "agenda/periodo-form";
    }

    @PostMapping("/periodo/save")
    public String savePorPeriodo(
            @Valid @ModelAttribute("cadastroDisponibilidadePeriodo") CadastroDisponibilidadePeriodoForm cadastroDisponibilidade,
            BindingResult resultado, ModelMap model, RedirectAttributes redirectAttributes) {
        if (!resultado.hasErrors()) {
            try {
                int quantidade = agendaService.cadastrarDisponibilidadesPorPeriodo(cadastroDisponibilidade, autenticacao()).size();
                redirectAttributes.addFlashAttribute("mensagemSucesso",
                        quantidade + " horário(s) disponível(is) cadastrado(s) com sucesso!");
                return "redirect:/agenda/periodo/form";
            } catch (RegraAgendaException e) {
                resultado.reject("agenda.periodo.regra", e.getMessage());
            } catch (DataIntegrityViolationException e) {
                resultado.reject("agenda.periodo.duplicada", "Já existe um horário cadastrado para este médico no período informado.");
            }
        }

        carregarMedicosPermitidos(cadastroDisponibilidade, model);
        model.addAttribute("horariosPadrao", agendaService.horariosPadrao());
        return "agenda/periodo-form";
    }

    private void carregarMedicosPermitidos(CadastroDisponibilidadeForm cadastroDisponibilidade, ModelMap model) {
        Authentication autenticacao = autenticacao();
        List<Medico> medicos = agendaService.medicosPermitidos(autenticacao);
        boolean medicoRestrito = agendaService.medicoEstaRestritoAPropriaAgenda(autenticacao);

        if (medicoRestrito && cadastroDisponibilidade.getMedicoId() == null && !medicos.isEmpty()) {
            cadastroDisponibilidade.setMedicoId(medicos.getFirst().getId());
        }

        model.addAttribute("medico", medicos);
        model.addAttribute("medicoRestrito", medicoRestrito);
        model.addAttribute("medicoAutenticado", medicoRestrito && !medicos.isEmpty() ? medicos.getFirst() : null);
    }

    private void carregarMedicosPermitidos(CadastroDisponibilidadePeriodoForm cadastroDisponibilidade, ModelMap model) {
        Authentication autenticacao = autenticacao();
        List<Medico> medicos = agendaService.medicosPermitidos(autenticacao);
        boolean medicoRestrito = agendaService.medicoEstaRestritoAPropriaAgenda(autenticacao);

        if (medicoRestrito && cadastroDisponibilidade.getMedicoId() == null && !medicos.isEmpty()) {
            cadastroDisponibilidade.setMedicoId(medicos.getFirst().getId());
        }

        model.addAttribute("medico", medicos);
        model.addAttribute("medicoRestrito", medicoRestrito);
        model.addAttribute("medicoAutenticado", medicoRestrito && !medicos.isEmpty() ? medicos.getFirst() : null);
    }

    private Authentication autenticacao() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
