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

public class RemoveAction extends Action {

    public RemoveAction(User user, Client client, GuiManager guiManager) {
        super(user, client, guiManager);
    }

    private Integer getSelectedId() {
        Integer[] userOwnedIds = guiManager.getCollection().stream()
                .filter((s) -> s.getUserLogin().equals(user.name()))
                .map(StudyGroup::getId)
                .toArray(Integer[]::new);

        BorderLayout layout = new BorderLayout();
        JPanel panel = new JPanel(layout);
        JLabel question = new JLabel("Выберете id для удаления");
        JLabel idLabel = new JLabel("Выберите id");
        JComboBox idField = new JComboBox(userOwnedIds);

        layout.addLayoutComponent(question, BorderLayout.NORTH);
        layout.addLayoutComponent(idLabel, BorderLayout.WEST);
        layout.addLayoutComponent(idField, BorderLayout.EAST);

        JOptionPane.showMessageDialog(null,
                idField,
                "Update",
                JOptionPane.PLAIN_MESSAGE);
        return (Integer) idField.getSelectedItem();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Integer id = this.getSelectedId();
        if(id == null) JOptionPane.showMessageDialog(null, "У вас нет объектов", "Неуспешное удаление", JOptionPane.ERROR_MESSAGE);
        Response response = client.sendAndAskResponse(new Request("remove_by_id", id.toString(), user));
        if(response.getStatus() == ResponseStatus.OK) {
            JOptionPane.showMessageDialog(null, "Объект удален успешно", "Успешное удаление", JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            JOptionPane.showMessageDialog(null, "Объект удалить не удалось", "Неуспешное удаление", JOptionPane.ERROR_MESSAGE);
        }
    }
}
