package smarthospital.service;

import smarthospital.model.Patient;
import smarthospital.persistence.HospitalCSVManager;

import java.io.IOException;
import java.util.*;

/**
 * Service managing Patient business logic and persistence.
 */
public class PatientService {
    private final HospitalCSVManager csvManager;

    public PatientService(HospitalCSVManager csvManager) {
        this.csvManager = csvManager;
    }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(csvManager.getPatients().values());
    }

    public Patient getPatientById(String id) {
        if (id == null) return null;
        return csvManager.getPatients().get(id.trim());
    }

    public boolean patientExists(String id) {
        return id != null && csvManager.getPatients().containsKey(id.trim());
    }

    public void addPatient(Patient p) throws IllegalArgumentException, IOException {
        if (p.getId() == null || p.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty.");
        }
        String cleanId = p.getId().trim();
        if (csvManager.getPatients().containsKey(cleanId)) {
            throw new IllegalArgumentException("Patient with ID '" + cleanId + "' already exists.");
        }
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name cannot be empty.");
        }
        if (p.getAge() <= 0 || p.getAge() > 130) {
            throw new IllegalArgumentException("Invalid age: must be between 1 and 130.");
        }
        p.setId(cleanId);
        csvManager.savePatient(p);
    }

    public void updatePatient(Patient p) throws IllegalArgumentException, IOException {
        if (p.getId() == null || !csvManager.getPatients().containsKey(p.getId().trim())) {
            throw new IllegalArgumentException("Patient with ID '" + p.getId() + "' not found.");
        }
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name cannot be empty.");
        }
        if (p.getAge() <= 0 || p.getAge() > 130) {
            throw new IllegalArgumentException("Invalid age: must be between 1 and 130.");
        }
        p.setId(p.getId().trim());
        csvManager.savePatient(p);
    }

    public boolean deletePatient(String id) throws IOException {
        if (id == null) return false;
        return csvManager.deleteRecord("PATIENT", id.trim());
    }

    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) return getAllPatients();
        String q = query.trim().toLowerCase();
        List<Patient> results = new ArrayList<>();
        for (Patient p : csvManager.getPatients().values()) {
            if (p.getId().toLowerCase().contains(q) ||
                p.getName().toLowerCase().contains(q) ||
                p.getDisease().toLowerCase().contains(q) ||
                p.getBloodGroup().toLowerCase().contains(q) ||
                p.getMedicalHistory().toLowerCase().contains(q)) {
                results.add(p);
            }
        }
        return results;
    }
}
