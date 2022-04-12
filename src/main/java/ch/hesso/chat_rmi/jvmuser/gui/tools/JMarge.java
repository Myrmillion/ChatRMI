
package ch.hesso.chat_rmi.jvmuser.gui.tools;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class JMarge extends JPanel
	{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public JMarge(JComponent jcomponent, int w, int h)
		{
		// Inputs
			{
			this.jcomponent = jcomponent;
			this.w = w;
			this.h = h;
			}

		geometry();
		control();
		appearance();
		}

	public JMarge(JComponent jcomponent, int marge)
		{
		this(jcomponent, marge, marge);
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	public int getW()
		{
		return this.w;
		}

	public int getH()
		{
		return this.h;
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void geometry()
		{
		panelN = new JPanel();
		panelS = new JPanel();
		panelE = new JPanel();
		panelW = new JPanel();

		setLayout(new BorderLayout());

		add(jcomponent, BorderLayout.CENTER);
		add(panelN, BorderLayout.NORTH);
		add(panelS, BorderLayout.SOUTH);
		add(panelE, BorderLayout.EAST);
		add(panelW, BorderLayout.WEST);
		}

	private void control()
		{
		// rien
		}

	private void appearance()
		{
		JComponents.setHeight(panelN, h);
		JComponents.setHeight(panelS, h);

		JComponents.setWidth(panelE, w);
		JComponents.setWidth(panelW, w);
		}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	// Inputs
	private JComponent jcomponent;
	private int w;
	private int h;

	// Tools
	private JPanel panelN;
	private JPanel panelS;
	private JPanel panelE;
	private JPanel panelW;

	}
