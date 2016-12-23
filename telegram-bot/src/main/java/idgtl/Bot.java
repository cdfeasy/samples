package idgtl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.exceptions.TelegramApiValidationException;
import org.telegram.telegrambots.updatesreceivers.BotSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by d.asadullin on 13.07.2016.
 */
@Component
public class Bot extends TelegramLongPollingBot{
    Random random=new Random();
    AtomicInteger percent=new AtomicInteger(10);
    private static List<String> world=new ArrayList<>();
    private static List<String> tableflip=new ArrayList<>();
    static {
        world.add("unity");
        world.add("юнити");
        world.add("хуюнити");
        world.add("жаваскрипт");
        world.add("жабаскрипт");
        world.add("фича");
        world.add("скала");
        world.add("велосипед");
        world.add("версионирование");
        world.add("стейтлесс");
        world.add("хак");
        world.add("говнокод");
        world.add("треш");
        world.add("шл");
        world.add("синглтон");
        world.add("хакс");
        world.add("синглтон");
        world.add("канал");
        world.add("синтаксис");
        world.add("раст");
        world.add("фичи");
        world.add("тимофей");
        world.add("php");
        world.add("нода");
        world.add("пехепе");
        world.add("пхп");
        world.add("хуепаха");
        world.add("залупаха");
        world.add("c#");
        world.add("дота");
        world.add("рак");
        world.add("говнокод");

        tableflip.add(" (╯°□°）╯︵ ┻━┻");
        tableflip.add(" (┛◉Д◉)┛彡┻━┻");
        tableflip.add("  (ﾉ≧∇≦)ﾉ ﾐ ┸━┸");
        tableflip.add(" (ノಠ益ಠ)ノ彡┻━┻");
        tableflip.add(" (╯ರ ~ ರ）╯︵ ┻━┻");
        tableflip.add(" (ﾉ´･ω･)ﾉ ﾐ ┸━┸");
        tableflip.add(" (┛✧Д✧))┛彡┻━┻");

    }
    public void start() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            BotSession botSession = telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[]args){
        Bot bot=new Bot();
        bot.start();
    }

    @Override
    public String getBotUsername() {
        return "InYourPantsBot";
    }

    @Override
    public String getBotToken() {
        return "259066586:AAGA0qrNDbpUg-soilsSjSUwMCx73cEyVLw";
    }


    private boolean checkTableflip(String msg, Message message ){
         if("/tableflip".equals(message.getText())||"/tableflip@InYourPantsBot".equals(message.getText())){
            switch (random.nextInt(10)){
                case 1: sendMsg(message," (╯°□°）╯︵ ┻━┻"); break;
                case 2: sendMsg(message, " (┛◉Д◉)┛彡┻━┻");break;
                case 3: sendMsg(message, "  (ﾉ≧∇≦)ﾉ ﾐ ┸━┸");break;
                case 4: sendMsg(message, " (ノಠ益ಠ)ノ彡┻━┻");break;
                case 5: sendMsg(message, " (╯ರ ~ ರ）╯︵ ┻━┻");break;
                case 6: sendMsg(message, " (ﾉ´･ω･)ﾉ ﾐ ┸━┸");break;
                case 7: sendMsg(message, " (┛✧Д✧))┛彡┻━┻");break;
                default: sendMsg(message, "(ノಠ益ಠ)ノ彡┻━┻");break;
            }
            return true;
        }
        return false;
    }
    private void printTableflip(){
//        switch (random.nextInt(10)){
//            case 1: sendMsg(message," (╯°□°）╯︵ ┻━┻"); break;
//            case 2: sendMsg(message, " (┛◉Д◉)┛彡┻━┻");break;
//            case 3: sendMsg(message, "  (ﾉ≧∇≦)ﾉ ﾐ ┸━┸");break;
//            case 4: sendMsg(message, " (ノಠ益ಠ)ノ彡┻━┻");break;
//            case 5: sendMsg(message, " (╯ರ ~ ರ）╯︵ ┻━┻");break;
//            case 6: sendMsg(message, " (ﾉ´･ω･)ﾉ ﾐ ┸━┸");break;
//            case 7: sendMsg(message, " (┛✧Д✧))┛彡┻━┻");break;
//            default: sendMsg(message, "(ノಠ益ಠ)ノ彡┻━┻");break;
//        }
    }


    @Override
    public void onUpdateReceived(Update update) {

    //    System.out.println(message.getText());
        if(update.hasInlineQuery()){
            if(update.getInlineQuery().getQuery()!=null&&update.getInlineQuery().getQuery().contains("tableflip")){
               sendInlineMsg(update.getInlineQuery().getId());
            }
            return;
        }
        Message message = update.getMessage();

        if(message==null || message.getDate()==null ||!(new Date().getTime()/1000-message.getDate()<10)){
            return;
        }
        if(!message.hasText()){
            return;
        }
        String msg=message.getText();
        if(checkTableflip(msg,message)){
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
        if(message == null || !message.hasText()||message.getText().length()<3){
            return;
        }
        String[] parts=message.getText().toLowerCase().split(" ");
        if (!message.getText().startsWith("/")&&parts.length<=2&&!message.getText().contains("http")&& random.nextInt(100)<percent.get()) {
            String text=message.getText();
            text=text.toLowerCase();
            if(text.endsWith("!")||text.endsWith("?")||text.endsWith(".")||text.endsWith(",")){
                text=text.substring(0,text.length()-1);
            }
            switch (random.nextInt(5)){
                case 1: sendMsg(message, text + " у тебя в штанах"); break;
                case 2: sendMsg(message, "В штанах твоих " + text);break;
                default: sendMsg(message, "В штанах у тебя " + text); break;
            }
            return;
        }
        for(String part:parts){
            if((part.endsWith("ай")||part.endsWith("уй"))&& random.nextInt(100)<percent.get()){
                if(part.equals("хуй")){
                    sendMsg(message, "Лучше бы в штанах у тебя был "+part);
                    return;
                }
                switch (random.nextInt(2)){
                    case 0: sendMsg(message, "Штаны себе сперва "+part); break;
                    case 1: sendMsg(message, part+" в штаны себе, пёс");break;
                    default: sendMsg(message, "Штаны себе сперва " + part); break;
                }
                return;
            }
            if(world.contains(part)&& random.nextInt(100)<percent.get()){
                switch (random.nextInt(5)){
                    case 1: sendMsg(message, part + " у тебя в штанах"); break;
                    case 2: sendMsg(message, "В штанах твоих " + part);break;
                    default: sendMsg(message, "В штанах у тебя " + part); break;
                }
                return;
            }
        }



    }
    private void sendMsg(Message message, String text) {
       sendMsg(message,text,false);
    }

    private void sendMsg(Message message, String text, boolean reply) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(message.getChatId().toString());
            if (reply) {
                sendMessage.setReplyToMessageId(message.getMessageId());
            }
            sendMessage.setText(text);
            try {
                sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                System.out.println("cannot send "+text);
                e.printStackTrace();
            }
    }

    private void sendInlineMsg(String msgId) {
        AnswerInlineQuery inlineQuery=new AnswerInlineQuery();
        inlineQuery.setInlineQueryId(msgId);
        inlineQuery.setCacheTime(0);
        List<InlineQueryResult> list=new ArrayList<>();
        int i=1000;
        for(String s:tableflip){
            InlineQueryResultArticle res=new InlineQueryResultArticle();
            res.setId(Integer.toString(i++));
            res.setTitle(s);
            InputTextMessageContent textMessageContent=new InputTextMessageContent();
            textMessageContent.setMessageText(s);
            res.setInputMessageContent(textMessageContent);
            list.add(res);
        }
        inlineQuery.setResults(list);
        try {
            answerInlineQuery(inlineQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}