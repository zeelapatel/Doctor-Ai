package com.esd.mediconnect1.dao;

import com.esd.mediconnect1.model.Prescription;
import com.esd.mediconnect1.model.PrescriptionStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PrescriptionDAOImpl implements PrescriptionDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void savePrescription(Prescription prescription) {
        if (prescription.getId() == null) {
            prescription.setIssueDate(LocalDateTime.now());
            prescription.setStatus(PrescriptionStatus.PENDING);
            entityManager.persist(prescription);
        } else {
            entityManager.merge(prescription);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Prescription> getPrescriptionById(Long id) {
        return Optional.ofNullable(entityManager.find(Prescription.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getPrescriptionsByDoctorId(Long doctorId) {
        try {
            TypedQuery<Prescription> query = entityManager.createQuery(
                "SELECT p FROM Prescription p WHERE p.doctor.id = :doctorId " +
                "ORDER BY p.issueDate DESC",
                Prescription.class);
            query.setParameter("doctorId", doctorId);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getPrescriptionsByPatientId(Long patientId) {
        try {
            TypedQuery<Prescription> query = entityManager.createQuery(
                "SELECT p FROM Prescription p WHERE p.patient.id = :patientId " +
                "ORDER BY p.issueDate DESC",
                Prescription.class);
            query.setParameter("patientId", patientId);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }

    @Override
    public void deletePrescription(Long id) {
        Prescription prescription = entityManager.find(Prescription.class, id);
        if (prescription != null) {
            entityManager.remove(prescription);
        }
    }

    @Override
    public void updatePrescription(Prescription prescription) {
        entityManager.merge(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getAllPrescriptions() {
        return entityManager.createQuery(
            "SELECT p FROM Prescription p ORDER BY p.issueDate DESC", 
            Prescription.class)
            .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getPrescriptionsByStatus(PrescriptionStatus status) {
        try {
            TypedQuery<Prescription> query = entityManager.createQuery(
                "SELECT p FROM Prescription p WHERE p.status = :status " +
                "ORDER BY p.issueDate DESC",
                Prescription.class);
            query.setParameter("status", status);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getPrescriptionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            TypedQuery<Prescription> query = entityManager.createQuery(
                "SELECT p FROM Prescription p WHERE p.issueDate BETWEEN :startDate AND :endDate " +
                "ORDER BY p.issueDate DESC",
                Prescription.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getPendingPrescriptions() {
        return getPrescriptionsByStatus(PrescriptionStatus.PENDING);
    }

    @Override
    public void updatePrescriptionStatus(Long id, PrescriptionStatus status) {
        Prescription prescription = entityManager.find(Prescription.class, id);
        if (prescription != null) {
            prescription.setStatus(status);
            entityManager.merge(prescription);
        }
    }

    @Override
    public void deleteByDoctorId(Long doctorId) {
        entityManager.createQuery("DELETE FROM Prescription WHERE doctor.id = :doctorId")
                     .setParameter("doctorId", doctorId)
                     .executeUpdate();
    }
    @Override
    public void deleteByPatientId(Long patientId) {
        entityManager.createQuery("DELETE FROM Prescription WHERE patient.id = :patientId")
                     .setParameter("patientId", patientId)
                     .executeUpdate();
    }
}