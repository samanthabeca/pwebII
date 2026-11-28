package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.dto.CadastroDisponibilidadeForm;
import br.edu.ifto.pwebII.model.dto.CadastroDisponibilidadePeriodoForm;
import br.edu.ifto.pwebII.model.entity.Agenda;
import br.edu.ifto.pwebII.model.entity.Medico;
import br.edu.ifto.pwebII.model.jdbc.repository.EspecialidadeRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("agenda")
public class AgendaController {

    private final AgendaService agendaService;
    private final EspecialidadeRepository especialidadeRep;

    public AgendaController(AgendaService agendaService, EspecialidadeRepository especialidadeRep) {
        this.agendaService = agendaService;
        this.especialidadeRep = especialidadeRep;
    }

    @GetMapping("/form")
    public String form(@ModelAttribute("cadastroDisponibilidade") CadastroDisponibilidadeForm cadastroDisponibilidade,
                       ModelMap model) {
        carregarMedicosPermitidos(cadastroDisponibilidade, model);
        return "agenda/form";
    }

    @GetMapping("/disponiveis")
    public String disponiveis(@RequestParam(required = false) Long medicoId,
                              @RequestParam(required = false) Long especialidadeId,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                              ModelMap model) {
        List<Medico> medicos = agendaService.listarMedicosParaFiltro();
        List<Medico> medicosDaEspecialidade = especialidadeId == null ? List.of() : medicos.stream()
                .filter(medico -> medico.getEspecialidade().getId_especialidade().equals(especialidadeId))
                .toList();

        if (especialidadeId != null && medicoId == null && medicosDaEspecialidade.size() == 1) {
            medicoId = medicosDaEspecialidade.getFirst().getId();
        }

        boolean filtrosAplicados = medicoId != null || especialidadeId != null || data != null;
        boolean especialidadeSemMedico = especialidadeId != null && medicoId == null;
        boolean selecaoMedicoObrigatoria = especialidadeSemMedico && medicosDaEspecialidade.size() > 1;
        List<Agenda> agendas = filtrosAplicados && !especialidadeSemMedico
                ? agendaService.listarDisponiveisParaReserva(medicoId, especialidadeId, data)
                : List.of();
        model.addAttribute("agenda", agendas);
        model.addAttribute("totalHorarios", agendas.size());
        model.addAttribute("medico", medicos);
        model.addAttribute("especialidade", especialidadeRep.findComMedicosOrderByNomeAsc());
        model.addAttribute("medicoId", medicoId);
        model.addAttribute("especialidadeId", especialidadeId);
        model.addAttribute("data", data);
        model.addAttribute("filtrosAplicados", filtrosAplicados);
        model.addAttribute("especialidadeSemMedico", especialidadeSemMedico);
        model.addAttribute("selecaoMedicoObrigatoria", selecaoMedicoObrigatoria);
        return "agenda/disponiveis";
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
