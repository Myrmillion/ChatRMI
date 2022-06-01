package ch.hesso.chat_rmi.jvmuser.gui;

import ch.hesso.chat_rmi.jvmuser.gui.tools.*;
import ch.hesso.chat_rmi.jvmuser.moo.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class JLogin extends Box {
    public JLogin() {
        super(BoxLayout.Y_AXIS);

        geometry();
        control();
        appearance();
    }

    private void geometry()
    {
        this.jAuthenticate = new JLabel("Authentication");
        this.jUsername = new JTextField();
        this.jPassword = new JPasswordField();
        this.jLogin = new JButton("Create");

        add(createVerticalGlue());
        add(new JCenterH(this.jAuthenticate));
        add(createVerticalStrut(STRUT_SMALL_SIZE));
        add(new JCenterH(this.jUsername));
        add(createVerticalStrut(STRUT_SMALL_SIZE));
        add(new JCenterH(this.jPassword));
        add(createVerticalStrut(STRUT_SMALL_SIZE));
        add(new JCenterH(this.jLogin));
        add(createVerticalGlue());
    }

    private void control() {
        jLogin.addActionListener(e -> {
            login();
        });

        jPassword.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == 10) // 10 is the 'Enter' key code
                {
                    login();
                }
            }
        });
    }


    private void login() {
        JFrameChat.mainJFrame.changePage(new JMain(jUsername.getText(), jPassword.getPassword()));
    }

    private void appearance() {
        // Labels
        this.jAuthenticate.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_TITLE_SIZE));

        // Username (JTextField)
        this.jUsername.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, FONT_TEXT_FIELD_SIZE));
        Utils.promptToText(this.jUsername, "Username");
        JComponents.setHeight(this.jUsername, 50);
        JComponents.setWidth(this.jUsername, 400);
        this.jUsername.setHorizontalAlignment(SwingConstants.CENTER);

        // Password (JPasswordField)
        this.jPassword.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, FONT_TEXT_FIELD_SIZE));
        Utils.promptToText(this.jPassword, "Password");
        JComponents.setHeight(this.jPassword, 50);
        JComponents.setWidth(this.jPassword, 400);
        this.jPassword.setHorizontalAlignment(SwingConstants.CENTER);

        // Create (Button)
        this.jLogin.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_BUTTON_SIZE));
        JComponents.setHeight(this.jLogin, 50);
        JComponents.setWidth(this.jLogin, 250);
    }

    private JLabel jAuthenticate;
    private JTextField jUsername;
    private JPasswordField jPassword;
    private JButton jLogin;

    private final static int STRUT_SMALL_SIZE = 6;
    public static final int FONT_TITLE_SIZE = 22;
    public static final int FONT_TEXT_FIELD_SIZE = 20;
    public static final int FONT_BUTTON_SIZE = 14;

}
