package com.esd.mediconnect1.dao;


import com.esd.mediconnect1.model.DoctorProfile;
import com.esd.mediconnect1.model.PatientProfile;
import java.util.Optional;

public interface PatientProfileDAO {

    void savePatientProfile(PatientProfile profile);

    Optional<PatientProfile> getPatientProfileByUserId(Long userId);

    void updatePatientProfile(PatientProfile profile);

	void deleteByUserId(Long id);

}
