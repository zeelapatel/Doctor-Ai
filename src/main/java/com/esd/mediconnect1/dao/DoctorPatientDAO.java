package com.esd.mediconnect1.dao;

import com.esd.mediconnect1.model.DoctorPatient;
import java.util.List;

public interface DoctorPatientDAO {

    void saveAssignment(DoctorPatient doctorPatient);

    void deleteAssignment(Long id);

    List<DoctorPatient> getAssignmentsByDoctorId(Long doctorId);

    List<DoctorPatient> getAssignmentsByPatientId(Long patientId);

	void deleteByDoctorId(Long id);

	void deleteByPatientId(Long patientId);
}
