package algorithms;

/**
 * Manual Miller-Rabin primality tester.
 *
 * The implementation avoids java.util.* and uses a small deterministic
 * pseudo-random generator so it fits the engine constraint.
 * It is intended for positive long values up to 2,000,000,000.
 */
public class MillerRabin {

    private long seed = 0x5DEECE66DL;

    public boolean isPrime(long n, int rounds) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if ((n & 1L) == 0L) return false;

        long[] smallPrimes = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        for (int i = 0; i < smallPrimes.length; i++) {
            if (n == smallPrimes[i]) return true;
            if (n % smallPrimes[i] == 0) return false;
        }

        long d = n - 1;
        int s = 0;
        while ((d & 1L) == 0L) {
            d >>= 1;
            s++;
        }

        if (rounds < 1) rounds = 1;

        for (int round = 0; round < rounds; round++) {
            long a = 2 + nextPositive(n - 3);
            long x = modPow(a, d, n);

            if (x == 1 || x == n - 1) continue;

            boolean witnessPassed = false;
            for (int r = 1; r < s; r++) {
                x = multiplyMod(x, x, n);
                if (x == n - 1) {
                    witnessPassed = true;
                    break;
                }
            }

            if (!witnessPassed) return false;
        }

        return true;
    }

    public String classify(long n) {
        if (n < 2) return "COMPOSITE";
        return isPrime(n, 8) ? "PROBABLY PRIME" : "COMPOSITE";
    }

    private long nextPositive(long bound) {
        seed = (seed * 6364136223846793005L + 1442695040888963407L);
        long value = seed & Long.MAX_VALUE;
        return value % bound;
    }

    private long modPow(long base, long exponent, long modulus) {
        long result = 1L;
        long current = base % modulus;
        long power = exponent;

        while (power > 0) {
            if ((power & 1L) != 0L) {
                result = multiplyMod(result, current, modulus);
            }
            current = multiplyMod(current, current, modulus);
            power >>= 1;
        }
        return result;
    }

    private long multiplyMod(long a, long b, long modulus) {
        long result = 0L;
        long x = a % modulus;
        long y = b;

        while (y > 0) {
            if ((y & 1L) != 0L) {
                result = addMod(result, x, modulus);
            }
            x = addMod(x, x, modulus);
            y >>= 1;
        }
        return result;
    }

    private long addMod(long a, long b, long modulus) {
        // a and b are already in [0, modulus). This form avoids overflow.
        if (a >= modulus - b) {
            return a - (modulus - b);
        }
        return a + b;
    }
}
