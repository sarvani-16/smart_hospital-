package smarthospital.m5_np;

import java.util.*;

/**
 * 3-SAT Formulation for Hospital Treatment and Resource Constraints.
 * 
 * Purpose:
 *   Models complex clinical protocols, patient safety constraints, and resource requirements
 *   as a 3-Conjunctive Normal Form (3-CNF) Boolean formula.
 *   Each clause contains at most 3 literals.
 * 
 * Variables:
 *   1: ICU_REQUIRED
 *   2: VENTILATOR_REQUIRED
 *   3: SPECIALIST_REQUIRED
 *   4: BLOOD_REQUIRED
 *   5: ISOLATION_REQUIRED
 *   6: SURGERY_SCHEDULED
 *   7: MEDICATION_A (Anticoagulant)
 *   8: MEDICATION_B (Thrombolytic)
 * 
 * Time Complexity:
 *   Formulation: O(C) where C is number of clauses.
 *   Solving: NP-Complete, solved via DPLL solver.
 * 
 * Space Complexity:
 *   O(C) for clause representation.
 */
public class ThreeSAT {

    public static final Map<Integer, String> VARIABLE_NAMES = Map.of(
            1, "ICU_REQUIRED",
            2, "VENTILATOR_REQUIRED",
            3, "SPECIALIST_REQUIRED",
            4, "BLOOD_REQUIRED",
            5, "ISOLATION_REQUIRED",
            6, "SURGERY_SCHEDULED",
            7, "MEDICATION_A (Anticoagulant)",
            8, "MEDICATION_B (Thrombolytic)"
    );

    private final List<List<Integer>> clauses = new ArrayList<>();
    private final Set<Integer> variables = new HashSet<>();

    public ThreeSAT() {
        initHospitalConstraints();
    }

    private void initHospitalConstraints() {
        // C1: Ventilator requires ICU: (~VENT v ICU v ICU)
        addClause(-2, 1, 1);

        // C2: Surgery requires Specialist: (~SURGERY v SPECIALIST v SPECIALIST)
        addClause(-6, 3, 3);

        // C3: MedA and MedB conflict unless Blood backup is available: (~MED_A v ~MED_B v BLOOD)
        addClause(-7, -8, 4);

        // C4: Critical condition requires at least one of ICU, Surgery, or Specialist: (ICU v SURGERY v SPECIALIST)
        addClause(1, 6, 3);

        // C5: Isolation protocol demands ICU or Specialist: (~ISOLATION v ICU v SPECIALIST)
        addClause(-5, 1, 3);

        // C6: Active ventilator without isolation is permitted only if Specialist monitors: (~VENT v ~ISOLATION v SPECIALIST)
        addClause(-2, -5, 3);
    }

    public void addClause(int l1, int l2, int l3) {
        List<Integer> clause = new ArrayList<>();
        clause.add(l1);
        clause.add(l2);
        clause.add(l3);
        clauses.add(clause);
        variables.add(Math.abs(l1));
        variables.add(Math.abs(l2));
        variables.add(Math.abs(l3));
    }

    public List<List<Integer>> getClauses() {
        return clauses;
    }

    public Set<Integer> getVariables() {
        return variables;
    }

    public SATSolver.Solution solve() {
        return SATSolver.solve(clauses, new HashSet<>(variables));
    }

    public String formatClauses() {
        StringBuilder sb = new StringBuilder();
        int idx = 1;
        for (List<Integer> c : clauses) {
            sb.append(String.format("Clause %2d: (", idx++));
            for (int i = 0; i < c.size(); i++) {
                if (i > 0) sb.append(" OR ");
                int lit = c.get(i);
                String name = VARIABLE_NAMES.getOrDefault(Math.abs(lit), "V" + Math.abs(lit));
                sb.append(lit < 0 ? "NOT " : "").append(name);
            }
            sb.append(")\n");
        }
        return sb.toString();
    }
}
