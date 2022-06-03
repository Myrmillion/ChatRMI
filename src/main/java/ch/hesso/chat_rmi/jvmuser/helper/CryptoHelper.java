package ch.hesso.chat_rmi.jvmuser.helper;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CryptoHelper
{
    public static final byte[] salt = new byte[]{101, -20, 105, 86, 82, 90, -101, -5, 122, 46, 13, 112, 69, -101, -5, -18, 5, -106, 107, -84, 79, -94, -122, -72, -96, 120, -93, -81, -16, 76, 74, 73, 59, -59, 67, -128, 83, 91, -23, 72, -3, -17, 54, 100, -66, -69, 43, 89, -113, -38, -96, -64, -100, -41, 123, 53, -121, -32, 33, -76, 67, -126, 9, 118};

    public static KeyPair getKeyPair(String username, char[] password) throws GeneralSecurityException
    {
        byte[] seed = appendBytes(username.toLowerCase().getBytes(UTF_8), salt, toBytes(password));

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
}
