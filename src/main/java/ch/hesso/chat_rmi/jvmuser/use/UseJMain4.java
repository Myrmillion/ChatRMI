package ch.hesso.chat_rmi.jvmuser.use;

import ch.hesso.chat_rmi.jvmuser.db.MessageEntity;
import ch.hesso.chat_rmi.jvmuser.db.MyObjectBox;
import ch.hesso.chat_rmi.jvmuser.helper.CryptoHelper;
import ch.hesso.chat_rmi.jvmuser.moo.User;
import io.objectbox.Box;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class UseJMain4
{
    public static void main(String[] args)
    {
        System.out.println("test");

        //new JFrameChat(new JMain(), "Chat RMI");
        try
        {
            Signature signature = Signature.getInstance("SHA256withRSA");

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair user1 = generator.generateKeyPair();

            KeyPair user2 = generator.generateKeyPair();

            String text = "yo";

            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, user2.getPublic());

        }
        catch (Exception e)
        {

        }
    }

    @Test
    public void TestSendable() throws NoSuchAlgorithmException
    {
        //        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        //        generator.initialize(2048);
        //        KeyPair user1 = generator.generateKeyPair();
        //        KeyPair user2 = generator.generateKeyPair();
        //        KeyPair user3 = generator.generateKeyPair();
        //
        //        Message message = new Message(new User("Nicolas", null), "Bonjour", false);
        //
        //        Sendable<Message> sendable = new Sendable(message, user2.getPublic(), user1.getPrivate());
        //
        //        Message received = sendable.decrypt(user2.getPrivate(), user1.getPublic());
        //        System.out.println(received.getUserFrom() + ": " + message.getText());
    }

    @Test
    public void TestRSA() throws Exception
    {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair user1 = generator.generateKeyPair();
        KeyPair user2 = generator.generateKeyPair();

        SecretKey secretKey = CryptoHelper.generateKey();
        byte[] encrypted = CryptoHelper.encryptRSA(secretKey, user2.getPublic());
        System.out.println(encrypted.length);

        SecretKey secretKey1 = CryptoHelper.decryptRSA(encrypted, user2.getPrivate());
        System.out.println(secretKey.hashCode());
        System.out.println(secretKey1.hashCode());
    }

    @Test
    public void generate64Bytes() {
        int length = 64;
        Random r = new Random();
        byte[] salt = new byte[length];
        r.nextBytes(salt);

        StringBuilder builder = new StringBuilder();
        for (byte b : salt) {
            //builder.append("0x"+ String.format("%02X", b) + ", ");
            builder.append(b + ", ");
        }
        System.out.println(builder.toString());
    }

    @Test
    public void testAppendByte() {
        byte[] b1 = new byte[] {1,2,3};
        byte[] b2 = new byte[] {11,21,31};
        byte[] b3 = new byte[] {14,24,34};
        byte[] r = CryptoHelper.appendBytes(b1, b2, b3);
        System.out.println(Arrays.toString(r));
    }

    @Test
    public void testConverter() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair user1 = generator.generateKeyPair();

        String test = User.getString(user1.getPublic());
        System.out.println(test);
        PublicKey pk1 = User.getPublicKey(test);
        System.out.println(Objects.equals(pk1, user1.getPublic()));
    }

    @Test
    public void resetDatabase() throws Exception {
        Box<MessageEntity> box = MyObjectBox.builder().name("objectbox-messages-db").build().boxFor(MessageEntity.class);
        box.removeAll();

    }



}
