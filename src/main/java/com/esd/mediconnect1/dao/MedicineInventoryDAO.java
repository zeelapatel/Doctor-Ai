package com.esd.mediconnect1.dao;

import com.esd.mediconnect1.model.MedicineInventory;
import java.util.List;
import java.util.Optional;

public interface MedicineInventoryDAO {
    void saveMedicine(MedicineInventory medicine);
    void updateMedicine(MedicineInventory medicine);
    Optional<MedicineInventory> findByName(String name);
    List<MedicineInventory> findAll();
    Optional<MedicineInventory> findById(Long id);
    void deleteMedicine(Long id);
    List<MedicineInventory> findLowStock(int threshold);
    void updateStock(Long id, int quantity);
    List<MedicineInventory> searchMedicines(String searchTerm);
	void updateDoseByMedicineName(String medication, String dosage);
}