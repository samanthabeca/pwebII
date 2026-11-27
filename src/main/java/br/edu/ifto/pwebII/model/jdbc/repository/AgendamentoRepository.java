package br.edu.ifto.pwebII.model.jdbc.repository;

import br.edu.ifto.pwebII.model.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("select agendamento from Agendamento agendamento where agendamento.agenda.id_agenda = :idAgenda")
    List<Agendamento> findAllByAgendaId(@Param("idAgenda") Long idAgenda);

    List<Agendamento> findAllByPaciente_Id(Long idPaciente);
}
