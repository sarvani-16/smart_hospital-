package smarthospital.service;

import smarthospital.model.Doctor;
import smarthospital.persistence.HospitalCSVManager;

import java.io.IOException;
import java.util.*;

/**
 * Service managing Doctor business logic and persistence.
 */
public class DoctorService {
    private final HospitalCSVManager csvManager;

    public DoctorService(HospitalCSVManager csvManager) {
        this.csvManager = csvManager;
    }

    public List<Doctor> getAllDoctors() {
        return new ArrayList<>(csvManager.getDoctors().values());
    }

    public Doctor getDoctorById(String id) {
        if (id == null) return null;
        return csvManager.getDoctors().get(id.trim());
    }

    public boolean doctorExists(String id) {
        return id != null && csvManager.getDoctors().containsKey(id.trim());
    }

    public void addDoctor(Doctor d) throws IllegalArgumentException, IOException {
        if (d.getId() == null || d.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor ID cannot be empty.");
        }
        String cleanId = d.getId().trim();
        if (csvManager.getDoctors().containsKey(cleanId)) {
            throw new IllegalArgumentException("Doctor with ID '" + cleanId + "' already exists.");
        }
        if (d.getName() == null || d.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor name cannot be empty.");
        }
        if (d.getDepartment() == null || d.getDepartment().trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty.");
        }
        d.setId(cleanId);
        csvManager.saveDoctor(d);
    }

    public void updateDoctor(Doctor d) throws IllegalArgumentException, IOException {
        if (d.getId() == null || !csvManager.getDoctors().containsKey(d.getId().trim())) {
            throw new IllegalArgumentException("Doctor with ID '" + d.getId() + "' not found.");
        }
        if (d.getName() == null || d.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor name cannot be empty.");
        }
        d.setId(d.getId().trim());
        csvManager.saveDoctor(d);
    }

    public boolean deleteDoctor(String id) throws IOException {
        if (id == null) return false;
        return csvManager.deleteRecord("DOCTOR", id.trim());
    }

    public List<Doctor> searchDoctors(String query) {
        if (query == null || query.trim().isEmpty()) return getAllDoctors();
        String q = query.trim().toLowerCase();
        List<Doctor> results = new ArrayList<>();
        for (Doctor d : csvManager.getDoctors().values()) {
            if (d.getId().toLowerCase().contains(q) ||
                d.getName().toLowerCase().contains(q) ||
                d.getSpecialization().toLowerCase().contains(q) ||
                d.getDepartment().toLowerCase().contains(q)) {
                results.add(d);
            }
        }
        return results;
    }
}
