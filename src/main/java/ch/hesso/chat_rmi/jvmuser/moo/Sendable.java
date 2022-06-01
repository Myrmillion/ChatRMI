package ch.hesso.chat_rmi.jvmuser.moo;

import ch.hesso.chat_rmi.jvmuser.helper.CryptoHelper;

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
            SecretKey key = CryptoHelper.generateKey();
            this.initializationVector = CryptoHelper.generateIv();
            this.content = CryptoHelper.encryptAES(content, key, new IvParameterSpec(initializationVector));
            this.symmetricKey = CryptoHelper.encryptRSA(key, publicKeyReceiver);
            this.signature = CryptoHelper.sign(this.content, privateKeySender);
        } catch (Exception e) {
            System.out.println(e);
        }
    }



    public T decrypt(PrivateKey privateKey) {
        return decrypt(privateKey, null);
    }
    public T decrypt(PrivateKey privateKeyReceiver, PublicKey publicKeySender) {
        try {
            if (publicKeySender != null) {
                boolean verified = CryptoHelper.verifySignature(this.signature, this.content, publicKeySender);
                if (!verified) {
                    System.out.println("Signature couldn't be verified");
                    return null;
                }
            }
            SecretKey secretKey = CryptoHelper.decryptRSA(this.symmetricKey, privateKeyReceiver);
            return (T)CryptoHelper.decryptAES(this.content, secretKey, new IvParameterSpec(this.initializationVector));
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }



    private byte[] content;
    private byte[] symmetricKey;
    private byte[] initializationVector;
    private byte[] signature;

}
