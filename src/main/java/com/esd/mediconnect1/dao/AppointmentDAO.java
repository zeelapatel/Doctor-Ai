package com.esd.mediconnect1.dao;

import com.esd.mediconnect1.model.Appointment;
import java.util.List;

public interface AppointmentDAO {
    
	void saveAppointment(Appointment appointment);
    List<Appointment> getAppointmentsByDoctorId(Long doctorId);
    List<Appointment> getAppointmentsByPatientId(Long patientId);
    Appointment getAppointmentById(Long appointmentId);
    void updateAppointment(Appointment appointment);
	void deleteByDoctorId(Long id);
	void deleteByPatientId(Long patientId);

}
