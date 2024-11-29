package com.esd.mediconnect1.controller;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.esd.mediconnect1.dao.MedicineInventoryDAO;
import com.esd.mediconnect1.dao.NotificationDAO;
import com.esd.mediconnect1.dao.PrescriptionDAO;
import com.esd.mediconnect1.model.MedicineInventory;
import com.esd.mediconnect1.model.Notification;
import com.esd.mediconnect1.model.Prescription;
import com.esd.mediconnect1.model.PrescriptionStatus;

@Controller
@RequestMapping("/pharmacist")
public class PharmacistController {

	@Autowired
	private PrescriptionDAO prescriptionDAO;

	@Autowired
	private MedicineInventoryDAO medicineInventoryDAO;

	@Autowired
	private NotificationDAO notificationDAO;

	@GetMapping("/dashboard")
	public String pharmacistDashboard(Model model) {
		// Add any necessary data to the model for the pharmacist dashboard
		model.addAttribute("message", "Welcome to the Pharmacist Dashboard!");
		return "pharmacist-dashboard";
	}

	@GetMapping("/prescriptions")
	public String listPrescriptions(Model model) {
		List<Prescription> prescriptions = prescriptionDAO.getPrescriptionsByStatus(PrescriptionStatus.PENDING);
		model.addAttribute("prescriptions", prescriptions);
		return "pharmacist-prescriptions";
	}

	@PostMapping("/prescription/update/{id}")
	public String updatePrescriptionStatus(@PathVariable Long id, @RequestParam String status) {
		Prescription prescription = prescriptionDAO.getPrescriptionById(id)
				.orElseThrow(() -> new RuntimeException("Prescription not found"));

		prescription.setStatus(PrescriptionStatus.valueOf(status));
		prescriptionDAO.savePrescription(prescription);
		if (status.equals("FULFILLED")) {
			medicineInventoryDAO.updateDoseByMedicineName(prescription.getMedication(), prescription.getDosage());
		}
		// Notify the patient about the status update
		Notification notification = new Notification();
		notification.setUser(prescription.getPatient());
		notification.setMessage("Your prescription status has been updated to: " + status);
		notificationDAO.saveNotification(notification);

		return "redirect:/pharmacist/prescriptions";
	}

	@GetMapping("/inventory")
	public String viewInventory(Model model) {
		List<MedicineInventory> inventory = medicineInventoryDAO.findAll();
		model.addAttribute("inventory", inventory);
		return "pharmacist-inventory";
	}

	@PostMapping("/inventory/upload")
	public String uploadStock(@RequestParam("file") MultipartFile file) {
		// Parse Excel file and update medicine inventory
		try {
			Workbook workbook = new XSSFWorkbook(file.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue; // Skip header row

				String medicineName = row.getCell(0).getStringCellValue();
				int stock = (int) row.getCell(1).getNumericCellValue();

				MedicineInventory medicine = medicineInventoryDAO.findByName(medicineName)
						.orElse(new MedicineInventory(null, medicineName, 0, null, null));
				medicine.setStock(medicine.getStock() + stock);
				medicineInventoryDAO.saveMedicine(medicine);
			}
			workbook.close();
		} catch (Exception e) {
			throw new RuntimeException("Error reading Excel file: " + e.getMessage());
		}

		return "redirect:/pharmacist/inventory";
	}

}
