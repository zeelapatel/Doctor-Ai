package com.esd.mediconnect1.dao;

import com.esd.mediconnect1.model.MedicineInventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class MedicineInventoryDAOImpl implements MedicineInventoryDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveMedicine(MedicineInventory medicine) {
        medicine.setCreatedAt(LocalDateTime.now());
        medicine.setUpdatedAt(LocalDateTime.now());
        if (medicine.getId() == null) {
            entityManager.persist(medicine);
        } else {
            entityManager.merge(medicine);
        }
    }

    @Override
    public void updateMedicine(MedicineInventory medicine) {
        medicine.setUpdatedAt(LocalDateTime.now());
        entityManager.merge(medicine);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicineInventory> findByName(String name) {
        try {
            TypedQuery<MedicineInventory> query = entityManager.createQuery(
                "SELECT m FROM MedicineInventory m WHERE LOWER(m.name) = LOWER(:name)",
                MedicineInventory.class);
            query.setParameter("name", name);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicineInventory> findAll() {
        return entityManager.createQuery(
            "SELECT m FROM MedicineInventory m ORDER BY m.name",
            MedicineInventory.class)
            .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicineInventory> findById(Long id) {
        return Optional.ofNullable(entityManager.find(MedicineInventory.class, id));
    }

    @Override
    public void deleteMedicine(Long id) {
        MedicineInventory medicine = entityManager.find(MedicineInventory.class, id);
        if (medicine != null) {
            entityManager.remove(medicine);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicineInventory> findLowStock(int threshold) {
        return entityManager.createQuery(
            "SELECT m FROM MedicineInventory m WHERE m.stock <= :threshold ORDER BY m.stock",
            MedicineInventory.class)
            .setParameter("threshold", threshold)
            .getResultList();
    }

    @Override
    public void updateStock(Long id, int quantity) {
        MedicineInventory medicine = entityManager.find(MedicineInventory.class, id);
        if (medicine != null) {
            medicine.setStock(medicine.getStock() + quantity);
            medicine.setUpdatedAt(LocalDateTime.now());
            entityManager.merge(medicine);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicineInventory> searchMedicines(String searchTerm) {
        return entityManager.createQuery(
            "SELECT m FROM MedicineInventory m WHERE LOWER(m.name) LIKE LOWER(:searchTerm)",
            MedicineInventory.class)
            .setParameter("searchTerm", "%" + searchTerm + "%")
            .getResultList();
    }

    @Override
    public void updateDoseByMedicineName(String medicineName, String dosage) {
       
        	int dosageAmount= Integer.parseInt(dosage);
            TypedQuery<MedicineInventory> query = entityManager.createQuery(
                "SELECT m FROM MedicineInventory m WHERE LOWER(m.name) = LOWER(:name)",
                MedicineInventory.class);
            query.setParameter("name", medicineName);
            
            MedicineInventory medicine = query.getSingleResult();
            
            if (medicine.getStock() >= dosageAmount) {
                medicine.setStock(medicine.getStock() - dosageAmount);
                medicine.setUpdatedAt(LocalDateTime.now());
                entityManager.merge(medicine);
            } else {
                System.out.println("out of stock");
            }
       
    }
}
