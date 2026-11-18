package br.edu.ifto.pwebII.model.jdbc.repository;

import br.edu.ifto.pwebII.model.entity.Secretaria;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecretariaRepository extends JpaRepository<Secretaria, Long> {

    Optional<Secretaria> findByCpf(String cpf);

    Optional<Secretaria> findByEmailIgnoreCase(String email);
}
