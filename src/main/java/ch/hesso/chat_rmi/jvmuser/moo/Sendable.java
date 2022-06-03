package ch.hesso.chat_rmi.jvmuser.moo;

import ch.hesso.chat_rmi.jvmuser.helper.CryptoHelper;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class Sendable<T extends Serializable> implements Serializable
{
    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public Sendable(T content, PublicKey publicKeyReceiver, PrivateKey privateKeySender)
    {
        encrypt(content, publicKeyReceiver, privateKeySender);
    }

    public Sendable(T content, User to)
    {
        encrypt(content, to.getPublicKey(), ChatController.getInstance().getPrivateKey());
    }

    /*------------------------------------------------------------------*\
    |*							Public Methods      					*|
    \*------------------------------------------------------------------*/

    public T decrypt(PrivateKey privateKey)
    {
        return decrypt(privateKey, null);
    }

    public T decrypt(PrivateKey privateKeyReceiver, PublicKey publicKeySender)
    {
        try
        {
            if (publicKeySender != null)
            {
                boolean verified = CryptoHelper.verifySignature(this.signature, this.content, publicKeySender);
                if (!verified)
                {
                    System.out.println("Signature couldn't be verified");
                    return null;
                }
            }
            SecretKey secretKey = CryptoHelper.decryptRSA(this.symmetricKey, privateKeyReceiver);
            return (T) CryptoHelper.decryptAES(this.content, secretKey, new IvParameterSpec(this.initializationVector));
        }
        catch (Exception e)
        {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }

    /*------------------------------*\
    |*			  equals			*|
    \*------------------------------*/

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Sendable<?> sendable = (Sendable<?>) o;
        return Arrays.equals(content, sendable.content) && Arrays.equals(symmetricKey, sendable.symmetricKey) && Arrays.equals(initializationVector, sendable.initializationVector) && Arrays.equals(signature, sendable.signature);
    }

    @Override
    public int hashCode()
    {
        int result = Arrays.hashCode(content);
        result = 31 * result + Arrays.hashCode(symmetricKey);
        result = 31 * result + Arrays.hashCode(initializationVector);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods					    	*|
    \*------------------------------------------------------------------*/

    private void encrypt(T content, PublicKey publicKeyReceiver, PrivateKey privateKeySender)
    {
        try
        {
            SecretKey key = CryptoHelper.generateKey();
            this.initializationVector = CryptoHelper.generateIv();
            this.content = CryptoHelper.encryptAES(content, key, new IvParameterSpec(initializationVector));
            this.symmetricKey = CryptoHelper.encryptRSA(key, publicKeyReceiver);
            this.signature = CryptoHelper.sign(this.content, privateKeySender);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    /*------------------------------------------------------------------*\
    |*							Private Attributes						*|
    \*------------------------------------------------------------------*/

    // Tools
    private byte[] content;
    private byte[] symmetricKey;
    private byte[] initializationVector;
    private byte[] signature;
}