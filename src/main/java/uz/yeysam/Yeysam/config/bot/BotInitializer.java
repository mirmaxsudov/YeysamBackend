package uz.yeysam.Yeysam.config.bot;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.yeysam.Yeysam.service.bot.YeysamTelegramBot;

@Component
@RequiredArgsConstructor
public class BotInitializer {
    private static final Logger log = LoggerFactory.getLogger(BotInitializer.class);
    private final YeysamTelegramBot yeysamTelegramBot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(yeysamTelegramBot);
        } catch (TelegramApiException ignored) {
            log.error("Error occurred: ");
        }
    }
}