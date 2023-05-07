package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.formdev.flatlaf.FlatDarculaLaf;

public class GuiManager {
    private final GuiState guiState;
    private final JFrame frame;
    private final Container contentPane;
    private final MenuBar menuBar;


    public GuiManager() {
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        this.guiState = GuiState.LOGIN;
        this.frame = new JFrame("Лабораторная работа 8");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.contentPane = this.frame.getContentPane();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setSize(300, 350);
        this.menuBar = new MenuBar();
        this.frame.setMenuBar(this.menuBar);


        this.loginAuth();

    }

    public void changeState(GuiState state){

    }

    public void loginAuth(){
        Panel panel = new Panel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        JLabel loginTextLabel = new JLabel("Введите логин: ");
        JTextField loginField = new JTextField();
        JLabel passwordTextLabel = new JLabel("Введите пароль: ");
        JPasswordField passwordField = new JPasswordField();
        JLabel errorLabel = new JLabel("");
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Regiser");

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(loginTextLabel)
                        .addComponent(passwordTextLabel)
                        .addComponent(loginButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(loginField)
                        .addComponent(passwordField)
                        .addComponent(errorLabel)
                        .addComponent(registerButton)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(loginTextLabel)
                        .addComponent(loginField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordTextLabel)
                        .addComponent(passwordField))
                        .addComponent(errorLabel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(loginButton)
                        .addComponent(registerButton))
        );
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errorLabel.setText("Логин успешный!");
                errorLabel.setForeground(Color.GREEN);
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errorLabel.setText("Логин не успешный!");
                errorLabel.setForeground(Color.RED);
            }
        });
        frame.add(panel);
        frame.setVisible(true);
    }
}
