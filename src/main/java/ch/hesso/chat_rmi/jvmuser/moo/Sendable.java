package ch.hesso.chat_rmi.jvmuser.moo;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class Sendable<T extends Serializable> implements Serializable {
    public Sendable(T content, PublicKey publicKeyReceiver, PrivateKey privateKeySender) {
        encrypt(content, publicKeyReceiver, privateKeySender);
    }

    public Sendable(T content, User to) {
        encrypt(content, to.getPublicKey(), ChatController.getInstance().getPrivateKey());
    }

    private void encrypt(T content, PublicKey publicKeyReceiver, PrivateKey privateKeySender) {
        try {
            SecretKey key = generateKey();
            this.initializationVector = generateIv();
            this.content = encryptAES(content, key, new IvParameterSpec(initializationVector));
            this.symmetricKey = encryptRSA(key, publicKeyReceiver);
            this.signature = sign(this.content, privateKeySender);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Encrypt the symetric key with RSA
    public static byte[] encryptRSA(SecretKey keyToEncrypt, PublicKey publicKey) throws Exception {

        /*// Create cipher
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        SealedObject sealedObject = new SealedObject(keyToEncrypt, cipher);
        System.out.println("SealedObject:" + sealedObject);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cos = new CipherOutputStream(outputStream, cipher);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(sealedObject);
        return outputStream.toByteArray();*/

        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.WRAP_MODE, publicKey);
        return cipher.wrap(keyToEncrypt);
    }

    // Encrypt the content with a symmetric key
    public static byte[] encryptAES(Serializable content, SecretKey key, IvParameterSpec ivParameterSpec) throws Exception {

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        SealedObject sealedObject = new SealedObject(content, cipher);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cos = new CipherOutputStream(outputStream, cipher);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(cos);
        objectOutputStream.writeObject(sealedObject);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }

    public static byte[] hash(byte[] content) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(content);
    }

    public static byte[] sign(byte[] content, PrivateKey privateKey) throws Exception {
        byte[] messageHash = hash(content);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(messageHash);
    }

    public T decrypt(PrivateKey privateKey) {
        return decrypt(privateKey, null);
    }
    public T decrypt(PrivateKey privateKeyReceiver, PublicKey publicKeySender) {
        try {
            if (publicKeySender != null) {
                boolean verified = verifySignature(this.signature, this.content, publicKeySender);
                if (!verified) {
                    System.out.println("Signature couldn't be verified");
                    return null;
                }
            }
            SecretKey secretKey = decryptRSA(this.symmetricKey, privateKeyReceiver);
            return (T)decryptAES(this.content, secretKey, new IvParameterSpec(this.initializationVector));
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }

    public static SecretKey decryptRSA(byte[] symmetricKey, PrivateKey privateKey) throws Exception {
        /*Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(symmetricKey);
        CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
        ObjectInputStream objectInputStream = new ObjectInputStream(cipherInputStream);

        SealedObject sealedObject = (SealedObject) objectInputStream.readObject();
        return (SecretKey) sealedObject.getObject(cipher);*/

        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.UNWRAP_MODE, privateKey);

        return (SecretKey) cipher.unwrap(symmetricKey, "AES", Cipher.SECRET_KEY);

    }

    public static Object decryptAES(byte[] content, SecretKey secretKey, IvParameterSpec ivParameterSpec) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
        CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
        ObjectInputStream objectInputStreamstream = new ObjectInputStream(cipherInputStream);

        SealedObject sealedObject = (SealedObject) objectInputStreamstream.readObject();
        return sealedObject.getObject(cipher);
    }

    public static boolean verifySignature(byte[] signature, byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedMessageHash = cipher.doFinal(signature);

        byte[] realMessageHash = hash(content);
        return Arrays.equals(decryptedMessageHash, realMessageHash);

    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static byte[] generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private byte[] content;
    private byte[] symmetricKey;
    private byte[] initializationVector;
    private byte[] signature;

    private final static String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private final static String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
}
