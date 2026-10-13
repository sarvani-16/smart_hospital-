# SMART HOSPITAL – Hospital Resource & Information System

[![Java 17](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://openjdk.org/)
[![Status](https://img.shields.io/badge/Status-Production%20Ready-brightgreen.svg)]()
[![Course](https://img.shields.io/badge/Course-Data%20Structures%20%26%20Algorithms-orange.svg)]()
[![Storage](https://img.shields.io/badge/Storage-Persistent%20RFC--4180%20CSV-purple.svg)]()

> **A Complete, Practical, Menu-Driven, and Persistent College DSA Capstone Project**  
> Implementing all six core DSA modules (**M1–M6**) across realistic hospital information retrieval, clinical bioinformatics, resource allocation, and real-time medical analytics.

---

## 1. Problem Statement

Modern healthcare institutions handle continuous streams of heterogeneous data—patient electronic health records (EHR), multi-department diagnostic reports, medical supply pipelines, doctor on-call schedules, clinical contraindications, and ICU sensor telemetry. Efficient operation requires:
- **Instant retrieval** of disease terms and diagnostic codes without scanning entire databases linearly.
- **Topological and suffix-based indexing** of clinical narrative documents.
- **Fuzzy matching and genomic sequence alignment** for diagnosis and pathogen identification.
- **Optimal network flow distribution** for life-saving medical resources (oxygen, blood, ICU beds).
- **Automated clinical constraint verification** and NP-complete reduction proofs for treatment safety.
- **High-throughput parallel analytics and unbiased random sampling** of streaming patient vitals.

**SmartHospital** bridges the gap between theoretical Data Structures & Algorithms (DSA) and real-world healthcare engineering.

---

## 2. System Architecture

The application is structured into decoupled, modular layers:
1. **Model Layer (`smarthospital.model`)**: Encapsulates clinical domain entities (`Patient`, `Doctor`, `Department`, `MedicalRecord`, `LabReport`, `Treatment`, `MedicalResource`, `HospitalObservation`).
2. **Persistence Engine (`smarthospital.persistence`)**: Atomic, RFC-4180 compliant CSV storage in `data/hospital_data.csv`.
3. **Service Layer (`smarthospital.service`)**: Business logic, relational referential integrity enforcement, and high-level medical workflows.
4. **DSA Algorithmic Core (`smarthospital.m1_string` to `m6_random_parallel`)**: 27 fully implemented, non-placeholder algorithms solving hospital challenges.
5. **Presentation Layer (`smarthospital.Main`, `InputUtil`)**: Fail-safe, crash-resistant interactive console interface.

```
                              [ User Console / CLI ]
                                         │
                                         ▼
                               [ smarthospital.Main ]
                                         │
            ┌────────────────────────────┼────────────────────────────┐
            ▼                            ▼                            ▼
   [ Management Services ]       [ Medical Search ]          [ Analytics Service ]
    - PatientService             - Cross-record query         - Hospital Summary
    - DoctorService              - Lexicon suggest            - Resource audit
    - MedicalRecordService                                    - Vital stream stats
    - LabReportService
    - ResourceService
            │                            │                            │
            └────────────────────────────┼────────────────────────────┘
                                         │
                   ┌─────────────────────┴─────────────────────┐
                   ▼                                           ▼
       [ Algorithmic Core M1-M6 ]                 [ HospitalCSVManager ]
       • M1: Strings (KMP, Z, RK, AC)             • Atomic temp file replace
       • M2: Suffixes (SA, SA-IS, LCP, Tree, SAM) • RFC-4180 quoting
       • M3: DP (Edit Dist, NW, SW, Bitmask)      • Safe initial seeding
       • M4: Flow (FF, EK, Dinic, BPM, Konig)     • Single master CSV
       • M5: NP (SAT, 3-SAT, Reductions, Approx)
       • M6: Parallel/Random (QSort, Reservoir,
             Miller-Rabin, Scan, Reduce, Brent)
                                                               │
                                                               ▼
                                                  [ data/hospital_data.csv ]
```

---

## 3. Persistent CSV Storage Engine

The project strictly follows permanent file-based persistence using a single master file: `data/hospital_data.csv`.

### CSV Header (22 Attributes):
```csv
recordType,id,patientId,doctorId,name,age,gender,contact,bloodGroup,disease,medicalHistory,department,specialization,diagnosis,treatment,testName,testResult,resourceName,quantity,status,documentPath,timestamp
```

### Key Persistence Rules:
1. **First Execution**:
   - Detects absence of `data/hospital_data.csv`.
   - Creates the `data/` directory.
   - Generates the file, writes the header, and seeds initial realistic fictional records (12 patients, 8 doctors, 6 departments, 11 medical records, 10 lab reports, 9 treatments, 12 resources, 18 telemetry observations).
   - Saves atomically.
2. **Subsequent Executions**:
   - Detects existing file.
   - **Never overwrites or recreates the file.**
   - **Never re-seeds duplicate initial data.**
   - Reads existing records directly into Java collections (`LinkedHashMap`, `ArrayList`).
3. **Atomic Writes**:
   - Updates write to a temporary file (`hospital_data.csv.tmp`) and perform an atomic filesystem replacement (`Files.move` with `ATOMIC_MOVE`), eliminating file corruption risks upon unexpected power loss or crashes.
4. **RFC-4180 Quoting**:
   - Fields containing commas, quotation marks, or newlines are escaped with double quotes (`"..."` and `""`).

---

## 4. DSA Module Mapping (M1 – M6)

| Module | Algorithm | Hospital Functionality | Time Complexity | Space Complexity |
| :--- | :--- | :--- | :--- | :--- |
| **M1** | **KMP (Knuth-Morris-Pratt)** | Searching disease names and medical terms across EHR notes | $O(N + M)$ | $O(M)$ |
| **M1** | **Z-Function** | Detecting repeated medical phrases and boilerplate notes | $O(N)$ | $O(N)$ |
| **M1** | **Rabin-Karp** | Fast diagnostic-code & ICD-10 identifier matching with rolling hash | Average $O(N + M)$ | $O(1)$ |
| **M1** | **Aho-Corasick** | Simultaneous multi-pattern screening for acute clinical alerts | $O(N + K)$ | $O(M \cdot \Sigma)$ |
| **M2** | **Suffix Array** | Indexing medical records for multiple fast binary-search queries | $O(N \log^2 N)$ / $O(M \log N)$ | $O(N)$ |
| **M2** | **SA-IS** | Linear-time $O(N)$ Suffix Array construction for large-scale clinical reports | $O(N)$ | $O(N)$ |
| **M2** | **LCP Array** | Substring frequency analysis and counting distinct clinical phrases | $O(N)$ | $O(N)$ |
| **M2** | **Kasai Algorithm** | $O(N)$ linear construction of LCP array from Suffix Array | $O(N)$ | $O(N)$ |
| **M2** | **Suffix Tree** | Topological substring retrieval & occurrence localization | $O(M + K)$ | $O(N)$ |
| **M2** | **Suffix Automaton (DAWG)** | Minimal deterministic automaton for instant $O(M)$ substring lookup | $O(M)$ | $O(N)$ |
| **M3** | **Levenshtein Distance** | Medical-term spelling correction against hospital vocabulary | $O(M \cdot N)$ | $O(M \cdot N)$ |
| **M3** | **Damerau-Levenshtein** | Transposition-aware spelling correction for clinical typos | $O(M \cdot N)$ | $O(M \cdot N)$ |
| **M3** | **Weighted Edit Distance** | Record error correction with OCR-digit and phonetic cost weights | $O(M \cdot N)$ | $O(M \cdot N)$ |
| **M3** | **Needleman-Wunsch** | Global biological sequence alignment of pathogen/patient genomes | $O(M \cdot N)$ | $O(M \cdot N)$ |
| **M3** | **Smith-Waterman** | Local sequence alignment for discovering conserved oncogene motifs | $O(M \cdot N)$ | $O(M \cdot N)$ |
| **M3** | **Bitmask DP** | Optimal diagnostic-test combination selection minimizing cost | $O(T \cdot 2^N)$ | $O(2^N)$ |
| **M4** | **Ford-Fulkerson** | Medical resource distribution max flow via DFS augmenting paths | $O(E \cdot \vert f_{\max}\vert)$ | $O(V^2)$ |
| **M4** | **Edmonds-Karp** | BFS shortest augmenting paths for hospital supply pipelines | $O(V \cdot E^2)$ | $O(V + E)$ |
| **M4** | **Dinic's Algorithm** | Level graph max flow & min-cut bottleneck detection for hospital networks | $O(V^2 \cdot E)$ | $O(V + E)$ |
| **M4** | **Bipartite Matching** | Doctor-to-Patient / Doctor-to-Case assignment via Hopcroft-Karp | $O(E \sqrt{V})$ | $O(V + E)$ |
| **M4** | **König's Theorem** | Minimum vertex cover from max matching for clinical conflict coverage | $O(V + E)$ | $O(V + E)$ |
| **M5** | **DPLL SAT Solver** | Backtracking Boolean satisfiability with unit propagation | Worst $O(2^N)$ | $O(V + C)$ |
| **M5** | **3-SAT** | Treatment & resource safety constraints formulated in 3-CNF | NP-Complete | $O(C)$ |
| **M5** | **3-SAT $\to$ CLIQUE** | Polynomial reduction from 3-SAT to CLIQUE ($k = m$) | $O(m^2)$ | $O(m^2)$ |
| **M5** | **CLIQUE $\to$ IND. SET** | Complement graph transformation $G \to \overline{G}$ | $O(V^2)$ | $O(V^2)$ |
| **M5** | **IND. SET $\to$ VERTEX COVER** | Fundamental duality transformation ($k' = \vert V\vert - k$) | $O(V + E)$ | $O(V)$ |
| **M5** | **Vertex Cover 2-Approx** | Maximal matching heuristic for large hospital conflict networks | $O(V + E)$ | $O(V + E)$ |
| **M6** | **Randomized QuickSort** | Medical record ranking avoiding adversarial quadratic worst-case | Expected $O(N \log N)$ | $O(\log N)$ |
| **M6** | **Reservoir Sampling** | Real-time uniform $K/N$ sampling of streaming ICU telemetry | $O(N)$ single pass | $O(K)$ |
| **M6** | **Miller-Rabin** | Cryptographic primality testing for HIPAA patient data encryption tokens | $O(k \log^3 N)$ | $O(\log N)$ |
| **M6** | **Blelloch Scan** | Work-efficient parallel prefix sum for patient statistics | Work $O(N)$, Span $O(\log N)$ | $O(N)$ |
| **M6** | **Parallel Reduce** | Multi-threaded ForkJoin reduction for aggregate hospital analytics | Work $O(N)$, Span $O(\log N)$ | $O(\log N)$ |
| **M6** | **Brent's Theorem** | Scalability bound $T_p \le (W - D)/p + D$ for hospital batch processing | $O(1)$ analytical | $O(1)$ |

---

## 5. Folder Structure

```
SmartHospital/
├── data/
│   └── hospital_data.csv          # Permanent persistent master CSV storage
├── bin/                           # Compiled Java bytecode
├── src/
│   └── smarthospital/
│       ├── Main.java              # Interactive console UI and menu controller
│       │
│       ├── model/                 # Domain entity data classes
│       │   ├── Patient.java
│       │   ├── Doctor.java
│       │   ├── Department.java
│       │   ├── MedicalRecord.java
│       │   ├── LabReport.java
│       │   ├── Treatment.java
│       │   ├── MedicalResource.java
│       │   └── HospitalObservation.java
│       │
│       ├── persistence/
│       │   └── HospitalCSVManager.java # RFC-4180 parser & atomic persistence
│       │
│       ├── service/               # Core business services
│       │   ├── PatientService.java
│       │   ├── DoctorService.java
│       │   ├── MedicalRecordService.java
│       │   ├── LabReportService.java
│       │   ├── ResourceService.java
│       │   ├── MedicalSearchService.java
│       │   └── AnalyticsService.java
│       │
│       ├── m1_string/             # String Algorithms
│       │   ├── KMP.java
│       │   ├── ZFunction.java
│       │   ├── RabinKarp.java
│       │   └── AhoCorasick.java
│       │
│       ├── m2_suffix/             # Suffix Structures
│       │   ├── SuffixArray.java
│       │   ├── SAIS.java
│       │   ├── LCPArray.java
│       │   ├── KasaiAlgorithm.java
│       │   ├── SuffixTree.java
│       │   └── SuffixAutomaton.java
│       │
│       ├── m3_dynamic/            # Advanced Dynamic Programming
│       │   ├── LevenshteinDistance.java
│       │   ├── DamerauLevenshtein.java
│       │   ├── WeightedEditDistance.java
│       │   ├── NeedlemanWunsch.java
│       │   ├── SmithWaterman.java
│       │   └── BitmaskDP.java
│       │
│       ├── m4_flow/               # Network Flow Algorithms
│       │   ├── FordFulkerson.java
│       │   ├── EdmondsKarp.java
│       │   ├── Dinic.java
│       │   ├── BipartiteMatching.java
│       │   └── KonigTheorem.java
│       │
│       ├── m5_np/                 # NP-Completeness & Reductions
│       │   ├── SATSolver.java
│       │   ├── ThreeSAT.java
│       │   ├── ThreeSATToClique.java
│       │   ├── CliqueToIndependentSet.java
│       │   ├── IndependentSetToVertexCover.java
│       │   └── VertexCoverApproximation.java
│       │
│       ├── m6_random_parallel/    # Randomized & Parallel Algorithms
│       │   ├── RandomizedQuickSort.java
│       │   ├── ReservoirSampling.java
│       │   ├── MillerRabin.java
│       │   ├── BlellochScan.java
│       │   ├── ParallelReduce.java
│       │   └── BrentTheorem.java
│       │
│       └── util/                  # Utilities & Automated Verification
│           ├── InputUtil.java
│           ├── SampleData.java
│           └── TestSmartHospital.java
│
├── README.md                      # Comprehensive project documentation
└── REPORT.md                      # Academic DSA project technical report
```

---

## 6. How to Compile and Run

### Prerequisites:
- Java Development Kit (JDK 17 or higher)
- PowerShell (Windows) or Terminal (Linux/macOS)

### 1. Compile:
From the project root directory (`c:\hospital`):

**Windows PowerShell:**
```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName)
```

**Linux / macOS (Bash):**
```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
```

### 2. Run the Main Application:
```bash
java -cp bin smarthospital.Main
```

### 3. Run the Automated 53-Point Verification Test Suite:
```bash
java -cp bin smarthospital.util.TestSmartHospital
```

---

## 7. Interactive Menu Guide

When launched, the user is presented with the main menu:

```
================================================================
               SMART HOSPITAL               
    Hospital Resource & Information System   
================================================================
 1. Patient Management
 2. Doctor Management
 3. Medical Records
 4. Laboratory Reports
 5. Treatments
 6. Medical Resources
 7. Medical Search
 8. M1 - String Algorithms
 9. M2 - Suffix Structures
10. M3 - Dynamic Programming
11. M4 - Network Flow
12. M5 - NP-Completeness
13. M6 - Randomized & Parallel
14. Hospital Analytics
15. View Stored Data
16. Save Data
 0. Exit
================================================================
```

### Key Menu Walkthroughs:

- **1. Patient Management**: Register patients, view tabular records, perform substring search, update vitals/history, or delete records. Referential integrity is strictly maintained.
- **6. Medical Resources**: View available beds, ventilators, oxygen, and blood units. Perform atomic allocations (which prevent inventory depletion below 0) and releases.
- **8. M1 - String Algorithms**:
  - `1`: Run **KMP** on clinical disease descriptions.
  - `2`: Run **Z-Function** to detect repeated boilerplate phrases.
  - `3`: Run **Rabin-Karp** to search ICD-10 diagnostic codes.
  - `4`: Run **Aho-Corasick** to screen multiple emergency keywords simultaneously.
- **9. M2 - Suffix Structures**:
  - `1`: Query Suffix Array with $O(M \log N)$ binary search.
  - `2`: Execute $O(N)$ **SA-IS** induced sorting.
  - `3 & 4`: Compute **Kasai LCP** and find longest repeated clinical sentences.
  - `5`: Query **Suffix Tree** for sub-patterns.
  - `6`: Query **Suffix Automaton (DAWG)** for instant substring recognition.
- **10. M3 - Dynamic Programming**:
  - `1 & 2`: Correct medical typos via **Levenshtein** and **Damerau-Levenshtein** (e.g. `anmeia` $\to$ `anemia`).
  - `3`: Compute **Weighted Edit Distance** with optical/clinical substitution penalties.
  - `4 & 5`: Run **Needleman-Wunsch** (global) and **Smith-Waterman** (local) sequence alignments on genetic sequences.
  - `6`: Run **Bitmask DP** to find the minimum-cost test battery covering all patient symptoms.
- **11. M4 - Network Flow**:
  - `1 & 2`: Compute maximum medical supply throughput via **Ford-Fulkerson** and **Edmonds-Karp**.
  - `3`: Execute **Dinic's Algorithm** and output saturated bottleneck channels (Min-Cut).
  - `4`: Match doctors to acute cases via **Hopcroft-Karp Bipartite Matching**.
  - `5`: Verify **König's Theorem** ($|\text{Max Matching}| = |\text{Min Vertex Cover}|$) on doctor-patient conflict networks.
- **12. M5 - NP-Completeness**:
  - `1 & 2`: Solve hospital treatment and resource safety rules via **DPLL 3-SAT**.
  - `3`: Run polynomial reduction **3-SAT $\to$ CLIQUE**.
  - `4`: Run reduction **CLIQUE $\to$ INDEPENDENT SET** on complement graph $G \to \overline{G}$.
  - `5`: Run reduction **INDEPENDENT SET $\to$ VERTEX COVER** ($k' = |V| - k$).
  - `6`: Execute **2-Approximation for Vertex Cover** on hospital conflict graphs.
- **13. M6 - Randomized & Parallel**:
  - `1`: Sort records by patient age, admission date, or urgency via **Randomized QuickSort**.
  - `2`: Ingest real-time telemetry into **Reservoir Sampling (Algorithm R)** with uniform $K/N$ probability.
  - `3`: Generate 128-bit cryptographic primes for HIPAA tokens using **Miller-Rabin**.
  - `4`: Execute **Blelloch Parallel Prefix Scan** on patient vital streams.
  - `5`: Compute hospital metrics in parallel via **ForkJoin Parallel Reduce**.
  - `6`: Analyze parallel speedup, span, and work using **Brent's Theorem**.

---

## 8. Sample Terminal Outputs

### M3: Needleman-Wunsch Biological Alignment Output
```
Global Alignment Score: 4 | Identity: 72.73%
Seq 1: GAATTCAGTTA
Match: | ||:  || |
Seq 2: G-AT-CGA--A
```

### M4: Dinic's Algorithm & Bottleneck Detection
```
>> Maximum Oxygen Delivery: 110 L/min
>> Critical Bottleneck Channels (Min-Cut):
   - Trauma Center -> Critical ICU (Capacity: 40)
   - Critical ICU -> Terminal Beds (Sink) (Capacity: 70)
```

### M5: Reduction & Vertex Cover 2-Approximation
```
Vertex Cover 2-Approximation Result:
  Maximal Matching Size (|M|)     = 3
  Approximate Cover Size (|C|)     = 6 (exactly 2 * |M|)
  Theoretical Guarantee            = |C| <= 2 * |OPT| (Ratio: 2.0)
  Selected Supervisory Nodes (6)  : [ICU Suite A, ICU Suite B, Surgery Room 1, Surgery Room 2, Isolation Ward 1, Isolation Ward 2]
```

---

## 9. Verification & Testing

SmartHospital includes a built-in automated test suite: `smarthospital.util.TestSmartHospital`.
It validates:
- [x] First-run CSV creation and directory provisioning.
- [x] Re-opening existing CSV without duplicate seeding.
- [x] Persistence of Patient, Doctor, Record additions, modifications, and deletions.
- [x] Exact correctness of all 4 String Algorithms (M1).
- [x] Exact correctness of all 6 Suffix Structures (M2).
- [x] Exact correctness of all 6 Dynamic Programming Algorithms (M3).
- [x] Exact correctness of all 5 Network Flow Algorithms & König's Theorem (M4).
- [x] Exact correctness of all 6 NP-Completeness Reductions & Approximations (M5).
- [x] Exact correctness of all 6 Randomized & Parallel Algorithms (M6).

**Execution Result**: `53 / 53 PASSED (100% SUCCESS)`.

---

## 10. Limitations & Future Scope

1. **GUI & Web Interface**: Currently implemented as a robust ANSI-compliant console application. Can be expanded with a Spring Boot REST API and React frontend.
2. **Distributed Storage**: The single master CSV engine is optimized for single-node deployments. Future iterations can integrate distributed columnar storage (Parquet / Apache Arrow).
3. **Advanced Bioinformatics**: Expand Needleman-Wunsch to support affine gap penalties (Gotoh algorithm) and PAM/BLOSUM substitution matrices for protein alignment.
