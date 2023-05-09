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

public class ClearAction extends Action{
    public ClearAction(User user, Client client, GuiManager guiManager) {
        super(user, client, guiManager);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int result = JOptionPane.showOptionDialog(null,
                "Вы уверены что хотите удалить свои объекты?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                QUESTION_MESSAGE,
                null,
                new String[]{"Да", "Нет"},
                "Нет");
        if(result == OK_OPTION){
            Response response = client.sendAndAskResponse(new Request("clear", "", user));
            if(response.getStatus() == ResponseStatus.OK) JOptionPane.showMessageDialog(null, "Объекты удалены!", "Итог", JOptionPane.PLAIN_MESSAGE);
            else JOptionPane.showMessageDialog(null, "Объекты не удалены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
