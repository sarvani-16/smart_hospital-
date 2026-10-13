package smarthospital.persistence;

import smarthospital.model.*;
import smarthospital.util.SampleData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * HospitalCSVManager handles persistent CSV storage for SmartHospital.
 * Uses a single master file: data/hospital_data.csv
 * 
 * Safe persistence guarantees:
 * - Directory and file created only if absent on first run.
 * - Existing CSV is NEVER overwritten with sample data on subsequent runs.
 * - Atomic write via temporary file to prevent corruption.
 * - Robust CSV parsing with RFC-4180 quote-escaping support.
 */
public class HospitalCSVManager {

    private static final String DEFAULT_CSV_PATH = "data/hospital_data.csv";
    private final Path csvPath;

    public static final String CSV_HEADER = "recordType,id,patientId,doctorId,name,age,gender,contact,bloodGroup,disease,medicalHistory,department,specialization,diagnosis,treatment,testName,testResult,resourceName,quantity,status,documentPath,timestamp";

    // In-memory data store
    private final Map<String, Patient> patients = new LinkedHashMap<>();
    private final Map<String, Doctor> doctors = new LinkedHashMap<>();
    private final Map<String, Department> departments = new LinkedHashMap<>();
    private final Map<String, MedicalRecord> medicalRecords = new LinkedHashMap<>();
    private final Map<String, LabReport> labReports = new LinkedHashMap<>();
    private final Map<String, Treatment> treatments = new LinkedHashMap<>();
    private final Map<String, MedicalResource> resources = new LinkedHashMap<>();
    private final List<HospitalObservation> observations = new ArrayList<>();

    public HospitalCSVManager() {
        this(DEFAULT_CSV_PATH);
    }

    public HospitalCSVManager(String relativePath) {
        Path p = Paths.get(relativePath);
        // Ensure relative to execution directory or parent
        if (!Files.exists(p) && Files.exists(Paths.get("SmartHospital", relativePath))) {
            p = Paths.get("SmartHospital", relativePath);
        }
        this.csvPath = p;
    }

    public Path getCsvPath() {
        return csvPath;
    }

    /**
     * Initializes file:
     * - If CSV does not exist: creates dir, creates CSV, writes header, seeds initial sample data, saves.
     * - If CSV exists: loads existing records without re-seeding or overwriting.
     */
    public synchronized void initializeFile() throws IOException {
        Path parentDir = csvPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        if (!Files.exists(csvPath) || Files.size(csvPath) == 0) {
            System.out.println("[Persistence] No existing CSV found. Creating: " + csvPath.toAbsolutePath());
            seedInitialSampleData();
            saveAll();
            System.out.println("[Persistence] Initial fictional hospital dataset successfully seeded (" + getTotalRecordCount() + " records).");
        } else {
            System.out.println("[Persistence] Existing CSV detected at: " + csvPath.toAbsolutePath());
            loadAll();
            System.out.println("[Persistence] Successfully loaded " + getTotalRecordCount() + " records from storage.");
        }
    }

    private void seedInitialSampleData() {
        for (Department d : SampleData.getInitialDepartments()) departments.put(d.getId(), d);
        for (Doctor d : SampleData.getInitialDoctors()) doctors.put(d.getId(), d);
        for (Patient p : SampleData.getInitialPatients()) patients.put(p.getId(), p);
        for (MedicalRecord mr : SampleData.getInitialMedicalRecords()) medicalRecords.put(mr.getId(), mr);
        for (LabReport lr : SampleData.getInitialLabReports()) labReports.put(lr.getId(), lr);
        for (Treatment t : SampleData.getInitialTreatments()) treatments.put(t.getId(), t);
        for (MedicalResource r : SampleData.getInitialResources()) resources.put(r.getId(), r);
        observations.addAll(SampleData.getInitialObservations());
    }

    public int getTotalRecordCount() {
        return patients.size() + doctors.size() + departments.size() + medicalRecords.size()
                + labReports.size() + treatments.size() + resources.size() + observations.size();
    }

    /**
     * Loads all records from CSV into in-memory structures.
     */
    public synchronized void loadAll() throws IOException {
        if (!Files.exists(csvPath)) return;

        patients.clear();
        doctors.clear();
        departments.clear();
        medicalRecords.clear();
        labReports.clear();
        treatments.clear();
        resources.clear();
        observations.clear();

        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // Header line
            if (line == null) return;

            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                List<String> tokens = parseCsvLine(line);
                if (tokens.size() < 22) {
                    System.err.println("[Warning] Line " + lineNum + " malformed, expected 22 columns, found " + tokens.size() + ". Skipping.");
                    continue;
                }

                String recordType = tokens.get(0).trim().toUpperCase();
                String id = tokens.get(1).trim();
                String patientId = tokens.get(2).trim();
                String doctorId = tokens.get(3).trim();
                String name = tokens.get(4).trim();
                String ageStr = tokens.get(5).trim();
                String gender = tokens.get(6).trim();
                String contact = tokens.get(7).trim();
                String bloodGroup = tokens.get(8).trim();
                String disease = tokens.get(9).trim();
                String medicalHistory = tokens.get(10).trim();
                String department = tokens.get(11).trim();
                String specialization = tokens.get(12).trim();
                String diagnosis = tokens.get(13).trim();
                String treatment = tokens.get(14).trim();
                String testName = tokens.get(15).trim();
                String testResult = tokens.get(16).trim();
                String resourceName = tokens.get(17).trim();
                String quantityStr = tokens.get(18).trim();
                String status = tokens.get(19).trim();
                String documentPath = tokens.get(20).trim();
                String timestamp = tokens.get(21).trim();

                try {
                    switch (recordType) {
                        case "PATIENT":
                            int age = ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr);
                            patients.put(id, new Patient(id, name, age, gender, contact, bloodGroup, disease, medicalHistory));
                            break;
                        case "DOCTOR":
                            doctors.put(id, new Doctor(id, name, specialization, department, contact));
                            break;
                        case "DEPARTMENT":
                            departments.put(id, new Department(id, name, medicalHistory, doctorId));
                            break;
                        case "MEDICAL_RECORD":
                            medicalRecords.put(id, new MedicalRecord(id, patientId, doctorId, diagnosis, treatment, medicalHistory, documentPath, timestamp));
                            break;
                        case "LAB":
                            labReports.put(id, new LabReport(id, patientId, doctorId, testName, testResult, status, documentPath, timestamp));
                            break;
                        case "TREATMENT":
                            treatments.put(id, new Treatment(id, patientId, doctorId, diagnosis, treatment, status, timestamp));
                            break;
                        case "RESOURCE":
                            int qty = quantityStr.isEmpty() ? 0 : Integer.parseInt(quantityStr);
                            resources.put(id, new MedicalResource(id, resourceName, department, qty, status));
                            break;
                        case "OBSERVATION":
                            observations.add(new HospitalObservation(id, patientId, testName, testResult, status, timestamp));
                            break;
                        default:
                            System.err.println("[Warning] Unknown recordType '" + recordType + "' on line " + lineNum);
                    }
                } catch (Exception ex) {
                    System.err.println("[Warning] Failed to parse line " + lineNum + ": " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Atomically saves all in-memory collections to the master CSV file.
     */
    public synchronized void saveAll() throws IOException {
        Path tempPath = Paths.get(csvPath.toString() + ".tmp");
        Path parent = csvPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8)) {
            writer.write(CSV_HEADER);
            writer.newLine();

            // Patients
            for (Patient p : patients.values()) {
                writer.write(formatCsvRow("PATIENT", p.getId(), "", "", p.getName(),
                        String.valueOf(p.getAge()), p.getGender(), p.getContact(), p.getBloodGroup(),
                        p.getDisease(), p.getMedicalHistory(), "", "", "", "", "", "", "", "", "", "", ""));
                writer.newLine();
            }

            // Doctors
            for (Doctor d : doctors.values()) {
                writer.write(formatCsvRow("DOCTOR", d.getId(), "", "", d.getName(),
                        "", "", d.getContact(), "", "", "", d.getDepartment(), d.getSpecialization(),
                        "", "", "", "", "", "", "", "", ""));
                writer.newLine();
            }

            // Departments
            for (Department dep : departments.values()) {
                writer.write(formatCsvRow("DEPARTMENT", dep.getId(), "", dep.getHeadDoctorId(), dep.getName(),
                        "", "", "", "", "", dep.getDescription(), dep.getName(), "", "", "", "", "", "", "", "", "", ""));
                writer.newLine();
            }

            // Medical Records
            for (MedicalRecord mr : medicalRecords.values()) {
                writer.write(formatCsvRow("MEDICAL_RECORD", mr.getId(), mr.getPatientId(), mr.getDoctorId(), "",
                        "", "", "", "", "", mr.getMedicalHistory(), "", "", mr.getDiagnosis(), mr.getTreatment(),
                        "", "", "", "", "", mr.getDocumentPath(), mr.getTimestamp()));
                writer.newLine();
            }

            // Lab Reports
            for (LabReport lr : labReports.values()) {
                writer.write(formatCsvRow("LAB", lr.getId(), lr.getPatientId(), lr.getDoctorId(), "",
                        "", "", "", "", "", "", "", "", "", "", lr.getTestName(), lr.getTestResult(),
                        "", "", lr.getStatus(), lr.getDocumentPath(), lr.getTimestamp()));
                writer.newLine();
            }

            // Treatments
            for (Treatment t : treatments.values()) {
                writer.write(formatCsvRow("TREATMENT", t.getId(), t.getPatientId(), t.getDoctorId(), "",
                        "", "", "", "", "", "", "", "", t.getDiagnosis(), t.getTreatmentPlan(),
                        "", "", "", "", t.getStatus(), "", t.getTimestamp()));
                writer.newLine();
            }

            // Resources
            for (MedicalResource r : resources.values()) {
                writer.write(formatCsvRow("RESOURCE", r.getId(), "", "", "",
                        "", "", "", "", "", "", r.getDepartment(), "", "", "", "", "",
                        r.getResourceName(), String.valueOf(r.getQuantity()), r.getStatus(), "", ""));
                writer.newLine();
            }

            // Observations
            for (HospitalObservation obs : observations) {
                writer.write(formatCsvRow("OBSERVATION", obs.getId(), obs.getPatientId(), "", "",
                        "", "", "", "", "", "", "", "", "", "", obs.getTestName(), obs.getTestResult(),
                        "", "", obs.getStatus(), "", obs.getTimestamp()));
                writer.newLine();
            }
        }

        // Atomically replace target file
        Files.move(tempPath, csvPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    public synchronized void savePatient(Patient p) throws IOException {
        patients.put(p.getId(), p);
        saveAll();
    }

    public synchronized void saveDoctor(Doctor d) throws IOException {
        doctors.put(d.getId(), d);
        saveAll();
    }

    public synchronized void saveDepartment(Department dep) throws IOException {
        departments.put(dep.getId(), dep);
        saveAll();
    }

    public synchronized void saveMedicalRecord(MedicalRecord r) throws IOException {
        medicalRecords.put(r.getId(), r);
        saveAll();
    }

    public synchronized void saveLabReport(LabReport l) throws IOException {
        labReports.put(l.getId(), l);
        saveAll();
    }

    public synchronized void saveTreatment(Treatment t) throws IOException {
        treatments.put(t.getId(), t);
        saveAll();
    }

    public synchronized void saveResource(MedicalResource r) throws IOException {
        resources.put(r.getId(), r);
        saveAll();
    }

    public synchronized void saveObservation(HospitalObservation o) throws IOException {
        observations.add(o);
        saveAll();
    }

    public synchronized boolean deleteRecord(String recordType, String id) throws IOException {
        boolean removed = false;
        switch (recordType.toUpperCase()) {
            case "PATIENT":
                removed = (patients.remove(id) != null);
                break;
            case "DOCTOR":
                removed = (doctors.remove(id) != null);
                break;
            case "DEPARTMENT":
                removed = (departments.remove(id) != null);
                break;
            case "MEDICAL_RECORD":
                removed = (medicalRecords.remove(id) != null);
                break;
            case "LAB":
                removed = (labReports.remove(id) != null);
                break;
            case "TREATMENT":
                removed = (treatments.remove(id) != null);
                break;
            case "RESOURCE":
                removed = (resources.remove(id) != null);
                break;
            case "OBSERVATION":
                removed = observations.removeIf(obs -> obs.getId().equalsIgnoreCase(id));
                break;
        }
        if (removed) {
            saveAll();
        }
        return removed;
    }

    // Helper format with RFC-4180 CSV escaping
    private String formatCsvRow(String... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            String field = fields[i] == null ? "" : fields[i];
            if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
                sb.append('"');
                sb.append(field.replace("\"", "\"\""));
                sb.append('"');
            } else {
                sb.append(field);
            }
        }
        return sb.toString();
    }

    // RFC-4180 compliant CSV line parser
    public static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    current.append('\"');
                    i++; // Skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result;
    }

    // Getters for in-memory collections
    public Map<String, Patient> getPatients() { return patients; }
    public Map<String, Doctor> getDoctors() { return doctors; }
    public Map<String, Department> getDepartments() { return departments; }
    public Map<String, MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public Map<String, LabReport> getLabReports() { return labReports; }
    public Map<String, Treatment> getTreatments() { return treatments; }
    public Map<String, MedicalResource> getResources() { return resources; }
    public List<HospitalObservation> getObservations() { return observations; }
}
