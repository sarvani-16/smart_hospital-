package smarthospital.util;

import smarthospital.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Fictional sample data generator for SmartHospital.
 * Used ONLY when data/hospital_data.csv is created for the very first time.
 */
public class SampleData {

    public static List<Department> getInitialDepartments() {
        List<Department> list = new ArrayList<>();
        list.add(new Department("DEP01", "Cardiology", "Heart diseases, ECG, angiography and interventions", "D201"));
        list.add(new Department("DEP02", "Neurology", "Brain, spine, stroke management and neuro-rehabilitation", "D202"));
        list.add(new Department("DEP03", "Orthopedics", "Bone fractures, joint replacements and trauma care", "D203"));
        list.add(new Department("DEP04", "Oncology", "Cancer diagnostics, chemotherapy, and tumor board", "D204"));
        list.add(new Department("DEP05", "Emergency Medicine", "Acute trauma, resuscitations and triage operations", "D205"));
        list.add(new Department("DEP06", "Pulmonology", "Respiratory tract infections, asthma, COPD, and ventilator support", "D206"));
        return list;
    }

    public static List<Doctor> getInitialDoctors() {
        List<Doctor> list = new ArrayList<>();
        list.add(new Doctor("D201", "Dr. Rajesh Gupta", "Interventional Cardiology", "Cardiology", "+91-98110-12345"));
        list.add(new Doctor("D202", "Dr. Sunita Verma", "Neurophysiology & Stroke", "Neurology", "+91-98220-23456"));
        list.add(new Doctor("D203", "Dr. Vikram Singh", "Orthopedic Surgery", "Orthopedics", "+91-98330-34567"));
        list.add(new Doctor("D204", "Dr. Pooja Nair", "Medical Oncology", "Oncology", "+91-98440-45678"));
        list.add(new Doctor("D205", "Dr. Arjun Reddy", "Trauma & Critical Care", "Emergency Medicine", "+91-98550-56789"));
        list.add(new Doctor("D206", "Dr. Sneha Patel", "Pulmonology & Critical Care", "Pulmonology", "+91-98660-67890"));
        list.add(new Doctor("D207", "Dr. Amit Joshi", "Nephrology & Dialysis", "Nephrology", "+91-98770-78901"));
        list.add(new Doctor("D208", "Dr. Meena Das", "General Internal Medicine", "Emergency Medicine", "+91-98880-89012"));
        return list;
    }

    public static List<Patient> getInitialPatients() {
        List<Patient> list = new ArrayList<>();
        list.add(new Patient("P101", "Rahul Kumar", 45, "Male", "+91-91001-11001", "B+", "Type-2 Diabetes Mellitus", "Mild hypertension; allergy to penicillin"));
        list.add(new Patient("P102", "Priya Sharma", 32, "Female", "+91-91002-22002", "O+", "Bronchial Asthma", "Childhood asthma; seasonal allergic rhinitis"));
        list.add(new Patient("P103", "Ananya Rao", 58, "Female", "+91-91003-33003", "A+", "Coronary Artery Disease", "Hyperlipidemia; stent placed in 2021"));
        list.add(new Patient("P104", "Kiran Kumar", 29, "Male", "+91-91004-44004", "AB+", "Acute Tibia Fracture", "No prior medical history; road traffic collision"));
        list.add(new Patient("P105", "Suresh Menon", 67, "Male", "+91-91005-55005", "O-", "Acute Ischemic Stroke", "Chronic atrial fibrillation; smoker for 30 years"));
        list.add(new Patient("P106", "Kavita Iyer", 52, "Female", "+91-91006-66006", "B-", "Invasive Breast Carcinoma", "Family history of maternal breast cancer; non-smoker"));
        list.add(new Patient("P107", "Rohan Roy", 38, "Male", "+91-91007-77007", "A-", "Chronic Kidney Disease Stage 3", "Glomerulonephritis diagnosed 2020; ACE-inhibitor therapy"));
        list.add(new Patient("P108", "Deepa Nair", 24, "Female", "+91-91008-88008", "AB-", "Community Acquired Pneumonia", "Asthma flare-up; persistent fever and productive cough"));
        list.add(new Patient("P109", "Manoj Saxena", 61, "Male", "+91-91009-99009", "O+", "Severe Cardiac Arrhythmia", "Ventricular tachycardia; history of myocardial infarction"));
        list.add(new Patient("P110", "Lakshmi Pillai", 73, "Female", "+91-91010-00110", "B+", "Degenerative Osteoarthritis", "Bilateral knee degeneration; chronic joint pain"));
        list.add(new Patient("P111", "Harish Chawla", 41, "Male", "+91-91011-11221", "A+", "Acute Appendicitis", "Sudden right lower quadrant abdominal pain"));
        list.add(new Patient("P112", "Geeta Mukherjee", 50, "Female", "+91-91012-22332", "O+", "Severe Hypertension Crisis", "Hypertensive encephalopathy; irregular medication compliance"));
        return list;
    }

    public static List<MedicalRecord> getInitialMedicalRecords() {
        List<MedicalRecord> list = new ArrayList<>();
        list.add(new MedicalRecord("MR301", "P101", "D208", "Type-2 Diabetes Mellitus with Neuropathy", "Metformin 500mg BD; Pregabalin 75mg HS", "Metabolic syndrome diagnosed 2018", "ICD10-E11.4", "2026-09-01 09:30"));
        list.add(new MedicalRecord("MR302", "P102", "D206", "Acute Exacerbation of Bronchial Asthma", "Salbutamol nebulization; Budesonide inhaler 200mcg", "Atopic asthma since age 10", "ICD10-J45.9", "2026-09-02 11:15"));
        list.add(new MedicalRecord("MR303", "P103", "D201", "Unstable Angina and Ischemic Heart Disease", "Atorvastatin 40mg; Aspirin 75mg; Clopidogrel 75mg", "Prior LAD stent implantation", "ICD10-I20.0", "2026-09-03 14:00"));
        list.add(new MedicalRecord("MR304", "P104", "D203", "Closed Comminuted Tibia-Fibula Fracture", "Closed reduction internal fixation (CRIF) with intramedullary nail", "Orthopedic trauma post motor collision", "ICD10-S82.2", "2026-09-04 16:45"));
        list.add(new MedicalRecord("MR305", "P105", "D202", "Acute Middle Cerebral Artery Ischemic Stroke", "IV Thrombolysis Alteplase; Neuro-monitoring in Neuro-ICU", "Atrial fibrillation; irregular anticoagulation", "ICD10-I63.4", "2026-09-05 08:20"));
        list.add(new MedicalRecord("MR306", "P106", "D204", "Stage IIB Infiltrating Ductal Carcinoma", "Neoadjuvant Chemotherapy AC-T protocol followed by lumpectomy", "ER+/PR+ HER2 negative breast cancer", "ICD10-C50.9", "2026-09-06 10:10"));
        list.add(new MedicalRecord("MR307", "P107", "D207", "Chronic Kidney Disease with Proteinuria", "Telmisartan 40mg; Dietary protein restriction; Sodium bicarbonate", "Progressive decline in eGFR over 3 years", "ICD10-N18.3", "2026-09-07 12:30"));
        list.add(new MedicalRecord("MR308", "P108", "D206", "Bilateral Lobar Pneumonia with Hypoxemia", "Ceftriaxone 1g IV BD; Azithromycin 500mg OD; Oxygen support", "Asthmatic; acute high fever and pleuritic pain", "ICD10-J18.9", "2026-09-08 15:00"));
        list.add(new MedicalRecord("MR309", "P109", "D201", "Ventricular Tachycardia and Cardiomyopathy", "Amiodarone infusion; Implantable Cardioverter Defibrillator (ICD) planned", "Prior anterolateral myocardial infarction", "ICD10-I47.2", "2026-09-09 17:20"));
        list.add(new MedicalRecord("MR310", "P110", "D203", "Severe Bilateral Tricompartmental Osteoarthritis", "Total Knee Arthroplasty (TKA); Physiotherapy protocol", "Progressive mobility restriction over 5 years", "ICD10-M17.1", "2026-09-10 10:50"));
        list.add(new MedicalRecord("MR311", "P111", "D205", "Acute Suppurative Appendicitis", "Emergency Laparoscopic Appendectomy; IV Cefotaxime", "No previous surgical history", "ICD10-K35.8", "2026-09-11 19:30"));
        return list;
    }

    public static List<LabReport> getInitialLabReports() {
        List<LabReport> list = new ArrayList<>();
        list.add(new LabReport("L401", "P101", "D208", "Glycated Hemoglobin (HbA1c)", "8.4% (Poor Control)", "Completed", "LAB-DOC-401", "2026-09-01 10:30"));
        list.add(new LabReport("L402", "P102", "D206", "Arterial Blood Gas (ABG)", "pH 7.34, pO2 68 mmHg, pCO2 46 mmHg", "Completed", "LAB-DOC-402", "2026-09-02 12:00"));
        list.add(new LabReport("L403", "P103", "D201", "High-Sensitivity Cardiac Troponin-I", "0.48 ng/mL (Significantly Elevated)", "Completed", "LAB-DOC-403", "2026-09-03 14:40"));
        list.add(new LabReport("L404", "P104", "D203", "Digital Radiography Right Lower Leg", "Complete transverse fracture of distal third tibia", "Completed", "LAB-DOC-404", "2026-09-04 17:15"));
        list.add(new LabReport("L405", "P105", "D202", "Non-Contrast Brain CT & Diffusion MRI", "Hyperdense MCA sign; Acute ischemic core 28ml", "Completed", "LAB-DOC-405", "2026-09-05 09:00"));
        list.add(new LabReport("L406", "P106", "D204", "Core Needle Biopsy & IHC Panel", "Grade 2 IDC; ER 90%, PR 75%, HER2 1+ (Negative)", "Completed", "LAB-DOC-406", "2026-09-06 11:30"));
        list.add(new LabReport("L407", "P107", "D207", "Serum Creatinine and eGFR", "Creatinine 2.8 mg/dL; eGFR 38 mL/min/1.73m2", "Completed", "LAB-DOC-407", "2026-09-07 13:10"));
        list.add(new LabReport("L408", "P108", "D206", "Sputum Culture & PCR Multiplex", "Streptococcus pneumoniae positive; Sensitive to Ceftriaxone", "Completed", "LAB-DOC-408", "2026-09-08 16:30"));
        list.add(new LabReport("L409", "P109", "D201", "12-Lead Holter ECG Telemetry", "Frequent non-sustained ventricular tachycardia runs", "Completed", "LAB-DOC-409", "2026-09-09 18:00"));
        list.add(new LabReport("L410", "P111", "D205", "Abdominal Ultrasonography", "Aperistaltic blind-ending tubular structure, 8.5mm diameter", "Completed", "LAB-DOC-410", "2026-09-11 20:15"));
        return list;
    }

    public static List<Treatment> getInitialTreatments() {
        List<Treatment> list = new ArrayList<>();
        list.add(new Treatment("T501", "P101", "D208", "Type-2 Diabetes Mellitus", "Oral hypoglycemic therapy with lifestyle modification and glucose logging", "Active", "2026-09-01 11:00"));
        list.add(new Treatment("T502", "P102", "D206", "Bronchial Asthma", "Inhaled corticosteroid dual therapy with rescue bronchodilator", "Active", "2026-09-02 12:30"));
        list.add(new Treatment("T503", "P103", "D201", "Coronary Artery Disease", "Dual antiplatelet therapy, high-intensity statin, coronary angiogram", "Active", "2026-09-03 15:30"));
        list.add(new Treatment("T504", "P104", "D203", "Acute Tibia Fracture", "Surgical internal fixation and post-op physical rehabilitation", "Completed", "2026-09-04 18:00"));
        list.add(new Treatment("T505", "P105", "D202", "Acute Ischemic Stroke", "Neuro-critical care, intravenous thrombolytic, secondary prevention", "Active", "2026-09-05 10:00"));
        list.add(new Treatment("T506", "P106", "D204", "Invasive Breast Carcinoma", "Cycle 1 Chemotherapy (Doxorubicin + Cyclophosphamide)", "Active", "2026-09-06 13:00"));
        list.add(new Treatment("T507", "P107", "D207", "Chronic Kidney Disease Stage 3", "Renoprotective regimen, blood pressure control, nephrology follow-up", "Active", "2026-09-07 14:00"));
        list.add(new Treatment("T508", "P108", "D206", "Community Acquired Pneumonia", "7-day course targeted intravenous antimicrobial therapy", "Active", "2026-09-08 17:00"));
        list.add(new Treatment("T509", "P109", "D201", "Severe Cardiac Arrhythmia", "Antiarrhythmic titration and ICD surgery implantation scheduling", "Active", "2026-09-09 19:00"));
        return list;
    }

    public static List<MedicalResource> getInitialResources() {
        List<MedicalResource> list = new ArrayList<>();
        list.add(new MedicalResource("R601", "ICU Beds", "Emergency Medicine", 18, "Available"));
        list.add(new MedicalResource("R602", "General Beds", "General Ward", 120, "Available"));
        list.add(new MedicalResource("R603", "Mechanical Ventilators", "Pulmonology", 14, "Available"));
        list.add(new MedicalResource("R604", "Blood Units (O+)", "Blood Bank", 45, "Available"));
        list.add(new MedicalResource("R605", "Blood Units (A+)", "Blood Bank", 32, "Available"));
        list.add(new MedicalResource("R606", "Blood Units (B+)", "Blood Bank", 28, "Available"));
        list.add(new MedicalResource("R607", "MRI 3.0T Scanner", "Radiology", 2, "Available"));
        list.add(new MedicalResource("R608", "128-Slice CT Scanner", "Radiology", 3, "Available"));
        list.add(new MedicalResource("R609", "Digital ECG Machines", "Cardiology", 10, "Available"));
        list.add(new MedicalResource("R610", "Digital X-Ray Units", "Radiology", 6, "Available"));
        list.add(new MedicalResource("R611", "Advanced Life Support Ambulance", "Emergency Medicine", 5, "Available"));
        list.add(new MedicalResource("R612", "Modular Operating Rooms", "Surgery", 8, "Available"));
        return list;
    }

    public static List<HospitalObservation> getInitialObservations() {
        List<HospitalObservation> list = new ArrayList<>();
        list.add(new HospitalObservation("OBS701", "P101", "Blood Glucose", "168 mg/dL", "Elevated", "2026-09-12 08:00"));
        list.add(new HospitalObservation("OBS702", "P101", "Systolic BP", "138 mmHg", "Elevated", "2026-09-12 08:05"));
        list.add(new HospitalObservation("OBS703", "P102", "Oxygen Saturation (SpO2)", "91%", "Critical", "2026-09-12 08:15"));
        list.add(new HospitalObservation("OBS704", "P102", "Heart Rate", "104 bpm", "Elevated", "2026-09-12 08:20"));
        list.add(new HospitalObservation("OBS705", "P103", "Heart Rate", "88 bpm", "Normal", "2026-09-12 08:30"));
        list.add(new HospitalObservation("OBS706", "P103", "Systolic BP", "142 mmHg", "Elevated", "2026-09-12 08:35"));
        list.add(new HospitalObservation("OBS707", "P104", "Pain Score (VAS)", "6 / 10", "Normal", "2026-09-12 09:00"));
        list.add(new HospitalObservation("OBS708", "P105", "Glasgow Coma Scale", "12 / 15", "Critical", "2026-09-12 09:15"));
        list.add(new HospitalObservation("OBS709", "P105", "Systolic BP", "165 mmHg", "Critical", "2026-09-12 09:20"));
        list.add(new HospitalObservation("OBS710", "P106", "Body Temperature", "37.2 C", "Normal", "2026-09-12 10:00"));
        list.add(new HospitalObservation("OBS711", "P107", "Urine Output", "45 mL/hr", "Normal", "2026-09-12 10:30"));
        list.add(new HospitalObservation("OBS712", "P108", "Oxygen Saturation (SpO2)", "93%", "Elevated", "2026-09-12 11:00"));
        list.add(new HospitalObservation("OBS713", "P108", "Body Temperature", "38.9 C", "Critical", "2026-09-12 11:05"));
        list.add(new HospitalObservation("OBS714", "P109", "Heart Rate", "136 bpm", "Critical", "2026-09-12 11:30"));
        list.add(new HospitalObservation("OBS715", "P110", "Systolic BP", "124 mmHg", "Normal", "2026-09-12 12:00"));
        list.add(new HospitalObservation("OBS716", "P111", "White Blood Count", "14800 /uL", "Elevated", "2026-09-12 12:30"));
        list.add(new HospitalObservation("OBS717", "P112", "Systolic BP", "192 mmHg", "Critical", "2026-09-12 13:00"));
        list.add(new HospitalObservation("OBS718", "P112", "Heart Rate", "112 bpm", "Elevated", "2026-09-12 13:05"));
        return list;
    }
}
