package smarthospital.model;

/**
 * Model representing continuous hospital telemetry/observation data.
 */
public class HospitalObservation {
    private String id;
    private String patientId;
    private String testName;
    private String testResult;
    private String status;
    private String timestamp;

    public HospitalObservation() {}

    public HospitalObservation(String id, String patientId, String testName, 
                               String testResult, String status, String timestamp) {
        this.id = id;
        this.patientId = patientId;
        this.testName = testName;
        this.testResult = testResult;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Extracts the leading numeric value from the testResult string for mathematical operations.
     */
    public double getNumericValue() {
        if (testResult == null || testResult.isEmpty()) return 0.0;
        try {
            String clean = testResult.split("[^0-9.]")[0].trim();
            if (!clean.isEmpty()) {
                return Double.parseDouble(clean);
            }
        } catch (NumberFormatException ignored) {}
        return 0.0;
    }

    @Override
    public String toString() {
        return String.format("[%s] Patient: %-6s | Vital/Param: %-16s | Val: %-12s | Status: %-8s | %s",
                id, patientId, testName, testResult, status, timestamp);
    }
}
