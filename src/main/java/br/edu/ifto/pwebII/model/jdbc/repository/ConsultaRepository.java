package br.edu.ifto.pwebII.model.jdbc.repository;

import br.edu.ifto.pwebII.model.entity.Consulta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConsultaRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Consulta consulta){
        em.persist(consulta);
    }

    public Consulta consulta(Long id){
        return em.find(Consulta.class, id);
    }

    public List<Consulta> consultas(){
        Query query = em.createQuery("from Consulta ");
        return query.getResultList();
    }

    public boolean remove(Long id){
        Consulta c = em.find(Consulta.class, id);
        em.remove(c);
        return true;
    }

    public void update(Consulta consulta){
        em.merge(consulta);
    }

    public List<Consulta> consultasPorPaciente(Long idPaciente) {
        Query query = em.createQuery("from Consulta c where c.paciente.id_paciente = :idPaciente");
        query.setParameter("idPaciente", idPaciente);
        return query.getResultList();
    }

    public List<Consulta> consultasPorMedico(Long idMedico) {
        Query query = em.createQuery("from Consulta c where c.medico.id_medico = :idMedico");
        query.setParameter("idMedico", idMedico);
        return query.getResultList();
    }
}