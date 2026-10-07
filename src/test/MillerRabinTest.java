package test;

import algorithms.MillerRabin;

public class MillerRabinTest {
    public static void main(String[] args) {
        MillerRabin tester = new MillerRabin();
        long[] primes = {2L, 3L, 97L, 1000000007L, 2147483647L};
        long[] composites = {1L, 4L, 91L, 1000000008L, 2147483646L};

        for (int i = 0; i < primes.length; i++) {
            if (!tester.isPrime(primes[i], 10)) {
                throw new RuntimeException("Prime rejected: " + primes[i]);
            }
        }
        for (int i = 0; i < composites.length; i++) {
            if (tester.isPrime(composites[i], 10)) {
                throw new RuntimeException("Composite accepted: " + composites[i]);
            }
        }
        System.out.println("SUCCESS: Miller-Rabin test passed.");
    }
}
