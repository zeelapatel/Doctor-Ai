package com.esd.mediconnect1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor; // Refers to the doctor who created the prescription

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient; // Refers to the patient for whom the prescription is created

    @Column(nullable = false)
    private String medication; // Medication name(s)

    @Column(nullable = false)
    private String dosage; // Dosage details (e.g., "1 tablet twice daily")

    @Column(nullable = false)
    private LocalDateTime issueDate; // Date and time when the prescription was issued

    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status;
 // Status of the prescription: PENDING, FULFILLED, UNAVAILABLE
}
