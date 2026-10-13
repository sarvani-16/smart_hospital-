package smarthospital.model;

/**
 * Model representing a Laboratory Diagnostic Report.
 */
public class LabReport {
    private String id;
    private String patientId;
    private String doctorId;
    private String testName;
    private String testResult;
    private String status;
    private String documentPath;
    private String timestamp;

    public LabReport() {}

    public LabReport(String id, String patientId, String doctorId, String testName,
                     String testResult, String status, String documentPath, String timestamp) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.testName = testName;
        this.testResult = testResult;
        this.status = status;
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

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return String.format("[%s] Patient: %-6s | Doctor: %-6s | Test: %-18s | Result: %-22s | Status: %-8s | %s",
                id, patientId, doctorId, testName, testResult, status, timestamp);
    }
}
