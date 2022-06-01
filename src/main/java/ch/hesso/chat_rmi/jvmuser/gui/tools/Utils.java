package ch.hesso.chat_rmi.jvmuser.gui.tools;

import javax.swing.*;
import java.awt.*;

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

    /*------------------------------------------------------------------*\
    |*							Private Methods					    	*|
    \*------------------------------------------------------------------*/
}
