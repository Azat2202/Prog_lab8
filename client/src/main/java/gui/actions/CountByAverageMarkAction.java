package gui.actions;

import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import dtp.User;
import gui.GuiManager;
import models.StudyGroup;
import utility.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

public class CountByAverageMarkAction extends Action{
    public CountByAverageMarkAction(User user, Client client, GuiManager guiManager) {
        super(user, client, guiManager);
    }

    private long askAverageMark(){
        BorderLayout layout = new BorderLayout();
        JPanel panel = new JPanel(layout);
        JLabel question = new JLabel("Введите среднюю оценку для подсчета количества");
        JLabel markLabel = new JLabel("Средняя оценка:");
        JFormattedTextField markField = new JFormattedTextField(DecimalFormat.getInstance());

        layout.addLayoutComponent(question, BorderLayout.NORTH);
        layout.addLayoutComponent(markLabel, BorderLayout.WEST);
        layout.addLayoutComponent(markField, BorderLayout.EAST);

        JOptionPane.showMessageDialog(null,
                markField,
                "Count",
                JOptionPane.PLAIN_MESSAGE);
        return Long.parseLong(markField.getText());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Response response = client.sendAndAskResponse(new Request("count_by_average_mark", String.valueOf(this.askAverageMark()), user));
        if(response.getStatus() == ResponseStatus.OK) JOptionPane.showMessageDialog(null, response.getResponse(), "Итог", JOptionPane.PLAIN_MESSAGE);
        else JOptionPane.showMessageDialog(null, "Ответ не получен!", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}
