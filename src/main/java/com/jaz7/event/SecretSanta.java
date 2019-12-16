package com.jaz7.event;

import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.UserIO;
import com.jaz7.reminder.Note;
import com.jaz7.reminder.Reminder;
import com.jaz7.reminder.RespondParser;
import com.jaz7.reminder.UserState;
import com.jaz7.user.User;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SecretSanta implements EventBase {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Logger LOGGER = Logger.getLogger(SecretSanta.class.getSimpleName());
    private static UserIO userIO;
    private static Reminder reminder;
    private static LocalDateTime eventStartDate;
    private static LocalDateTime eventFinishDate;
    private static LocalDateTime eventLockDate;
    private static EventStatus eventStatus = EventStatus.UNAVAILABLE;
    public static String[] yesNoAnswers;
    private static String eventInfo = BotOptions.botAnswers.get("EventInfo");
    private static List<User> participants = Collections.synchronizedList(new ArrayList<>());
//    private static Map<EventStatus, BiConsumer<User, String>> commands = new HashMap<>();


    public SecretSanta(
            UserIO userIO,
            Reminder reminder,
            LocalDateTime eventStartDate,
            LocalDateTime eventFinishDate,
            LocalDateTime eventLockDate) throws DateTimeException {
        this.userIO = userIO;
        this.reminder = reminder;
        this.eventStartDate = eventStartDate;
        this.eventFinishDate = eventFinishDate;
        this.eventLockDate = eventLockDate; //todo schedule
        LocalDateTime now = LocalDateTime.now();
        if (eventFinishDate.isBefore(now))
            throw new DateTimeException("Event finish time is before now");
        if (eventStartDate.isBefore(now)) {
            eventStatus = EventStatus.AVAILABLE;
        } else {
            eventStatus = EventStatus.COMING_SOON;
        }
        yesNoAnswers = new String[]{
                BotOptions.botAnswers.get("Accept"),
                BotOptions.botAnswers.get("Decline"),
        };
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                User[] destinations = shiftLeft(participants.toArray(new User[0]), 1);
                for (int i = 0; i < destinations.length; i++){
                    userIO.showMessage(BotOptions.botAnswers.get("YouHaveMessage"), destinations[i].chatId);
                    userIO.showMessage(participants.get(i).wish, destinations[i].chatId);
                }
            }
        }, Date.from(eventFinishDate.atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public void eventInfo(String userMessage, String chatId) {
        userIO.showMessage(eventInfo, chatId);
        User user = Reminder.users.get(chatId);
        LocalDateTime now = LocalDateTime.now();
        if (!participants.contains(user) && now.isAfter(eventStartDate) && now.isBefore(eventLockDate)){
            userIO.showOnClickButton(BotOptions.botAnswers.get("EventInvite"), yesNoAnswers, chatId);
            user.currentState = UserState.RESPOND_TO_EVENT_INVITE;
        }
    }

    @Override
    public void doAction(String userMessage, String chatId) {
        User user = Reminder.users.get(chatId);
        if (!participants.contains(user)){
            userIO.showMessage(BotOptions.botAnswers.get("YouShouldJoinEventFirst"), chatId);
            return;
        }
        if (LocalDateTime.now().isBefore(eventLockDate)){
            userIO.showOnClickButton(BotOptions.botAnswers.get("ReadyToWish"), yesNoAnswers, chatId);
            user.currentState = UserState.RESPOND_TO_DO_WISH_OFFER;
        }
        else{
            userIO.showMessage(BotOptions.botAnswers.get("ItsTooLate"), chatId);
        }
    }

    @Override
    public UserState parseRespondToInvite(User user, String userMessage){
        boolean respond;
        try {
            respond = RespondParser.parseRespondToOfferRespond(userMessage, user.chatId);
        } catch (IllegalArgumentException e) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), user.chatId);
            return user.currentState;
        }
        if (respond){
            participants.add(user);
            LocalDateTime now = LocalDateTime.now();
            reminder.notes.add(
                    new Note(
                            user.chatId,
                            String.format("%s", BotOptions.botAnswers.get("WriteWishRemind")),
                            now.plusDays(1),
                            now,
                            1,
                            "Xmas"));
            userIO.showMessage(BotOptions.botAnswers.get("JoinEvent"), user.chatId);
        }
        return UserState.IDLE;
    }

    @Override
    public UserState parseRespondToDoWish(User user, String userMessage){
        boolean respond;
        try {
            respond = RespondParser.parseRespondToOfferRespond(userMessage, user.chatId);
        } catch (IllegalArgumentException e) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), user.chatId);
            return user.currentState;
        }
        if (respond){
            userIO.showMessage(BotOptions.botAnswers.get("WriteWish"), user.chatId);
            return UserState.SET_WISH;
        }
        return UserState.IDLE;
    }

    @Override
    public UserState setWish(User user, String userMessage){
        user.wish = userMessage;
        userIO.showMessage(BotOptions.botAnswers.get("WishHasBeenSet"), user.chatId);
        List<Note> remindToWishNotes = reminder.getUserNotes(user.chatId)
                .stream()
                .filter(x -> x.getToken().equals("Xmas"))
                .collect(Collectors.toList());
        for (Note note: remindToWishNotes){
            reminder.notes.remove(note);
        }
        return UserState.IDLE;
    }

    private static User[] shiftLeft(User[] a, int shift) {
        if (a != null) {
            int length = a.length;
            User[] b = new User[length];
            // шаг 1
            System.arraycopy(a, shift, b, 0, length - shift);
            // шаг 2
            System.arraycopy(a, 0, b, length - shift, shift);
            return b;
        } else {
            return null;
        }
    }
}
