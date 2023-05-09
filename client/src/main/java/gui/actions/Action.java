package gui.actions;

import dtp.User;
import gui.GuiManager;
import models.StudyGroup;
import utility.Client;

import javax.swing.*;

public abstract class Action extends AbstractAction {
    protected User user;
    protected Client client;
    protected GuiManager guiManager;

    public Action(User user, Client client, GuiManager guiManager) {
        this.user = user;
        this.client = client;
        this.guiManager = guiManager;
    }
}
