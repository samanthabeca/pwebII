package br.edu.ifto.pwebII.service;

import br.edu.ifto.pwebII.model.dto.CadastroDisponibilidadeForm;
import br.edu.ifto.pwebII.model.dto.CadastroDisponibilidadePeriodoForm;
import br.edu.ifto.pwebII.model.entity.Agenda;
import br.edu.ifto.pwebII.model.entity.Medico;
import br.edu.ifto.pwebII.model.entity.StatusAgenda;
import br.edu.ifto.pwebII.model.jdbc.repository.AgendaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.MedicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class AgendaService {

    private static final List<LocalTime> HORARIOS_PADRAO = List.of(
            LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0),
            LocalTime.of(11, 0), LocalTime.of(14, 0),
            LocalTime.of(15, 0), LocalTime.of(16, 0), LocalTime.of(17, 0)
    );

    private final AgendaRepository agendaRep;
    private final MedicoRepository medicoRep;

    public AgendaService(AgendaRepository agendaRep, MedicoRepository medicoRep) {
        this.agendaRep = agendaRep;
        this.medicoRep = medicoRep;
    }

    @Transactional
    public Agenda cadastrarDisponibilidade(CadastroDisponibilidadeForm form, Authentication autenticacao) {
        validarDadosObrigatorios(form);

        Medico medico = medicoRep.findById(form.getMedicoId())
                .orElseThrow(() -> new RegraAgendaException("O médico selecionado não existe."));

        validarPermissaoParaMedico(medico, autenticacao);
        validarHorarioFuturo(form);

        if (agendaRep.existeHorarioDoMedico(medico.getId(), form.getData(), form.getHorario())) {
            throw new RegraAgendaException("Já existe um horário cadastrado para este médico nessa data e horário.");
        }

        Agenda agenda = new Agenda();
        agenda.setMedico(medico);
        agenda.setData(form.getData());
        agenda.setHorario(form.getHorario());
        agenda.setStatus(StatusAgenda.DISPONIVEL);
        return agendaRep.save(agenda);
    }

    @Transactional
    public List<Agenda> cadastrarDisponibilidadesPorPeriodo(CadastroDisponibilidadePeriodoForm form,
                                                            Authentication autenticacao) {
        validarDadosObrigatorios(form);

        Medico medico = medicoRep.findById(form.getMedicoId())
                .orElseThrow(() -> new RegraAgendaException("O médico selecionado não existe."));

        validarPermissaoParaMedico(medico, autenticacao);
        if (form.getDataInicial().isAfter(form.getDataFinal())) {
            throw new RegraAgendaException("A data inicial deve ser anterior ou igual à data final.");
        }
        if (!HORARIOS_PADRAO.containsAll(form.getHorarios())) {
            throw new RegraAgendaException("Selecione apenas horários disponíveis no formulário.");
        }
        if (new HashSet<>(form.getHorarios()).size() != form.getHorarios().size()) {
            throw new RegraAgendaException("Não repita horários no mesmo cadastro.");
        }

        List<Agenda> agendas = new ArrayList<>();
        LocalDate data = form.getDataInicial();
        while (!data.isAfter(form.getDataFinal())) {
            if (ehDiaUtil(data)) {
                for (LocalTime horario : form.getHorarios()) {
                    validarHorarioFuturo(data, horario);
                    if (agendaRep.existeHorarioDoMedico(medico.getId(), data, horario)) {
                        throw new RegraAgendaException("Já existe um horário cadastrado para este médico em "
                                + data + " às " + horario + ".");
                    }

                    Agenda agenda = new Agenda();
                    agenda.setMedico(medico);
                    agenda.setData(data);
                    agenda.setHorario(horario);
                    agenda.setStatus(StatusAgenda.DISPONIVEL);
                    agendas.add(agenda);
                }
            }
            data = data.plusDays(1);
        }

        if (agendas.isEmpty()) {
            throw new RegraAgendaException("O período informado não possui dias úteis para cadastro.");
        }
        return agendaRep.saveAll(agendas);
    }

    public List<LocalTime> horariosPadrao() {
        return HORARIOS_PADRAO;
    }

    public List<Agenda> listarDisponiveisParaReserva(Long medicoId, Long especialidadeId, LocalDate data) {
        LocalDateTime agora = LocalDateTime.now();
        return agendaRep.findDisponiveisParaReserva(StatusAgenda.DISPONIVEL, agora.toLocalDate(), agora.toLocalTime(),
                medicoId, especialidadeId, data);
    }

    public List<Medico> listarMedicosParaFiltro() {
        return medicoRep.findAllByOrderByNomeAsc();
    }

    public List<Medico> medicosPermitidos(Authentication autenticacao) {
        if (podeGerenciarAgendaDeQualquerMedico(autenticacao)) {
            return medicoRep.findAll();
        }

        return List.of(medicoAutenticado(autenticacao));
    }

    public boolean medicoEstaRestritoAPropriaAgenda(Authentication autenticacao) {
        return !podeGerenciarAgendaDeQualquerMedico(autenticacao) && possuiPerfil(autenticacao, "ROLE_MEDICO");
    }

    private void validarDadosObrigatorios(CadastroDisponibilidadeForm form) {
        if (form.getMedicoId() == null || form.getData() == null || form.getHorario() == null) {
            throw new RegraAgendaException("Médico, data e horário são obrigatórios.");
        }
    }

    private void validarDadosObrigatorios(CadastroDisponibilidadePeriodoForm form) {
        if (form.getMedicoId() == null || form.getDataInicial() == null || form.getDataFinal() == null
                || form.getHorarios() == null || form.getHorarios().isEmpty()) {
            throw new RegraAgendaException("Médico, período e pelo menos um horário são obrigatórios.");
        }
    }

    private void validarHorarioFuturo(CadastroDisponibilidadeForm form) {
        validarHorarioFuturo(form.getData(), form.getHorario());
    }

    private void validarHorarioFuturo(LocalDate data, LocalTime horario) {
        if (data.atTime(horario).isBefore(LocalDateTime.now())) {
            throw new RegraAgendaException("Não é possível cadastrar um horário no passado.");
        }
    }

    private boolean ehDiaUtil(LocalDate data) {
        return data.getDayOfWeek() != DayOfWeek.SATURDAY && data.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    private void validarPermissaoParaMedico(Medico medico, Authentication autenticacao) {
        if (podeGerenciarAgendaDeQualquerMedico(autenticacao)) {
            return;
        }

        Medico medicoAutenticado = medicoAutenticado(autenticacao);
        if (!medicoAutenticado.getId().equals(medico.getId())) {
            throw new RegraAgendaException("O médico só pode cadastrar horários para a própria agenda.");
        }
    }

    private Medico medicoAutenticado(Authentication autenticacao) {
        if (!possuiPerfil(autenticacao, "ROLE_MEDICO")) {
            throw new RegraAgendaException("Você não tem permissão para cadastrar disponibilidade.");
        }

        return medicoRep.findByEmailIgnoreCase(autenticacao.getName())
                .orElseThrow(() -> new RegraAgendaException("Não foi possível identificar o médico autenticado."));
    }

    private boolean podeGerenciarAgendaDeQualquerMedico(Authentication autenticacao) {
        return possuiPerfil(autenticacao, "ROLE_ADMIN") || possuiPerfil(autenticacao, "ROLE_SECRETARIA");
    }

    private boolean possuiPerfil(Authentication autenticacao, String perfil) {
        return autenticacao != null && autenticacao.isAuthenticated()
                && autenticacao.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(perfil));
    }
}
