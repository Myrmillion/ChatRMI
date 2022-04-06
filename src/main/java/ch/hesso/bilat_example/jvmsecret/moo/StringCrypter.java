
package ch.hesso.bilat_example.jvmsecret.moo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StringCrypter implements Serializable
	{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public StringCrypter(String message)
		{
		this.message = message;
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	@Override
	public String toString()
		{
		return message;
		}

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	public String getMessage()
		{
		return this.message;
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	/*------------------------------*\
	|*		Serialisation			*|
	\*------------------------------*/

	private void writeObject(ObjectOutputStream out) throws IOException
		{
		System.out.println("writeObject");// debug

		out.writeObject(crypter(message));
		}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
		{
		System.out.println("readObject");// debug

		message=decrypter((String)in.readObject());
		}

	/*------------------------------*\
	|*		Cryptage				*|
	\*------------------------------*/

	/**
	 * faire mieux apres cours cryptage!!
	 */
	private static String crypter(String message)
		{
		return "X" + message;
		}

	private static String decrypter(String message)
		{
		//return message; // maniere de prouver que message a ete crypter
		return message.substring(1);
		}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	// Inputs/Outputs
	private String message;

	}
