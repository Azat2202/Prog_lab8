package gui;

import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import dtp.User;
import models.Coordinates;
import models.StudyGroup;
import utility.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

class CartesianPanel extends JPanel {
    private Client client;
    private User user;
    public CartesianPanel(Client client, User user){
        super();
        this.client = client;
        this.user = user;
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
        Response response = client.sendAndAskResponse(new Request("show", "", user));
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
            img = ImageIO.read(new File("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\tyler.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setFont(new Font("Tahoma", Font.BOLD, fontSize));

        response.getCollection().stream().sorted(StudyGroup::compareTo).forEach(studyGroup -> {
            //Image
            g2.drawImage(img,
                    (int) (halfWidth + (studyGroup.getCoordinates().getX() / maxCordX * (halfWidth - elementWidth))) - elementWidth / 2,
                    (int) (halfHeight + (studyGroup.getCoordinates().getY() / maxCordY * (halfHeight - elementHeight))) - elementHeight / 2,
                    (int) (halfWidth + (studyGroup.getCoordinates().getX() / maxCordX * (halfWidth - elementWidth))) + elementWidth / 2,
                    (int) (halfHeight + (studyGroup.getCoordinates().getY() / maxCordY * (halfHeight - elementHeight))) + elementHeight / 2,
                    0,
                    0,
                    img.getWidth(),
                    img.getHeight(),
                    null
            );
            //Border
            g2.drawRect((int) (halfWidth + (studyGroup.getCoordinates().getX() / maxCordX * (halfWidth - elementWidth))) - elementWidth / 2 - 1,
                    (int) (halfHeight + (studyGroup.getCoordinates().getY() / maxCordY * (halfHeight - elementHeight))) - elementHeight / 2 - 1,
                    elementWidth + 2,
                    elementHeight + 2);
            //Number
            g2.drawString(studyGroup.getId().toString(),
                    (int) (halfWidth + (studyGroup.getCoordinates().getX() / maxCordX * (halfWidth - elementWidth))) - elementWidth / 4,
                    (int) (halfHeight + (studyGroup.getCoordinates().getY() / maxCordY * (halfHeight - elementHeight))) + elementHeight / 4
                    );
        });
    }
}