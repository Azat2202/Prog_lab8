package gui;

import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import dtp.User;
import gui.actions.UpdateAction;
import models.Coordinates;
import models.StudyGroup;
import org.apache.commons.lang3.tuple.Pair;
import utility.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

class CartesianPanel extends JPanel {
    private Client client;
    private User user;
    private GuiManager guiManager;
    private LinkedHashMap<Rectangle, Integer> rectangles = new LinkedHashMap<>();
    public CartesianPanel(Client client, User user, GuiManager guiManager){
        super();
        this.client = client;
        this.user = user;
        this.guiManager = guiManager;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() != 2) return;
                Rectangle toClick;
                try {
                    toClick = rectangles.keySet().stream()
                            .filter(r -> r.contains(e.getPoint()))
                            .sorted(Comparator.comparing(Rectangle::getX).reversed())
                            .toList().get(0);
                } catch (ArrayIndexOutOfBoundsException k) {
                    return;
                }
                Integer id = rectangles.get(toClick);
                new UpdateAction(user, client, guiManager).updateJOptionWorker(id);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        // Draw x-axis
        g2.drawLine(0, height / 2, width, height / 2);

        // Draw y-axis
        g2.drawLine(width / 2, 0, width / 2, height);

        // Draw arrows
        g2.drawLine(width - 10, height / 2 - 5, width, height / 2);
        g2.drawLine(width - 10, height / 2 + 5, width, height / 2);
        g2.drawLine(width / 2 - 5, 10, width / 2, 0);
        g2.drawLine(width / 2 + 5, 10, width / 2, 0);
        this.drawRectangles(g2);
    }

    private void drawRectangles(Graphics2D g2){
        Random random = new Random();
        Response response = client.sendAndAskResponse(new Request("show", "", user));
        Map<String, Color> users = response.getCollection().stream()
                .map(StudyGroup::getUserLogin)
                .distinct()
                .collect(Collectors.toMap(
                    s -> s, s -> {
                    int red = random.nextInt(25) * 10;
                    int green = random.nextInt(25) * 10;
                    int blue = random.nextInt(25) * 10;
                    return new Color(red, green, blue);
                }));
        if(response.getStatus() != ResponseStatus.OK) return;
        int width = getWidth();
        int halfWidth = width / 2;
        int height = getHeight();
        int halfHeight = height / 2;
        int elementWidth = 130;
        int elementHeight = 130;
        int fontSize = 45;
        float delta = 0.2F;

        /* ЭТО ОЧЕНЬ СТРАШНЫЙ МЕТОД
        ОН НУЖЕН ЧТОБЫ СОСЕДНИЕ КВАДРАТЫ СДВИГАЛИСЬ
        НО ОН РАБОТАЕТ ЗА О(n^2) а можно СПОКОЙНО написать за О(n)
        Н-О М-Н-Е Л-Е-Н-Ь
         */

        while(response.getCollection().stream().map(StudyGroup::getCoordinates).distinct().count() < response.getCollection().size()){
            for(StudyGroup studyGroup : response.getCollection()){
                if(response.getCollection().stream()
                        .anyMatch((i) -> i.getCoordinates().equals(studyGroup.getCoordinates())
                                && !i.getId().equals(studyGroup.getId()))){
                    studyGroup.getCoordinates().setX(studyGroup.getCoordinates().getX() + delta);
                    studyGroup.getCoordinates().setY(studyGroup.getCoordinates().getY() + delta);
                    break;
                }
            }
        }

        float maxCordX = response.getCollection().stream()
                .map(StudyGroup::getCoordinates)
                .map(Coordinates::getX)
                .max(Float::compareTo)
                .orElse(0F);
        Double maxCordY = response.getCollection().stream()
                .map(StudyGroup::getCoordinates)
                .map(Coordinates::getY)
                .max(Double::compareTo)
                .orElse(0D);
        BufferedImage img;
        try {
            img = ImageIO.read(new File("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\icons\\tyler.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        this.rectangles = new LinkedHashMap<>();
        response.getCollection().stream().sorted(StudyGroup::compareTo).forEach(studyGroup -> {
            int dx1 = (int) (halfWidth + (studyGroup.getCoordinates().getX() / maxCordX * (halfWidth - elementWidth)));
            int dx2 = (int) (halfHeight + (studyGroup.getCoordinates().getY() / maxCordY * (halfHeight - elementHeight)));
            this.rectangles.put( new Rectangle(dx1 - elementWidth / 2 - 1,
                    dx2 - elementHeight / 2 - 1,
                    elementWidth + 2,
                    elementHeight + 2), studyGroup.getId());
            //Image
            g2.drawImage(img,
                    dx1 - elementWidth / 2,
                    dx2 - elementHeight / 2,
                    dx1 + elementWidth / 2,
                    dx2 + elementHeight / 2,
                    0,
                    0,
                    img.getWidth(),
                    img.getHeight(),
                    null
            );
            //Border
            g2.setColor(users.get(studyGroup.getUserLogin()));
            g2.drawRect(dx1 - elementWidth / 2 - 1,
                    dx2 - elementHeight / 2 - 1,
                    elementWidth + 2,
                    elementHeight + 2);
            g2.setColor(Color.WHITE);
            //Numbers
            g2.drawString(studyGroup.getId().toString(),
                    dx1 - elementWidth / 4,
                    dx2 + elementHeight / 4
                    );
        });
    }
}