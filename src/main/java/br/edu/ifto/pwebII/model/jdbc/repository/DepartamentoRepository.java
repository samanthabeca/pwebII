package br.edu.ifto.pwebII.model.jdbc.repository;

import br.edu.ifto.pwebII.model.entity.Departamento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepartamentoRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Departamento departamento){
        em.persist(departamento);
    }

    public Departamento departamento(Long id){
        return em.find(Departamento.class, id);
    }

    public List<Departamento> departamentos(){
        Query query = em.createQuery("from Departamento");
        return query.getResultList();
    }

    public boolean remove(Long id){
        Departamento d = em.find(Departamento.class, id);
        if (d != null) {
            em.remove(d);
            return true;
        }
        return false;
    }

    public void update(Departamento departamento){
        em.merge(departamento);
    }

    public int contarFuncionarios(Long idDepartamento){
        TypedQuery<Long> query = em.createQuery(
                "select count(f) from Funcionario f where f.departamento.id = :idDept", Long.class);
        query.setParameter("idDept", idDepartamento);

        Long count = query.getSingleResult();
        return count != null ? count.intValue() : 0;
    }
}