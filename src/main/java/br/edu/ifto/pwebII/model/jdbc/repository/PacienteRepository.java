package br.edu.ifto.pwebII.model.jdbc.repository;

import br.edu.ifto.pwebII.model.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository <Paciente, Long> {
    //add outros metodos que necessitar

    List<Paciente> findByNome(String nome);

    Optional<Paciente> findByCpf(String cpf);

    Optional<Paciente> findByEmailIgnoreCase(String email);


}
