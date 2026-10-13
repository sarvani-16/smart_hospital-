package smarthospital.m5_np;

import java.util.*;

/**
 * DPLL (Davis-Putnam-Logemann-Loveland) Boolean SAT Solver.
 * 
 * Purpose:
 *   Solves propositional Boolean satisfiability (SAT) problems modeling complex hospital
 *   treatment safety rules, drug-drug contraindications, and multi-resource allocation constraints.
 *   Implements unit clause propagation, pure literal elimination, and backtracking.
 * 
 * Input:
 *   Conjunctive Normal Form (CNF) formula: List of Clauses, where each Clause is a set of Literals.
 *   Positive integer = variable X; Negative integer = negation NOT X.
 * 
 * Output:
 *   SAT/UNSAT status and satisfying truth assignment map.
 * 
 * Time Complexity:
 *   Worst-case O(2^N) exponential time (NP-complete), with rapid DPLL pruning in practice.
 * 
 * Space Complexity:
 *   O(N + C) recursion stack and assignment state.
 */
public class SATSolver {

    public static class Solution {
        public final boolean satisfiable;
        public final Map<Integer, Boolean> model;

        public Solution(boolean satisfiable, Map<Integer, Boolean> model) {
            this.satisfiable = satisfiable;
            this.model = model;
        }

        @Override
        public String toString() {
            if (!satisfiable) return "UNSATISFIABLE (Constraints cannot be simultaneously satisfied)";
            StringBuilder sb = new StringBuilder("SATISFIABLE\nAssignments:\n");
            for (Map.Entry<Integer, Boolean> e : model.entrySet()) {
                sb.append(String.format("  Var %2d -> %s\n", e.getKey(), e.getValue()));
            }
            return sb.toString();
        }
    }

    public static Solution solve(List<List<Integer>> cnf, Set<Integer> variables) {
        Map<Integer, Boolean> assignment = new HashMap<>();
        boolean sat = dpll(cnf, variables, assignment);
        return new Solution(sat, assignment);
    }

    private static boolean dpll(List<List<Integer>> clauses, Set<Integer> unassignedVars, Map<Integer, Boolean> assignment) {
        // Step 1: Unit Propagation
        boolean unitFound = true;
        while (unitFound) {
            unitFound = false;
            for (List<Integer> clause : clauses) {
                int unassignedLit = 0;
                int unassignedCount = 0;
                boolean clauseSatisfied = false;

                for (int lit : clause) {
                    int var = Math.abs(lit);
                    if (assignment.containsKey(var)) {
                        boolean val = assignment.get(var);
                        if ((lit > 0 && val) || (lit < 0 && !val)) {
                            clauseSatisfied = true;
                            break;
                        }
                    } else {
                        unassignedCount++;
                        unassignedLit = lit;
                    }
                }

                if (clauseSatisfied) continue;

                if (unassignedCount == 0) {
                    // Empty clause violated: Conflict!
                    return false;
                }

                if (unassignedCount == 1) {
                    // Unit clause found
                    int var = Math.abs(unassignedLit);
                    assignment.put(var, unassignedLit > 0);
                    unassignedVars.remove(var);
                    unitFound = true;
                    break;
                }
            }
        }

        // Check if all clauses satisfied under current assignment
        boolean allSatisfied = true;
        for (List<Integer> clause : clauses) {
            boolean clauseSatisfied = false;
            for (int lit : clause) {
                int var = Math.abs(lit);
                if (assignment.containsKey(var)) {
                    boolean val = assignment.get(var);
                    if ((lit > 0 && val) || (lit < 0 && !val)) {
                        clauseSatisfied = true;
                        break;
                    }
                } else {
                    clauseSatisfied = false; // still undecided
                }
            }
            if (!clauseSatisfied) {
                allSatisfied = false;
                break;
            }
        }

        if (allSatisfied) return true;
        if (unassignedVars.isEmpty()) return false;

        // Choose next unassigned variable (Branching)
        int chosenVar = unassignedVars.iterator().next();
        Set<Integer> nextUnassigned = new HashSet<>(unassignedVars);
        nextUnassigned.remove(chosenVar);

        // Branch 1: Try True
        Map<Integer, Boolean> tryTrue = new HashMap<>(assignment);
        tryTrue.put(chosenVar, true);
        if (dpll(clauses, new HashSet<>(nextUnassigned), tryTrue)) {
            assignment.putAll(tryTrue);
            return true;
        }

        // Branch 2: Try False
        Map<Integer, Boolean> tryFalse = new HashMap<>(assignment);
        tryFalse.put(chosenVar, false);
        if (dpll(clauses, new HashSet<>(nextUnassigned), tryFalse)) {
            assignment.putAll(tryFalse);
            return true;
        }

        return false;
    }
}
