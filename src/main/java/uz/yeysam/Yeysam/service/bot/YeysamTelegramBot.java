package uz.yeysam.Yeysam.service.bot;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.yeysam.Yeysam.config.bot.BotConfiguration;
import uz.yeysam.Yeysam.service.base.UserService;

@Service
@RequiredArgsConstructor
public class YeysamTelegramBot extends TelegramLongPollingBot {
    private final UserService userService;
    private final BotConfiguration botConfiguration;

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        System.out.println(update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            var text = message.getText();
            var from = message.getFrom();

            if ("/start".equals(text)) {
                userService.registerOrUpdate(from);
                SendMessage reply = new SendMessage();
                reply.setChatId(message.getChatId().toString());
                reply.setText("Welcome, " + from.getFirstName() + "! Use /help to see commands.");
                execute(reply);
            }
            // TODO: handle other commands like /help, /search, etc.
        }
    }

    @Override
    public String getBotUsername() {
        return botConfiguration.getBotName();
    }


    @Override
    public String getBotToken() {
        return botConfiguration.getBotToken();
    }
}
