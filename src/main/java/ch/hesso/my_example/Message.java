package ch.hesso.my_example;

import java.io.Serializable;

public class Message implements Serializable
{
    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public Message(String username, String text)
    {
        this.username = username;
        this.text = text;
    }

    /*------------------------------------------------------------------*\
    |*							Public Methods 							*|
    \*------------------------------------------------------------------*/

    /*------------------------------*\
    |*				Get				*|
    \*------------------------------*/

    public String getUsername()
    {
        return this.username;
    }

    public String getText()
    {
        return this.text;
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods						    *|
    \*------------------------------------------------------------------*/



    /*------------------------------------------------------------------*\
    |*							Private Attributes						*|
    \*------------------------------------------------------------------*/

    // Inputs
    private String username;
    private String text;

    // Outputs

    // Tools
}
