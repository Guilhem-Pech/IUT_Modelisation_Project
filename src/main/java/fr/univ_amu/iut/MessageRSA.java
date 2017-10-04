package fr.univ_amu.iut;


import java.util.ArrayList;
import java.util.List;

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


    private static int euclide(int b, int n){
        int v = 0;
        int u = 1;
        int b0 = b;
        int n0 = n;
        int q = n/b;
        int r = n0 - q * b0;
        int tmp;
        while (r>0) {
            tmp = (v - q * u) % n;
            v = u;
            u = tmp;
            n0 = b0;
            b0 = r;
            q = n0 / b0;
            r = n0 - q * b0;
        }
        if (b0 != 1){
            System.out.println("Can't be inverted !");
            return 0;
        }
        return u;
    }


    private static int pgcd(int a,int b) {
        int r = a;
        while (r!=0)  {r = a%b; a=b; b=r; }
        return(Math.abs(a));
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
        while (pgcd(a,r) != 1)
         r = random_between(min,a);
        return r;
    }

    private void generateKey(){
        p = 1009;
        q = 1999;


        n = p * q;
        int phi = (p - 1) * (q-1);
        e = euclide(d,phi);
        d = randFirstNumber((p<q?q:p),phi);

    }

    private int cryptInt(int i){
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
        System.out.println(i);
        return (char) ((i^d)%n);
    }

    private int decryptInt(int i){
        return ((i^d)%n);
    }

    private void decryptString(){
        for (int c: cryptedmessage ) {
            decryptedMessage.concat(String.valueOf(decryptChar(c)));
        }
    }

    public int[] getPublicKeys(){
        int[] publicKey = {n,e};
        return publicKey;
    }

    public int[] getPrivateKeys() {
        int[] publicKey = {n, d};
        return publicKey;
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
}
