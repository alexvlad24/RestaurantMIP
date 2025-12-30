package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ProdusRepository {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("RestaurantPU");

    // ==========================================
    // METODE PENTRU PRODUSE
    // ==========================================

    public void salveazaProdus(Produs p) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Folosim merge pentru a acoperi si cazul de update, nu doar insert
            if (p.getId() == null) {
                em.persist(p);
            } else {
                em.merge(p);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // Adaugă asta în RestaurantRepository.java dacă nu există deja
    public Masa getMasa(int numar) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Masa.class, numar);
        } finally {
            em.close();
        }
    }

    public void salveazaMasa(Masa m) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(m);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Comanda> getToateComenzile() {
        try {
            Thread.sleep(1000); // 1 secundă pauză
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        EntityManager em = emf.createEntityManager();
        try {
            // Selectează tot din tabela Comanda
            return em.createQuery("SELECT c FROM Comanda c", Comanda.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Produs> gasesteToateProdusele() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produs p", Produs.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void stergeProdus(Produs p) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Trebuie să atașăm obiectul la context înainte de ștergere
            Produs deSters = em.find(Produs.class, p.getId());
            if (deSters != null) {
                em.remove(deSters);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ==========================================
    // METODE PENTRU USER (LOGIN & OSPATARI)
    // ==========================================

    public void adaugaUser(User u) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(u);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public User gasesteUser(String username, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            // Căutăm un user care are exact acest nume și parolă
            return em.createQuery("SELECT u FROM User u WHERE u.username = :user AND u.password = :pass", User.class)
                    .setParameter("user", username)
                    .setParameter("pass", password)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    public List<User> getOspatari() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.rol = 'OSPATAR'", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void stergeUser(User u) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User deSters = em.find(User.class, u.getId());
            if (deSters != null) {
                // Aici va intra în acțiune CascadeType.REMOVE din entitatea User
                // și va șterge automat toate comenzile asociate acestui ospătar.
                em.remove(deSters);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ==========================================
    // METODE PENTRU COMENZI (ISTORIC)
    // ==========================================

    public void salveazaComanda(Comanda c) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // --- FIXUL CRITIC ---
            // Dacă comanda are o masă setată, trebuie să o "reconectăm" (merge)
            // la acest EntityManager înainte de a salva comanda.
            if (c.getMasa() != null) {
                Masa masaConectata = em.merge(c.getMasa());
                c.setMasa(masaConectata);
            }

            // Acum putem salva comanda. Folosim persist ca să se genereze ID-ul pe obiectul 'c'
            em.persist(c);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace(); // Foarte important: Afișăm eroarea în consolă dacă apare!
        } finally {
            em.close();
        }
    }

    public List<Comanda> getComenziUser(User u) {
        EntityManager em = emf.createEntityManager();
        try {
            // Returnează istoricul comenzilor pentru un anumit user
            return em.createQuery("SELECT c FROM Comanda c WHERE c.user.id = :uid", Comanda.class)
                    .setParameter("uid", u.getId())
                    .getResultList();
        } finally {
            em.close();
        }
    }
}