package idgtl;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by d.asadullin on 23.08.2016.
 */
public class SkypeBot {
    Random random=new Random();
    AtomicInteger percent=new AtomicInteger(10);
    public static void main(String[] args) throws ConnectionException, InvalidCredentialsException, NotParticipatingException, InterruptedException {
        Skype skype = new SkypeBuilder("live:cdf.easy_1", "YouPantsBotPass1").withAllResources().build();
        skype.login();
        skype.getEventDispatcher().registerListener(new Listener() {
            @EventHandler
            public void onMessage(MessageReceivedEvent e) {
                System.out.println("Got message: " + e.getMessage().getContent());
            }
        });
        skype.subscribe();
        Thread.sleep(100000);
// Do stuff
        skype.logout();
    }

    public String getBotUsername() {
        return "InYourPantsBot";
    }

    public String getBotToken() {
        return "259066586:AAGA0qrNDbpUg-soilsSjSUwMCx73cEyVLw";
    }

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
        if (message != null && message.hasText() && message.getText().split(" ").length<=2&& random.nextInt(100)<percent.get()) {
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
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);

    }
}
