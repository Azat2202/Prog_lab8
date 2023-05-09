package gui.actions;

import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import dtp.User;
import gui.GuiManager;
import models.*;
import models.Color;
import utility.Client;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.Date;

import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;

public class UpdateAction extends Action {
    public UpdateAction(User user, Client client, GuiManager guiManager) {
        super(user, client, guiManager);
    }

    private Integer getSelectedId() {
        Integer[] userOwnedIds = guiManager.getCollection().stream()
                .filter((s) -> s.getUserLogin().equals(user.name()))
                .map(StudyGroup::getId)
                .toArray(Integer[]::new);

        BorderLayout layout = new BorderLayout();
        JPanel panel = new JPanel(layout);
        JLabel question = new JLabel("Выберете id для изменения");
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

    private StudyGroup getObject(Integer id) {
        return guiManager.getCollection().stream()
                .filter((s) -> s.getId().equals(id))
                .toList().get(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Integer id = this.getSelectedId();
        updateJOptionWorker(id);
    }

    public void updateJOptionWorker(Integer id) {
        if(id == null) JOptionPane.showMessageDialog(null, "У вас нет объектов", "Неуспешное удаление", JOptionPane.ERROR_MESSAGE);


        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);


        JLabel mainLabel = new JLabel("Изменение объекта " + id);
        JLabel nameLabel = new JLabel("Имя: ");
        JLabel cordXLabel = new JLabel("Координата Х: ");
        JLabel cordYLabel = new JLabel("Координата Y: ");
        JLabel studentsCountLabel = new JLabel("Количество студентов: ");
        JLabel expelledStudentsLabel = new JLabel("Отчисленные студенты: ");
        JLabel averageMarkLabel = new JLabel("Средняя оценка: ");
        JLabel formOfEducationLabel = new JLabel("Вид обучения: ");
        JLabel personLabel = new JLabel("Создание админа ");
        JLabel personNameLabel = new JLabel("Имя: ");
        JLabel personWeightLabel = new JLabel("Вес: ");
        JLabel personEyeColorLabel = new JLabel("Цвет глаз: ");
        JLabel personHairColorLabel = new JLabel("Цвет волос: ");
        JLabel personNationalityLabel = new JLabel("Национальность: ");
        JLabel personLocationXLabel = new JLabel("Координата X: ");
        JLabel personLocationYLabel = new JLabel("Координата Y: ");
        JLabel personLocationNameLabel = new JLabel("Название локации: ");
        JFormattedTextField nameField;
        JFormattedTextField cordXField;
        JFormattedTextField cordYField;
        JFormattedTextField studentsCountField;
        JFormattedTextField expelledStudentsField;
        JFormattedTextField averageMarkField;
        JComboBox formOfEducationField;
        JFormattedTextField personNameField;
        JFormattedTextField personWeightField;
        JComboBox personEyeColorField;
        JComboBox personHairColorField;
        JComboBox personNationalityField;
        JFormattedTextField personLocationCordXField;
        JFormattedTextField personLocationCordYField;
        JFormattedTextField personLocationNameField;
        // Action Listeners
        {
            nameField = new JFormattedTextField(new DefaultFormatter() {
                @Override
                public Object stringToValue(String text) throws ParseException {
                    if (text.trim().isEmpty()) {
                        throw new ParseException("Field cannot be empty", 0);
                    }
                    return super.stringToValue(text);
                }
            });
            cordXField = new JFormattedTextField(new DefaultFormatter() {
                @Override
                public Object stringToValue(String text) throws ParseException {
                    Float num;
                    try {
                        num = Float.parseFloat(text);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Введите число типа float", 0);
                    }
                    if (num <= -206) throw new ParseException("Число должно быть больше -206", 0);
                    return num;
                }
            });
            cordYField = new JFormattedTextField(new DefaultFormatter() {
                @Override
                public Object stringToValue(String text) throws ParseException {
                    Double num;
                    try {
                        num = Double.parseDouble(text);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Введите число типа double", 0);
                    }
                    if (num > 463) throw new ParseException("Максимальное значение поля: 463", 0);
                    return num;
                }
            });
            studentsCountField = new JFormattedTextField(new DefaultFormatter() {
                @Override
                public Object stringToValue(String text) throws ParseException {
                    Long num;
                    try {
                        num = Long.parseLong(text);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Введите число типа long", 0);
                    }
                    if (num <= 0) throw new ParseException("Значение поля должно быть больше 0", 0);
                    return num;
                }
            });
            expelledStudentsField = new JFormattedTextField(new DefaultFormatter() {
                @Override
                public Object stringToValue(String text) throws ParseException {
                    Long num;
                    try {
                        num = Long.parseLong(text);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Введите число типа long", 0);
                    }
                    if (num <= 0) throw new ParseException("Значение поля должно быть больше 0", 0);
                    return num;
                }
            });
            averageMarkField = new JFormattedTextField(new DefaultFormatter() {
                @Override
                public Object stringToValue(String text) throws ParseException {
                    Long num;
                    try {
                        num = Long.parseLong(text);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Введите число типа long", 0);
                    }
                    if (num <= 0) throw new ParseException("Значение поля должно быть больше 0", 0);
                    return num;
                }
            });
            formOfEducationField = new JComboBox<>(FormOfEducation.values());
            personNameField = new JFormattedTextField(new DefaultFormatter() {
                @Override
                public Object stringToValue(String text) throws ParseException {
                    if (text.trim().isEmpty()) {
                        throw new ParseException("Field cannot be empty", 0);
                    }
                    return super.stringToValue(text);
                }
            });
            personWeightField = new JFormattedTextField(new DefaultFormatter() {
                @Override
                public Object stringToValue(String text) throws ParseException {
                    Integer num;
                    try {
                        num = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Введите число типа int", 0);
                    }
                    if (num <= 0) throw new ParseException("Значение поля должно быть больше 0", 0);
                    return num;
                }
            });
            personEyeColorField = new JComboBox(Color.values());
            personHairColorField = new JComboBox(Color.values());
            personNationalityField = new JComboBox(Country.values());
            personLocationCordXField = new JFormattedTextField(new DefaultFormatter() {
                @Override
                public Object stringToValue(String text) throws ParseException {
                    Double num;
                    try {
                        num = Double.parseDouble(text);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Введите число типа float", 0);
                    }
                    return num;
                }
            });
            personLocationCordYField = new JFormattedTextField(new DefaultFormatter() {
                @Override
                public Object stringToValue(String text) throws ParseException {
                    Long num;
                    try {
                        num = Long.parseLong(text);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Введите число типа double", 0);
                    }
                    return num;
                }
            });
            personLocationNameField = new JFormattedTextField(new DefaultFormatter() {
                @Override
                public Object stringToValue(String text) throws ParseException {
                    if (text.trim().isEmpty()) {
                        throw new ParseException("Field cannot be empty", 0);
                    }
                    return super.stringToValue(text);
                }
            });
        }
        // Default Values
        {
            StudyGroup studyGroup = this.getObject(id);
            nameField.setValue(studyGroup.getName());
            cordXField.setValue(studyGroup.getCoordinates().getX());
            cordYField.setValue(studyGroup.getCoordinates().getY());
            studentsCountField.setValue(studyGroup.getStudentsCount());
            expelledStudentsField.setValue(studyGroup.getExpelledStudents());
            averageMarkField.setValue(studyGroup.getAverageMark());
            personNameField.setValue(studyGroup.getGroupAdmin().getName());
            personWeightField.setValue(studyGroup.getGroupAdmin().getWeight());
            personLocationCordXField.setValue(studyGroup.getGroupAdmin().getLocation().getX());
            personLocationCordYField.setValue(studyGroup.getGroupAdmin().getLocation().getY());
            personLocationNameField.setValue(studyGroup.getGroupAdmin().getLocation().getName());
        }
        // Group Layout
        {
            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                            .addComponent(mainLabel))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(nameLabel)
                            .addComponent(nameField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(cordXLabel)
                            .addComponent(cordXField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(cordYLabel)
                            .addComponent(cordYField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(studentsCountLabel)
                            .addComponent(studentsCountField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(expelledStudentsLabel)
                            .addComponent(expelledStudentsField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(averageMarkLabel)
                            .addComponent(averageMarkField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(formOfEducationLabel)
                            .addComponent(formOfEducationField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(personLabel))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(personNameLabel)
                            .addComponent(personNameField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(personWeightLabel)
                            .addComponent(personWeightField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(personEyeColorLabel)
                            .addComponent(personEyeColorField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(personHairColorLabel)
                            .addComponent(personHairColorField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(personNationalityLabel)
                            .addComponent(personNationalityField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(personLocationXLabel)
                            .addComponent(personLocationCordXField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(personLocationYLabel)
                            .addComponent(personLocationCordYField))
                    .addGroup(layout.createParallelGroup()
                            .addComponent(personLocationNameLabel)
                            .addComponent(personLocationNameField))

            );
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                            .addComponent(mainLabel)
                            .addComponent(nameLabel)
                            .addComponent(cordXLabel)
                            .addComponent(cordYLabel)
                            .addComponent(studentsCountLabel)
                            .addComponent(expelledStudentsLabel)
                            .addComponent(averageMarkLabel)
                            .addComponent(formOfEducationLabel)
                            .addComponent(personLabel)
                            .addComponent(personNameLabel)
                            .addComponent(personWeightLabel)
                            .addComponent(personEyeColorLabel)
                            .addComponent(personHairColorLabel)
                            .addComponent(personNationalityLabel)
                            .addComponent(personLocationXLabel)
                            .addComponent(personLocationYLabel)
                            .addComponent(personLocationNameLabel)
                    )
                    .addGroup(layout.createParallelGroup()
                            .addComponent(nameField)
                            .addComponent(cordXField)
                            .addComponent(cordYField)
                            .addComponent(studentsCountField)
                            .addComponent(expelledStudentsField)
                            .addComponent(averageMarkField)
                            .addComponent(formOfEducationField)
                            .addComponent(personNameField)
                            .addComponent(personWeightField)
                            .addComponent(personEyeColorField)
                            .addComponent(personHairColorField)
                            .addComponent(personNationalityField)
                            .addComponent(personLocationCordXField)
                            .addComponent(personLocationCordYField)
                            .addComponent(personLocationNameField)
                    ));
        }
        int result = JOptionPane.showOptionDialog(null, panel, "Update", JOptionPane.YES_OPTION,
                QUESTION_MESSAGE, null, new String[]{"Изменить"}, "Изменить");
        if(result == OK_OPTION){
            StudyGroup newStudyGroup = new StudyGroup(
                    nameField.getText(),
                    new Coordinates(
                            Float.parseFloat(cordXField.getText()),
                            Double.parseDouble(cordYField.getText())
                    ),
                    new Date(),
                    Long.parseLong(studentsCountField.getText()),
                    Long.parseLong(expelledStudentsField.getText()),
                    Long.parseLong(averageMarkField.getText()),
                    (FormOfEducation) formOfEducationField.getSelectedItem(),
                    new Person(
                            personNameField.getText(),
                            Integer.parseInt(personWeightField.getText()),
                            (Color) personEyeColorField.getSelectedItem(),
                            (Color) personHairColorField.getSelectedItem(),
                            (Country) personNationalityField.getSelectedItem(),
                            new Location(
                                    Double.parseDouble(personLocationCordXField.getText()),
                                    Long.parseLong(personLocationCordYField.getText()),
                                    personLocationNameField.getText()
                            )
                    ),
                    user.name()
            );
            if(!newStudyGroup.validate()) {
                JOptionPane.showMessageDialog(null, "Объект не валиден!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Response response = client.sendAndAskResponse(new Request("update", id.toString(), user, newStudyGroup));
            if(response.getStatus() == ResponseStatus.OK) JOptionPane.showMessageDialog(null, "Объект изменен!", "Итог", JOptionPane.PLAIN_MESSAGE);
            else JOptionPane.showMessageDialog(null, "Объект не изменен!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
