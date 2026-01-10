package com.inf2.dao.impl;

import com.inf2.dao.AdvisorDAO;
import com.inf2.domain.Advisor;
import com.inf2.dto.auth.UserUpdateRequest;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.UUID;

@Singleton
public class AdvisorDAOImpl implements AdvisorDAO {

    @Inject
    private Provider<EntityManager> emProvider;


    public Advisor find(UUID id) {
        return emProvider.get().find(Advisor.class, id);
    }
    public Advisor findByEmail(String email) {
        CriteriaBuilder cb = emProvider.get().getCriteriaBuilder();
        CriteriaQuery<Advisor> query = cb.createQuery(Advisor.class);
        Root<Advisor> advisor = query.from(Advisor.class);

        Predicate advisorEmailPredicate = cb.equal(advisor.get("email"), email);
        query.select(advisor).where(advisorEmailPredicate);

        Advisor result = emProvider.get().createQuery(query).getSingleResult();
        return result;
    }
    public Advisor save(
            Advisor user) {
        EntityManager em = emProvider.get();
        try {

            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            em.persist(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("DAO failed to save user via DTO: "+e.getMessage());
        }
    }

    public void  update(UUID id, UserUpdateRequest userUpdateRequest) {

        EntityManager em = emProvider.get(); //to access it not only in the try
        try {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            Advisor advisor = em.find(Advisor.class, id);
            advisor.setEmail(userUpdateRequest.getEmail());
            em.persist(advisor);
            transaction.commit();
        } catch (Exception e) {
            if (em != null && em.isOpen()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("DAO failed to update advisor");
        }
    }

    public void delete(UUID id) {
        EntityManager em = emProvider.get();
        try {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            em.remove(em.find(Advisor.class, id));
            transaction.commit();
        } catch (Exception e) {
            if (em != null && em.isOpen()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Service failed to delete advisor");
        }
    }
}
