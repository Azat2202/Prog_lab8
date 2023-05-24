package gui;

import com.formdev.flatlaf.FlatDarculaLaf;
import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import dtp.User;
import gui.actions.*;
import models.Coordinates;
import models.StudyGroup;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;
import utility.Client;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.JOptionPane.*;


/*
 */

public class GuiManager {
    private final Client client;
    private static Locale locale = new Locale("ru");
    private DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("GuiLabels", GuiManager.getLocale());
    private final JFrame frame;
    private Container contentPane;
    private Panel panel;
    private JTable table = null;
    private StreamTableModel tableModel = null;
    private CartesianPanel cartesianPanel = null;
    private ArrayList<StudyGroup> tableData = null;
    private ArrayList<StudyGroup> collection = null;
    private Map<JButton, String> buttonsToChangeLocale = new LinkedHashMap<>();
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
        this.frame = new JFrame(resourceBundle.getString("LabWork8"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.contentPane = this.frame.getContentPane();
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        SwingUtilities.invokeLater(this::run);

    }

    public GuiManager(Client client, User user) {
        this.client = client;
        this.user = user;
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        this.frame = new JFrame(resourceBundle.getString("LabWork8"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        SwingUtilities.invokeLater(this::run);

    }

    public void run(){
        this.contentPane = this.frame.getContentPane();
        panel = new Panel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        if(user == null) this.loginAuth();
        frame.setJMenuBar(this.createMenuBar());

        JButton tableExecute = new JButton(resourceBundle.getString("Table"));
        JButton cartesianExecute = new JButton(resourceBundle.getString("Coordinates"));
        this.tableData = this.getTableDataStudyGroup();
        this.tableModel = new StreamTableModel(columnNames, tableData.size());
        this.tableModel.setDataVector(tableData, columnNames);
        this.table = new JTable(tableModel);

        new Timer(3000, (i) ->{
            ArrayList<StudyGroup> newTableData = this.getTableDataStudyGroup();
            if(!(this.tableData.equals(newTableData))) {
                this.tableData = newTableData;
                this.tableModel.setDataVector(this.tableData, columnNames);
                this.tableModel.fireTableDataChanged();
                this.cartesianPanel.updateUserColors();
                this.cartesianPanel.reanimate();
            }
        }).start();

        // Выбрать столбец для сортировки
        table.getTableHeader().setReorderingAllowed(false);
        table.setDragEnabled(false);
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                int column = table.columnAtPoint(point);
                tableModel.performSorting(column);
                table.repaint();
            }
        });
        // Выбрать строку для изменения
        this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Integer id = tableModel.getRow(table.getSelectedRow()).getId();
                new UpdateAction(user, client, GuiManager.this).updateJOptionWorker(id);
            }
        });



        JScrollPane tablePane = new JScrollPane(table);
        this.cartesianPanel = new CartesianPanel(client, user, this);
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
            this.cartesianPanel.reanimate();
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

//    public Object[][] getTableData(){
//        Response response = client.sendAndAskResponse(new Request("show", "", user));
//        if(response.getStatus() != ResponseStatus.OK) return null;
//        this.collection = response.getCollection();
//        return response.getCollection().stream()
//                .map(this::createRow)
//                .toArray(Object[][]::new);
//    }

    public ArrayList<StudyGroup> getTableDataStudyGroup(){
        Response response = client.sendAndAskResponse(new Request("show", "", user));
        if(response.getStatus() != ResponseStatus.OK) return null;
        this.collection = new ArrayList<>(response.getCollection());
        return new ArrayList<>(response.getCollection());
    }

    private Object[] createRow(StudyGroup studyGroup){
        return new Object[]{
                studyGroup.getId(),
                studyGroup.getName(),
                studyGroup.getCoordinates(),
                dateFormat.format(studyGroup.getCreationDate()),
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
        JMenu actions = new JMenu(resourceBundle.getString("Actions"));
        JMenuItem add = new JMenuItem(resourceBundle.getString("Add"));
        JMenuItem addIfMax = new JMenuItem(resourceBundle.getString("AddIfMax"));
        JMenuItem clear = new JMenuItem(resourceBundle.getString("Clear"));
        JMenuItem countByAverageMark = new JMenuItem(resourceBundle.getString("CountByAverageMark"));
        JMenuItem countLessThanExpelled = new JMenuItem(resourceBundle.getString("CountLessThanExpelledStudents"));
        JMenuItem executeScript = new JMenuItem(resourceBundle.getString("executeScript"));
        JMenuItem exit = new JMenuItem(resourceBundle.getString("Exit"));
        JMenuItem info = new JMenuItem(resourceBundle.getString("Info"));
        JMenuItem removeAllByAverageMark = new JMenuItem(resourceBundle.getString("removeByAverageMark"));
        JMenuItem removeGreater = new JMenuItem(resourceBundle.getString("removeGreater"));
        JMenuItem update = new JMenuItem(resourceBundle.getString("Update"));
        JMenuItem remove = new JMenuItem(resourceBundle.getString("Remove"));
        JMenuItem language = new JMenuItem(resourceBundle.getString("Language"));

        add.addActionListener(new AddAction(user, client, this));
        update.addActionListener(new UpdateAction(user, client, this));
        remove.addActionListener(new RemoveAction(user, client, this));
        addIfMax.addActionListener(new AddIfMaxAction(user, client, this));
        clear.addActionListener(new ClearAction(user, client, this));
        countByAverageMark.addActionListener(new CountByAverageMarkAction(user, client, this));
        executeScript.addActionListener(new ExecuteScriptAction(user, client, this));
        countLessThanExpelled.addActionListener(new CountLessThanExpelledStudentsAction(user, client, this));
        exit.addActionListener(new ExitAction(user, client, this));
        info.addActionListener(new InfoAction(user, client, this));
        removeAllByAverageMark.addActionListener(new RemoveAllByAverageMarkAction(user, client, this));
        removeGreater.addActionListener(new RemoveGreaterAction(user, client, this));
        language.addActionListener(new ChangeLanguageAction(user, client, this));

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
        language.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\language.png")
                         .getImage()
                         .getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING)));
        executeScript.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\execute.png")
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
        actions.add(language);
        actions.addSeparator();
        actions.add(executeScript);
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
        JLabel loginTextLabel = new JLabel(resourceBundle.getString("WriteLogin"));
        JTextField loginField = new JTextField();
        JLabel passwordTextLabel = new JLabel(resourceBundle.getString("EnterPass"));
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
            int result = JOptionPane.showOptionDialog(null, panel, resourceBundle.getString("Login"), JOptionPane.YES_NO_OPTION,
                    QUESTION_MESSAGE, null, new String[]{resourceBundle.getString("Login"), resourceBundle.getString("Register")}, resourceBundle.getString("Login"));
            if (result == OK_OPTION) {
                if (!checkFields(loginField, passwordField, errorLabel)) continue;
                Response response = client.sendAndAskResponse(
                        new Request(
                                "ping",
                                "",
                                new User(loginField.getText(), String.valueOf(passwordField.getPassword()))));
                if (response.getStatus() == ResponseStatus.OK) {
                    errorLabel.setText(resourceBundle.getString("LoginAcc"));
                    errorLabel.setForeground(GREEN_OK);
                    this.user = new User(loginField.getText(), String.valueOf(passwordField.getPassword()));
                    return;
                } else {
                    errorLabel.setText(resourceBundle.getString("LoginNotAcc"));
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
                    errorLabel.setText(resourceBundle.getString("RegAcc"));
                    errorLabel.setForeground(GREEN_OK);
                    this.user = new User(loginField.getText(), String.valueOf(passwordField.getPassword()));
                    return;
                } else {
                    errorLabel.setText(resourceBundle.getString("RegNotAcc"));
                    errorLabel.setForeground(RED_WARNING);
                }
            } else if (result == CLOSED_OPTION) {
                System.exit(666);
            }
        }
    }

    private boolean checkFields(JTextField loginField, JPasswordField passwordField, JLabel errorLabel){
        if(loginField.getText().isEmpty()) {
            errorLabel.setText(resourceBundle.getString("LoginNotNull"));
            errorLabel.setForeground(RED_WARNING);
            return false;
        } else if(String.valueOf(passwordField.getPassword()).isEmpty()){
            errorLabel.setText(resourceBundle.getString("PassNotNull"));
            errorLabel.setForeground(RED_WARNING);
            return false;
        }
        return true;
    }

    public Collection<StudyGroup> getCollection() {
        return collection;
    }

    public static Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        GuiManager.locale = locale;
        Locale.setDefault(locale);
        ResourceBundle.clearCache();
        resourceBundle = ResourceBundle.getBundle("GuiLabels", locale);
        dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
        this.buttonsToChangeLocale.forEach((i, j) -> i.setText(resourceBundle.getString(j)));
        this.tableData = this.getTableDataStudyGroup();
        this.tableModel.setDataVector(this.tableData, columnNames);
        this.tableModel.fireTableDataChanged();
        this.frame.remove(panel);
        this.frame.setTitle(resourceBundle.getString("LabWork8"));
        this.run();
    }

    private void setFilters(){

    }
}
