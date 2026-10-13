package smarthospital;

import smarthospital.m1_string.*;
import smarthospital.m2_suffix.*;
import smarthospital.m3_dynamic.*;
import smarthospital.m4_flow.*;
import smarthospital.m5_np.*;
import smarthospital.m6_random_parallel.*;
import smarthospital.model.*;
import smarthospital.persistence.HospitalCSVManager;
import smarthospital.service.*;
import smarthospital.util.InputUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SMART HOSPITAL – Hospital Resource & Information System
 * Main Console Application & Interactive Menu Controller.
 */
public class Main {

    private static HospitalCSVManager csvManager;
    private static PatientService patientService;
    private static DoctorService doctorService;
    private static MedicalRecordService recordService;
    private static LabReportService labService;
    private static ResourceService resourceService;
    private static MedicalSearchService searchService;
    private static AnalyticsService analyticsService;

    public static void main(String[] args) {
        System.out.println("Initializing SmartHospital System...");
        try {
            csvManager = new HospitalCSVManager();
            csvManager.initializeFile();

            patientService = new PatientService(csvManager);
            doctorService = new DoctorService(csvManager);
            recordService = new MedicalRecordService(csvManager, patientService, doctorService);
            labService = new LabReportService(csvManager, patientService, doctorService);
            resourceService = new ResourceService(csvManager);
            searchService = new MedicalSearchService(csvManager);
            analyticsService = new AnalyticsService(csvManager);

        } catch (Exception e) {
            System.err.println("Fatal Error during initialization: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = InputUtil.readInt(scanner, "Enter selection [0-16]: ", 0, 16);

            try {
                switch (choice) {
                    case 1:  handlePatientMenu(scanner); break;
                    case 2:  handleDoctorMenu(scanner); break;
                    case 3:  handleRecordMenu(scanner); break;
                    case 4:  handleLabMenu(scanner); break;
                    case 5:  handleTreatmentMenu(scanner); break;
                    case 6:  handleResourceMenu(scanner); break;
                    case 7:  handleMedicalSearchMenu(scanner); break;
                    case 8:  handleM1StringAlgorithms(scanner); break;
                    case 9:  handleM2SuffixStructures(scanner); break;
                    case 10: handleM3DynamicProgramming(scanner); break;
                    case 11: handleM4NetworkFlow(scanner); break;
                    case 12: handleM5NPCompleteness(scanner); break;
                    case 13: handleM6RandomizedParallel(scanner); break;
                    case 14: handleHospitalAnalytics(scanner); break;
                    case 15: handleViewStoredData(scanner); break;
                    case 16: handleSaveData(); break;
                    case 0:
                        System.out.println("\nSaving data to persistent CSV storage before exit...");
                        csvManager.saveAll();
                        System.out.println("Data saved successfully. Thank you for using SmartHospital!");
                        running = false;
                        break;
                }
            } catch (Exception e) {
                System.out.println("\n>> [Error occurred during operation]: " + e.getMessage());
                InputUtil.pressEnterToContinue(scanner);
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n" + "=".repeat(64));
        System.out.println("               SMART HOSPITAL               ");
        System.out.println("    Hospital Resource & Information System   ");
        System.out.println("=".repeat(64));
        System.out.println(" 1. Patient Management");
        System.out.println(" 2. Doctor Management");
        System.out.println(" 3. Medical Records");
        System.out.println(" 4. Laboratory Reports");
        System.out.println(" 5. Treatments");
        System.out.println(" 6. Medical Resources");
        System.out.println(" 7. Medical Search");
        System.out.println(" 8. M1 - String Algorithms");
        System.out.println(" 9. M2 - Suffix Structures");
        System.out.println("10. M3 - Dynamic Programming");
        System.out.println("11. M4 - Network Flow");
        System.out.println("12. M5 - NP-Completeness");
        System.out.println("13. M6 - Randomized & Parallel");
        System.out.println("14. Hospital Analytics");
        System.out.println("15. View Stored Data");
        System.out.println("16. Save Data");
        System.out.println(" 0. Exit");
        System.out.println("=".repeat(64));
    }

    // =========================================================================
    // 1. PATIENT MANAGEMENT
    // =========================================================================
    private static void handlePatientMenu(Scanner scanner) throws IOException {
        while (true) {
            InputUtil.printHeader("PATIENT MANAGEMENT");
            System.out.println("1. Add Patient");
            System.out.println("2. View All Patients");
            System.out.println("3. Search Patient");
            System.out.println("4. Update Patient");
            System.out.println("5. Delete Patient");
            System.out.println("0. Return to Main Menu");
            int choice = InputUtil.readInt(scanner, "Choose option [0-5]: ", 0, 5);
            if (choice == 0) break;

            switch (choice) {
                case 1: {
                    System.out.println("\n--- Add New Patient ---");
                    String id = InputUtil.readString(scanner, "Patient ID (e.g. P113): ", false);
                    if (patientService.patientExists(id)) {
                        System.out.println(">> Error: Patient ID already exists!");
                        break;
                    }
                    String name = InputUtil.readString(scanner, "Full Name: ", false);
                    int age = InputUtil.readInt(scanner, "Age: ", 1, 130);
                    String gender = InputUtil.readString(scanner, "Gender (Male/Female/Other): ", false);
                    String contact = InputUtil.readString(scanner, "Contact Number: ", false);
                    String blood = InputUtil.readString(scanner, "Blood Group (e.g. O+, A-, B+): ", false);
                    String disease = InputUtil.readString(scanner, "Primary Disease / Diagnosis: ", false);
                    String history = InputUtil.readString(scanner, "Medical History / Allergies: ", false);

                    Patient p = new Patient(id, name, age, gender, contact, blood, disease, history);
                    patientService.addPatient(p);
                    System.out.println(">> Success: Patient " + id + " registered and saved to CSV!");
                    break;
                }
                case 2: {
                    List<Patient> patients = patientService.getAllPatients();
                    InputUtil.printSubHeader("All Registered Patients (" + patients.size() + ")");
                    for (Patient p : patients) {
                        System.out.println(p);
                    }
                    break;
                }
                case 3: {
                    String q = InputUtil.readString(scanner, "Enter search query (name, ID, or disease): ", false);
                    List<Patient> results = patientService.searchPatients(q);
                    System.out.printf("Found %d matching patients:\n", results.size());
                    for (Patient p : results) {
                        System.out.println(p);
                    }
                    break;
                }
                case 4: {
                    String id = InputUtil.readString(scanner, "Enter Patient ID to update: ", false);
                    Patient p = patientService.getPatientById(id);
                    if (p == null) {
                        System.out.println(">> Error: Patient not found.");
                        break;
                    }
                    System.out.println("Current details: " + p);
                    String name = InputUtil.readString(scanner, "New Name (leave blank to keep current): ", true);
                    if (!name.isEmpty()) p.setName(name);

                    String ageStr = InputUtil.readString(scanner, "New Age (leave blank to keep current): ", true);
                    if (!ageStr.isEmpty()) {
                        try { p.setAge(Integer.parseInt(ageStr)); } catch (NumberFormatException ignored) {}
                    }

                    String disease = InputUtil.readString(scanner, "New Disease (leave blank to keep current): ", true);
                    if (!disease.isEmpty()) p.setDisease(disease);

                    String history = InputUtil.readString(scanner, "New Medical History (leave blank to keep current): ", true);
                    if (!history.isEmpty()) p.setMedicalHistory(history);

                    patientService.updatePatient(p);
                    System.out.println(">> Success: Patient " + id + " updated and persisted to CSV!");
                    break;
                }
                case 5: {
                    String id = InputUtil.readString(scanner, "Enter Patient ID to delete: ", false);
                    if (InputUtil.readYesNo(scanner, "Are you sure you want to delete patient " + id + "?")) {
                        boolean ok = patientService.deletePatient(id);
                        if (ok) {
                            System.out.println(">> Success: Patient " + id + " deleted from database.");
                        } else {
                            System.out.println(">> Error: Patient ID not found.");
                        }
                    }
                    break;
                }
            }
            InputUtil.pressEnterToContinue(scanner);
        }
    }

    // =========================================================================
    // 2. DOCTOR MANAGEMENT
    // =========================================================================
    private static void handleDoctorMenu(Scanner scanner) throws IOException {
        while (true) {
            InputUtil.printHeader("DOCTOR MANAGEMENT");
            System.out.println("1. Add Doctor");
            System.out.println("2. View All Doctors");
            System.out.println("3. Search Doctor");
            System.out.println("4. Update Doctor");
            System.out.println("5. Delete Doctor");
            System.out.println("0. Return to Main Menu");
            int choice = InputUtil.readInt(scanner, "Choose option [0-5]: ", 0, 5);
            if (choice == 0) break;

            switch (choice) {
                case 1: {
                    String id = InputUtil.readString(scanner, "Doctor ID (e.g. D209): ", false);
                    if (doctorService.doctorExists(id)) {
                        System.out.println(">> Error: Doctor ID already exists!");
                        break;
                    }
                    String name = InputUtil.readString(scanner, "Doctor Full Name: ", false);
                    String spec = InputUtil.readString(scanner, "Specialization: ", false);
                    String dept = InputUtil.readString(scanner, "Department: ", false);
                    String contact = InputUtil.readString(scanner, "Contact Info: ", false);

                    Doctor d = new Doctor(id, name, spec, dept, contact);
                    doctorService.addDoctor(d);
                    System.out.println(">> Success: Doctor " + id + " registered and saved to CSV!");
                    break;
                }
                case 2: {
                    List<Doctor> docs = doctorService.getAllDoctors();
                    InputUtil.printSubHeader("All Doctors (" + docs.size() + ")");
                    for (Doctor d : docs) {
                        System.out.println(d);
                    }
                    break;
                }
                case 3: {
                    String q = InputUtil.readString(scanner, "Enter search query (name, specialty, or department): ", false);
                    List<Doctor> docs = doctorService.searchDoctors(q);
                    System.out.printf("Found %d matching doctors:\n", docs.size());
                    for (Doctor d : docs) {
                        System.out.println(d);
                    }
                    break;
                }
                case 4: {
                    String id = InputUtil.readString(scanner, "Enter Doctor ID to update: ", false);
                    Doctor d = doctorService.getDoctorById(id);
                    if (d == null) {
                        System.out.println(">> Error: Doctor not found.");
                        break;
                    }
                    String name = InputUtil.readString(scanner, "New Name (leave blank to keep current): ", true);
                    if (!name.isEmpty()) d.setName(name);

                    String spec = InputUtil.readString(scanner, "New Specialization (leave blank to keep current): ", true);
                    if (!spec.isEmpty()) d.setSpecialization(spec);

                    String dept = InputUtil.readString(scanner, "New Department (leave blank to keep current): ", true);
                    if (!dept.isEmpty()) d.setDepartment(dept);

                    doctorService.updateDoctor(d);
                    System.out.println(">> Success: Doctor " + id + " updated and persisted to CSV!");
                    break;
                }
                case 5: {
                    String id = InputUtil.readString(scanner, "Enter Doctor ID to delete: ", false);
                    if (InputUtil.readYesNo(scanner, "Are you sure you want to delete doctor " + id + "?")) {
                        boolean ok = doctorService.deleteDoctor(id);
                        if (ok) {
                            System.out.println(">> Success: Doctor " + id + " deleted.");
                        } else {
                            System.out.println(">> Error: Doctor not found.");
                        }
                    }
                    break;
                }
            }
            InputUtil.pressEnterToContinue(scanner);
        }
    }

    // =========================================================================
    // 3. MEDICAL RECORDS
    // =========================================================================
    private static void handleRecordMenu(Scanner scanner) throws IOException {
        while (true) {
            InputUtil.printHeader("MEDICAL RECORDS MANAGEMENT");
            System.out.println("1. Add Medical Record");
            System.out.println("2. View All Medical Records");
            System.out.println("3. View Records by Patient ID");
            System.out.println("4. Delete Medical Record");
            System.out.println("0. Return to Main Menu");
            int choice = InputUtil.readInt(scanner, "Choose option [0-4]: ", 0, 4);
            if (choice == 0) break;

            switch (choice) {
                case 1: {
                    String id = InputUtil.readString(scanner, "Record ID (e.g. MR312): ", false);
                    String pid = InputUtil.readString(scanner, "Patient ID: ", false);
                    if (!patientService.patientExists(pid)) {
                        System.out.println(">> Error: Referenced Patient ID '" + pid + "' does not exist!");
                        break;
                    }
                    String did = InputUtil.readString(scanner, "Doctor ID: ", false);
                    if (!doctorService.doctorExists(did)) {
                        System.out.println(">> Error: Referenced Doctor ID '" + did + "' does not exist!");
                        break;
                    }
                    String diag = InputUtil.readString(scanner, "Clinical Diagnosis: ", false);
                    String rx = InputUtil.readString(scanner, "Treatment / Prescription Plan: ", false);
                    String hist = InputUtil.readString(scanner, "Clinical Notes / History: ", false);
                    String docPath = InputUtil.readString(scanner, "ICD-10 Code or Document Path (e.g. ICD10-I25.1): ", false);
                    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    MedicalRecord mr = new MedicalRecord(id, pid, did, diag, rx, hist, docPath, time);
                    recordService.addMedicalRecord(mr);
                    System.out.println(">> Success: Medical record " + id + " added and saved to CSV!");
                    break;
                }
                case 2: {
                    List<MedicalRecord> records = recordService.getAllRecords();
                    InputUtil.printSubHeader("All Medical Records (" + records.size() + ")");
                    for (MedicalRecord mr : records) {
                        System.out.println(mr);
                    }
                    break;
                }
                case 3: {
                    String pid = InputUtil.readString(scanner, "Enter Patient ID: ", false);
                    List<MedicalRecord> list = recordService.getRecordsByPatient(pid);
                    System.out.printf("Found %d records for patient %s:\n", list.size(), pid);
                    for (MedicalRecord mr : list) {
                        System.out.println(mr);
                    }
                    break;
                }
                case 4: {
                    String id = InputUtil.readString(scanner, "Enter Record ID to delete: ", false);
                    if (InputUtil.readYesNo(scanner, "Delete record " + id + "?")) {
                        boolean ok = recordService.deleteRecord(id);
                        if (ok) System.out.println(">> Success: Record " + id + " deleted.");
                        else System.out.println(">> Error: Record not found.");
                    }
                    break;
                }
            }
            InputUtil.pressEnterToContinue(scanner);
        }
    }

    // =========================================================================
    // 4. LABORATORY REPORTS
    // =========================================================================
    private static void handleLabMenu(Scanner scanner) throws IOException {
        while (true) {
            InputUtil.printHeader("LABORATORY REPORTS MANAGEMENT");
            System.out.println("1. Add Laboratory Report");
            System.out.println("2. View All Laboratory Reports");
            System.out.println("3. View Reports by Patient ID");
            System.out.println("4. Delete Laboratory Report");
            System.out.println("0. Return to Main Menu");
            int choice = InputUtil.readInt(scanner, "Choose option [0-4]: ", 0, 4);
            if (choice == 0) break;

            switch (choice) {
                case 1: {
                    String id = InputUtil.readString(scanner, "Lab Report ID (e.g. L411): ", false);
                    String pid = InputUtil.readString(scanner, "Patient ID: ", false);
                    if (!patientService.patientExists(pid)) {
                        System.out.println(">> Error: Referenced Patient ID '" + pid + "' does not exist!");
                        break;
                    }
                    String did = InputUtil.readString(scanner, "Doctor ID: ", false);
                    if (!doctorService.doctorExists(did)) {
                        System.out.println(">> Error: Referenced Doctor ID '" + did + "' does not exist!");
                        break;
                    }
                    String testName = InputUtil.readString(scanner, "Test Name (e.g. Serum Creatinine): ", false);
                    String result = InputUtil.readString(scanner, "Test Result: ", false);
                    String status = InputUtil.readString(scanner, "Status (Completed/Pending/Critical): ", false);
                    String doc = InputUtil.readString(scanner, "Document/Barcode ID: ", false);
                    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    LabReport lr = new LabReport(id, pid, did, testName, result, status, doc, time);
                    labService.addLabReport(lr);
                    System.out.println(">> Success: Lab Report " + id + " added and persisted to CSV!");
                    break;
                }
                case 2: {
                    List<LabReport> list = labService.getAllLabReports();
                    InputUtil.printSubHeader("All Laboratory Reports (" + list.size() + ")");
                    for (LabReport lr : list) {
                        System.out.println(lr);
                    }
                    break;
                }
                case 3: {
                    String pid = InputUtil.readString(scanner, "Enter Patient ID: ", false);
                    List<LabReport> list = labService.getReportsByPatient(pid);
                    System.out.printf("Found %d lab reports for patient %s:\n", list.size(), pid);
                    for (LabReport lr : list) {
                        System.out.println(lr);
                    }
                    break;
                }
                case 4: {
                    String id = InputUtil.readString(scanner, "Enter Lab Report ID to delete: ", false);
                    if (InputUtil.readYesNo(scanner, "Delete report " + id + "?")) {
                        boolean ok = labService.deleteReport(id);
                        if (ok) System.out.println(">> Success: Report " + id + " deleted.");
                        else System.out.println(">> Error: Report not found.");
                    }
                    break;
                }
            }
            InputUtil.pressEnterToContinue(scanner);
        }
    }

    // =========================================================================
    // 5. TREATMENTS
    // =========================================================================
    private static void handleTreatmentMenu(Scanner scanner) throws IOException {
        while (true) {
            InputUtil.printHeader("TREATMENT REGIMENS & THERAPIES");
            System.out.println("1. Add Treatment Regimen");
            System.out.println("2. View All Treatments");
            System.out.println("3. Update Treatment Status");
            System.out.println("0. Return to Main Menu");
            int choice = InputUtil.readInt(scanner, "Choose option [0-3]: ", 0, 3);
            if (choice == 0) break;

            switch (choice) {
                case 1: {
                    String id = InputUtil.readString(scanner, "Treatment ID (e.g. T510): ", false);
                    String pid = InputUtil.readString(scanner, "Patient ID: ", false);
                    String did = InputUtil.readString(scanner, "Doctor ID: ", false);
                    String diag = InputUtil.readString(scanner, "Target Diagnosis: ", false);
                    String plan = InputUtil.readString(scanner, "Therapy Regimen / Protocol: ", false);
                    String status = InputUtil.readString(scanner, "Status (Active/Completed/Paused): ", false);
                    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    Treatment t = new Treatment(id, pid, did, diag, plan, status, time);
                    csvManager.saveTreatment(t);
                    System.out.println(">> Success: Treatment " + id + " saved to CSV!");
                    break;
                }
                case 2: {
                    List<Treatment> list = new ArrayList<>(csvManager.getTreatments().values());
                    InputUtil.printSubHeader("All Treatments (" + list.size() + ")");
                    for (Treatment t : list) {
                        System.out.println(t);
                    }
                    break;
                }
                case 3: {
                    String id = InputUtil.readString(scanner, "Enter Treatment ID: ", false);
                    Treatment t = csvManager.getTreatments().get(id);
                    if (t == null) {
                        System.out.println(">> Error: Treatment not found.");
                        break;
                    }
                    String st = InputUtil.readString(scanner, "New Status (Active/Completed/Paused): ", false);
                    t.setStatus(st);
                    csvManager.saveTreatment(t);
                    System.out.println(">> Success: Treatment status updated and persisted.");
                    break;
                }
            }
            InputUtil.pressEnterToContinue(scanner);
        }
    }

    // =========================================================================
    // 6. MEDICAL RESOURCES
    // =========================================================================
    private static void handleResourceMenu(Scanner scanner) throws IOException {
        while (true) {
            InputUtil.printHeader("MEDICAL RESOURCE MANAGEMENT");
            System.out.println("1. Add Medical Resource");
            System.out.println("2. View All Resources");
            System.out.println("3. Update Resource Quantity");
            System.out.println("4. Allocate Resource to Emergency/Unit");
            System.out.println("5. Release / Return Resource");
            System.out.println("0. Return to Main Menu");
            int choice = InputUtil.readInt(scanner, "Choose option [0-5]: ", 0, 5);
            if (choice == 0) break;

            switch (choice) {
                case 1: {
                    String id = InputUtil.readString(scanner, "Resource ID (e.g. R613): ", false);
                    String name = InputUtil.readString(scanner, "Resource Name (e.g. ECMO Machine): ", false);
                    String dept = InputUtil.readString(scanner, "Department: ", false);
                    int qty = InputUtil.readInt(scanner, "Initial Quantity: ", 0, 10000);
                    MedicalResource r = new MedicalResource(id, name, dept, qty, qty > 0 ? "Available" : "Exhausted");
                    resourceService.addResource(r);
                    System.out.println(">> Success: Resource " + id + " registered and saved to CSV!");
                    break;
                }
                case 2: {
                    List<MedicalResource> list = resourceService.getAllResources();
                    InputUtil.printSubHeader("Hospital Resource Inventory (" + list.size() + ")");
                    for (MedicalResource r : list) {
                        System.out.println(r);
                    }
                    break;
                }
                case 3: {
                    String id = InputUtil.readString(scanner, "Enter Resource ID: ", false);
                    int qty = InputUtil.readInt(scanner, "Enter new total quantity: ", 0, 10000);
                    resourceService.updateQuantity(id, qty);
                    System.out.println(">> Success: Resource quantity updated to " + qty + " and saved to CSV.");
                    break;
                }
                case 4: {
                    String id = InputUtil.readString(scanner, "Enter Resource ID to allocate: ", false);
                    int amount = InputUtil.readInt(scanner, "Units to allocate: ", 1, 1000);
                    boolean ok = resourceService.allocateResource(id, amount);
                    if (ok) {
                        MedicalResource res = resourceService.getResourceById(id);
                        System.out.printf(">> Success: Allocated %d units of %s. Remaining: %d\n", amount, res.getResourceName(), res.getQuantity());
                    } else {
                        System.out.println(">> Error: Insufficient available quantity to allocate!");
                    }
                    break;
                }
                case 5: {
                    String id = InputUtil.readString(scanner, "Enter Resource ID to release: ", false);
                    int amount = InputUtil.readInt(scanner, "Units to release: ", 1, 1000);
                    resourceService.releaseResource(id, amount);
                    MedicalResource res = resourceService.getResourceById(id);
                    System.out.printf(">> Success: Released %d units of %s. Current available: %d\n", amount, res.getResourceName(), res.getQuantity());
                    break;
                }
            }
            InputUtil.pressEnterToContinue(scanner);
        }
    }

    // =========================================================================
    // 7. MEDICAL SEARCH (Unified)
    // =========================================================================
    private static void handleMedicalSearchMenu(Scanner scanner) {
        InputUtil.printHeader("UNIFIED MEDICAL SEARCH");
        String term = InputUtil.readString(scanner, "Enter disease, clinical symptom, or medical term: ", false);

        System.out.println("\nExecuting high-performance search across hospital records...");
        List<MedicalSearchService.RecordMatch> matches = searchService.searchWithKMP(term);

        if (matches.isEmpty()) {
            System.out.println(">> No exact matches found. Querying fuzzy clinical vocabulary...");
            List<LevenshteinDistance.Suggestion> suggestions = LevenshteinDistance.findClosestTerms(term, null, 3);
            System.out.println("Did you mean one of these medical terms?");
            for (LevenshteinDistance.Suggestion s : suggestions) {
                System.out.println("  - " + s);
            }
        } else {
            System.out.printf(">> Found %d matching hospital records:\n", matches.size());
            for (MedicalSearchService.RecordMatch rm : matches) {
                System.out.println(rm);
            }
        }
        InputUtil.pressEnterToContinue(scanner);
    }

    // =========================================================================
    // 8. M1 - STRING ALGORITHMS
    // =========================================================================
    private static void handleM1StringAlgorithms(Scanner scanner) {
        while (true) {
            InputUtil.printHeader("MODULE 1 (CO1) - STRING ALGORITHMS");
            System.out.println("1. KMP Algorithm          - Search disease names & medical terms");
            System.out.println("2. Z-Function Algorithm   - Detect repeated medical phrases");
            System.out.println("3. Rabin-Karp Algorithm   - Search diagnostic & ICD-10 codes");
            System.out.println("4. Aho-Corasick Algorithm - Simultaneous multi-keyword screening");
            System.out.println("0. Return to Main Menu");
            int choice = InputUtil.readInt(scanner, "Choose algorithm [0-4]: ", 0, 4);
            if (choice == 0) break;

            switch (choice) {
                case 1: {
                    System.out.println("\n[KMP Algorithm - Knuth-Morris-Pratt]");
                    System.out.println("Complexity: O(N + M) Time | O(M) Space");
                    String query = InputUtil.readString(scanner, "Enter disease name/term (e.g. 'Asthma', 'Diabetes', 'Fracture'): ", false);
                    List<MedicalSearchService.RecordMatch> hits = searchService.searchWithKMP(query);
                    System.out.printf(">> Found %d occurrences using KMP:\n", hits.size());
                    for (MedicalSearchService.RecordMatch hit : hits) {
                        System.out.println("   " + hit);
                    }
                    break;
                }
                case 2: {
                    System.out.println("\n[Z-Function Algorithm]");
                    System.out.println("Complexity: O(N) Time | O(N) Space");
                    System.out.println("Extracting clinical notes corpus from medical records...");
                    String corpus = searchService.buildHospitalCorpus();
                    int minLen = InputUtil.readInt(scanner, "Minimum phrase length to detect [3-30]: ", 3, 30);
                    List<ZFunction.PhraseOccurrence> repeated = ZFunction.findRepeatedPhrases(corpus, minLen, 2);
                    System.out.printf(">> Detected %d repeated medical phrases in clinical documents:\n", repeated.size());
                    int count = 0;
                    for (ZFunction.PhraseOccurrence po : repeated) {
                        System.out.printf("   - \"%s\" (Repeated %d times at offsets %s)\n", po.phrase, po.count, po.positions);
                        if (++count >= 10) break; // Display top 10
                    }
                    break;
                }
                case 3: {
                    System.out.println("\n[Rabin-Karp Rolling Hash Algorithm]");
                    System.out.println("Complexity: Average O(N + M) Time | O(1) Space");
                    String code = InputUtil.readString(scanner, "Enter diagnostic / ICD-10 code (e.g. 'E11', 'I20', 'J45', 'LAB-DOC'): ", false);
                    List<MedicalSearchService.RecordMatch> hits = searchService.searchDiagnosticCodeWithRabinKarp(code);
                    System.out.printf(">> Found %d occurrences using Rabin-Karp rolling hash:\n", hits.size());
                    for (MedicalSearchService.RecordMatch hit : hits) {
                        System.out.println("   " + hit);
                    }
                    break;
                }
                case 4: {
                    System.out.println("\n[Aho-Corasick Multi-Pattern Automaton]");
                    System.out.println("Complexity: O(M * Sigma) Construction | O(N + K) Search Time");
                    System.out.println("Simultaneously screening for critical hospital alert keywords:");
                    List<String> defaultKeywords = List.of("allergy", "asthma", "stroke", "chemotherapy", "fracture", "arrhythmia", "penicillin");
                    System.out.println("Keywords: " + defaultKeywords);
                    String custom = InputUtil.readString(scanner, "Enter additional comma-separated keywords (or leave empty): ", true);
                    List<String> searchKws = new ArrayList<>(defaultKeywords);
                    if (!custom.isEmpty()) {
                        for (String k : custom.split(",")) {
                            if (!k.trim().isEmpty()) searchKws.add(k.trim());
                        }
                    }
                    Map<String, List<MedicalSearchService.RecordMatch>> alertHits = searchService.multiKeywordSearch(searchKws);
                    for (Map.Entry<String, List<MedicalSearchService.RecordMatch>> entry : alertHits.entrySet()) {
                        System.out.printf("Keyword '%s' -> %d alert hits\n", entry.getKey(), entry.getValue().size());
                        for (MedicalSearchService.RecordMatch m : entry.getValue()) {
                            System.out.printf("   [%s: %s] %s\n", m.recordType, m.recordId, m.snippet);
                        }
                    }
                    break;
                }
            }
            InputUtil.pressEnterToContinue(scanner);
        }
    }

    // =========================================================================
    // 9. M2 - SUFFIX STRUCTURES
    // =========================================================================
    private static void handleM2SuffixStructures(Scanner scanner) {
        while (true) {
            InputUtil.printHeader("MODULE 2 (CO2) - SUFFIX STRUCTURES");
            System.out.println("1. Suffix Array    - Medical-record indexing & binary search");
            System.out.println("2. SA-IS Algorithm - O(N) linear-time suffix array construction for large reports");
            System.out.println("3. LCP Array       - Substring frequency & distinct substring count");
            System.out.println("4. Kasai Algorithm - O(N) LCP construction from Suffix Array");
            System.out.println("5. Suffix Tree     - Substring retrieval & occurrence localization");
            System.out.println("6. Suffix Automaton- DAWG minimal automaton for instant substring queries");
            System.out.println("0. Return to Main Menu");
            int choice = InputUtil.readInt(scanner, "Choose structure [0-6]: ", 0, 6);
            if (choice == 0) break;

            String corpus = searchService.buildHospitalCorpus();

            switch (choice) {
                case 1: {
                    System.out.println("\n[Suffix Array Construction (Prefix Doubling)]");
                    System.out.println("Complexity: O(N log^2 N) Construction | O(M log N) Query");
                    SuffixArray sa = new SuffixArray(corpus);
                    System.out.printf(">> Suffix Array built over %d characters of clinical text.\n", corpus.length());
                    String query = InputUtil.readString(scanner, "Enter substring pattern to search: ", false);
                    List<Integer> positions = sa.search(query);
                    System.out.printf(">> Found %d occurrences at character offsets: %s\n", positions.size(), positions);
                    for (int pos : positions) {
                        int end = Math.min(pos + 50, corpus.length());
                        System.out.printf("   Offset %4d: \"...%s...\"\n", pos, corpus.substring(pos, end).replace("\n", " "));
                    }
                    break;
                }
                case 2: {
                    System.out.println("\n[SA-IS (Suffix Array Induced Sorting)]");
                    System.out.println("Complexity: Guaranteed O(N) Linear Time & O(N) Space");
                    long tStart = System.nanoTime();
                    int[] sa = SAIS.buildSuffixArray(corpus);
                    long duration = System.nanoTime() - tStart;
                    System.out.printf(">> SA-IS computed %d suffix indices in %,d nanoseconds (%.3f ms).\n",
                            sa.length, duration, duration / 1_000_000.0);
                    System.out.print("First 10 Suffix Array entries: ");
                    for (int i = 0; i < Math.min(10, sa.length); i++) {
                        System.out.print(sa[i] + " ");
                    }
                    System.out.println();
                    break;
                }
                case 3: {
                    System.out.println("\n[LCP Array Analysis]");
                    System.out.println("Complexity: O(N) Time");
                    SuffixArray sa = new SuffixArray(corpus);
                    LCPArray lcp = new LCPArray(corpus, sa.getSA());
                    System.out.println(">> Longest Repeated Clinical Substring: \"" + lcp.getLongestRepeatedSubstring() + "\"");
                    System.out.println(">> Total Distinct Substrings in Corpus: " + lcp.countDistinctSubstrings());
                    List<LCPArray.RepeatedSubstringInfo> phrases = lcp.findRepeatedPhrases(10);
                    System.out.printf(">> Found %d repeated phrases of length >= 10. Sample:\n", phrases.size());
                    for (int i = 0; i < Math.min(5, phrases.size()); i++) {
                        System.out.println("   " + phrases.get(i));
                    }
                    break;
                }
                case 4: {
                    System.out.println("\n[Kasai's Algorithm for LCP Construction]");
                    System.out.println("Complexity: O(N) Linear Time");
                    int[] sa = SAIS.buildSuffixArray(corpus);
                    long tStart = System.nanoTime();
                    int[] lcp = KasaiAlgorithm.constructLCP(corpus, sa);
                    long duration = System.nanoTime() - tStart;
                    System.out.printf(">> Kasai constructed LCP array of size %d in %,d ns.\n", lcp.length, duration);
                    System.out.print("First 15 LCP values: ");
                    for (int i = 0; i < Math.min(15, lcp.length); i++) {
                        System.out.print(lcp[i] + " ");
                    }
                    System.out.println();
                    break;
                }
                case 5: {
                    System.out.println("\n[Suffix Tree Construction]");
                    System.out.println("Complexity: O(N) Substring Retrieval");
                    // Use a concise clinical text snippet for tree visualization
                    String snippet = "P101: Type-2 Diabetes Mellitus; P102: Bronchial Asthma; P103: Coronary Artery Disease";
                    SuffixTree tree = new SuffixTree(snippet);
                    System.out.println("Built Suffix Tree for: \"" + snippet + "\"");
                    System.out.println("Total Tree Nodes: " + tree.getNodeCount());
                    String query = InputUtil.readString(scanner, "Enter substring to query in Suffix Tree: ", false);
                    List<Integer> hits = tree.findAllOccurrences(query);
                    System.out.printf(">> Substring present? %s (Occurrences: %d at %s)\n",
                            tree.contains(query), hits.size(), hits);
                    System.out.println("Longest repeated substring in snippet: \"" + tree.longestRepeatedSubstring() + "\"");
                    break;
                }
                case 6: {
                    System.out.println("\n[Suffix Automaton (DAWG)]");
                    System.out.println("Complexity: O(N) States & Transitions | O(M) Instant Substring Query");
                    SuffixAutomaton sam = new SuffixAutomaton(corpus);
                    System.out.printf(">> Suffix Automaton constructed with %d states.\n", sam.getStateCount());
                    System.out.println(">> Total Distinct Substrings: " + sam.countDistinctSubstrings());
                    String query = InputUtil.readString(scanner, "Enter substring to query in Suffix Automaton: ", false);
                    System.out.printf(">> Contains \"%s\"? %s | Occurrence count: %d\n",
                            query, sam.contains(query), sam.countOccurrences(query));
                    break;
                }
            }
            InputUtil.pressEnterToContinue(scanner);
        }
    }

    // =========================================================================
    // 10. M3 - DYNAMIC PROGRAMMING
    // =========================================================================
    private static void handleM3DynamicProgramming(Scanner scanner) {
        while (true) {
            InputUtil.printHeader("MODULE 3 (CO3) - ADVANCED DYNAMIC PROGRAMMING");
            System.out.println("1. Levenshtein Distance        - Medical term spelling correction");
            System.out.println("2. Damerau-Levenshtein         - Transposition-aware medical spelling correction");
            System.out.println("3. Weighted Edit Distance      - Record-error correction with custom costs");
            System.out.println("4. Needleman-Wunsch Algorithm  - Global biological sequence alignment");
            System.out.println("5. Smith-Waterman Algorithm    - Local biological sequence alignment & motif discovery");
            System.out.println("6. Bitmask Dynamic Programming - Optimal diagnostic-test combination selection");
            System.out.println("0. Return to Main Menu");
            int choice = InputUtil.readInt(scanner, "Choose algorithm [0-6]: ", 0, 6);
            if (choice == 0) break;

            switch (choice) {
                case 1: {
                    System.out.println("\n[Levenshtein Distance - Medical Lexicon Autocorrect]");
                    System.out.println("Complexity: O(M * N) Time | O(M * N) Space");
                    String query = InputUtil.readString(scanner, "Enter misspelled medical term (e.g. 'diabetis', 'arrhytmia'): ", false);
                    List<LevenshteinDistance.Suggestion> suggestions = LevenshteinDistance.findClosestTerms(query, null, 5);
                    System.out.println(">> Top Vocabulary Suggestions:");
                    for (LevenshteinDistance.Suggestion s : suggestions) {
                        System.out.println("   - " + s);
                    }
                    break;
                }
                case 2: {
                    System.out.println("\n[Damerau-Levenshtein Distance - Transposition Correction]");
                    System.out.println("Complexity: O(M * N) Time | O(M * N) Space");
                    String s1 = InputUtil.readString(scanner, "Word 1 (e.g. 'diebtes'): ", false);
                    String s2 = InputUtil.readString(scanner, "Word 2 (e.g. 'diabetes'): ", false);
                    int levDist = LevenshteinDistance.computeDistance(s1, s2);
                    int damDist = DamerauLevenshtein.computeDistance(s1, s2);
                    System.out.printf("Standard Levenshtein Distance  : %d\n", levDist);
                    System.out.printf("Damerau-Levenshtein (with swap): %d\n", damDist);
                    System.out.println("Notice: Adjacent character transposition counts as a single edit step in Damerau-Levenshtein.");
                    break;
                }
                case 3: {
                    System.out.println("\n[Weighted Edit Distance - Domain-Specific Costs]");
                    System.out.println("Complexity: O(M * N) Time");
                    String s1 = InputUtil.readString(scanner, "Clinical record string 1: ", false);
                    String s2 = InputUtil.readString(scanner, "Clinical record string 2: ", false);
                    double wDist = WeightedEditDistance.computeDistance(s1, s2);
                    System.out.printf(">> Weighted Edit Distance: %.2f\n", wDist);
                    System.out.println("Costs: OCR/Letter-digit=0.4, Vowel/Phonetic=0.6-0.7, Insert/Del=1.0, Default Sub=1.5");
                    break;
                }
                case 4: {
                    System.out.println("\n[Needleman-Wunsch Global Biological Sequence Alignment]");
                    System.out.println("Complexity: O(M * N) Time & Space");
                    System.out.println("Example: Pathogen genomic variant vs Patient diagnostic sequence");
                    String seq1 = InputUtil.readString(scanner, "Sequence 1 (default 'GAATTCAGTTA'): ", true);
                    if (seq1.isEmpty()) seq1 = "GAATTCAGTTA";
                    String seq2 = InputUtil.readString(scanner, "Sequence 2 (default 'GGATCGA'): ", true);
                    if (seq2.isEmpty()) seq2 = "GGATCGA";

                    NeedlemanWunsch.AlignmentResult res = NeedlemanWunsch.align(seq1, seq2);
                    System.out.println("\n" + res);
                    break;
                }
                case 5: {
                    System.out.println("\n[Smith-Waterman Local Biological Sequence Alignment]");
                    System.out.println("Complexity: O(M * N) Time & Space");
                    System.out.println("Example: Finding conserved oncogene/viral motif in patient sample");
                    String seq1 = InputUtil.readString(scanner, "Sequence 1 (default 'ACACACTA'): ", true);
                    if (seq1.isEmpty()) seq1 = "ACACACTA";
                    String seq2 = InputUtil.readString(scanner, "Sequence 2 (default 'AGCACACA'): ", true);
                    if (seq2.isEmpty()) seq2 = "AGCACACA";

                    SmithWaterman.LocalAlignmentResult res = SmithWaterman.align(seq1, seq2);
                    System.out.println("\n" + res);
                    break;
                }
                case 6: {
                    System.out.println("\n[Bitmask DP - Diagnostic Test Battery Optimization]");
                    System.out.println("Complexity: O(T * 2^N) Time | O(2^N) Space");
                    List<String> symptoms = BitmaskDP.DEFAULT_SYMPTOMS;
                    System.out.println("Available Symptoms to cover:");
                    for (int i = 0; i < symptoms.size(); i++) {
                        System.out.printf("  %d. %s\n", i + 1, symptoms.get(i));
                    }
                    List<BitmaskDP.DiagnosticTest> catalog = BitmaskDP.getDefaultCatalog(symptoms);
                    BitmaskDP.OptimalPanelResult opt = BitmaskDP.solve(symptoms, catalog);

                    System.out.printf("\n>> Optimal Diagnostic Test Battery (Total Cost: $%.2f):\n", opt.totalCost);
                    for (BitmaskDP.DiagnosticTest test : opt.selectedTests) {
                        System.out.println("   " + test);
                    }
                    System.out.printf("Symptom Coverage: %d / %d required symptoms satisfied.\n",
                            Integer.bitCount(opt.coveredMask), symptoms.size());
                    break;
                }
            }
            InputUtil.pressEnterToContinue(scanner);
        }
    }

    // =========================================================================
    // 11. M4 - NETWORK FLOW
    // =========================================================================
    private static void handleM4NetworkFlow(Scanner scanner) {
        while (true) {
            InputUtil.printHeader("MODULE 4 (CO4) - NETWORK FLOW");
            System.out.println("1. Ford-Fulkerson Algorithm - Medical-resource distribution");
            System.out.println("2. Edmonds-Karp Algorithm   - BFS shortest augmenting paths for hospital supply");
            System.out.println("3. Dinic's Algorithm        - Level graph max flow & min-cut bottleneck detection");
            System.out.println("4. Bipartite Matching       - Doctor-to-Patient / Doctor-to-Case assignment");
            System.out.println("5. König's Theorem          - Minimum vertex cover for conflict coverage");
            System.out.println("0. Return to Main Menu");
            int choice = InputUtil.readInt(scanner, "Choose algorithm [0-5]: ", 0, 5);
            if (choice == 0) break;

            switch (choice) {
                case 1: {
                    System.out.println("\n[Ford-Fulkerson - Medical Resource Distribution]");
                    System.out.println("Complexity: O(E * |f_max|) Time");
                    FordFulkerson ff = new FordFulkerson(6);
                    ff.setNodeName(0, "Central Pharmacy Supply");
                    ff.setNodeName(1, "Emergency Depot");
                    ff.setNodeName(2, "ICU Depot");
                    ff.setNodeName(3, "Cardiology Ward");
                    ff.setNodeName(4, "Surgery Suite");
                    ff.setNodeName(5, "Patient Terminal Sink");

                    ff.addEdge(0, 1, 16);
                    ff.addEdge(0, 2, 13);
                    ff.addEdge(1, 2, 10);
                    ff.addEdge(1, 3, 12);
                    ff.addEdge(2, 4, 14);
                    ff.addEdge(3, 2, 9);
                    ff.addEdge(3, 5, 20);
                    ff.addEdge(4, 5, 4);

                    int maxFlow = ff.computeMaxFlow(0, 5);
                    System.out.printf(">> Maximum Deliverable Medical Supply Flow: %d units/hr\n", maxFlow);
                    break;
                }
                case 2: {
                    System.out.println("\n[Edmonds-Karp - BFS Augmenting Paths]");
                    System.out.println("Complexity: O(V * E^2) Time");
                    int[][] cap = new int[6][6];
                    cap[0][1] = 16; cap[0][2] = 13;
                    cap[1][2] = 10; cap[1][3] = 12;
                    cap[2][4] = 14; cap[3][2] = 9;
                    cap[3][5] = 20; cap[4][5] = 4;

                    Map<Integer, String> names = Map.of(
                            0, "Depot", 1, "Emergency", 2, "ICU",
                            3, "Cardio", 4, "Surgery", 5, "Patients"
                    );

                    EdmondsKarp.FlowResult res = EdmondsKarp.computeMaxFlow(cap, 0, 5, names);
                    System.out.println("Augmentation History:");
                    for (String log : res.iterationLogs) {
                        System.out.println("  " + log);
                    }
                    System.out.printf(">> Total Maximum Flow: %d units\n", res.maxFlow);
                    break;
                }
                case 3: {
                    System.out.println("\n[Dinic's Algorithm - Large Hospital Resource Network]");
                    System.out.println("Complexity: O(V^2 * E) Time | O(V + E) Space");
                    Dinic dinic = new Dinic(8);
                    dinic.setNodeLabel(0, "Central Oxygen Plant");
                    dinic.setNodeLabel(1, "Main Hospital Buffer");
                    dinic.setNodeLabel(2, "Trauma Center");
                    dinic.setNodeLabel(3, "Critical ICU");
                    dinic.setNodeLabel(4, "Pediatric Wing");
                    dinic.setNodeLabel(5, "General Ward A");
                    dinic.setNodeLabel(6, "General Ward B");
                    dinic.setNodeLabel(7, "Terminal Beds (Sink)");

                    dinic.addEdge(0, 1, 100);
                    dinic.addEdge(0, 2, 60);
                    dinic.addEdge(1, 3, 50);
                    dinic.addEdge(1, 4, 30);
                    dinic.addEdge(2, 3, 40);
                    dinic.addEdge(2, 5, 20);
                    dinic.addEdge(3, 7, 70);
                    dinic.addEdge(4, 7, 25);
                    dinic.addEdge(5, 6, 20);
                    dinic.addEdge(6, 7, 20);

                    int maxFlow = dinic.maxFlow(0, 7);
                    System.out.printf(">> Maximum Oxygen Delivery: %d L/min\n", maxFlow);
                    List<Dinic.CutEdge> cut = dinic.getMinCutEdges(0);
                    System.out.println(">> Critical Bottleneck Channels (Min-Cut):");
                    for (Dinic.CutEdge ce : cut) {
                        System.out.println("   - " + ce);
                    }
                    break;
                }
                case 4: {
                    System.out.println("\n[Bipartite Matching - Hopcroft-Karp Doctor Assignment]");
                    System.out.println("Complexity: O(E * sqrt(V)) Time");
                    BipartiteMatching bpm = new BipartiteMatching();
                    // Available Doctors
                    bpm.addDoctor("Dr. Rajesh Gupta (Cardiology)");
                    bpm.addDoctor("Dr. Sunita Verma (Neurology)");
                    bpm.addDoctor("Dr. Vikram Singh (Orthopedics)");
                    bpm.addDoctor("Dr. Pooja Nair (Oncology)");
                    bpm.addDoctor("Dr. Arjun Reddy (Trauma/Emergency)");

                    // Acute Cases
                    bpm.addCase("Case 1: Acute Myocardial Infarction");
                    bpm.addCase("Case 2: Acute MCA Ischemic Stroke");
                    bpm.addCase("Case 3: Femur Fracture Surgery");
                    bpm.addCase("Case 4: Severe Polytrauma Stabilization");
                    bpm.addCase("Case 5: Uncontrolled Ventricular Tachycardia");

                    // Compatibility Edges
                    bpm.addCompatibility(0, 0); // Dr. Gupta -> Case 1
                    bpm.addCompatibility(0, 4); // Dr. Gupta -> Case 5
                    bpm.addCompatibility(1, 1); // Dr. Verma -> Case 2
                    bpm.addCompatibility(2, 2); // Dr. Singh -> Case 3
                    bpm.addCompatibility(4, 3); // Dr. Reddy -> Case 4
                    bpm.addCompatibility(4, 0); // Dr. Reddy -> Case 1

                    BipartiteMatching.MatchingResult mr = bpm.solve();
                    System.out.printf(">> Maximum Assigned Doctor-Case Pairs: %d\n", mr.maxMatchingSize);
                    for (BipartiteMatching.MatchPair mp : mr.matches) {
                        System.out.println("   " + mp);
                    }
                    if (!mr.unassignedDoctors.isEmpty()) {
                        System.out.println("Available on standby: " + mr.unassignedDoctors);
                    }
                    break;
                }
                case 5: {
                    System.out.println("\n[König's Theorem - Conflict Coverage & Minimum Oversight]");
                    System.out.println("Theorem: |Maximum Bipartite Matching| = |Minimum Vertex Cover|");
                    BipartiteMatching bpm = new BipartiteMatching();
                    bpm.addDoctor("Dr. Gupta (Cardiology)");
                    bpm.addDoctor("Dr. Verma (Neurology)");
                    bpm.addDoctor("Dr. Singh (Orthopedics)");
                    bpm.addDoctor("Dr. Reddy (Emergency)");

                    bpm.addCase("Case A (Arrhythmia)");
                    bpm.addCase("Case B (Stroke)");
                    bpm.addCase("Case C (Trauma)");

                    bpm.addCompatibility(0, 0);
                    bpm.addCompatibility(1, 1);
                    bpm.addCompatibility(3, 2);
                    bpm.addCompatibility(3, 0);

                    BipartiteMatching.MatchingResult mr = bpm.solve();
                    KonigTheorem.KonigResult kr = KonigTheorem.computeMinimumVertexCover(bpm, mr);
                    System.out.println(kr);
                    break;
                }
            }
            InputUtil.pressEnterToContinue(scanner);
        }
    }

    // =========================================================================
    // 12. M5 - NP-COMPLETENESS & APPROXIMATION
    // =========================================================================
    private static void handleM5NPCompleteness(Scanner scanner) {
        while (true) {
            InputUtil.printHeader("MODULE 5 (CO5) - NP-COMPLETENESS & APPROXIMATION");
            System.out.println("1. SAT Solver (DPLL)                - Unit propagation & backtracking");
            System.out.println("2. 3-SAT                            - Treatment and resource constraints");
            System.out.println("3. 3-SAT -> CLIQUE Reduction        - Karp's polynomial reduction");
            System.out.println("4. CLIQUE -> INDEPENDENT SET        - Complement graph transformation");
            System.out.println("5. INDEPENDENT SET -> VERTEX COVER  - Fundamental duality reduction");
            System.out.println("6. Vertex Cover 2-Approximation     - Maximal matching heuristic on conflict network");
            System.out.println("0. Return to Main Menu");
            int choice = InputUtil.readInt(scanner, "Choose item [0-6]: ", 0, 6);
            if (choice == 0) break;

            ThreeSAT threeSat = new ThreeSAT();

            switch (choice) {
                case 1: {
                    System.out.println("\n[DPLL Boolean SAT Solver]");
                    System.out.println("Solving clinical safety constraints formula...");
                    SATSolver.Solution sol = threeSat.solve();
                    System.out.println(sol);
                    break;
                }
                case 2: {
                    System.out.println("\n[3-SAT Clinical Treatment Constraints]");
                    System.out.println("Formulated Hospital Safety Rules (3-CNF):");
                    System.out.print(threeSat.formatClauses());
                    System.out.println("\nEvaluating satisfiability...");
                    SATSolver.Solution sol = threeSat.solve();
                    if (sol.satisfiable) {
                        System.out.println(">> SUCCESS: Clinical protocols are mathematically consistent and SATISFIABLE.");
                    } else {
                        System.out.println(">> CONFLICT: Safety rules contradict. UNSATISFIABLE.");
                    }
                    break;
                }
                case 3: {
                    System.out.println("\n[Reduction: 3-SAT -> CLIQUE]");
                    System.out.println("Theorem: 3-SAT is satisfiable <===> Graph G has a Clique of size k = m (clauses).");
                    ThreeSATToClique.ReductionGraph rg = ThreeSATToClique.reduce(threeSat);
                    System.out.printf("Constructed Reduction Graph:\n  Vertices: %d (3 vertices per clause)\n  Edges   : %d\n  Target k: %d\n",
                            rg.getVertexCount(), rg.getEdgeCount(), rg.targetK);
                    List<ThreeSATToClique.ClauseVertex> clique = ThreeSATToClique.findCliqueOfSizeK(rg, rg.targetK);
                    if (!clique.isEmpty()) {
                        System.out.printf(">> Found Clique of size k = %d (Proves 3-SAT is Satisfiable):\n", clique.size());
                        for (ThreeSATToClique.ClauseVertex cv : clique) {
                            System.out.println("   " + cv);
                        }
                    }
                    break;
                }
                case 4: {
                    System.out.println("\n[Reduction: CLIQUE -> INDEPENDENT SET]");
                    System.out.println("Theorem: S is a Clique in G <===> S is an Independent Set in Complement Graph G_bar.");
                    ThreeSATToClique.ReductionGraph rg = ThreeSATToClique.reduce(threeSat);
                    CliqueToIndependentSet.ComplementGraph cg = CliqueToIndependentSet.reduce(rg.adj, rg.targetK);
                    System.out.printf("Complement Graph G_bar:\n  Vertices: %d\n  Edges in G_bar: %d\n  Target k: %d\n",
                            cg.vertexCount, cg.getEdgeCount(), cg.targetK);

                    List<ThreeSATToClique.ClauseVertex> clique = ThreeSATToClique.findCliqueOfSizeK(rg, rg.targetK);
                    List<Integer> cliqueIndices = new ArrayList<>();
                    for (ThreeSATToClique.ClauseVertex cv : clique) cliqueIndices.add(cv.id);

                    boolean validIS = CliqueToIndependentSet.verifyIndependentSet(cg, cliqueIndices);
                    System.out.printf(">> Independent Set of size %d verified in G_bar: %s\n",
                            cliqueIndices.size(), validIS ? "TRUE (Q.E.D.)" : "FALSE");
                    break;
                }
                case 5: {
                    System.out.println("\n[Reduction: INDEPENDENT SET -> VERTEX COVER]");
                    System.out.println("Theorem: S is an Independent Set in G <===> V \\ S is a Vertex Cover of size |V| - k.");
                    ThreeSATToClique.ReductionGraph rg = ThreeSATToClique.reduce(threeSat);
                    CliqueToIndependentSet.ComplementGraph cg = CliqueToIndependentSet.reduce(rg.adj, rg.targetK);

                    List<ThreeSATToClique.ClauseVertex> clique = ThreeSATToClique.findCliqueOfSizeK(rg, rg.targetK);
                    List<Integer> isIndices = new ArrayList<>();
                    for (ThreeSATToClique.ClauseVertex cv : clique) isIndices.add(cv.id);

                    IndependentSetToVertexCover.VertexCoverReductionResult vcRes =
                            IndependentSetToVertexCover.reduce(cg.complementAdj, isIndices);
                    System.out.println(vcRes);
                    break;
                }
                case 6: {
                    System.out.println("\n[Vertex Cover 2-Approximation for Large Conflict Networks]");
                    System.out.println("Algorithm: Maximal Matching Heuristic | Bound: |C| <= 2 * |OPT|");
                    List<String> units = List.of(
                            "ICU Suite A", "ICU Suite B", "Surgery Room 1", "Surgery Room 2",
                            "Emergency Triage", "CT Scanner Suite", "Isolation Ward 1", "Isolation Ward 2"
                    );
                    int n = units.size();
                    boolean[][] conflictAdj = new boolean[n][n];
                    // Hospital scheduling/resource conflict edges
                    conflictAdj[0][1] = true; conflictAdj[1][0] = true; // Both need ECMO
                    conflictAdj[2][3] = true; conflictAdj[3][2] = true; // Both need Anesthesiology team
                    conflictAdj[0][5] = true; conflictAdj[5][0] = true; // Urgent CT scan transport
                    conflictAdj[4][2] = true; conflictAdj[2][4] = true; // Emergency surgical transfer
                    conflictAdj[6][7] = true; conflictAdj[7][6] = true; // Isolation HVAC barrier conflict

                    VertexCoverApproximation.ApproxCoverResult res = VertexCoverApproximation.approximateVertexCover(conflictAdj, units);
                    System.out.println(res);
                    System.out.println("\nSelected Maximal Matching Edges (causing conflicts):");
                    for (VertexCoverApproximation.ConflictEdge ce : res.maximalMatchingEdges) {
                        System.out.println("  * " + ce);
                    }
                    break;
                }
            }
            InputUtil.pressEnterToContinue(scanner);
        }
    }

    // =========================================================================
    // 13. M6 - RANDOMIZED & PARALLEL ALGORITHMS
    // =========================================================================
    private static void handleM6RandomizedParallel(Scanner scanner) {
        while (true) {
            InputUtil.printHeader("MODULE 6 (CO6) - RANDOMIZED & PARALLEL ALGORITHMS");
            System.out.println("1. Randomized QuickSort - Medical record ranking & sorting");
            System.out.println("2. Reservoir Sampling   - Real-time continuous hospital observation stream");
            System.out.println("3. Miller-Rabin Test    - Cryptographic primality for HIPAA data tokens");
            System.out.println("4. Blelloch Scan        - Parallel prefix sum for patient statistics");
            System.out.println("5. Parallel Reduce      - ForkJoin hospital analytics calculation");
            System.out.println("6. Brent's Theorem      - Parallel processing work/span scalability analysis");
            System.out.println("0. Return to Main Menu");
            int choice = InputUtil.readInt(scanner, "Choose algorithm [0-6]: ", 0, 6);
            if (choice == 0) break;

            switch (choice) {
                case 1: {
                    System.out.println("\n[Randomized QuickSort - Expected O(N log N)]");
                    List<Patient> list = new ArrayList<>(csvManager.getPatients().values());
                    System.out.println("Sorting patients by Age (Ascending)...");
                    RandomizedQuickSort.SortStats stats = RandomizedQuickSort.sort(list, Comparator.comparingInt(Patient::getAge));
                    System.out.printf(">> Sorted %d records in %,d ns using %d comparisons.\n",
                            list.size(), stats.durationNanos, stats.comparisons);
                    for (Patient p : list) {
                        System.out.printf("  Age %2d: %-20s (ID: %s, Condition: %s)\n", p.getAge(), p.getName(), p.getId(), p.getDisease());
                    }
                    break;
                }
                case 2: {
                    System.out.println("\n[Reservoir Sampling (Algorithm R)]");
                    System.out.println("Guarantee: Uniform K / N probability for continuous streaming observations.");
                    int k = InputUtil.readInt(scanner, "Enter reservoir sample size K [3-10]: ", 3, 10);
                    ReservoirSampling rs = new ReservoirSampling(k);
                    List<HospitalObservation> obs = csvManager.getObservations();
                    rs.streamAll(obs);

                    System.out.printf(">> Streamed %d continuous vitals. Reservoir maintained %d uniform samples (p = %.2f%%):\n",
                            rs.getTotalStreamed(), rs.getSample().size(), rs.getCurrentInclusionProbability() * 100.0);
                    for (HospitalObservation o : rs.getSample()) {
                        System.out.println("   " + o);
                    }
                    break;
                }
                case 3: {
                    System.out.println("\n[Miller-Rabin Primality Test]");
                    System.out.println("Complexity: O(k * log^3 N) | Error probability <= 4^(-k)");
                    long testNum = 1000000007L;
                    boolean isP = MillerRabin.isPrime(testNum, 20);
                    System.out.printf("Test number %d: Is Prime? %s\n", testNum, isP);

                    System.out.println("Generating 128-bit cryptographic prime for HIPAA patient record token...");
                    BigInteger key = MillerRabin.generateHospitalSecurityKey(128, 25);
                    System.out.println(">> Generated HIPAA Prime Token: " + key);
                    System.out.println("Verified Prime with 25 rounds: " + MillerRabin.isProbablePrime(key, 25));
                    break;
                }
                case 4: {
                    System.out.println("\n[Blelloch Parallel Prefix Scan]");
                    System.out.println("Work: O(N) | Span: O(log N)");
                    BlellochScan.ScanResult res = analyticsService.scanObservationMetrics();
                    System.out.printf("Computed prefix sum over %d continuous vitals.\n", res.original.length);
                    System.out.printf("Total Sum: %.2f | Tree Work Operations: %d | Span Depth: %d\n",
                            res.totalSum, res.work, res.span);
                    System.out.println("First 8 Original vs Cumulative (Inclusive Scan) values:");
                    for (int i = 0; i < Math.min(8, res.original.length); i++) {
                        System.out.printf("  Index %2d: Vital = %6.2f | Cumulative Total = %8.2f\n", i, res.original[i], res.inclusivePrefix[i]);
                    }
                    break;
                }
                case 5: {
                    System.out.println("\n[Parallel Reduce (ForkJoin Framework)]");
                    System.out.println("Work: O(N) | Span: O(log N)");
                    ParallelReduce.ReductionMetrics ageMetrics = analyticsService.analyzePatientAges();
                    System.out.println("Patient Age Analytics:");
                    System.out.println(ageMetrics);

                    ParallelReduce.ReductionMetrics resMetrics = analyticsService.analyzeResourceInventory();
                    System.out.println("\nResource Inventory Analytics:");
                    System.out.println(resMetrics);
                    break;
                }
                case 6: {
                    System.out.println("\n[Brent's Theorem Scalability Analysis]");
                    System.out.println("Analyzes theoretical parallel processing time T_p <= (W - D) / p + D");
                    double work = 50000.0; // 50,000 microseconds work for 100k batch records
                    double span = 150.0;    // 150 microseconds critical path
                    int[] cores = new int[]{1, 2, 4, 8, 16, 32};
                    BrentTheorem.AnalysisReport report = BrentTheorem.analyze(work, span, cores);
                    System.out.println(report);
                    break;
                }
            }
            InputUtil.pressEnterToContinue(scanner);
        }
    }

    // =========================================================================
    // 14. HOSPITAL ANALYTICS
    // =========================================================================
    private static void handleHospitalAnalytics(Scanner scanner) {
        InputUtil.printHeader("HOSPITAL OPERATIONAL ANALYTICS");
        AnalyticsService.HospitalSummaryReport s = analyticsService.getSummaryCounts();
        System.out.println("Hospital Entity Counters:");
        System.out.printf("  Total Patients       : %d\n", s.totalPatients);
        System.out.printf("  Total Doctors        : %d\n", s.totalDoctors);
        System.out.printf("  Total Departments    : %d\n", s.totalDepartments);
        System.out.printf("  Total Medical Records: %d\n", s.totalMedicalRecords);
        System.out.printf("  Total Lab Reports    : %d\n", s.totalLabReports);
        System.out.printf("  Total Treatments     : %d\n", s.totalTreatments);
        System.out.printf("  Total Resources      : %d\n", s.totalResources);
        System.out.printf("  Total Observations   : %d\n", s.totalObservations);

        System.out.println("\nDepartment Doctor Allocation:");
        for (Map.Entry<String, Integer> e : analyticsService.getDepartmentDoctorCounts().entrySet()) {
            System.out.printf("  %-20s: %d doctors\n", e.getKey(), e.getValue());
        }

        System.out.println("\nDisease Prevalence Ranking:");
        for (Map.Entry<String, Integer> e : analyticsService.getDiseaseFrequency().entrySet()) {
            System.out.printf("  %-30s: %d patients\n", e.getKey(), e.getValue());
        }

        System.out.println("\nTreatment Plan Statuses:");
        for (Map.Entry<String, Integer> e : analyticsService.getTreatmentStatusDistribution().entrySet()) {
            System.out.printf("  %-15s: %d\n", e.getKey(), e.getValue());
        }

        InputUtil.pressEnterToContinue(scanner);
    }

    // =========================================================================
    // 15. VIEW STORED DATA
    // =========================================================================
    private static void handleViewStoredData(Scanner scanner) {
        InputUtil.printHeader("PERSISTENT CSV STORAGE STATUS");
        try {
            System.out.println("Storage Path     : " + csvManager.getCsvPath().toAbsolutePath());
            boolean exists = Files.exists(csvManager.getCsvPath());
            System.out.println("File Exists?     : " + exists);
            if (exists) {
                System.out.println("File Size        : " + Files.size(csvManager.getCsvPath()) + " bytes");
            }
            System.out.println("In-Memory Status :");
            System.out.println("  Patients        : " + csvManager.getPatients().size());
            System.out.println("  Doctors         : " + csvManager.getDoctors().size());
            System.out.println("  Departments     : " + csvManager.getDepartments().size());
            System.out.println("  Medical Records : " + csvManager.getMedicalRecords().size());
            System.out.println("  Lab Reports     : " + csvManager.getLabReports().size());
            System.out.println("  Treatments      : " + csvManager.getTreatments().size());
            System.out.println("  Resources       : " + csvManager.getResources().size());
            System.out.println("  Observations    : " + csvManager.getObservations().size());
            System.out.println("  Total Entities  : " + csvManager.getTotalRecordCount());
        } catch (Exception e) {
            System.out.println("Error reading storage info: " + e.getMessage());
        }
        InputUtil.pressEnterToContinue(scanner);
    }

    // =========================================================================
    // 16. SAVE DATA
    // =========================================================================
    private static void handleSaveData() {
        System.out.println("\nPersisting all in-memory records to " + csvManager.getCsvPath() + "...");
        try {
            csvManager.saveAll();
            System.out.println(">> SUCCESS: Persistent CSV updated atomically! Total records saved: " + csvManager.getTotalRecordCount());
        } catch (IOException e) {
            System.err.println(">> FAILED to save CSV: " + e.getMessage());
        }
    }
}
