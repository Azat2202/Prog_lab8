package commands;

import dtp.Request;
import dtp.Response;
import dtp.ResponseStatus;
import exceptions.IllegalArguments;

/**
 * Команда 'register'
 * Регистрирует пользователя
 */
public class Register extends Command {

    public Register() {
        super("register", ": Зарагестрировать пользователя");
    }

    /**
     * Исполнить команду
     * @param request запрос клиента
     * @throws IllegalArguments неверные аргументы команды
     */
    @Override
    public Response execute(Request request) throws IllegalArguments {
        this.commandLogger.debug("получен юзер: " + request.getUser());
        return new Response(ResponseStatus.OK,"Вы успешно зарегистрированы!");
    }
}
