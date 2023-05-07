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
import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import dtp.User;
import utility.Client;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;

public class GuiManager {
    private final Client client;

    private final GuiState guiState;
    private final JFrame frame;
    private final Container contentPane;
    private final MenuBar menuBar;

    private final static Color RED_WARNING = Color.decode("#FF4040");
    private final static Color GREEN_OK = Color.decode("#00BD39");


    public GuiManager(Client client) {
        this.client = client;


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


        this.run();

    }

    public void run(){
        Panel panel = new Panel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        this.loginAuth();
    }

    public void loginAuth(){
        JPanel panel = new JPanel();
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
                        .addComponent(passwordTextLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(loginField)
                        .addComponent(passwordField)
                        .addComponent(errorLabel)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(loginTextLabel)
                        .addComponent(loginField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordTextLabel)
                        .addComponent(passwordField))
                        .addComponent(errorLabel));
        while(true) {
            int result = JOptionPane.showOptionDialog(null, panel, "Логин", JOptionPane.YES_NO_OPTION,
                    QUESTION_MESSAGE, null, new String[]{"Login", "Register"}, "Login");
            if (result == 1) {
                if (!checkFields(loginField, passwordField, errorLabel)) continue;
                Response response = client.sendAndAskResponse(
                        new Request(
                                "ping",
                                "",
                                new User(loginField.getText(), String.valueOf(passwordField.getPassword()))));
                if (response.getStatus() == ResponseStatus.OK) {
                    errorLabel.setText("Логин успешный!");
                    errorLabel.setForeground(GREEN_OK);
                } else {
                    errorLabel.setText("Логин не успешный!");
                    errorLabel.setForeground(RED_WARNING);
                }
            } else {
                if (!checkFields(loginField, passwordField, errorLabel)) continue;
                Response response = client.sendAndAskResponse(
                        new Request(
                                "register",
                                "",
                                new User(loginField.getText(), String.valueOf(passwordField.getPassword()))));
                if (response.getStatus() == ResponseStatus.OK) {
                    errorLabel.setText("Вы успешно зарегестрировались!");
                    errorLabel.setForeground(GREEN_OK);
                } else {
                    errorLabel.setText("Логин занят!");
                    errorLabel.setForeground(RED_WARNING);
                }
            }
        }
//        frame.add(panel);
//        frame.setVisible(true);
    }

    private boolean checkFields(JTextField loginField, JPasswordField passwordField, JLabel errorLabel){
        if(loginField.getText().isEmpty()) {
            errorLabel.setText("Логин не может быть пустым!");
            errorLabel.setForeground(RED_WARNING);
            return false;
        } else if(String.valueOf(passwordField.getPassword()).isEmpty()){
            errorLabel.setText("Пароль не может быть пустым!");
            errorLabel.setForeground(RED_WARNING);
            return false;
        }
        return true;
    }
}
