package ch.hesso.chat_rmi.jvmuser.moo;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SealedObject;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Sendable implements Serializable
{
    public Sendable(Serializable content, PublicKey publicKey, PrivateKey privateKey)
    {
        try
        {
            // Create cipher
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            SealedObject sealedObject = new SealedObject(content, cipher);

            // Wrap the output stream
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            CipherOutputStream cos = new CipherOutputStream(ostream, cipher);
            ObjectOutputStream outputStream = new ObjectOutputStream(cos);
            outputStream.writeObject(sealedObject);
            outputStream.close();
            this.content = ostream.toByteArray();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    private byte[] content;
}
