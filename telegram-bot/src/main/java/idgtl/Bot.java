package idgtl;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by d.asadullin on 13.07.2016.
 */
public class Bot extends TelegramLongPollingBot {
    Random random=new Random();
    AtomicInteger percent=new AtomicInteger(10);
    public static void main(String[] args) {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "InYourPantsBot";
    }

    @Override
    public String getBotToken() {
        return "259066586:AAGA0qrNDbpUg-soilsSjSUwMCx73cEyVLw";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
    //    System.out.println(message.getText());
        if(message==null || message.getDate()==null ||!(new Date().getTime()/1000-message.getDate()<10)){
            return;
        }
        if(message != null && message.hasText()&& message.getText().startsWith("/percent=")){
            try{
                int val=Integer.valueOf( message.getText().replace("/percent=",""));
                if(val>1&&val<=100){
                    percent.set(val);
                    System.out.println("Процент установлен в "+percent.get());

                }
            } catch (NumberFormatException ex){

            }
        }
//        if (message != null && message.hasText()) {
//            switch (random.nextInt(5)){
//                case 1: sendMsg(message, message.getText()+" у тебя в штанах"); break;
//                case 2: sendMsg(message, "В штанах твоих "+message.getText());break;
//                default: sendMsg(message, "В штанах у тебя "+message.getText()); break;
//            }
//        }
        if (message != null && message.hasText() && message.getText().split(" ").length<=2&&!message.getText().contains("http")&& random.nextInt(100)<percent.get()) {
            switch (random.nextInt(5)){
                case 1: sendMsg(message, message.getText()+" у тебя в штанах"); break;
                case 2: sendMsg(message, "В штанах твоих "+message.getText());break;
                default: sendMsg(message, "В штанах у тебя "+message.getText()); break;
            }
        }
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplayToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}