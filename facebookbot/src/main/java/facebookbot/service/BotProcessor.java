package facebookbot.service;

import facebookbot.entity.resp.Messaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Created by d.asadullin on 25.07.2016.
 */
@Component
@Scope("singleton")
public class BotProcessor {
    @Autowired
    FacebookSender sender;

    private Logger logger = LoggerFactory.getLogger(BotProcessor.class.getName());
    private static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    @Autowired
    SessionService sessionService;

 //   @Autowired
  //  SmsSender smsSender;

    public static String start="/start";
    public static String helpShort="Help";
    public static String helpRus="Помощь";
    public static String balanceShort="Balance";
    public static String balanceRus="Баланс";
    public static String newsShort="News";;
    public static String newsRus="Новости";;
    public static String exitShort="Exit";
    public static String exitRus="Выход";
    public static String help=new String(new byte[]{})+" "+helpShort;
    public static String balance=" "+balanceShort;
    public static String news=" "+newsShort;;
    public static String exit=" "+exitShort;
    public static Pattern p = Pattern.compile("[0-9]{7,17}");
    public static String helpMessage=String.format("Список доступных команд:\\r\\n " +
            "%s (%s) - узнать последние новости\\r\\n " +
            "%s (%s) - проверить баланс карты\\r\\n " +
            "%s (%s) - узнать список команд\\r\\n " +
            "%s (%s) - выйти",news,newsRus,balance,balanceRus,help,helpRus,exit,exitRus);
    String prop;



    public void process(Messaging message) {
        Session session = sessionService.get(message.getSender().getId());
        if (message.getMessage()!=null&&message.getMessage().getText()!=null) {
            logger.info("Get request from [{}] with text[{}]",message.getSender().getId(),message.getMessage().getText());
//            if (message.getText().equals("/help"))
//                sendMsg(message, "no data");
//            else
//                sendMsg(message, message.getText()+" "+message.getText());
            handle(message, session);
        }
    }

    private boolean needLogin(Session session) {
        return !session.isApproved;
    }

    private boolean needMsisdn(Session session) {
        return session.getMsisdn() == null;
    }

    private boolean processApproving(Messaging message, Session session) {
        if (session.getSendKey()== null) {
            String key = "12345";//Integer.toString(new Random().nextInt(100000));
           // if (smsSender.send(session.getMsisdn(), "Ваш код подтверждения: "+key)) {
            if (true) {
                sendSimpleMsg(message, "Спасибо! Вам отправлено SMS сообщение с кодом подтверждения.\\r\\nВведите полученный код.");
                logger.info("[{}] code [{}] sent",message.getSender().getId(),key);
                session.setSendKey(key);
            }
        } else {
            if (message.getMessage().getText().equals(session.getSendKey())) {
                session.setIsApproved(true);
                logger.info("[{}] auth completed",message.getSender().getId());
                sendSimpleMsg(message, "Поздравляем!");
                sendHelpMessage(message);
                return true;
            } else {
                logger.info("[{}] incorrect code",message.getSender().getId());
                session.setTryCount(session.getTryCount()+1);
                if(session.getTryCount()>=3){
                    sendSimpleMsg(message, "Лимит попыток превышен");
                    session.clear();
                }else {
                    if(session.getTryCount()==1) {
                        sendSimpleMsg(message, "Некорректный код подтверждения. Осталось 2 попытки");
                    }
                    if(session.getTryCount()==2) {
                        sendSimpleMsg(message, "Некорректный код подтверждения. Осталось 1 попытка");
                    }
                }
            }
        }
        return false;
    }

    private boolean processGetMsisdn(Messaging message, Session session) {
        String text = message.getMessage().getText();
        text = text.replaceAll("[^0-9]+", "");
        if(text.length()==10){
            text="7"+text;
        } else if (text.length()==11&& text.startsWith("8")) {
            text = "7" + text.substring(1);
        }else if(text.length()!=11){
            sendSimpleMsg(message, "Если вы являетесь клиентом Citibank, то введите Ваш номер телефона. Если нет, то свяжитесь с оператором CitiPhone по телефону 8 (800) 700-38-38");
            return false;
        }
        session.setMsisdn(text);
        return true;
    }


    public void handle(Messaging message, Session session) {
        processStart(message,session);
        if(session.getIsNew()){
            sendSimpleMsg(message, "Citibank Telegram Bot рад приветствовать Вас! ");
            session.setIsNew(false);
        }
        if("noauth".equals(message.getMessage().getText())){
            session.setMsisdn("000000000");
            session.setIsApproved(true);
        }
        if(processExit(message, session)){
            return;
        }
        if(processHelp(message, session)){
            return;
        }
        if (needMsisdn(session)) {
            if (!processGetMsisdn(message, session)) {
                //sendSimpleMsg(message, "Если вы являетесь клиентом Citibank, то введите Ваш номер телефона. Если нет, то свяжитесь с оператором CitiPhone по телефону 8 (800) 700-38-38");
                return;
            }
        }
        if (needLogin(session)) {
            if (!processApproving(message, session)) {
                return;
            }
            return;
        }
        processCommands(message,session);

    }
    public boolean processExit(Messaging message, Session session){
        if(exitShort.equals(message.getMessage().getText())||exit.equals(message.getMessage().getText())||exitRus.equals(message.getMessage().getText())){
            session.clear();
            session.setIsNew(true);
            sendSimpleMsg(message, "Спасибо за использование нашего сервиса");
            return true;
        }
        return false;
    }

    public boolean processStart(Messaging message, Session session){
        if(start.equals(message.getMessage().getText())){
            session.clear();
            return true;
        }
        return false;
    }

    private void sendHelpMessage(Messaging message){
        Map<String,String> map=new LinkedHashMap<>();
        map.put(newsRus,newsShort);
        map.put(balanceRus,balanceShort);
        map.put(helpRus,helpShort);
        map.put(exitRus,exitShort);
        sender.sendMenu("Список доступных команд:",message.getSender().getId(), map);
    }
    public boolean processHelp(Messaging message, Session session) {
        if (helpShort.equals(message.getMessage().getText())||help.equals(message.getMessage().getText())||helpRus.equals(message.getMessage().getText())){
            session.setView(Session.View.HELP);
            sendHelpMessage(message);
            return true;
        }
        return false;
    }

    public boolean processNews(Messaging message, Session session){
        if(newsShort.equals(message.getMessage().getText())||news.equals(message.getMessage().getText())||newsRus.equals(message.getMessage().getText())){
            session.setView(Session.View.NEWS);
            sendSimpleMsg(message, String.format("Изменение тарифов для держателей кредитных карт\\r\\n" +
                    "05 июля 2016 года\\r\\n" +
                    "Уважаемые клиенты Ситибанка!\\r\\n" +
                    "С 5 августа 2016 года вступают в силу новые тарифы для клиентов," +
                    "заключивших договор о выпуске и обслуживании кредитных карт до 31 декабря 2014 года."));
            return true;
        }
        return false;
    }
    public boolean processBalance(Messaging message, Session session){
        if(balanceShort.equals(message.getMessage().getText())||balance.equals(message.getMessage().getText())||balanceRus.equals(message.getMessage().getText())){
            session.setView(Session.View.BALANCE);
            sendSimpleMsg(message, String.format("Введите последние 4 цифры номера вашей карты"));
            return true;
        } else if(Session.View.BALANCE.equals(session.getView())){
            int val=-1;
            try{
                val=Integer.parseInt(message.getMessage().getText());
            }catch (NumberFormatException ex){

            }
            if(message.getMessage().getText().length()==4 && val>=0){
                sendSimpleMsg(message, String.format("Карта *%s. %s. Доступно 10000.99 руб.",message.getMessage().getText(),format.format(new Date())));
            }else{
                sendSimpleMsg(message, String.format("Введите последние 4 цифры номера вашей карты"));
            }
            return true;
        }
        return false;
    }



    public void  processCommands(Messaging message, Session session) {
        if(processNews(message,session)){
            return;
        }
        if(processBalance(message, session)){
            return;
        }

        sendSimpleMsg(message, String.format("Неизвестная команда"));
    }


    private void sendSimpleMsg(Messaging message, String text) {
       sender.send(text,message.getSender().getId());
    }

}
