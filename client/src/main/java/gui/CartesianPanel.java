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
        int width = getWidth();
        int halfWidth = width / 2;
        int height = getHeight();
        int halfHeight = height / 2;
        int elementWidth = 90;
        int elementHeight = 90;
        BufferedImage img;
        try {
            img = ImageIO.read(new File("C:\\Users\\azat2\\IdeaProjects\\Prog_lab8\\client\\tyler.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedImage resized = new BufferedImage(elementWidth, elementHeight, img.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        System.out.println(height);
        System.out.println(width);
        response.getCollection().forEach(studyGroup -> {
            g2.drawImage(img,
                    (int) (halfWidth + (studyGroup.getCoordinates().getX() / maxCordX * halfWidth)) - elementWidth / 2,
                    (int) (halfHeight + (studyGroup.getCoordinates().getY() / maxCordY * halfHeight)) - elementHeight / 2,
                    (int) (halfWidth + (studyGroup.getCoordinates().getX() / maxCordX * halfWidth)) + elementWidth / 2,
                    (int) (halfHeight + (studyGroup.getCoordinates().getY() / maxCordY * halfHeight)) + elementHeight / 2,
                    0,
                    0,
                    img.getWidth(),
                    img.getHeight(),
                    null
            );
            System.out.println(halfWidth + (studyGroup.getCoordinates().getX() / maxCordX * halfWidth));
            System.out.println((halfHeight + (studyGroup.getCoordinates().getY() / maxCordY * halfHeight)));
        });
    }
}