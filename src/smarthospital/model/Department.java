package smarthospital.model;

/**
 * Model representing a Hospital Department.
 */
public class Department {
    private String id;
    private String name;
    private String description;
    private String headDoctorId;

    public Department(String id, String name, String description, String headDoctorId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.headDoctorId = headDoctorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHeadDoctorId() {
        return headDoctorId;
    }

    public void setHeadDoctorId(String headDoctorId) {
        this.headDoctorId = headDoctorId;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Head Doctor: %s | Info: %s", id, name, 
                (headDoctorId == null || headDoctorId.isEmpty()) ? "None" : headDoctorId, description);
    }
}
