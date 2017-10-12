package fr.univ_amu.iut;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        MessageRSA Hey = new MessageRSA("Ceci est un message de test les amis !");
        System.out.println(Hey.getUncryptedMessage() + " ORIGINAL ");
        for (int i = 0; i < 100; i++) {
            MessageRSA toDecrypt = new MessageRSA(Hey.getCryptedMessage(),Hey.getPrivateKeys()[0],Hey.getPrivateKeys()[1]);
            Hey.generateKey();
            Hey.cryptString();
            System.out.println(toDecrypt.getUncryptedMessage());

        }


    }
}
