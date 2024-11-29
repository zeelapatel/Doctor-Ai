package com.esd.mediconnect1.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.esd.mediconnect1.model.PatientNote;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Repository
@Transactional
public class PatientNoteDAOImpl implements PatientNoteDAO {

	 @PersistenceContext
	    private EntityManager entityManager;
	
	@Override
	public void saveNote(PatientNote note) {
		if (note.getId()==null) {
			entityManager.persist(note);
		}else {
			entityManager.merge(note);
		}
		
	}

	@Override
    @Transactional(readOnly = true)
    public List<PatientNote> getNotesByPatientId(Long patientId) {
        try {
            TypedQuery<PatientNote> query = entityManager.createQuery(
                "SELECT n FROM PatientNote n " +
                "WHERE n.patient.id = :patientId " +
                "ORDER BY n.createdAt DESC",
                PatientNote.class);
            query.setParameter("patientId", patientId);
            return query.getResultList();
        } catch (NoResultException e) {
            return List.of();
        }
    }
	
	@Override
	public void deleteByPatientId(Long id) {
		// TODO Auto-generated method stub
		entityManager.createQuery("DELETE FROM PatientNote WHERE patient.id = :id")
		.setParameter("id", id)
		.executeUpdate();
	}
}
