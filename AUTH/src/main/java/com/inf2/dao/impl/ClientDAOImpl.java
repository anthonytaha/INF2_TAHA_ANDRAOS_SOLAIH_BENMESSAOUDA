package com.inf2.dao.impl;

import com.inf2.dao.ClientDAO;
import com.inf2.domain.Client;
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

import java.util.List;
import java.util.UUID;

@Singleton
public class ClientDAOImpl implements ClientDAO { //step 2 : does the database operations (insert, find etc) --> couche qui intéragit directement avec la bdd
    @Inject
    private Provider<EntityManager> emProvider;


    public Client find(UUID id) {
        return emProvider.get().find(Client.class, id);
    }
    public List<Client> findAll() {
        return emProvider.get()
                .createQuery("SELECT c FROM Client c", Client.class)
                .getResultList();
    }
    public Client findByEmail(String email) {
        CriteriaBuilder cb = emProvider.get().getCriteriaBuilder();
        CriteriaQuery<Client> query = cb.createQuery(Client.class);
        Root<Client> Client = query.from(Client.class);

        Predicate ClientEmailPredicate = cb.equal(Client.get("email"), email);
        query.select(Client).where(ClientEmailPredicate);

        Client result = emProvider.get().createQuery(query).getSingleResult();
        return result;
    }
    public Client save(
            Client user) {
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
            throw new RuntimeException("DAO failed to save user via DTO");
        }
    }

    public void  update(UUID id, UserUpdateRequest userUpdateRequest) {

        EntityManager em = emProvider.get(); //to access it not only in the try
        try {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            Client Client = em.find(Client.class, id);
            Client.setEmail(userUpdateRequest.getEmail());
            em.persist(Client);
            transaction.commit();
        } catch (Exception e) {
            if (em != null && em.isOpen()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("DAO failed to update Client");
        }
    }

    public void delete(UUID id) {
        EntityManager em = emProvider.get();
        try {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            em.remove(em.find(Client.class, id));
            transaction.commit();
        } catch (Exception e) {
            if (em != null && em.isOpen()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Service failed to delete Client");
        }
    }
}
