package gui;

import com.formdev.flatlaf.FlatDarculaLaf;
import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import dtp.User;
import gui.actions.AddAction;
import gui.actions.RemoveAction;
import gui.actions.ShowAction;
import gui.actions.UpdateAction;
import main.App;
import models.Coordinates;
import models.StudyGroup;
import utility.Client;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Comparator;

import static javax.swing.JOptionPane.*;

public class GuiManager {
    private final Client client;

    private final GuiState guiState;
    private final JFrame frame;
    private final Container contentPane;
    private final MenuBar menuBar;
    private JTable table = null;
    private CartesianPanel cartesianPanel = null;

    private User user = null;

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
        frame.setJMenuBar(this.createMenuBar());
        frame.setResizable(true);
        frame.setVisible(true);
//        frame.setSize(App.APP_DEFAULT_WIDTH, App.APP_DEFAULT_HEIGHT);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        this.menuBar = new MenuBar();
        this.frame.setMenuBar(this.menuBar);

        SwingUtilities.invokeLater(this::run);

    }

    public void run(){
        Panel panel = new Panel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        this.loginAuth();

        JButton tableExecute = new JButton("Таблица");
        JButton cartesianExecute = new JButton("Координаты");
        this.table = this.getTable();
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        //Компаратор для времени
        sorter.setComparator(3, Comparator.comparing(
                i -> LocalDateTime.parse(((String) i).replace(" ", "T"))));
        table.setRowSorter(sorter);
        JScrollPane tablePane = new JScrollPane(table);
        this.cartesianPanel = new CartesianPanel(client, user);
        JPanel cardPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        cardPanel.add(tablePane, "Table");
        cardPanel.add(cartesianPanel, "Cartesian");

        tableExecute.addActionListener((actionEvent) -> {
            cardLayout.show(cardPanel, "Table");
        });
        cartesianExecute.addActionListener((actionEvent) -> {
            cardLayout.show(cardPanel, "Cartesian");
        });

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(cardPanel)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(tableExecute)
                                .addComponent(cartesianExecute))));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(cardPanel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(tableExecute)
                        .addComponent(cartesianExecute)));
        frame.add(panel);
        frame.setVisible(true);
    }

     public JTable getTable(){
        Response response = client.sendAndAskResponse(new Request("show", "", user));
        if(response.getStatus() != ResponseStatus.OK) return null;
        String[] columnNames = {"id",
                "group_name",
                "cord",
                "creation_date",
                "students_count",
                "expelled_students",
                "average_mark",
                "form_of_education",
                "person_name",
                "person_weight",
                "person_eye_color",
                "person_hair_color",
                "person_nationality",
                "person_location",
                "person_location_name",
                "owner_login"
        };
        Object[][] rowData = response.getCollection().stream()
                .map(this::createRow)
                .toArray(Object[][]::new);
        return new JTable(rowData, columnNames);
     }

     private Object[] createRow(StudyGroup studyGroup){
        return new Object[]{
                studyGroup.getId(),
                studyGroup.getName(),
                studyGroup.getCoordinates(),
                StudyGroup.timeFormatter(studyGroup.getCreationDate()),
                studyGroup.getStudentsCount(),
                studyGroup.getExpelledStudents(),
                studyGroup.getAverageMark(),
                studyGroup.getFormOfEducation().toString(),
                studyGroup.getGroupAdmin().getName(),
                studyGroup.getGroupAdmin().getWeight(),
                studyGroup.getGroupAdmin().getEyeColor(),
                studyGroup.getGroupAdmin().getHairColor(),
                studyGroup.getGroupAdmin().getNationality(),
                studyGroup.getGroupAdmin().getLocation().getCoordinates(),
                studyGroup.getGroupAdmin().getLocation().getName(),
                studyGroup.getUserLogin()
        };
     }

    private JMenuBar createMenuBar(){
        int iconSize = 15;

        JMenuBar menuBar = new JMenuBar();
        JMenu actions = new JMenu("Actions");
        JMenuItem add = new JMenuItem("Add");
        JMenuItem update = new JMenuItem("Update");
        JMenuItem remove = new JMenuItem("Remove");
        JMenuItem show = new JMenuItem("Show");

        add.addActionListener(new AddAction());
        update.addActionListener(new UpdateAction());
        remove.addActionListener(new RemoveAction());
        show.addActionListener(new ShowAction());

        //I hate swing :)
        add.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\add.png")
                .getImage()
                .getScaledInstance(iconSize, iconSize, Image.SCALE_FAST)));
        update.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\update.png")
                .getImage()
                .getScaledInstance(iconSize, iconSize, Image.SCALE_FAST)));
        remove.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\remove.png")
                .getImage()
                .getScaledInstance(iconSize, iconSize, Image.SCALE_FAST)));
        show.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\show.png")
                .getImage()
                .getScaledInstance(iconSize, iconSize, Image.SCALE_FAST)));


        actions.add(add);
        actions.add(update);
        actions.addSeparator();
        actions.add(show);
        actions.addSeparator();
        actions.add(remove);
        menuBar.add(actions);
        return menuBar;
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
                    QUESTION_MESSAGE, null, new String[]{"Вход", "Регистрация"}, "Вход");
            if (result == OK_OPTION) {
                if (!checkFields(loginField, passwordField, errorLabel)) continue;
                Response response = client.sendAndAskResponse(
                        new Request(
                                "ping",
                                "",
                                new User(loginField.getText(), String.valueOf(passwordField.getPassword()))));
                if (response.getStatus() == ResponseStatus.OK) {
                    errorLabel.setText("Логин успешный!");
                    errorLabel.setForeground(GREEN_OK);
                    this.user = new User(loginField.getText(), String.valueOf(passwordField.getPassword()));
                    return;
                } else {
                    errorLabel.setText("Логин не успешный!");
                    errorLabel.setForeground(RED_WARNING);
                }
            } else if (result == NO_OPTION){
                if (!checkFields(loginField, passwordField, errorLabel)) continue;
                Response response = client.sendAndAskResponse(
                        new Request(
                                "register",
                                "",
                                new User(loginField.getText(), String.valueOf(passwordField.getPassword()))));
                if (response.getStatus() == ResponseStatus.OK) {
                    errorLabel.setText("Вы успешно зарегистрировались!");
                    errorLabel.setForeground(GREEN_OK);
                    this.user = new User(loginField.getText(), String.valueOf(passwordField.getPassword()));
                    return;
                } else {
                    errorLabel.setText("Логин занят!");
                    errorLabel.setForeground(RED_WARNING);
                }
            } else if (result == CLOSED_OPTION) {
                System.exit(666);
            }
        }
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
