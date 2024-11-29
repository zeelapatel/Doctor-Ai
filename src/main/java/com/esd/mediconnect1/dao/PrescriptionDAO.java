package com.esd.mediconnect1.dao;

import com.esd.mediconnect1.model.Prescription;
import com.esd.mediconnect1.model.PrescriptionStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PrescriptionDAO {
    void savePrescription(Prescription prescription);
    Optional<Prescription> getPrescriptionById(Long id);
    List<Prescription> getPrescriptionsByDoctorId(Long doctorId);
    List<Prescription> getPrescriptionsByPatientId(Long patientId);
    void deletePrescription(Long id);
    void updatePrescription(Prescription prescription);
    List<Prescription> getAllPrescriptions();
    List<Prescription> getPrescriptionsByStatus(PrescriptionStatus status);
    List<Prescription> getPrescriptionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Prescription> getPendingPrescriptions();
    void updatePrescriptionStatus(Long id, PrescriptionStatus status);
	void deleteByDoctorId(Long id);
	void deleteByPatientId(Long patientId);
}