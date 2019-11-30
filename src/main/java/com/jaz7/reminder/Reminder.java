package com.jaz7.reminder;
import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.UserIO;

import java.util.*;
import java.util.logging.Logger;

public class Reminder {
    public final SortedSet<Note> notes; //Все заметки
    public NotePrinter notePrinter;
    public DateTimeParser dateTimeParser;
    public static final Map<String, NoteKeeper> userStates = new HashMap<String, NoteKeeper>(); //Все пользователи
    public static final List<String> readyToChatUsers = new ArrayList<>(); //Пользователи, которые ищут собеседника
    public static List<String> chattingUsers =  new ArrayList<>(); //Пользователи, которые находятся в состоянии общения

    private Random random = new Random();
    private Timer timer;
    private UserIO userIO;
    private static final Logger LOGGER = Logger.getLogger(Reminder.class.getSimpleName());

    public Reminder(UserIO userIO, int notePrinterPeriodInSeconds, NoteSerializer noteSerializer) {
        this.userIO = userIO;
        notes = Collections.synchronizedSortedSet(noteSerializer.deserializeNotes());
        notePrinter = new NotePrinter(userIO, notes, noteSerializer);
        dateTimeParser = new DateTimeParser(userIO);
        LOGGER.info("Reminder has been created");
        timer = new Timer();
        timer.schedule(notePrinter, 1000 * notePrinterPeriodInSeconds, 1000 * notePrinterPeriodInSeconds);
        LOGGER.info("NotePrinter has been started");
    }

    public void addMeeting(String command, String chatId){
        LOGGER.info(chatId + ": Switch to adding meeting state");
        userStates.get(chatId).currentState = UserState.ADDING;
        DateTimeParser.updateCurrentDate();
        userStates.get(chatId).noteAdder.addingState = AddingState.SET_MEETING;
        userIO.showMessage(BotOptions.botAnswers.get("WriteNote"), chatId);
    }

    public void joinMeeting(String command, String chatId){
        LOGGER.info(chatId + ": Switch to joining to meeting");
        userStates.get(chatId).currentState = UserState.JOINING;
        userIO.showMessage(BotOptions.botAnswers.get("SendToken"), chatId);
    }

    public void addNote(String command, String chatId) {
        // Спрашивает у пользователя String noteText, LocalDateTime remindDate, LocalDateTime eventDate
        // Добавляет новую заметку в лист заметок.
        // Вызывает checkNotesToPrint.
        // Показывает результат операции (напр. "Заметка установлена на *дата*")

        LOGGER.info(chatId + ": Switch to adding note state");
        userStates.get(chatId).currentState = UserState.ADDING;
        DateTimeParser.updateCurrentDate();
        userStates.get(chatId).noteAdder.addingState = AddingState.SET_TEXT;
        userIO.showMessage(BotOptions.botAnswers.get("WriteNote"), chatId);
    }

    public void removeNote(String command, String chatId) {
        // Удаляет заметку. Показывает пользователю список всех напоминаний предлагает выбрать
        // номер заметки в списке и удалить ее.

        LOGGER.info(chatId + ": Switch to removing note state");
        userStates.get(chatId).currentState = UserState.REMOVING;
        NotePrinter.showUsersNotes("2", chatId, this, UserState.REMOVING);
        if (NotePrinter.getUserNotes(this, chatId).size() <= 0) {
            userStates.get(chatId).currentState = UserState.IDLE;
        }
        else{
            userIO.showMessage(BotOptions.botAnswers.get("Delete"), chatId);
        }
    }

    public void showUserNotes(String command, String chatId) {
        // Позволяет вывесли ближайшие 10 событий, все события, события на сегодня. Всю инфу спрашивает у пользователя.

        LOGGER.info(chatId + ": Switch to showing notes state");
        userStates.get(chatId).currentState = UserState.SHOWING;
        userIO.showOnClickButton(BotOptions.botAnswers.get("ChoosePeriod"),
                new String[]{
                        BotOptions.botAnswers.get("ForToday"),
                        BotOptions.botAnswers.get("10Upcoming"),
                        BotOptions.botAnswers.get("All")
                }, chatId);
    }

    public void startChatting(String command, String chatId){
        synchronized (readyToChatUsers) {
            LOGGER.info(chatId + ": Switch to chatting state");
            userIO.showMessage(BotOptions.botAnswers.get("CurrentOnline") + chattingUsers.size(), chatId);
            chattingUsers.add(chatId);
            readyToChatUsers.add(chatId);
            switchCompanion(null, chatId);
        }
    }

    public void switchCompanion(String command, String chatId){
        synchronized (userStates) {
            synchronized (readyToChatUsers) {
                if (!readyToChatUsers.contains(chatId)) {
                    readyToChatUsers.add(chatId);
                }
                String currentCompanion = userStates.get(chatId).companionChatId;
                if (currentCompanion != null) {
                    userStates.get(currentCompanion).companionChatId = null;
                    //userIO.showMessage(BotOptions.botAnswers.get("CompanionLeft"), currentCompanion);
                    readyToChatUsers.add(currentCompanion);
                    //
                    // userIO.showMessage(BotOptions.botAnswers.get("LookingForCompanion"), currentCompanion);
                }
                userStates.get(chatId).companionChatId = null;
                userIO.showMessage(BotOptions.botAnswers.get("LookingForCompanion"), chatId);
                if (readyToChatUsers.size() == 1) {
                    userIO.showMessage(BotOptions.botAnswers.get("NoOnlineUsers"), chatId);
                } else {
                    String companion = getRandomUserToChat(chatId);
                    userStates.get(chatId).companionChatId = companion;
                    userStates.get(companion).companionChatId = chatId;
                    userIO.showMessage(BotOptions.botAnswers.get("ChattingWith") + chatId, companion);
                    userIO.showMessage(BotOptions.botAnswers.get("ChattingWith") + companion, chatId);
                    readyToChatUsers.remove(chatId);
                    readyToChatUsers.remove(companion);
                }
            }
        }
    }

    public void stopChatting(String command, String chatId){
        synchronized (readyToChatUsers) {
            LOGGER.info(chatId + ": Switching off chatting");
            chattingUsers.remove(chatId);
            readyToChatUsers.remove(chatId);
            String currentCompanion = userStates.get(chatId).companionChatId;
            if (currentCompanion != null && userStates.get(currentCompanion).companionChatId.equals(chatId)) {
                userStates.get(chatId).companionChatId = null;
                userIO.showMessage(BotOptions.botAnswers.get("CompanionLeft"), currentCompanion);
                //userIO.showMessage(BotOptions.botAnswers.get("LookingForCompanion"), currentCompanion);
                userStates.get(currentCompanion).companionChatId = null;
                readyToChatUsers.add(currentCompanion);
                switchCompanion(null, currentCompanion);
            }
            userIO.showMessage(BotOptions.botAnswers.get("LeftTheChat"), chatId);
        }
    }

    private String getRandomUserToChat(String finderChatId){
       String user = readyToChatUsers.get(random.nextInt(readyToChatUsers.size()));
       while (user.equals(finderChatId)){
           user = readyToChatUsers.get(random.nextInt(readyToChatUsers.size()));
       }
       return user;
    }
}
