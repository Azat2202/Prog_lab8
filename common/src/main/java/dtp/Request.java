package dtp;

import models.StudyGroup;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

public class Request implements Serializable {
    private final String commandName;
    private String args = "";
    private StudyGroup object = null;
    private User user;
    private Locale locale;

    public Request(String commandName, String args, User user, Locale locale) {
        this.commandName = commandName.trim();
        this.args = args;
        this.user = user;
        this.locale = locale;
    }

    public Request(String commandName, User user, StudyGroup object, Locale locale) {
        this.commandName = commandName.trim();
        this.object = object;
        this.user = user;
        this.locale = locale;
    }

    public Request(String commandName, String args, User user, StudyGroup object, Locale locale) {
        this.commandName = commandName.trim();
        this.args = args.trim();
        this.object = object;
        this.user = user;
        this.locale = locale;
    }

    public boolean isEmpty() {
        return commandName.isEmpty() && args.isEmpty() && object == null;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getArgs() {
        return args;
    }

    public StudyGroup getObject() {
        return object;
    }

    public User getUser() {
        return user;
    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request request)) return false;
        return Objects.equals(commandName, request.commandName) && Objects.equals(args, request.args) && Objects.equals(object, request.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandName, args, object, user);
    }

    @Override
    public String toString(){
        return "Request[" + commandName + ',' + user +
                (args.isEmpty()
                        ? ""
                        : "," + args ) +
                ((object == null)
                        ? "]"
                        : "," + object + "]");
    }
}
