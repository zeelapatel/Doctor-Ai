package com.esd.mediconnect1.dao;

import java.util.List;
import java.util.Optional;

import com.esd.mediconnect1.model.DoctorProfile;
import com.esd.mediconnect1.model.User;

public interface DoctorProfileDAO {

	Optional<DoctorProfile> getDoctorProfileByUserId(Long id);

	void updateDoctorProfile(DoctorProfile profile);

	void saveDoctorProfile(DoctorProfile updatedProfile);
	void deleteByUserId(Long id);

	List<DoctorProfile> getAllDoctors();

}
