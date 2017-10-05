package fr.univ_amu.iut;


import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.util.ArrayList;

import static java.lang.Math.random;


/**
 * Created by p16001500 on 28/09/17.
 */


public class MessageRSA {
    private ArrayList<Integer> cryptedmessage;
    private String decryptedMessage;
    private int n;
    private int e;
    private int d;
    private int p;
    private int q;



    public MessageRSA() {
        cryptedmessage = new ArrayList<Integer>();
        decryptedMessage = "";

    }

    public MessageRSA(String message){
        this();
        decryptedMessage = message;
        cryptedmessage = new ArrayList<Integer>();
        this.generateKey();
        this.cryptString();
    }



    public MessageRSA(ArrayList<Integer> message){
        this();
        cryptedmessage = message;
        decryptedMessage = "";
    }


    private static int gcd(int a, int b) {
        int r;
        // Exchange m and n if m < n
        if (a < b) {
            r = b;  b = a; a = r;
        }
        // It can be assumed that m >= n
        while (b > 0) {
            r = a % b;
            a = b;
            b = r;
        }
       return a;
    }

    private static boolean isFirst(int n)
    {
        if(n<=1) return false;
        for(int i = 2;i*i<=n;i++)
        {
            if (n%i ==0)
                return false;
            i++;
        }
        return true;
    }
    private static int random_between(int j,int k){
        return (int) (random()*(k-j+1)+j);
    }

    private static int randFirstNumber(int min,int a){
        int r = random_between(min,a);
        while (gcd(a,r) != 1)
         r = random_between(min,a);
        return r;
    }

    private void generateKey(){
        p = randFirstNumber(50,100);
        q = randFirstNumber(50,100);


        n = p*q;
        int phi = (p - 1) * (q - 1);

        e = randFirstNumber((p<q?q:p),phi);
        d = modInverse(e,phi);


    }

    private int cryptInt(int i){
        System.out.println( "i = " + i+ " " +(i^e)%n  + " " +  decryptInt((i^e)%n));
        return (i^e)%n;
    }

    private int cryptChar(char k){
        return cryptInt((int)k);
    }

    private void cryptString(){
        cryptedmessage.clear();
        for (char c: decryptedMessage.toCharArray()) {
            cryptedmessage.add(cryptChar(c));
        }

    }

    private char decryptChar(int i){
        return (char) ((i^d)%n);
    }

    private int decryptInt(int i){
        return ((i^d)%n);
    }

    private void decryptString(){
        for (int c: cryptedmessage ) {
            decryptedMessage.concat(String.valueOf(decryptChar(c)));
            System.out.print(decryptInt(c)+" ");
        }
    }

    public int[] getPublicKeys(){
        int[] publicKey = {n,e};
        return publicKey;
    }

    public int[] getPrivateKeys() {
        int[] privateKey = {n, d};
        return privateKey;
    }
    public void setPrivateKey(int n, int d){
        this.n = n;
        this.d = d;
    }

    public ArrayList getCryptedMessage(){
        return cryptedmessage;
    }

    public String getUncryptedMessage(){
        this.decryptString();
        return decryptedMessage;
    }

    /**
     * Computes the canonical class of n modulo mod
     * @param n the integer to reduce modulo mod
     * @param mod the modulo
     * @return the integer k such that k = n [mod] et 0 <= k < mod
     */
    public static int reduce(int n, int mod) {
        int m = n % mod;	// -mod < m < mod

        if (m >= 0 )
            return m;
        else
            return m + mod;
    }

    /**
     * Computes the GCD and the coefficients of the Bezout equality.
     * @param m the first integer
     * @param n the second integer
     * @return an array g of 3 integers.  g[0] is the GCD of m and n.
     *  g[1] and g[2] are two integers such that g[0] = m g[1] + n g[2].
     */
    public static int[] extgcd(int m, int n) {
        // Both arrays ma and na are arrays of 3 integers such that
        // ma[0] = m ma[1] + n ma[2] and na[0] = m na[1] + n na[2]
        int[] ma = new int[]  {m, 1, 0};
        int[] na = new int[]  {n, 0, 1};
        int[] ta;		// Temporary variable
        int i;			// Loop index
        int q;			// Quotient
        int r;			// Rest

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

    public static int modInverse(int n, int mod) {
        int[] g = extgcd(mod, n);
        if (g[0] != 1)
            return -1;		// n and mod not coprime
        else
            System.out.println(n+" mod "+ mod +" = "+ reduce(g[2], mod) );
            return reduce(g[2], mod);
    }
}
