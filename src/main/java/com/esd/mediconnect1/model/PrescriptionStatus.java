package com.esd.mediconnect1.model;

public enum PrescriptionStatus {
	PENDING, FULFILLED, PARTIALLY_FULFILLED, UNAVAILABLE;

	public static PrescriptionStatus fromString(String status) {
		try {
			return valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid prescription status: " + status);
		}
	}
}
