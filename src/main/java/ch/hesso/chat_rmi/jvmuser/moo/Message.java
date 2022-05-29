package ch.hesso.chat_rmi.jvmuser.moo;

import java.io.Serializable;

public class Message implements Serializable
{
    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public Message(String text, boolean isImportant)
    {
        this.text = text;
        this.isImportant = isImportant;
    }


    /*------------------------------------------------------------------*\
    |*							Public Methods 							*|
    \*------------------------------------------------------------------*/

    /*------------------------------*\
    |*				Get				*|
    \*------------------------------*/

    public String getText()
    {
        return text;
    }

    public boolean isImportant()
    {
        return isImportant;
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods						    *|
    \*------------------------------------------------------------------*/



    /*------------------------------------------------------------------*\
    |*							Private Attributes						*|
    \*------------------------------------------------------------------*/

    // Inputs
    private final String text;
    private final boolean isImportant;

    // Outputs

    // Tools
}
