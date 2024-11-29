package com.esd.mediconnect1.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esd.mediconnect1.model.DoctorProfile;
import com.esd.mediconnect1.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
@Transactional
public class DoctorProfileDAOImpl implements DoctorProfileDAO {
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	public Optional<DoctorProfile> getDoctorProfileByUserId(Long id) {
		try {
            TypedQuery<DoctorProfile> query = entityManager.createQuery(
                "SELECT d FROM DoctorProfile d WHERE d.user.id = :userId",
                DoctorProfile.class);
            query.setParameter("userId", id);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
	}

	@Override
	public void updateDoctorProfile(DoctorProfile profile) {
		// TODO Auto-generated method stub
        entityManager.merge(profile);

	}

	@Override
	public void saveDoctorProfile(DoctorProfile updatedProfile) {
		// TODO Auto-generated method stub
		if (updatedProfile.getId() == null) {
            entityManager.persist(updatedProfile);
        } else {
            entityManager.merge(updatedProfile);
        }
	}
	
	public void deleteByUserId(Long userId) {
        entityManager.createQuery("DELETE FROM DoctorProfile WHERE user.id = :userId")
                     .setParameter("userId", userId)
                     .executeUpdate();
    }

	@Override
	public List<DoctorProfile> getAllDoctors() {
		// TODO Auto-generated method stub
		return entityManager.createQuery("SELECT d FROM DoctorProfile d WHERE d.firstName IS NOT NULL AND d.lastName IS NOT NULL", DoctorProfile.class).getResultList();
	}

}
