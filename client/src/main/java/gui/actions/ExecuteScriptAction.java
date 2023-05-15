package gui.actions;

import commandLine.Console;
import dtp.User;
import exceptions.ExitObliged;
import gui.GuiManager;
import utility.Client;
import utility.ExecuteFileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.text.DecimalFormat;

public class ExecuteScriptAction extends Action{
    public ExecuteScriptAction(User user, Client client, GuiManager guiManager) {
        super(user, client, guiManager);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        JLabel fileAsker = new JLabel(resourceBundle.getString("SelectFile"));
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(fileAsker)
                        .addComponent(fileChooser)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                    .addComponent(fileAsker)
                    .addComponent(fileChooser));

        JOptionPane.showMessageDialog(null,
                panel,
                resourceBundle.getString("ScriptExecute"),
                JOptionPane.QUESTION_MESSAGE);
        try {
            new ExecuteFileManager(new Console(), client).fileExecution(fileChooser.getSelectedFile().getAbsolutePath());
        } catch (Exception ignored) {ignored.printStackTrace();}
    }
}
