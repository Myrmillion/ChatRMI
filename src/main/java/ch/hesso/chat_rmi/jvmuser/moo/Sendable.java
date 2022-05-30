package ch.hesso.chat_rmi.jvmuser.moo;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.*;
import java.security.*;
import java.util.Base64;

public class Sendable<T extends Serializable> implements Serializable
{
    public Sendable(T content, PublicKey publicKey)
    {
        encrypt(content, publicKey);
    }

    private void encrypt(T content, PublicKey publicKey) {
        try
        {
            SecretKey key = generateKey();
            this.initializationVectorSpec = generateIv();
            this.content = encryptAES(content, key, initializationVectorSpec);
            this.symmetricKey = encryptRSA(key, publicKey);
        }
        catch (Exception e)
        {
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

    public T decrypt(PrivateKey privateKey) {
        try {
           SecretKey secretKey = decryptRSA(this.symmetricKey, privateKey);
           T decrypted = (T)decryptAES(this.content, secretKey, this.initializationVectorSpec);
           return decrypted;
        }
        catch (Exception e) {
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

        return (SecretKey) cipher.unwrap(symmetricKey, "AES",Cipher.SECRET_KEY);

    }

    public static Object decryptAES(byte[] content, SecretKey secretKey, IvParameterSpec ivParameterSpec) throws Exception{
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
        CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
        ObjectInputStream objectInputStreamstream = new ObjectInputStream(cipherInputStream);

        SealedObject sealedObject = (SealedObject) objectInputStreamstream.readObject();
        return sealedObject.getObject(cipher);
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public byte[] content; //TODO private
    private byte[] symmetricKey;
    private IvParameterSpec initializationVectorSpec;

    private final static String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private final static String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
}
