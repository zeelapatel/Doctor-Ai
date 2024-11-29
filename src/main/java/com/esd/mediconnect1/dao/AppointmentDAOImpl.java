package com.esd.mediconnect1.dao;

import com.esd.mediconnect1.model.Appointment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class AppointmentDAOImpl implements AppointmentDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveAppointment(Appointment appointment) {
        if (appointment.getId() == null) {
            entityManager.persist(appointment);
        } else {
            entityManager.merge(appointment);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        try {
            TypedQuery<Appointment> query = entityManager.createQuery(
                "SELECT a FROM Appointment a " +
                "WHERE a.doctor.id = :doctorId " +
                "ORDER BY a.appointmentDate, a.appointmentTime",
                Appointment.class);
            query.setParameter("doctorId", doctorId);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        try {
            TypedQuery<Appointment> query = entityManager.createQuery(
                "SELECT a FROM Appointment a " +
                "WHERE a.patient.id = :patientId " +
                "ORDER BY a.appointmentDate, a.appointmentTime",
                Appointment.class);
            query.setParameter("patientId", patientId);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Long appointmentId) {
        return entityManager.find(Appointment.class, appointmentId);
    }

    @Override
    public void updateAppointment(Appointment appointment) {
        entityManager.merge(appointment);
    }

    public void deleteAppointment(Long appointmentId) {
        Appointment appointment = entityManager.find(Appointment.class, appointmentId);
        if (appointment != null) {
            entityManager.remove(appointment);
        }
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        try {
            TypedQuery<Appointment> query = entityManager.createQuery(
                "SELECT a FROM Appointment a " +
                "WHERE a.appointmentDate = :date " +
                "ORDER BY a.appointmentTime",
                Appointment.class);
            query.setParameter("date", date);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingAppointments(Long doctorId) {
        try {
            TypedQuery<Appointment> query = entityManager.createQuery(
                "SELECT a FROM Appointment a " +
                "WHERE a.doctor.id = :doctorId " +
                "AND a.appointmentDate >= CURRENT_DATE " +
                "ORDER BY a.appointmentDate, a.appointmentTime",
                Appointment.class);
            query.setParameter("doctorId", doctorId);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }

    @Override
    public void deleteByDoctorId(Long doctorId) {
        entityManager.createQuery("DELETE FROM Appointment WHERE doctor.id = :doctorId")
                     .setParameter("doctorId", doctorId)
                     .executeUpdate();
    }

    @Override
    public void deleteByPatientId(Long patientId) {
        entityManager.createQuery("DELETE FROM Appointment WHERE patient.id = :patientId")
                     .setParameter("patientId", patientId)
                     .executeUpdate();
    }
}
