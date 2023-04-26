package utility;

import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import exceptions.CommandRuntimeError;
import exceptions.ExitObliged;
import exceptions.IllegalArguments;
import exceptions.NoSuchCommand;
import managers.CommandManager;

import java.util.concurrent.Callable;
import java.util.concurrent.RecursiveTask;

public class RequestHandler implements Callable<Response> {
    private CommandManager commandManager;
    private Request request;

    public RequestHandler(CommandManager commandManager, Request request) {
        this.commandManager = commandManager;
        this.request = request;
    }

    @Override
    public Response call() {
        try {
            commandManager.addToHistory(request.getUser(), request.getCommandName());
            return commandManager.execute(request);
        } catch (IllegalArguments e) {
            return new Response(ResponseStatus.WRONG_ARGUMENTS,
                    "Неверное использование аргументов команды");
        } catch (CommandRuntimeError e) {
            return new Response(ResponseStatus.ERROR,
                    "Ошибка при исполнении программы");
        } catch (NoSuchCommand e) {
            return new Response(ResponseStatus.ERROR, "Такой команды нет в списке");
        } catch (ExitObliged e) {
            return new Response(ResponseStatus.EXIT);
        }
    }
}

