package fr.univ_amu.iut;


    import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Math.random;


/**
 * Created by p16001500 on 28/09/17.
 */


public class MessageRSA {
    private ArrayList<BigInteger> cryptedmessage;
    private String decryptedMessage;
    private BigInteger n;
    private BigInteger e;
    private BigInteger d;
    private BigInteger p;
    private BigInteger q;


    /**
     * Default constructor
     */
    public MessageRSA() {
        cryptedmessage = new ArrayList<BigInteger>();
        decryptedMessage = "";

    }

    /**
     * This constructor built the object and crypt the message given
     * @param message Clear message
     */
    public MessageRSA(String message){
        this();
        decryptedMessage = message;
        this.generateKey();
        this.cryptString();
    }


    /**
     * Constructor built the object and uncrypt a message given with the private key
     * @param message crypted message
     * @param n
     * @param d
     */
    public MessageRSA(ArrayList<BigInteger> message, BigInteger n, BigInteger d){
        this();
        cryptedmessage = message;
        this.setPrivateKey(n,d);
        this.decryptString();
    }

    /**
     * Generate the Greatest common divisor between 2 numbers
     * @param a
     * @param b
     * @return
     */
    private static BigInteger gcd(BigInteger a, BigInteger b) {
        BigInteger r;
        // Exchange m and n if m < n
        if (a.compareTo(b)== -1) {
            r = b;  b = a; a = r;
        }
        while (b.compareTo(BigInteger.ZERO) == 1) {
            r = a.mod(b);
            a = b;
            b = r;
        }
       return a;
    }

    /**
     * Generate an int between 2 int
     * @param j min
     * @param k max
     * @return
     */
   private static long random_between(long j,long k){
        return (long) (random()*(k-j+1)+j);
    }

    private static BigInteger randFirstNumber(BigInteger min,BigInteger a){
        BigInteger r = random_between(min,a);
        while (!gcd(a,r).equals(BigInteger.ONE))
            r = random_between(min,a);
        return r;
    }

    /**
     * Generate a BigInteger between 2 BigInteger
     * @param j
     * @param k
     * @return
     */
    private static BigInteger random_between(BigInteger j, BigInteger k) {
       return BigInteger.valueOf((long) (random()*(k.longValue()-j.longValue()+1)+j.longValue()));
    }

    /**
     * Generate the public and the private key
     */
    public void generateKey(){
        p = BigInteger.probablePrime(5,new SecureRandom());
        q = BigInteger.probablePrime(5,new SecureRandom());
        while(p.equals(q))
            q = BigInteger.probablePrime(5,new SecureRandom());


        n =   p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        e = randFirstNumber(p.compareTo(q)==-1?q:p,phi);
        d = BigInteger.valueOf(modInverse(e.longValue(),phi.longValue()));


    }

    /**
     * Crypt a long given
     * @param i
     * @return
     */
    private BigInteger cryptLong(long i){
        return BigInteger.valueOf(i).pow(e.intValue()).mod(n);//(i^e)%n;
    }

    /**
     * Crypt a given char
     * @param k
     * @return
     */
    private BigInteger cryptChar(char k){
        return cryptLong((long)k);
    }

    /**
     * Crypt the uncrypted message
     */
    public void cryptString(){
        if (p == null || d == null )
            this.generateKey();
        cryptedmessage.clear();
        for (char c: decryptedMessage.toCharArray()) {
            cryptedmessage.add(cryptChar(c));
        }

    }

    /**
     * Uncrypt an BigInt
     * @param i
     * @return
     */
    private BigInteger decryptLong(BigInteger i){
        return  i.pow(d.intValue()).mod(n); //(i^d)%n);
    }

    /**
     * Uncrypt the message
     */
    public void decryptString(){
        decryptedMessage = "";
        for (BigInteger c: cryptedmessage ) {
            decryptedMessage += (char)decryptLong(c).longValue();
        }
    }

    /**
     * Get the public key of the message
     * @return a BigInteger table with [0] == n and [1] == e
     */
    public BigInteger[] getPublicKeys(){
        BigInteger[] publicKey = {n,e};
        return publicKey;
    }

    /**
     * Get the private key of the message
     * @return a BigInteger table with [0] == n and [1] == d
     */
    public BigInteger[] getPrivateKeys() {
        BigInteger[] privateKey = {n, d};
        return privateKey;
    }

    /**
     * Set manualy the private key on a message
     * @param n
     * @param d
     */
    public void setPrivateKey(BigInteger n, BigInteger d){
        this.n = n;
        this.d = d;
    }

    /**
     * Return the crypted message
     * @return the crypted message in an arraylist
     */
    public ArrayList<BigInteger> getCryptedMessage(){
        return cryptedmessage;
    }

    /**
     * Return the uncrypted message
     * @return the string of the uncrypted message
     */
    public String getUncryptedMessage(){
        return decryptedMessage;
    }

    /**
     * Computes the canonical class of n modulo mod
     * @param n the integer to reduce modulo mod
     * @param mod the modulo
     * @return the integer k such that k = n [mod] et 0 <= k < mod
     */
    public static long reduce(long n, long mod) {
        long m = n % mod;	// -mod < m < mod

        if (m >= 0 )
            return m;
        else
            return m + mod;
    }

    /**
     * Computes the GCD and the coefficients of the Bezout equality.
     * @param m the first long
     * @param n the second long
     * @return an array g of 3 integers.  g[0] is the GCD of m and n.
     *  g[1] and g[2] are two integers such that g[0] = m g[1] + n g[2].
     */
    public static long[] extgcd(long m, long n) {
        // Both arrays ma and na are arrays of 3 integers such that
        // ma[0] = m ma[1] + n ma[2] and na[0] = m na[1] + n na[2]
        long[] ma = new long[]  {m, 1, 0};
        long[] na = new long[]  {n, 0, 1};
        long[] ta;		// Temporary variable
        int i;			// Loop index
        long q;			// Quotient
        long r;			// Rest

        // Exchange ma and na if m < n
        if (m < n) {
            ta = na;  na = ma; ma = ta;
        }

        // It can be assumed that m >= n
        while (na[0] > 0) {
            q = ma[0] / na[0];	// Quotient
            for (i = 0; i < 3; i++) {
                r = ma[i] - q * na[i];
                ma[i] = na[i];
                na[i] = r;
            }
        }
        return ma;
    }

    /**
     * Calculate the reverse modular with the extended euclide algorithm
     * @param n
     * @param mod
     * @return
     */
    public static long modInverse(long n, long mod) {
        long[] g = extgcd(mod, n);
        if (g[0] != 1)
            throw new ArithmeticException("Mudular calculation impossible "+ n + " and " + mod + " not coprime");
        else
            return reduce(g[2], mod);
    }

    @Override
    public String toString() {
        return "MessageRSA{" +
                "cryptedmessage=" + cryptedmessage + '\n' +
                ", decryptedMessage='" + decryptedMessage + '\'' +
                '}';
    }
}
