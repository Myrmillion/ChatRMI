package ch.hesso.chat_rmi.jvmuser.use;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

public class UseJMain4
{
    public static void main(String[] args)
    {
        System.out.println("test");

        //new JFrameChat(new JMain(), "Chat RMI");
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair user1 = generator.generateKeyPair();

            KeyPair user2 = generator.generateKeyPair();

            String text = "yo";

            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, user2.getPublic());

        } catch (Exception e) {

        }
    }
}
