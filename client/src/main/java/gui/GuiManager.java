package gui;

import com.formdev.flatlaf.FlatDarculaLaf;
import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import dtp.User;
import gui.actions.*;
import models.StudyGroup;
import utility.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import static javax.swing.JOptionPane.*;

public class GuiManager {
    private final Client client;

    private final GuiState guiState;
    private final JFrame frame;
    private final Container contentPane;
    private final MenuBar menuBar = null;
    private JTable table = null;
    private DefaultTableModel tableModel = null;
    private CartesianPanel cartesianPanel = null;
    private Object[][] tableData = null;
    private Collection<StudyGroup> collection = null;

    private User user;

    private final static Color RED_WARNING = Color.decode("#FF4040");
    private final static Color GREEN_OK = Color.decode("#00BD39");

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
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        SwingUtilities.invokeLater(this::run);

    }

    public void run(){
        Panel panel = new Panel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        this.loginAuth();
        frame.setJMenuBar(this.createMenuBar());

        JButton tableExecute = new JButton("Таблица");
        JButton cartesianExecute = new JButton("Координаты");
        this.tableData = this.getTableData();
        this.tableModel = new DefaultTableModel(columnNames, tableData.length);
        this.tableModel.setDataVector(tableData, columnNames);
        this.table = new JTable(tableModel);
        new Timer(1000, (i) ->{
            Object[][] newTableData = this.getTableData();
            if(!Arrays.deepEquals(this.tableData, newTableData)) {
                this.tableModel.setDataVector(this.getTableData(), columnNames);
                this.tableModel.fireTableDataChanged();
                this.cartesianPanel.repaint();
            }
        }).start();
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        //Компараторы
        {
            //Компаратор для времени
            sorter.setComparator(3, Comparator.comparing(
                    i -> LocalDateTime.parse(((String) i).replace(" ", "T"))));
        }

        table.setRowSorter(sorter);
        JScrollPane tablePane = new JScrollPane(table);
        this.cartesianPanel = new CartesianPanel(client, user);
        JPanel cardPanel = new JPanel();
        ImageIcon userIcon = new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\user.png")
                .getImage()
                .getScaledInstance(25, 25, Image.SCALE_AREA_AVERAGING));
        JLabel userLabel = new JLabel(user.name());
        userLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        userLabel.setIcon(userIcon);
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
                                .addComponent(cartesianExecute)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(userLabel)
                                .addGap(5))));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(cardPanel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(tableExecute)
                        .addComponent(cartesianExecute)
                        .addComponent(userLabel)
                        .addGap(5)));
        frame.add(panel);
        frame.setVisible(true);
    }

    public Object[][] getTableData(){
        Response response = client.sendAndAskResponse(new Request("show", "", user));
        if(response.getStatus() != ResponseStatus.OK) return null;
        this.collection = response.getCollection();
        return response.getCollection().stream()
                .map(this::createRow)
                .toArray(Object[][]::new);
    }

    private Object[] createRow(StudyGroup studyGroup){
        return new Object[]{
                studyGroup.getId(),
                studyGroup.getName(),
                studyGroup.getCoordinates(),
                studyGroup.getCreationDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
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
        int iconSize = 40;

        JMenuBar menuBar = new JMenuBar();
        JMenu actions = new JMenu("Actions");
        JMenuItem add = new JMenuItem("Add");
        JMenuItem addIfMax = new JMenuItem("AddIfMax");
        JMenuItem clear = new JMenuItem("Clear");
        JMenuItem countByAverageMark = new JMenuItem("CountByAverageMark");
        JMenuItem countLessThanExpelled = new JMenuItem("CountLessThanExpelledStudents");
        JMenuItem executeScript = new JMenuItem("executeScript");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem info = new JMenuItem("Info");
        JMenuItem removeAllByAverageMark = new JMenuItem("removeByAverageMark");
        JMenuItem removeGreater = new JMenuItem("removeGreater");
        JMenuItem update = new JMenuItem("Update");
        JMenuItem remove = new JMenuItem("Remove");

        add.addActionListener(new AddAction(user, client, this));
        update.addActionListener(new UpdateAction(user, client, this));
        remove.addActionListener(new RemoveAction(user, client, this));
        addIfMax.addActionListener(new AddIfMaxAction(user, client, this));
        clear.addActionListener(new ClearAction(user, client, this));
        countByAverageMark.addActionListener(new CountByAverageMarkAction(user, client, this));
        countLessThanExpelled.addActionListener(new CountLessThanExpelledStudentsAction(user, client, this));
        exit.addActionListener(new ExitAction(user, client, this));
        info.addActionListener(new InfoAction(user, client, this));
        removeAllByAverageMark.addActionListener(new RemoveAllByAverageMarkAction(user, client, this));
        removeGreater.addActionListener(new RemoveGreaterAction(user, client, this));

        //I hate swing :)
        add.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\add.png")
                .getImage()
                .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING)));
        addIfMax.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\add_if_max.png")
                .getImage()
                .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING)));
        update.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\update.png")
                .getImage()
                .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING)));
        remove.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\remove.png")
                .getImage()
                .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING)));
        clear.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\clear.png")
                .getImage()
                .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING)));
        countByAverageMark.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\count_by_average_mark.png")
                 .getImage()
                 .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING)));
        countLessThanExpelled.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\count_less_than_expelled.png")
                 .getImage()
                 .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING)));
        exit.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\exit.png")
                         .getImage()
                         .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING)));
        info.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\info.png")
                         .getImage()
                         .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING)));
        removeAllByAverageMark.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\remove_all_by_average_mark.png")
                         .getImage()
                         .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING)));
        removeGreater.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\remove_greater.png")
                         .getImage()
                         .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING)));



        actions.add(add);
        actions.add(addIfMax);
        actions.addSeparator();
        actions.add(update);
        actions.addSeparator();
        actions.add(remove);
        actions.add(removeGreater);
        actions.add(removeAllByAverageMark);
        actions.add(clear);
        actions.addSeparator();
        actions.add(countByAverageMark);
        actions.add(countLessThanExpelled);
        actions.add(info);
        actions.addSeparator();
        actions.add(exit);

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

    public Collection<StudyGroup> getCollection() {
        return collection;
    }

}
