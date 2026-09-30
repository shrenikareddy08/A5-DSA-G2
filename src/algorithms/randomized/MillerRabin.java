package algorithms.randomized;

import java.math.BigInteger;
import java.util.Random;

public class MillerRabin {
    public boolean isProbablyPrime(long n, int rounds) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0) return false;

        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }

        Random random = new Random(42);
        BigInteger N = BigInteger.valueOf(n);
        BigInteger D = BigInteger.valueOf(d);

        for (int r = 0; r < rounds; r++) {
            long a = 2 + Math.abs(random.nextLong()) % (n - 3);
            BigInteger x = BigInteger.valueOf(a).modPow(D, N);
            if (x.equals(BigInteger.ONE) || x.equals(N.subtract(BigInteger.ONE))) continue;

            boolean witnessPass = false;
            for (int i = 1; i < s; i++) {
                x = x.multiply(x).mod(N);
                if (x.equals(N.subtract(BigInteger.ONE))) {
                    witnessPass = true;
                    break;
                }
            }
            if (!witnessPass) return false;
        }
        return true;
    }
}
