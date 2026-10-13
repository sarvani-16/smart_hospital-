package smarthospital.model;

/**
 * Model representing a Medical Record in the SmartHospital system.
 */
public class MedicalRecord {
    private String id;
    private String patientId;
    private String doctorId;
    private String diagnosis;
    private String treatment;
    private String medicalHistory;
    private String documentPath;
    private String timestamp;

    public MedicalRecord() {}

    public MedicalRecord(String id, String patientId, String doctorId, String diagnosis,
                         String treatment, String medicalHistory, String documentPath, String timestamp) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.medicalHistory = medicalHistory;
        this.documentPath = documentPath;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] Patient: %-6s | Doctor: %-6s | Diagnosis: %-22s | Code: %-12s | Date: %s\n       Rx: %s",
                id, patientId, doctorId, diagnosis, (documentPath == null ? "N/A" : documentPath), timestamp, treatment);
    }
}
