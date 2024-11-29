package com.esd.mediconnect1.dao;

import com.esd.mediconnect1.model.PatientNote;
import java.util.List;

public interface PatientNoteDAO {
    void saveNote(PatientNote note);
    List<PatientNote> getNotesByPatientId(Long patientId);
	void deleteByPatientId(Long id);
}
