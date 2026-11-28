package br.edu.ifto.pwebII.model.jdbc.repository;

import br.edu.ifto.pwebII.model.entity.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {

    Optional<Especialidade> findByNomeIgnoreCase(String nome);

    List<Especialidade> findAllByOrderByNomeAsc();

    @Query("""
            select especialidade
            from Especialidade especialidade
            where exists (
                select medico
                from Medico medico
                where medico.especialidade = especialidade
            )
            order by especialidade.nome asc
            """)
    List<Especialidade> findComMedicosOrderByNomeAsc();
}
