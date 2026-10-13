package smarthospital.m6_random_parallel;

import smarthospital.model.HospitalObservation;

import java.util.*;

/**
 * Reservoir Sampling (Algorithm R) for Continuous Hospital Observations.
 * 
 * Purpose:
 *   Maintains an unbiased uniform random sample of size K from an endless stream of real-time
 *   hospital telemetry data (ICU vital signs, sensor alerts, continuous glucose/BP feeds)
 *   without storing the entire streaming dataset in memory.
 * 
 * Mathematical Guarantee:
 *   After N items have streamed past (for any N >= K), every item has an equal probability
 *   of exactly K / N of being present in the reservoir.
 * 
 * Input:
 *   Stream or collection of HospitalObservation records, and reservoir capacity K.
 * 
 * Output:
 *   Uniformly sampled list of K HospitalObservation records.
 * 
 * Time Complexity:
 *   O(N) single-pass linear time where N is the total number of streamed items.
 * 
 * Space Complexity:
 *   O(K) constant memory bound to reservoir size.
 */
public class ReservoirSampling {

    private final int k;
    private final List<HospitalObservation> reservoir;
    private int totalStreamed = 0;
    private final Random rng;

    public ReservoirSampling(int k) {
        if (k <= 0) throw new IllegalArgumentException("Reservoir capacity K must be positive.");
        this.k = k;
        this.reservoir = new ArrayList<>(k);
        this.rng = new Random();
    }

    public ReservoirSampling(int k, long seed) {
        if (k <= 0) throw new IllegalArgumentException("Reservoir capacity K must be positive.");
        this.k = k;
        this.reservoir = new ArrayList<>(k);
        this.rng = new Random(seed);
    }

    /**
     * Ingests a new continuous observation into the reservoir.
     */
    public synchronized void streamObservation(HospitalObservation obs) {
        totalStreamed++;
        if (reservoir.size() < k) {
            // First K items fill the reservoir directly
            reservoir.add(obs);
        } else {
            // Pick a random integer from [0, totalStreamed - 1]
            int j = rng.nextInt(totalStreamed);
            if (j < k) {
                // Replace item at index j with probability K / totalStreamed
                reservoir.set(j, obs);
            }
        }
    }

    /**
     * Ingests an entire batch of streamed observations.
     */
    public void streamAll(Collection<HospitalObservation> stream) {
        for (HospitalObservation obs : stream) {
            streamObservation(obs);
        }
    }

    public List<HospitalObservation> getSample() {
        return Collections.unmodifiableList(reservoir);
    }

    public int getTotalStreamed() {
        return totalStreamed;
    }

    public int getReservoirCapacity() {
        return k;
    }

    public double getCurrentInclusionProbability() {
        if (totalStreamed == 0) return 0.0;
        return Math.min(1.0, (double) k / totalStreamed);
    }
}
