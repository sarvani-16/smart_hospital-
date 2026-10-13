package smarthospital.model;

/**
 * Model representing a Doctor in the SmartHospital system.
 */
public class Doctor {
    private String id;
    private String name;
    private String specialization;
    private String department;
    private String contact;

    public Doctor() {}

    public Doctor(String id, String name, String specialization, String department, String contact) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.department = department;
        this.contact = contact;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return String.format("[%s] Dr. %-18s | Spec: %-20s | Dept: %-15s | Contact: %s",
                id, name, specialization, department, contact);
    }
}
