package smarthospital.service;

import smarthospital.model.MedicalRecord;
import smarthospital.persistence.HospitalCSVManager;

import java.io.IOException;
import java.util.*;

/**
 * Service managing Medical Record business logic and relational integrity.
 */
public class MedicalRecordService {
    private final HospitalCSVManager csvManager;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public MedicalRecordService(HospitalCSVManager csvManager, PatientService patientService, DoctorService doctorService) {
        this.csvManager = csvManager;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    public List<MedicalRecord> getAllRecords() {
        return new ArrayList<>(csvManager.getMedicalRecords().values());
    }

    public MedicalRecord getRecordById(String id) {
        if (id == null) return null;
        return csvManager.getMedicalRecords().get(id.trim());
    }

    public void addMedicalRecord(MedicalRecord mr) throws IllegalArgumentException, IOException {
        if (mr.getId() == null || mr.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Record ID cannot be empty.");
        }
        String cleanId = mr.getId().trim();
        if (csvManager.getMedicalRecords().containsKey(cleanId)) {
            throw new IllegalArgumentException("Medical Record with ID '" + cleanId + "' already exists.");
        }
        if (!patientService.patientExists(mr.getPatientId())) {
            throw new IllegalArgumentException("Validation Error: Patient ID '" + mr.getPatientId() + "' does not exist in the hospital database.");
        }
        if (!doctorService.doctorExists(mr.getDoctorId())) {
            throw new IllegalArgumentException("Validation Error: Doctor ID '" + mr.getDoctorId() + "' does not exist in the hospital database.");
        }
        if (mr.getDiagnosis() == null || mr.getDiagnosis().trim().isEmpty()) {
            throw new IllegalArgumentException("Diagnosis cannot be empty.");
        }
        mr.setId(cleanId);
        csvManager.saveMedicalRecord(mr);
    }

    public boolean deleteRecord(String id) throws IOException {
        if (id == null) return false;
        return csvManager.deleteRecord("MEDICAL_RECORD", id.trim());
    }

    public List<MedicalRecord> getRecordsByPatient(String patientId) {
        List<MedicalRecord> list = new ArrayList<>();
        if (patientId == null) return list;
        for (MedicalRecord mr : csvManager.getMedicalRecords().values()) {
            if (patientId.equalsIgnoreCase(mr.getPatientId())) {
                list.add(mr);
            }
        }
        return list;
    }

    public List<MedicalRecord> getRecordsByDoctor(String doctorId) {
        List<MedicalRecord> list = new ArrayList<>();
        if (doctorId == null) return list;
        for (MedicalRecord mr : csvManager.getMedicalRecords().values()) {
            if (doctorId.equalsIgnoreCase(mr.getDoctorId())) {
                list.add(mr);
            }
        }
        return list;
    }
}
