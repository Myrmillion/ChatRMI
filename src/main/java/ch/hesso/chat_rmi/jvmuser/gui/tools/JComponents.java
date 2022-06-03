package ch.hesso.chat_rmi.jvmuser.gui.tools;

import java.awt.Dimension;

import javax.swing.JComponent;

public class JComponents
{

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

    public static void setWidth(JComponent component, int width)
    {
        component.setMaximumSize(new Dimension(width, component.getMaximumSize().height));
        component.setMinimumSize(new Dimension(width, component.getMinimumSize().height));
        component.setPreferredSize(new Dimension(width, component.getPreferredSize().height));
    }

    public static void setHeight(JComponent component, int height)
    {
        component.setMaximumSize(new Dimension(component.getMaximumSize().width, height));
        component.setMinimumSize(new Dimension(component.getMinimumSize().width, height));
        component.setPreferredSize(new Dimension(component.getPreferredSize().width, height));
    }

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

}
