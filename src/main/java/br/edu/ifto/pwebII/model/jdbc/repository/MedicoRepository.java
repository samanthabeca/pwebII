package br.edu.ifto.pwebII.model.jdbc.repository;

import br.edu.ifto.pwebII.model.entity.Medico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MedicoRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Medico medico){
        em.persist(medico);
    }

    public Medico medico(Long id){
        return em.find(Medico.class, id);
    }

    public List<Medico> medicos(){
        Query query = em.createQuery("from Medico");
        return query.getResultList();
    }

    public boolean remove(Long id){
        Medico m = em.find(Medico.class, id);
        if (m != null) {
            em.remove(m);
            return true;
        }
        return false;
    }

    public void update(Medico medico){
        em.merge(medico);
    }
}