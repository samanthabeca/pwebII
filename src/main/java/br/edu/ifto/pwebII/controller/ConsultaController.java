package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.dto.CadastroConsultaForm;
import br.edu.ifto.pwebII.model.entity.Agenda;
import br.edu.ifto.pwebII.model.entity.Agendamento;
import br.edu.ifto.pwebII.model.entity.Consulta;
import br.edu.ifto.pwebII.model.entity.Medico;
import br.edu.ifto.pwebII.model.entity.Paciente;
import br.edu.ifto.pwebII.model.entity.StatusAgenda;
import br.edu.ifto.pwebII.model.entity.StatusAgendamento;
import br.edu.ifto.pwebII.model.entity.StatusConsulta;
import br.edu.ifto.pwebII.model.jdbc.repository.AgendaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.AgendamentoRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.ConsultaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.MedicoRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.PacienteRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Controller
@RequestMapping("consulta")
public class ConsultaController {

    private final ConsultaRepository consultaRep;
    private final PacienteRepository pacienteRep;
    private final MedicoRepository medicoRep;
    private final AgendaRepository agendaRep;
    private final AgendamentoRepository agendamentoRep;

    @Autowired
    public ConsultaController(ConsultaRepository consultaRep, PacienteRepository pacienteRep, MedicoRepository medicoRep,
                              AgendaRepository agendaRep, AgendamentoRepository agendamentoRep) {
        this.consultaRep = consultaRep;
        this.pacienteRep = pacienteRep;
        this.medicoRep = medicoRep;
        this.agendaRep = agendaRep;
        this.agendamentoRep = agendamentoRep;
    }

    @GetMapping("/form")
    public ModelAndView form(@ModelAttribute("cadastroConsulta") CadastroConsultaForm cadastroConsulta,
                             ModelMap model) {
        carregarDadosDoCadastroManual(model);
        return new ModelAndView("consulta/cadastro-form", model);
    }

    @GetMapping("/list")
    public ModelAndView listar(ModelMap model) {
        List<Consulta> consultas = consultaRep.findAll();
        model.addAttribute("consulta", consultas);
        model.addAttribute("totalConsultas", consultas.size());
        model.addAttribute("valorTotal", calcularValorTotal(consultas));
        model.addAttribute("consultasGerenciaveis", consultasGerenciaveis(consultas));
        return new ModelAndView("consulta/list", model);
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("cadastroConsulta") CadastroConsultaForm cadastroConsulta,
                       BindingResult resultado, ModelMap model, RedirectAttributes redirectAttribute) {
        if (!podeCriarConsultaManual()) {
            resultado.reject("consulta.permissao.negada", "Você não tem permissão para cadastrar consultas manualmente.");
        }

        Agenda agenda = buscarAgendaDeEncaixe(cadastroConsulta, resultado);
        Paciente paciente = buscarPaciente(cadastroConsulta, resultado);

        if (resultado.hasErrors()) {
            carregarDadosDoCadastroManual(model);
            return "consulta/cadastro-form";
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setAgenda(agenda);
        agendamento.setPaciente(paciente);
        agendamento.setStatus(StatusAgendamento.REALIZADO);
        agendamentoRep.save(agendamento);

        Consulta consulta = new Consulta();
        consulta.setAgendamento(agendamento);
        consulta.setPaciente(paciente);
        consulta.setMedico(agenda.getMedico());
        consulta.setData(agenda.getData().atTime(agenda.getHorario()));
        consulta.setValor(cadastroConsulta.getValor());
        consulta.setObservacao(cadastroConsulta.getObservacao());
        consulta.setStatus(StatusConsulta.CRIADA);
        consultaRep.save(consulta);

        agenda.setStatus(StatusAgenda.AGENDADO);
        agendaRep.save(agenda);

        redirectAttribute.addFlashAttribute("mensagemSucesso", "Consulta de encaixe cadastrada com sucesso!");
        return "redirect:/consulta/list";
    }

    /**
     * @param id
     * @return
     * @PathVariable é utilizado quando o valor da variável é passada diretamente na URL
     */
    @PostMapping("/remove/{id}")
    public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Consulta consulta = consultaRep.findById(id).orElse(null);
        if (consulta == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Não foi possível excluir a consulta.");
        } else if (!podeGerenciarConsulta(consulta)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Você não tem permissão para excluir esta consulta.");
        } else {
            consultaRep.delete(consulta);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Consulta excluída com sucesso!");
        }

        return "redirect:/consulta/list";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, ModelMap model, RedirectAttributes redirectAttributes) {
        Consulta consulta = consultaRep.findById(id).orElse(null);

        if (consulta == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Consulta não encontrada.");
            return new ModelAndView("redirect:/consulta/list");
        }
        if (!podeGerenciarConsulta(consulta)) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Você não tem permissão para editar esta consulta.");
            return new ModelAndView("redirect:/consulta/list");
        }

        garantirRelacionamentos(consulta);
        model.addAttribute("consulta", consulta);
        carregarDadosDoFormulario(model);

        return new ModelAndView("consulta/form", model);
    }

    @PostMapping("/update")
    public String update(@Valid Consulta consulta, BindingResult resultado, ModelMap model,
                         RedirectAttributes redirect) {
        Consulta consultaPersistida = consulta.getId_consulta() == null
                ? null
                : consultaRep.findById(consulta.getId_consulta()).orElse(null);

        if (consultaPersistida == null) {
            redirect.addFlashAttribute("mensagemErro", "A consulta informada não existe mais.");
            return "redirect:/consulta/list";
        }
        if (!podeGerenciarConsulta(consultaPersistida)) {
            redirect.addFlashAttribute("mensagemErro", "Você não tem permissão para editar esta consulta.");
            return "redirect:/consulta/list";
        }
        preservarVinculoDoAgendamento(consulta, consultaPersistida, resultado);
        validarRelacionamentos(consulta, resultado);

        if (resultado.hasErrors()) {
            carregarDadosDoFormulario(model);
            return "consulta/form";
        }

        consultaRep.save(consulta);
        redirect.addFlashAttribute("mensagemSucesso", "Consulta atualizada com sucesso!");
        return "redirect:/consulta/list";
    }

    @GetMapping("/paciente/{id}")
    public ModelAndView listByPaciente(@PathVariable("id") Long id, ModelAndView model,
                                       RedirectAttributes redirectAttributes) {
        Paciente paciente = pacienteRep.findById(id).orElse(null);
        if (paciente == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Paciente não encontrado.");
            return new ModelAndView("redirect:/paciente/list");
        }
        List<Consulta> listaFiltrada = consultaRep.findAllByPaciente_Id(id);

        model.addObject("consulta", listaFiltrada);
        model.addObject("paciente", paciente);
        model.addObject("totalConsultas", listaFiltrada.size());
        model.addObject("valorTotal", calcularValorTotal(listaFiltrada));
        model.addObject("consultasGerenciaveis", consultasGerenciaveis(listaFiltrada));
        model.setViewName("consulta/list");

        return model;
    }

    @GetMapping("/medico/{id}")
    public ModelAndView listByMedico(@PathVariable("id") Long id, ModelAndView model,
                                     RedirectAttributes redirectAttributes) {
        Medico medico = medicoRep.findById(id).orElse(null);
        if (medico == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Médico não encontrado.");
            return new ModelAndView("redirect:/medico/list");
        }
        List<Consulta> listaFiltrada = consultaRep.findAllByMedico_Id(id);

        model.addObject("consulta", listaFiltrada);
        model.addObject("medico", medico);
        model.addObject("totalConsultas", listaFiltrada.size());
        model.addObject("valorTotal", calcularValorTotal(listaFiltrada));
        model.addObject("consultasGerenciaveis", consultasGerenciaveis(listaFiltrada));
        model.setViewName("consulta/list");

        return model;
    }

    @GetMapping("/{id}")
    public ModelAndView show(@PathVariable("id") Long id, ModelAndView model,
                             RedirectAttributes redirectAttributes) {
        Consulta consulta = consultaRep.findById(id).orElse(null);
        if (consulta == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Consulta não encontrada.");
            return new ModelAndView("redirect:/consulta/list");
        }

        model.addObject("consulta", consulta);
        model.addObject("podeGerenciarConsulta", podeGerenciarConsulta(consulta));
        model.setViewName("consulta/detail");

        return model;
    }

    private BigDecimal calcularValorTotal(List<Consulta> consultas) {
        return consultas.stream()
                .map(Consulta::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<Long> consultasGerenciaveis(List<Consulta> consultas) {
        return consultas.stream()
                .filter(this::podeGerenciarConsulta)
                .map(Consulta::getId_consulta)
                .collect(Collectors.toSet());
    }

    private boolean podeGerenciarConsulta(Consulta consulta) {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            return false;
        }
        if (possuiPerfil(autenticacao, "ROLE_ADMIN")) {
            return true;
        }
        if (!possuiPerfil(autenticacao, "ROLE_MEDICO")
                || consulta.getMedico() == null || consulta.getMedico().getId() == null) {
            return !possuiPerfil(autenticacao, "ROLE_MEDICO");
        }

        return medicoRep.findByEmailIgnoreCase(autenticacao.getName())
                .map(medico -> medico.getId().equals(consulta.getMedico().getId()))
                .orElse(false);
    }

    private boolean possuiPerfil(Authentication autenticacao, String perfil) {
        return autenticacao.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(perfil));
    }

    private void carregarDadosDoFormulario(ModelMap model) {
        model.addAttribute("paciente", pacienteRep.findAll());
        model.addAttribute("medico", medicoRep.findAll());
    }

    private void carregarDadosDoCadastroManual(ModelMap model) {
        model.addAttribute("paciente", pacienteRep.findAll());
        model.addAttribute("agenda", agendaRep.findAllByStatusOrderByDataAscHorarioAsc(StatusAgenda.DISPONIVEL_ENCAIXE));
    }

    private void garantirRelacionamentos(Consulta consulta) {
        if (consulta.getPaciente() == null) {
            consulta.setPaciente(new Paciente());
        }
        if (consulta.getMedico() == null) {
            consulta.setMedico(new Medico());
        }
    }

    private void validarRelacionamentos(Consulta consulta, BindingResult resultado) {
        garantirRelacionamentos(consulta);

        if (consulta.getPaciente().getId() == null) {
            resultado.rejectValue("paciente.id", "consulta.paciente.obrigatorio", "Selecione um paciente.");
        } else if (!pacienteRep.existsById(consulta.getPaciente().getId())) {
            resultado.rejectValue("paciente.id", "consulta.paciente.invalido", "O paciente selecionado não existe.");
        }

        if (consulta.getMedico().getId() == null) {
            resultado.rejectValue("medico.id", "consulta.medico.obrigatorio", "Selecione um médico.");
        } else if (!medicoRep.existsById(consulta.getMedico().getId())) {
            resultado.rejectValue("medico.id", "consulta.medico.invalido", "O médico selecionado não existe.");
        }
    }

    private Agenda buscarAgendaDeEncaixe(CadastroConsultaForm cadastroConsulta, BindingResult resultado) {
        if (cadastroConsulta.getAgendaId() == null) {
            return null;
        }

        Agenda agenda = agendaRep.findById(cadastroConsulta.getAgendaId()).orElse(null);
        if (agenda == null) {
            resultado.rejectValue("agendaId", "consulta.agenda.invalida", "O horário selecionado não existe.");
            return null;
        }
        if (agenda.getStatus() != StatusAgenda.DISPONIVEL_ENCAIXE) {
            resultado.rejectValue("agendaId", "consulta.agenda.indisponivel", "O horário selecionado não está disponível para encaixe.");
        }
        if (agenda.getData().atTime(agenda.getHorario()).isBefore(LocalDateTime.now())) {
            resultado.rejectValue("agendaId", "consulta.agenda.passada", "Não é possível criar uma consulta em horário passado.");
        }
        if (!medicoPodeUsarAgenda(agenda)) {
            resultado.rejectValue("agendaId", "consulta.agenda.medico", "O médico só pode cadastrar consulta nos próprios horários.");
        }
        return agenda;
    }

    private Paciente buscarPaciente(CadastroConsultaForm cadastroConsulta, BindingResult resultado) {
        if (cadastroConsulta.getPacienteId() == null) {
            return null;
        }

        return pacienteRep.findById(cadastroConsulta.getPacienteId()).orElseGet(() -> {
            resultado.rejectValue("pacienteId", "consulta.paciente.invalido", "O paciente selecionado não existe.");
            return null;
        });
    }

    private boolean podeCriarConsultaManual() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        return autenticacao != null && autenticacao.isAuthenticated()
                && (possuiPerfil(autenticacao, "ROLE_MEDICO") || possuiPerfil(autenticacao, "ROLE_SECRETARIA"));
    }

    private boolean medicoPodeUsarAgenda(Agenda agenda) {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null || !possuiPerfil(autenticacao, "ROLE_MEDICO")) {
            return true;
        }

        return medicoRep.findByEmailIgnoreCase(autenticacao.getName())
                .map(medico -> medico.getId().equals(agenda.getMedico().getId()))
                .orElse(false);
    }

    private void preservarVinculoDoAgendamento(Consulta consulta, Consulta consultaPersistida,
                                                BindingResult resultado) {
        Agendamento agendamento = consultaPersistida.getAgendamento();
        if (agendamento == null) {
            return;
        }

        Agenda agenda = agendamento.getAgenda();
        boolean dadosDoAgendamentoForamAlterados = consulta.getPaciente() == null
                || consulta.getPaciente().getId() == null
                || !consulta.getPaciente().getId().equals(agendamento.getPaciente().getId())
                || consulta.getMedico() == null
                || consulta.getMedico().getId() == null
                || !consulta.getMedico().getId().equals(agenda.getMedico().getId())
                || consulta.getData() == null
                || !consulta.getData().equals(agenda.getData().atTime(agenda.getHorario()));

        if (dadosDoAgendamentoForamAlterados) {
            resultado.reject("consulta.agendamento.inalteravel",
                    "Paciente, médico, data e horário são definidos pelo agendamento e não podem ser alterados.");
        }

        consulta.setAgendamento(agendamento);
        consulta.setPaciente(agendamento.getPaciente());
        consulta.setMedico(agenda.getMedico());
        consulta.setData(agenda.getData().atTime(agenda.getHorario()));
        consulta.setStatus(consultaPersistida.getStatus());
    }
}
