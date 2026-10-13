package smarthospital.service;

import smarthospital.m6_random_parallel.*;
import smarthospital.model.*;
import smarthospital.persistence.HospitalCSVManager;

import java.util.*;

/**
 * Hospital Analytics Service.
 * Provides high-level operational reporting, disease distribution metrics,
 * resource utilization analytics, and demonstrates M6 parallel/randomized processing.
 */
public class AnalyticsService {

    private final HospitalCSVManager csvManager;

    public AnalyticsService(HospitalCSVManager csvManager) {
        this.csvManager = csvManager;
    }

    public static class HospitalSummaryReport {
        public int totalPatients;
        public int totalDoctors;
        public int totalDepartments;
        public int totalMedicalRecords;
        public int totalLabReports;
        public int totalTreatments;
        public int totalResources;
        public int totalObservations;
    }

    public HospitalSummaryReport getSummaryCounts() {
        HospitalSummaryReport rep = new HospitalSummaryReport();
        rep.totalPatients = csvManager.getPatients().size();
        rep.totalDoctors = csvManager.getDoctors().size();
        rep.totalDepartments = csvManager.getDepartments().size();
        rep.totalMedicalRecords = csvManager.getMedicalRecords().size();
        rep.totalLabReports = csvManager.getLabReports().size();
        rep.totalTreatments = csvManager.getTreatments().size();
        rep.totalResources = csvManager.getResources().size();
        rep.totalObservations = csvManager.getObservations().size();
        return rep;
    }

    public Map<String, Integer> getDepartmentDoctorCounts() {
        Map<String, Integer> counts = new TreeMap<>();
        for (Doctor d : csvManager.getDoctors().values()) {
            counts.put(d.getDepartment(), counts.getOrDefault(d.getDepartment(), 0) + 1);
        }
        return counts;
    }

    public Map<String, Integer> getDiseaseFrequency() {
        Map<String, Integer> freq = new LinkedHashMap<>();
        for (Patient p : csvManager.getPatients().values()) {
            String d = p.getDisease();
            freq.put(d, freq.getOrDefault(d, 0) + 1);
        }
        return freq;
    }

    public Map<String, Integer> getTreatmentStatusDistribution() {
        Map<String, Integer> dist = new TreeMap<>();
        for (Treatment t : csvManager.getTreatments().values()) {
            dist.put(t.getStatus(), dist.getOrDefault(t.getStatus(), 0) + 1);
        }
        return dist;
    }

    /**
     * Aggregates hospital statistics using Parallel Reduce (M6).
     */
    public ParallelReduce.ReductionMetrics analyzePatientAges() {
        List<Patient> patients = new ArrayList<>(csvManager.getPatients().values());
        double[] ages = new double[patients.size()];
        for (int i = 0; i < patients.size(); i++) {
            ages[i] = patients.get(i).getAge();
        }
        return ParallelReduce.compute(ages);
    }

    /**
     * Aggregates total resource stock using Parallel Reduce (M6).
     */
    public ParallelReduce.ReductionMetrics analyzeResourceInventory() {
        List<MedicalResource> resources = new ArrayList<>(csvManager.getResources().values());
        double[] quantities = new double[resources.size()];
        for (int i = 0; i < resources.size(); i++) {
            quantities[i] = resources.get(i).getQuantity();
        }
        return ParallelReduce.compute(quantities);
    }

    /**
     * Aggregates continuous hospital observation telemetry using Blelloch Scan (M6).
     */
    public BlellochScan.ScanResult scanObservationMetrics() {
        List<HospitalObservation> obs = csvManager.getObservations();
        double[] values = new double[obs.size()];
        for (int i = 0; i < obs.size(); i++) {
            values[i] = obs.get(i).getNumericValue();
        }
        return BlellochScan.scan(values);
    }
}
