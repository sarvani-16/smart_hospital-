package smarthospital.model;

/**
 * Model representing a Treatment plan and status.
 */
public class Treatment {
    private String id;
    private String patientId;
    private String doctorId;
    private String diagnosis;
    private String treatmentPlan;
    private String status;
    private String timestamp;

    public Treatment() {}

    public Treatment(String id, String patientId, String doctorId, String diagnosis,
                     String treatmentPlan, String status, String timestamp) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
        this.treatmentPlan = treatmentPlan;
        this.status = status;
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

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] Patient: %-6s | Doctor: %-6s | Diagnosis: %-20s | Status: %-10s | Date: %s\n       Regimen: %s",
                id, patientId, doctorId, diagnosis, status, timestamp, treatmentPlan);
    }
}
