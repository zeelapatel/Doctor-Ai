package com.esd.mediconnect1.dao;

import java.util.List;
import java.util.Optional;

import com.esd.mediconnect1.model.User;

public interface UserDAO {
    
	//common
	void saveUser(User user);
    void deleteUser(Long id);
    User getUserById(Long id);
    User findByUsername(String username);
    boolean adminExists();
	List<User> getAllUsers();
	void updateUser(User user);
	
	
	//patient
	List<User> getAllPatient();
	Optional<User> findPatientById(Long patientId);
	
	//doctor
    List<User> getPatientsByDoctorId(Long doctorId);
    List<User> getAllDoctors();
}
