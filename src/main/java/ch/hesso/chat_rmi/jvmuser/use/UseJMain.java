package ch.hesso.chat_rmi.jvmuser.use;

import ch.hesso.chat_rmi.jvmuser.gui.JLogin;
import ch.hesso.chat_rmi.jvmuser.gui.JMain;
import ch.hesso.chat_rmi.jvmuser.gui.tools.JFrameChat;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

public class UseJMain
{
    /*------------------------------------------------------------------*\
    |*							Public Methods							*|
    \*------------------------------------------------------------------*/

    public static void main(String[] args)
    {
        main();
    }

    public static void main()
    {
        new JFrameChat(new JLogin(), "Chat RMI");
        
        //        try
        //        {
        //            Signature signature = Signature.getInstance("SHA256withRSA");
        //
        //            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        //            generator.initialize(2048);
        //            KeyPair user1 = generator.generateKeyPair();
        //
        //            KeyPair user2 =s generator.generateKeyPair();
        //
        //            String text = "yo";
        //
        //            Cipher encryptCipher = Cipher.getInstance("RSA");
        //            encryptCipher.init(Cipher.ENCRYPT_MODE, user2.getPublic());
        //
        //        }
        //        catch (Exception e)
        //        {
        //
        //        }
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods 						*|
    \*------------------------------------------------------------------*/
}
