package ch.hesso.chat_rmi.jvmuser.helper;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Arrays;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CryptoHelper
{
    public static final byte[] salt = new byte[]{101, -20, 105, 86, 82, 90, -101, -5, 122, 46, 13, 112, 69, -101, -5, -18, 5, -106, 107, -84, 79, -94, -122, -72, -96, 120, -93, -81, -16, 76, 74, 73, 59, -59, 67, -128, 83, 91, -23, 72, -3, -17, 54, 100, -66, -69, 43, 89, -113, -38, -96, -64, -100, -41, 123, 53, -121, -32, 33, -76, 67, -126, 9, 118};

    /*------------------------------------------------------------------*\
    |*							Public Methods      					*|
    \*------------------------------------------------------------------*/

    /*------------------------------*\
    |*			  Static		   	*|
    \*------------------------------*/

    public static KeyPair getKeyPair(String username, char[] password, byte[] macAddress) throws GeneralSecurityException
    {
        byte[] seed = appendBytes(username.toLowerCase().getBytes(UTF_8), salt, toBytes(password), macAddress);

        SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
        rnd.setSeed(seed);

        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4);
        KeyPairGenerator pairGenerator = KeyPairGenerator.getInstance("RSA");
        pairGenerator.initialize(spec, rnd);

        return pairGenerator.generateKeyPair();
    }

    public static byte[] appendBytes(byte[]... arrays)
    {
        int length = 0;
        for (byte[] array : arrays)
        {
            length += array.length;
        }
        byte[] result = new byte[length];
        int i = 0;
        for (byte[] array : arrays)
        {
            for (byte b : array)
            {
                result[i] = b;
                i++;
            }
        }
        return result;
    }

    public static byte[] toBytes(char[] chars)
    {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }

    // Encrypt the symetric key with RSA
    public static byte[] encryptRSA(SecretKey keyToEncrypt, PublicKey publicKey) throws Exception
    {
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
    public static byte[] encryptAES(Serializable content, SecretKey key, IvParameterSpec ivParameterSpec) throws Exception
    {

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

    public static byte[] hash(byte[] content) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(content);
    }

    public static byte[] sign(byte[] content, PrivateKey privateKey) throws Exception
    {
        byte[] messageHash = hash(content);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(messageHash);
    }

    public static SecretKey decryptRSA(byte[] symmetricKey, PrivateKey privateKey) throws Exception
    {
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

    public static Object decryptAES(byte[] content, SecretKey secretKey, IvParameterSpec ivParameterSpec) throws Exception
    {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
        CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
        ObjectInputStream objectInputStreamstream = new ObjectInputStream(cipherInputStream);

        SealedObject sealedObject = (SealedObject) objectInputStreamstream.readObject();
        return sealedObject.getObject(cipher);
    }

    public static boolean verifySignature(byte[] signature, byte[] content, PublicKey publicKey) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedMessageHash = cipher.doFinal(signature);

        byte[] realMessageHash = hash(content);
        return Arrays.equals(decryptedMessageHash, realMessageHash);

    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static byte[] generateIv()
    {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static Object StringToObject(String s) throws IOException, ClassNotFoundException
    {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    public static String ObjectToString(Serializable o) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /*------------------------------------------------------------------*\
    |*							Private Attributes						*|
    \*------------------------------------------------------------------*/

    /*------------------------------*\
    |*			  Static		   	*|
    \*------------------------------*/

    private final static String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private final static String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
}
