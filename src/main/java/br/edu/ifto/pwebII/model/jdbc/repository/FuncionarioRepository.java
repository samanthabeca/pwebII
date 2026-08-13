package br.edu.ifto.pwebII.model.jdbc.repository;

import br.edu.ifto.pwebII.model.entity.Funcionario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FuncionarioRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Funcionario funcionario){
        em.persist(funcionario);
    }

    public Funcionario funcionario(Long id){
        return em.find(Funcionario.class, id);
    }

    public List<Funcionario> funcionarios(){
        Query query = em.createQuery("from Funcionario ");
        return query.getResultList();
    }

    public boolean remove(Long id){
        Funcionario f = em.find(Funcionario.class, id);
        em.remove(f);
        return true;
    }

    public void update(Funcionario funcionario){
        em.merge(funcionario);
    }
}