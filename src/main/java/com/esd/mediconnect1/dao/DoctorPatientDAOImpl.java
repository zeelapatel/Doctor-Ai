package com.esd.mediconnect1.dao;

import com.esd.mediconnect1.model.DoctorPatient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class DoctorPatientDAOImpl implements DoctorPatientDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveAssignment(DoctorPatient doctorPatient) {
        if (doctorPatient.getId() == null) {
            entityManager.persist(doctorPatient);
        } else {
            entityManager.merge(doctorPatient);
        }
    }

    @Override
    public void deleteAssignment(Long id) {
        DoctorPatient doctorPatient = entityManager.find(DoctorPatient.class, id);
        if (doctorPatient != null) {
            entityManager.remove(doctorPatient);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorPatient> getAssignmentsByDoctorId(Long doctorId) {
        try {
            TypedQuery<DoctorPatient> query = entityManager.createQuery(
                "SELECT dp FROM DoctorPatient dp WHERE dp.doctor.id = :doctorId",
                DoctorPatient.class);
            query.setParameter("doctorId", doctorId);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorPatient> getAssignmentsByPatientId(Long patientId) {
        try {
            TypedQuery<DoctorPatient> query = entityManager.createQuery(
                "SELECT dp FROM DoctorPatient dp WHERE dp.patient.id = :patientId",
                DoctorPatient.class);
            query.setParameter("patientId", patientId);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public DoctorPatient getAssignment(Long doctorId, Long patientId) {
        try {
            TypedQuery<DoctorPatient> query = entityManager.createQuery(
                "SELECT dp FROM DoctorPatient dp " +
                "WHERE dp.doctor.id = :doctorId AND dp.patient.id = :patientId",
                DoctorPatient.class);
            query.setParameter("doctorId", doctorId);
            query.setParameter("patientId", patientId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<DoctorPatient> getAllAssignments() {
        try {
            return entityManager.createQuery(
                "SELECT dp FROM DoctorPatient dp",
                DoctorPatient.class)
                .getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }
    @Override
    public void deleteByDoctorId(Long doctorId) {
        entityManager.createQuery("DELETE FROM DoctorPatient WHERE doctor.id = :doctorId")
                     .setParameter("doctorId", doctorId)
                     .executeUpdate();
    }
    
    @Override
    public void deleteByPatientId(Long patientId) {
        entityManager.createQuery("DELETE FROM DoctorPatient WHERE patient.id = :patientId")
                     .setParameter("patientId", patientId)
                     .executeUpdate();
    }
}