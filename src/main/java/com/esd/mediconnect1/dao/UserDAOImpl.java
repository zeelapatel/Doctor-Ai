package com.esd.mediconnect1.dao;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esd.mediconnect1.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserDAOImpl implements UserDAO {

	@PersistenceContext
	private EntityManager entityManager;
	

	@Override
	public void saveUser(User user) {
		if (user.getId() == null) {
			entityManager.persist(user);
		} else {
			entityManager.merge(user);
		}
	}

	@Override
	public void deleteUser(Long id) {
		User user = entityManager.find(User.class, id);
		if (user != null) {
			entityManager.remove(user);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserById(Long id) {
		return entityManager.find(User.class, id);
	}

	@Override
	@Transactional(readOnly = true)
	public User findByUsername(String username) {
		try {
			TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username",
					User.class);
			query.setParameter("username", username);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
		return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
	}

	public void updateUser(User user) {
		entityManager.merge(user);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean adminExists() {
	    try {
	        TypedQuery<Long> query = entityManager.createQuery(
	            "SELECT COUNT(u) FROM User u WHERE u.role = :role", Long.class);
	        query.setParameter("role", "ADMIN");
	        return query.getSingleResult() > 0;
	    } catch (NoResultException e) {
	        return false;
	    }
	}

	@Override
	public List<User> getAllPatient() {
		return entityManager.createQuery("SELECT u FROM User u WHERE u.role = 'patient'", User.class).getResultList();

	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findPatientById(Long patientId) {
	    try {
	        TypedQuery<User> query = entityManager.createQuery(
	            "SELECT u FROM User u WHERE u.id = :id AND u.role = :role", User.class);
	        query.setParameter("id", patientId);
	        query.setParameter("role", "patient");
	        return Optional.ofNullable(query.getSingleResult());
	    } catch (NoResultException e) {
	        return Optional.empty();
	    }
	}
	@Override
	@Transactional(readOnly = true)
	public List<User> getPatientsByDoctorId(Long doctorId) {
	    try {
	        TypedQuery<User> query = entityManager.createQuery(
	            "SELECT DISTINCT a.patient FROM Appointment a " +
	            "WHERE a.doctor.id = :doctorId AND a.patient.role = 'patient'",
	            User.class);
	        query.setParameter("doctorId", doctorId);
	        return query.getResultList();
	    } catch (NoResultException e) {
	        return List.of(); // Return empty list instead of null
	    }
	}

	@Override
	public List<User> getAllDoctors() {
		return entityManager.createQuery("SELECT u FROM User u WHERE u.role = 'DOCTOR'", User.class).getResultList();
	}
}