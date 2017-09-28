package fr.univ_amu.iut;

import java.util.ArrayList;

import static java.lang.Math.random;

/**
 * Created by p16001500 on 28/09/17.
 */

public class MessageRSA {
    private ArrayList cryptedmessage;
    private int n;
    private int e;
    private int d;
    private int p;
    private int q;


    public MessageRSA(int msg) {
        this.generateKey();
    }

    public MessageRSA(char msg) {
        this((int)msg);
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


    private static int pgcd(int a, int b) {
        while (a%b != 0) {
            a = b;
            b = a % b;
        }
        return b;
    }

    private static int random_between(int j,int k){
        return (int) (random()*(k-j+1)+j);
    }

    private static int randFirstNumber(int a){
        int r = random_between(2,a)
        while (pgcd(a,r) != 1)
         r = random_between(2,a);
        return r;
    }

    public void generateKey(){
        n = p * q;
        int phi = (p - 1) * (q-1);
        d = randFirstNumber(phi);
        e = euclide(d,phi);
    }


}
