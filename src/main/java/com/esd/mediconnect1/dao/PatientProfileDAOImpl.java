package com.esd.mediconnect1.dao;

import com.esd.mediconnect1.model.PatientProfile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Repository
@Transactional
public class PatientProfileDAOImpl implements PatientProfileDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void savePatientProfile(PatientProfile profile) {
        if (profile.getId() == null) {
            entityManager.persist(profile);
        } else {
            entityManager.merge(profile);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatientProfile> getPatientProfileByUserId(Long userId) {
        try {
            TypedQuery<PatientProfile> query = entityManager.createQuery(
                "SELECT p FROM PatientProfile p WHERE p.user.id = :userId",
                PatientProfile.class);
            query.setParameter("userId", userId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updatePatientProfile(PatientProfile profile) {
        entityManager.merge(profile);
    }

    @Transactional(readOnly = true)
    public PatientProfile getPatientProfileById(Long profileId) {
        return entityManager.find(PatientProfile.class, profileId);
    }
    
    public void deleteByUserId(Long userId) {
        entityManager.createQuery("DELETE FROM PatientProfile WHERE user.id = :userId")
                     .setParameter("userId", userId)
                     .executeUpdate();
    }

//    public void deletePatientProfile(Long profileId) {
//        PatientProfile profile = entityManager.find(PatientProfile.class, profileId);
//        if (profile != null) {
//            entityManager.remove(profile);
//        }
//    }
//
//    @Transactional(readOnly = true)
//    public List<PatientProfile> getAllPatientProfiles() {
//        try {
//            return entityManager.createQuery(
//                "SELECT p FROM PatientProfile p",
//                PatientProfile.class)
//                .getResultList();
//        } catch (NoResultException e) {
//            return List.of();
//        }
//    }
//
//    @Transactional(readOnly = true)
//    public boolean existsByUserId(Long userId) {
//        try {
//            TypedQuery<Long> query = entityManager.createQuery(
//                "SELECT COUNT(p) FROM PatientProfile p WHERE p.user.id = :userId",
//                Long.class);
//            query.setParameter("userId", userId);
//            return query.getSingleResult() > 0;
//        } catch (NoResultException e) {
//            return false;
//        }
//    }
}
