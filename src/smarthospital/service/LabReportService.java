package smarthospital.service;

import smarthospital.model.LabReport;
import smarthospital.persistence.HospitalCSVManager;

import java.io.IOException;
import java.util.*;

/**
 * Service managing Laboratory Diagnostics and persistence.
 */
public class LabReportService {
    private final HospitalCSVManager csvManager;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public LabReportService(HospitalCSVManager csvManager, PatientService patientService, DoctorService doctorService) {
        this.csvManager = csvManager;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    public List<LabReport> getAllLabReports() {
        return new ArrayList<>(csvManager.getLabReports().values());
    }

    public LabReport getLabReportById(String id) {
        if (id == null) return null;
        return csvManager.getLabReports().get(id.trim());
    }

    public void addLabReport(LabReport lr) throws IllegalArgumentException, IOException {
        if (lr.getId() == null || lr.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Lab Report ID cannot be empty.");
        }
        String cleanId = lr.getId().trim();
        if (csvManager.getLabReports().containsKey(cleanId)) {
            throw new IllegalArgumentException("Lab Report with ID '" + cleanId + "' already exists.");
        }
        if (!patientService.patientExists(lr.getPatientId())) {
            throw new IllegalArgumentException("Validation Error: Patient ID '" + lr.getPatientId() + "' does not exist in the hospital database.");
        }
        if (!doctorService.doctorExists(lr.getDoctorId())) {
            throw new IllegalArgumentException("Validation Error: Doctor ID '" + lr.getDoctorId() + "' does not exist in the hospital database.");
        }
        if (lr.getTestName() == null || lr.getTestName().trim().isEmpty()) {
            throw new IllegalArgumentException("Test name cannot be empty.");
        }
        lr.setId(cleanId);
        csvManager.saveLabReport(lr);
    }

    public boolean deleteReport(String id) throws IOException {
        if (id == null) return false;
        return csvManager.deleteRecord("LAB", id.trim());
    }

    public List<LabReport> getReportsByPatient(String patientId) {
        List<LabReport> list = new ArrayList<>();
        if (patientId == null) return list;
        for (LabReport lr : csvManager.getLabReports().values()) {
            if (patientId.equalsIgnoreCase(lr.getPatientId())) {
                list.add(lr);
            }
        }
        return list;
    }
}
