package br.edu.ifto.pwebII.model.jdbc.repository;

import br.edu.ifto.pwebII.model.entity.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PacienteRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Paciente paciente){
        em.persist(paciente);
    }

    public Paciente paciente(Long id){
        return em.find(Paciente.class, id);
    }

    public List<Paciente> pacientes(){
        Query query = em.createQuery("from Paciente");
        return query.getResultList();
    }

    public boolean remove(Long id){
        Paciente p = em.find(Paciente.class, id);
        if (p != null) {
            em.remove(p);
            return true;
        }
        return false;
    }

    public void update(Paciente paciente){
        em.merge(paciente);
    }
}