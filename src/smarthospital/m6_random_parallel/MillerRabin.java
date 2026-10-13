package smarthospital.m6_random_parallel;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Miller-Rabin Randomized Primality Test.
 * 
 * Purpose:
 *   Performs fast probabilistic primality testing on large integers.
 *   Used in SmartHospital security infrastructure:
 *   - Cryptographic key generation for HIPAA-compliant patient record encryption.
 *   - Anonymization pseudorandom token generators.
 *   - Digital signature verification for electronic drug prescriptions.
 * 
 * Algorithm:
 *   Given odd n > 2, write n - 1 = 2^s * d with d odd.
 *   For k independent random bases a in [2, n - 2]:
 *     Compute x = a^d mod n.
 *     If x == 1 or x == n - 1, candidate passes this round.
 *     Repeat s - 1 times: x = x^2 mod n. If x == n - 1, passes.
 *     If neither occurs, n is composite.
 *   If it passes k rounds, probability of false prime <= 4^(-k).
 * 
 * Time Complexity:
 *   O(k * log^3 n) using repeated modular exponentiation.
 * 
 * Space Complexity:
 *   O(log n) bits for intermediate values.
 */
public class MillerRabin {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Miller-Rabin test on 64-bit long integer.
     */
    public static boolean isPrime(long n, int iterations) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0) return false;

        // Factor out powers of 2 from n - 1: n - 1 = 2^s * d
        long d = n - 1;
        int s = 0;
        while (d % 2 == 0) {
            d /= 2;
            s++;
        }

        for (int i = 0; i < iterations; i++) {
            // Pick random base a in [2, n - 2]
            long a = 2 + (Math.abs(SECURE_RANDOM.nextLong()) % (n - 3));
            long x = modularExponentiation(a, d, n);

            if (x == 1 || x == n - 1) continue;

            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = modularMultiply(x, x, n);
                if (x == n - 1) {
                    composite = false;
                    break;
                }
            }

            if (composite) return false; // Definitely composite
        }
        return true; // Probably prime with confidence >= 1 - 4^(-iterations)
    }

    /**
     * Miller-Rabin test on arbitrary precision BigInteger.
     */
    public static boolean isProbablePrime(BigInteger n, int iterations) {
        if (n.compareTo(BigInteger.ONE) <= 0) return false;
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) return true;
        if (!n.testBit(0)) return false; // Even number > 2

        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        BigInteger d = nMinusOne;
        int s = 0;
        while (!d.testBit(0)) {
            d = d.shiftRight(1);
            s++;
        }

        for (int i = 0; i < iterations; i++) {
            BigInteger a;
            do {
                a = new BigInteger(n.bitLength(), SECURE_RANDOM);
            } while (a.compareTo(BigInteger.TWO) < 0 || a.compareTo(nMinusOne) >= 0);

            BigInteger x = a.modPow(d, n);
            if (x.equals(BigInteger.ONE) || x.equals(nMinusOne)) continue;

            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(nMinusOne)) {
                    composite = false;
                    break;
                }
            }
            if (composite) return false;
        }
        return true;
    }

    private static long modularExponentiation(long base, long exp, long mod) {
        long result = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = modularMultiply(result, base, mod);
            }
            exp >>= 1;
            base = modularMultiply(base, base, mod);
        }
        return result;
    }

    private static long modularMultiply(long a, long b, long mod) {
        return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).mod(BigInteger.valueOf(mod)).longValue();
    }

    /**
     * Generates a probable prime of specified bit length for hospital security tokens.
     */
    public static BigInteger generateHospitalSecurityKey(int bitLength, int confidenceRounds) {
        BigInteger candidate;
        do {
            candidate = new BigInteger(bitLength, SECURE_RANDOM).setBit(0).setBit(bitLength - 1);
        } while (!isProbablePrime(candidate, confidenceRounds));
        return candidate;
    }
}
