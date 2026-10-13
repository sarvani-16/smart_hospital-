package smarthospital.model;

/**
 * Model representing a Medical Resource (ICU Beds, Ventilators, MRI, etc.).
 */
public class MedicalResource {
    private String id;
    private String resourceName;
    private String department;
    private int quantity;
    private String status;

    public MedicalResource() {}

    public MedicalResource(String id, String resourceName, String department, int quantity, String status) {
        this.id = id;
        this.resourceName = resourceName;
        this.department = department;
        this.quantity = quantity;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Resource quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean allocate(int amount) {
        if (amount <= 0 || amount > quantity) {
            return false;
        }
        this.quantity -= amount;
        if (this.quantity == 0) {
            this.status = "Exhausted";
        }
        return true;
    }

    public void release(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot release negative quantity.");
        }
        this.quantity += amount;
        if (this.quantity > 0 && "Exhausted".equalsIgnoreCase(this.status)) {
            this.status = "Available";
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | Dept: %-15s | Available Qty: %3d | Status: %s",
                id, resourceName, department, quantity, status);
    }
}
