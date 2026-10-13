package smarthospital.model;

/**
 * Model representing a Patient in the SmartHospital system.
 */
public class Patient {
    private String id;
    private String name;
    private int age;
    private String gender;
    private String contact;
    private String bloodGroup;
    private String disease;
    private String medicalHistory;

    public Patient() {}

    public Patient(String id, String name, int age, String gender, String contact, 
                   String bloodGroup, String disease, String medicalHistory) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contact = contact;
        this.bloodGroup = bloodGroup;
        this.disease = disease;
        this.medicalHistory = medicalHistory;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-18s | Age: %2d | %-6s | Blood: %-3s | Condition: %-20s | History: %s",
                id, name, age, gender, bloodGroup, disease, medicalHistory);
    }
}
