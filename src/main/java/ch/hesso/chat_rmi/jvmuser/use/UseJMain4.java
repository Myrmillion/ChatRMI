package ch.hesso.chat_rmi.jvmuser.use;

import ch.hesso.chat_rmi.jvmuser.moo.Message;
import ch.hesso.chat_rmi.jvmuser.moo.Sendable;
import ch.hesso.chat_rmi.jvmuser.moo.User;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
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

    @Test
    public void TestSendable() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair user1 = generator.generateKeyPair();
        KeyPair user2 = generator.generateKeyPair();

        Message message = new Message(new User("Nicolas", null), "Bonjour", false);

        Sendable<Message> sendable = new Sendable(message, user2.getPublic());

        System.out.println(sendable.content);

        Message received = sendable.decrypt(user2.getPrivate());
        System.out.println(received.getUserFrom() + ": " + message.getText());
    }

    @Test
    public void TestRSA() throws Exception {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair user1 = generator.generateKeyPair();
        KeyPair user2 = generator.generateKeyPair();

        SecretKey secretKey = Sendable.generateKey();
        byte[] encrypted = Sendable.encryptRSA(secretKey, user2.getPublic());
        System.out.println(encrypted.length);

        SecretKey secretKey1 = Sendable.decryptRSA(encrypted, user2.getPrivate());
        System.out.println(secretKey.hashCode());
        System.out.println(secretKey1.hashCode());
    }
}
