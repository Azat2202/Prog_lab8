package gui.actions;

import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import dtp.User;
import gui.GuiManager;
import utility.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;

public class ExitAction extends Action{
    public ExitAction(User user, Client client, GuiManager guiManager) {
        super(user, client, guiManager);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int result = JOptionPane.showOptionDialog(null,
                "Вы уверены что хотите выйти?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                QUESTION_MESSAGE,
                null,
                new String[]{"Да", "Нет"},
                "Нет");
        if(result == OK_OPTION){
            System.exit(0);
        }
    }
}
