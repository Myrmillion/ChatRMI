
package ch.hesso.bilat_example.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Date;

import javax.swing.*;

import ch.hesso.bilat_example.jvmhorloge.moo.HorlogeRemote_I;

public class JHorloge extends Box
	{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public JHorloge(HorlogeRemote_I horlogeRemote)
		{
		super(BoxLayout.Y_AXIS);

		this.horlogeRemote = horlogeRemote;

		geometry();
		control();
		appearance();
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void geometry()
		{
		this.jButton = new JButton("Date");
		this.jTextArea = new JTextArea();
		this.jLabel = new JLabel();

		add(Box.createHorizontalBox());
		add(jButton);
		add(jLabel);
		add(jTextArea);
		add(Box.createHorizontalBox());
		}

	private void control()
		{
		jButton.addActionListener(new ActionListener()
			{

			@Override
			public void actionPerformed(ActionEvent e)
				{
				try
					{
					Date date = horlogeRemote.getDate();
					jLabel.setText(date.toString());
					}
				catch (RemoteException e1)
					{
					if (e1.getCause().toString().contains("SocketTimeoutException"))
						{
						jLabel.setText("SocketTimeoutException " + count++);
						}
					else
						{
						jLabel.setText("RemoteException");
						}
					}
				}

			private int count = 0;
			});
		}

	private void appearance()
		{
		this.jLabel.setBackground(Color.RED);
		}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	// Inputs
    private HorlogeRemote_I horlogeRemote;

	// Tools
	private JButton jButton;
	private JLabel jLabel;
	private JTextArea jTextArea;

	}
