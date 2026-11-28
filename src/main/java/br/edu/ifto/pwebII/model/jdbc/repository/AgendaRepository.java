package br.edu.ifto.pwebII.model.jdbc.repository;

import br.edu.ifto.pwebII.model.entity.Agenda;
import br.edu.ifto.pwebII.model.entity.StatusAgenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    @Query("select agenda from Agenda agenda where agenda.medico.id = :medicoId")
    List<Agenda> findAllByMedicoId(@Param("medicoId") Long medicoId);

    List<Agenda> findAllByStatusOrderByDataAscHorarioAsc(StatusAgenda status);

    @Query("""
            select case when count(agenda) > 0 then true else false end
            from Agenda agenda
            where agenda.medico.id = :medicoId
              and agenda.data = :data
              and agenda.horario = :horario
            """)
    boolean existeHorarioDoMedico(@Param("medicoId") Long medicoId,
                                  @Param("data") LocalDate data,
                                  @Param("horario") LocalTime horario);
}
