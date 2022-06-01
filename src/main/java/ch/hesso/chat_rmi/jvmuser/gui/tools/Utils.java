package ch.hesso.chat_rmi.jvmuser.gui.tools;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

public class Utils
{
    /*------------------------------------------------------------------*\
    |*							Public Methods      					*|
    \*------------------------------------------------------------------*/

    public static void promptToText(JTextField textField, String message)
    {
        TextPrompt tp = new TextPrompt(message, textField);

        tp.setHorizontalAlignment(SwingConstants.CENTER);
        tp.changeAlpha(65);
        tp.changeStyle(Font.BOLD);
    }

    public static Stream<Byte> byteStream(byte[] array)
    {
        Stream.Builder<Byte> builder = Stream.builder();

        for (Byte aByte : array)
        {
            builder.add(aByte);
        }

        return builder.build();
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods					    	*|
    \*------------------------------------------------------------------*/
}
