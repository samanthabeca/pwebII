package br.edu.ifto.pwebII.model.jdbc.repository;

import br.edu.ifto.pwebII.model.entity.Agenda;
import br.edu.ifto.pwebII.model.entity.StatusAgenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findAllByMedico_Id(Long id);

    List<Agenda> findAllByStatusOrderByDataAscHorarioAsc(StatusAgenda status);
}
