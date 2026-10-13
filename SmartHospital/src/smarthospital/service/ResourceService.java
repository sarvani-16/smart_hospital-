package smarthospital.service;

import smarthospital.model.MedicalResource;
import smarthospital.persistence.HospitalCSVManager;

import java.io.IOException;
import java.util.*;

/**
 * Service managing hospital medical resource inventory, allocation, and tracking.
 */
public class ResourceService {
    private final HospitalCSVManager csvManager;

    public ResourceService(HospitalCSVManager csvManager) {
        this.csvManager = csvManager;
    }

    public List<MedicalResource> getAllResources() {
        return new ArrayList<>(csvManager.getResources().values());
    }

    public MedicalResource getResourceById(String id) {
        if (id == null) return null;
        return csvManager.getResources().get(id.trim());
    }

    public void addResource(MedicalResource r) throws IllegalArgumentException, IOException {
        if (r.getId() == null || r.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Resource ID cannot be empty.");
        }
        String cleanId = r.getId().trim();
        if (csvManager.getResources().containsKey(cleanId)) {
            throw new IllegalArgumentException("Resource with ID '" + cleanId + "' already exists.");
        }
        if (r.getResourceName() == null || r.getResourceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Resource name cannot be empty.");
        }
        if (r.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        r.setId(cleanId);
        csvManager.saveResource(r);
    }

    public void updateQuantity(String id, int newQty) throws IllegalArgumentException, IOException {
        if (newQty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        MedicalResource res = getResourceById(id);
        if (res == null) {
            throw new IllegalArgumentException("Resource ID '" + id + "' not found.");
        }
        res.setQuantity(newQty);
        res.setStatus(newQty == 0 ? "Exhausted" : "Available");
        csvManager.saveResource(res);
    }

    public boolean allocateResource(String id, int amount) throws IllegalArgumentException, IOException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Allocation amount must be positive.");
        }
        MedicalResource res = getResourceById(id);
        if (res == null) {
            throw new IllegalArgumentException("Resource ID '" + id + "' not found.");
        }
        if (res.getQuantity() < amount) {
            return false; // Insufficient quantity
        }
        boolean ok = res.allocate(amount);
        if (ok) {
            csvManager.saveResource(res);
        }
        return ok;
    }

    public void releaseResource(String id, int amount) throws IllegalArgumentException, IOException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Release amount must be positive.");
        }
        MedicalResource res = getResourceById(id);
        if (res == null) {
            throw new IllegalArgumentException("Resource ID '" + id + "' not found.");
        }
        res.release(amount);
        csvManager.saveResource(res);
    }

    public boolean deleteResource(String id) throws IOException {
        if (id == null) return false;
        return csvManager.deleteRecord("RESOURCE", id.trim());
    }
}
