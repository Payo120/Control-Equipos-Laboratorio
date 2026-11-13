package me.edu.uam.controlequiposlaboratorio.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import me.edu.uam.controlequiposlaboratorio.model.Equipo;
import me.edu.uam.controlequiposlaboratorio.model.TipoEquipo;

import java.util.List;

public class EquipoDAO {

    public List<TipoEquipo> listarTipos() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("SELECT t FROM TipoEquipo t ORDER BY t.nombre", TipoEquipo.class)
                    .getResultList();
        }
    }

    public List<Equipo> listarEquipos() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery("SELECT e FROM Equipo e JOIN FETCH e.tipoEquipo ORDER BY e.idEquipo", Equipo.class)
                    .getResultList();
        }
    }

    public boolean existeCodigo(String codigo, Integer idExcluir) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            String jpql = "SELECT COUNT(e) FROM Equipo e WHERE LOWER(e.codigo) = LOWER(:codigo)";
            if (idExcluir != null) {
                jpql += " AND e.idEquipo <> :idExcluir";
            }
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("codigo", codigo.trim());
            if (idExcluir != null) {
                query.setParameter("idExcluir", idExcluir);
            }
            return query.getSingleResult() > 0;
        }
    }

    public void guardar(Equipo equipo) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TipoEquipo tipoManaged = em.find(TipoEquipo.class, equipo.getTipoEquipo().getIdTipo());
            equipo.setTipoEquipo(tipoManaged);
            em.persist(equipo);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void actualizar(Equipo equipo) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TipoEquipo tipoManaged = em.find(TipoEquipo.class, equipo.getTipoEquipo().getIdTipo());
            equipo.setTipoEquipo(tipoManaged);
            em.merge(equipo);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminar(Integer idEquipo) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Equipo equipo = em.find(Equipo.class, idEquipo);
            if (equipo != null) {
                em.remove(equipo);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Equipo> buscarYFiltrar(String fragmentoNombre, TipoEquipo tipoFiltro) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            StringBuilder jpql = new StringBuilder("SELECT e FROM Equipo e JOIN FETCH e.tipoEquipo WHERE 1=1");

            boolean tieneNombre = fragmentoNombre != null && !fragmentoNombre.trim().isEmpty();
            boolean tieneTipo = tipoFiltro != null;

            if (tieneNombre) {
                jpql.append(" AND LOWER(e.nombre) LIKE LOWER(:nombre)");
            }
            if (tieneTipo) {
                jpql.append(" AND e.tipoEquipo = :tipo");
            }
            jpql.append(" ORDER BY e.idEquipo");

            TypedQuery<Equipo> query = em.createQuery(jpql.toString(), Equipo.class);
            if (tieneNombre) {
                query.setParameter("nombre", "%" + fragmentoNombre.trim() + "%");
            }
            if (tieneTipo) {
                query.setParameter("tipo", tipoFiltro);
            }
            return query.getResultList();
        }
    }
}