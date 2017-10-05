package fr.univ_amu.iut;




/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        MessageRSA Hey = new MessageRSA("I love myself");

        System.out.println(Hey.getPrivateKeys()[1] + " " +Hey.getPrivateKeys()[0]);

        MessageRSA toDecrypt = new MessageRSA(Hey.getCryptedMessage());
        toDecrypt.setPrivateKey(Hey.getPrivateKeys()[0],Hey.getPrivateKeys()[1]);
        System.out.println(toDecrypt.getUncryptedMessage());

    }
}
