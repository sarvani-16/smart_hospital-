package smarthospital.service;

import smarthospital.m1_string.*;
import smarthospital.m2_suffix.*;
import smarthospital.model.*;
import smarthospital.persistence.HospitalCSVManager;

import java.util.*;

/**
 * High-performance search service integrating M1 string algorithms and M2 suffix structures
 * across the SmartHospital medical records, diagnostic logs, and patient files.
 */
public class MedicalSearchService {

    private final HospitalCSVManager csvManager;

    public MedicalSearchService(HospitalCSVManager csvManager) {
        this.csvManager = csvManager;
    }

    public static class RecordMatch {
        public final String recordType;
        public final String recordId;
        public final String patientId;
        public final String matchedField;
        public final String snippet;
        public final int matchOffset;

        public RecordMatch(String recordType, String recordId, String patientId, String matchedField, String snippet, int offset) {
            this.recordType = recordType;
            this.recordId = recordId;
            this.patientId = patientId;
            this.matchedField = matchedField;
            this.snippet = snippet;
            this.matchOffset = offset;
        }

        @Override
        public String toString() {
            return String.format("[%s: %s | Patient: %s] Field: %-15s | Offset %2d: \"%s\"",
                    recordType, recordId, (patientId.isEmpty() ? "N/A" : patientId), matchedField, matchOffset, snippet);
        }
    }

    /**
     * KMP Search: Searches disease names and medical terms across all patient profiles and medical records.
     */
    public List<RecordMatch> searchWithKMP(String term) {
        List<RecordMatch> matches = new ArrayList<>();
        if (term == null || term.trim().isEmpty()) return matches;
        String p = term.trim();

        // 1. Search in Patients
        for (Patient patient : csvManager.getPatients().values()) {
            List<Integer> dMatches = KMP.search(patient.getDisease(), p, true);
            for (int idx : dMatches) {
                matches.add(new RecordMatch("PATIENT", patient.getId(), patient.getId(), "Disease", patient.getDisease(), idx));
            }
            List<Integer> hMatches = KMP.search(patient.getMedicalHistory(), p, true);
            for (int idx : hMatches) {
                matches.add(new RecordMatch("PATIENT", patient.getId(), patient.getId(), "MedicalHistory", patient.getMedicalHistory(), idx));
            }
        }

        // 2. Search in Medical Records
        for (MedicalRecord mr : csvManager.getMedicalRecords().values()) {
            List<Integer> diagMatches = KMP.search(mr.getDiagnosis(), p, true);
            for (int idx : diagMatches) {
                matches.add(new RecordMatch("MEDICAL_RECORD", mr.getId(), mr.getPatientId(), "Diagnosis", mr.getDiagnosis(), idx));
            }
            List<Integer> rxMatches = KMP.search(mr.getTreatment(), p, true);
            for (int idx : rxMatches) {
                matches.add(new RecordMatch("MEDICAL_RECORD", mr.getId(), mr.getPatientId(), "Treatment", mr.getTreatment(), idx));
            }
        }

        // 3. Search in Lab Reports
        for (LabReport lr : csvManager.getLabReports().values()) {
            List<Integer> testMatches = KMP.search(lr.getTestName(), p, true);
            for (int idx : testMatches) {
                matches.add(new RecordMatch("LAB_REPORT", lr.getId(), lr.getPatientId(), "TestName", lr.getTestName(), idx));
            }
            List<Integer> resMatches = KMP.search(lr.getTestResult(), p, true);
            for (int idx : resMatches) {
                matches.add(new RecordMatch("LAB_REPORT", lr.getId(), lr.getPatientId(), "TestResult", lr.getTestResult(), idx));
            }
        }

        return matches;
    }

    /**
     * Rabin-Karp Search: Searches diagnostic codes (ICD-10 codes, document IDs, barcodes).
     */
    public List<RecordMatch> searchDiagnosticCodeWithRabinKarp(String code) {
        List<RecordMatch> matches = new ArrayList<>();
        if (code == null || code.trim().isEmpty()) return matches;
        String c = code.trim();

        for (MedicalRecord mr : csvManager.getMedicalRecords().values()) {
            if (mr.getDocumentPath() != null) {
                List<Integer> m = RabinKarp.search(mr.getDocumentPath(), c, true);
                for (int idx : m) {
                    matches.add(new RecordMatch("MEDICAL_RECORD", mr.getId(), mr.getPatientId(), "DocumentPath/ICD", mr.getDocumentPath(), idx));
                }
            }
            if (mr.getDiagnosis() != null) {
                List<Integer> m = RabinKarp.search(mr.getDiagnosis(), c, true);
                for (int idx : m) {
                    matches.add(new RecordMatch("MEDICAL_RECORD", mr.getId(), mr.getPatientId(), "Diagnosis", mr.getDiagnosis(), idx));
                }
            }
        }

        for (LabReport lr : csvManager.getLabReports().values()) {
            if (lr.getDocumentPath() != null) {
                List<Integer> m = RabinKarp.search(lr.getDocumentPath(), c, true);
                for (int idx : m) {
                    matches.add(new RecordMatch("LAB_REPORT", lr.getId(), lr.getPatientId(), "DocumentPath/Barcode", lr.getDocumentPath(), idx));
                }
            }
        }

        return matches;
    }

    /**
     * Aho-Corasick: Multi-keyword simultaneous screening.
     */
    public Map<String, List<RecordMatch>> multiKeywordSearch(List<String> keywords) {
        Map<String, List<RecordMatch>> map = new LinkedHashMap<>();
        for (String kw : keywords) {
            map.put(kw, new ArrayList<>());
        }

        AhoCorasick ac = new AhoCorasick(keywords);

        for (Patient p : csvManager.getPatients().values()) {
            String combined = p.getName() + " | " + p.getDisease() + " | " + p.getMedicalHistory();
            List<AhoCorasick.MatchResult> hits = ac.searchInText(combined);
            for (AhoCorasick.MatchResult hit : hits) {
                map.get(hit.keyword).add(new RecordMatch("PATIENT", p.getId(), p.getId(), "ClinicalProfile", combined, hit.startIndex));
            }
        }

        for (MedicalRecord mr : csvManager.getMedicalRecords().values()) {
            String combined = mr.getDiagnosis() + " | " + mr.getTreatment() + " | " + mr.getMedicalHistory();
            List<AhoCorasick.MatchResult> hits = ac.searchInText(combined);
            for (AhoCorasick.MatchResult hit : hits) {
                map.get(hit.keyword).add(new RecordMatch("MEDICAL_RECORD", mr.getId(), mr.getPatientId(), "ClinicalSummary", combined, hit.startIndex));
            }
        }

        return map;
    }

    /**
     * Builds unified searchable hospital corpus text from all records.
     */
    public String buildHospitalCorpus() {
        StringBuilder sb = new StringBuilder();
        for (Patient p : csvManager.getPatients().values()) {
            sb.append(p.getId()).append(": ").append(p.getName()).append(" - ").append(p.getDisease()).append(". ").append(p.getMedicalHistory()).append("\n");
        }
        for (MedicalRecord mr : csvManager.getMedicalRecords().values()) {
            sb.append(mr.getId()).append(": ").append(mr.getDiagnosis()).append(". Treatment: ").append(mr.getTreatment()).append("\n");
        }
        for (LabReport lr : csvManager.getLabReports().values()) {
            sb.append(lr.getId()).append(": ").append(lr.getTestName()).append(" -> ").append(lr.getTestResult()).append("\n");
        }
        return sb.toString();
    }
}
