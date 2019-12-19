package com.jaz7.event;

import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.UserIO;
import com.jaz7.reminder.Note;
import com.jaz7.reminder.Reminder;
import com.jaz7.reminder.RespondParser;
import com.jaz7.user.User;
import com.jaz7.user.UserState;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SecretSanta implements BotEvent {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Logger LOGGER = Logger.getLogger(SecretSanta.class.getSimpleName());
    private  UserIO userIO;
    private  Reminder reminder;
    private  LocalDateTime eventStartDate;
    private  LocalDateTime eventFinishDate;
    private  LocalDateTime eventLockDate;
    private static String[] yesNoAnswers;
    private static String eventInfo = BotOptions.botAnswers.get("EventInfo");
    private static List<User> participants = new CopyOnWriteArrayList<>();

    public SecretSanta(
            UserIO userIO,
            Reminder reminder,
            LocalDateTime eventStartDate,
            LocalDateTime eventFinishDate,
            LocalDateTime eventLockDate) throws DateTimeException {
        LOGGER.info("Initializing Secret Santa...");
        this.userIO = userIO;
        this.reminder = reminder;
        this.eventStartDate = eventStartDate;
        this.eventFinishDate = eventFinishDate;
        this.eventLockDate = eventLockDate;
        LocalDateTime now = LocalDateTime.now();
        if (eventFinishDate.isBefore(now))
            throw new DateTimeException("Event finish time is before now");
        yesNoAnswers = new String[]{
                BotOptions.botAnswers.get("Accept"),
                BotOptions.botAnswers.get("Decline"),
        };
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                User[] destinations = leftShiftToArray(participants.toArray(new User[0]), 1);
                if (destinations != null) {
                    for (int i = 0; i < destinations.length; i++){
                        userIO.showMessage(BotOptions.botAnswers.get("YouHaveMessage"), destinations[i].chatId);
                        userIO.showMessage(participants.get(i).wish, destinations[i].chatId);
                    }
                }
            }
        }, Date.from(eventFinishDate.atZone(ZoneId.systemDefault()).toInstant()));
        LOGGER.info("Secret Santa has been initialized");
    }

    @Override
    public void eventInfo(String userMessage, String chatId) {
        LOGGER.info(String.format("%s: Showing event info", chatId));
        userIO.showMessage(eventInfo, chatId);
        User user = Reminder.users.get(chatId);
        LocalDateTime now = LocalDateTime.now();
        if (!participants.contains(user) && now.isAfter(eventStartDate) && now.isBefore(eventLockDate)){
            userIO.showOnClickButton(BotOptions.botAnswers.get("EventInvite"), yesNoAnswers, chatId);
            user.currentState = UserState.RESPOND_TO_EVENT_INVITE;
            LOGGER.info(String.format("%s: Responding to invite to event", chatId));
        }
    }

    @Override
    public void doAction(String userMessage, String chatId) {
        LOGGER.info(String.format("%s: Doing event action...", chatId));
        User user = Reminder.users.get(chatId);
        if (!participants.contains(user)){
            LOGGER.info(String.format("%s: User is not in participants list", chatId));
            userIO.showMessage(BotOptions.botAnswers.get("YouShouldJoinEventFirst"), chatId);
            return;
        }
        if (LocalDateTime.now().isBefore(eventLockDate)){
            LOGGER.info(String.format("%s: Asking to set wish", chatId));
            userIO.showOnClickButton(BotOptions.botAnswers.get("ReadyToWish"), yesNoAnswers, chatId);
            user.currentState = UserState.RESPOND_TO_DO_WISH_OFFER;
        }
        else{
            userIO.showMessage(BotOptions.botAnswers.get("ItsTooLate"), chatId);
        }
    }

    @Override
    public UserState respondToInvite(User user, String userMessage){
        LOGGER.info(String.format("%s: Parsing invite respond", user.chatId));
        boolean respond;
        try {
            respond = RespondParser.parseRespondToOfferRespond(userMessage, user.chatId);
        } catch (IllegalArgumentException e) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), user.chatId);
            return user.currentState;
        }
        if (respond){
            LOGGER.info(String.format("%s: User joined the event", user.chatId));
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
    public UserState respondToDoWish(User user, String userMessage){
        LOGGER.info(String.format("%s: Parsing respond to do wish", user.chatId));
        boolean respond;
        try {
            respond = RespondParser.parseRespondToOfferRespond(userMessage, user.chatId);
        } catch (IllegalArgumentException e) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), user.chatId);
            return user.currentState;
        }
        if (respond){
            LOGGER.info(String.format("%s: Switch to setting wish state", user.chatId));
            userIO.showMessage(BotOptions.botAnswers.get("WriteWish"), user.chatId);
            return UserState.SET_WISH;
        }
        return UserState.IDLE;
    }

    @Override
    public UserState setWish(User user, String userMessage){
        LOGGER.info(String.format("%S: Setting wish", user.chatId));
        user.wish = userMessage;
        userIO.showMessage(BotOptions.botAnswers.get("WishHasBeenSet"), user.chatId);
        List<Note> remindToWishNotes = reminder.getUserNotes(user.chatId)
                .stream()
                .filter(x -> x.getToken().equals("Xmas"))
                .collect(Collectors.toList());
        for (Note note: remindToWishNotes){
            reminder.notes.remove(note);
        }
        LOGGER.info(String.format("%s: Wish has been set", user.chatId));
        return UserState.IDLE;
    }

    private static User[] leftShiftToArray(User[] a, int shift) {
        LOGGER.info(String.format("Shifting array with size: %d", shift));
        if (a != null) {
            int length = a.length;
            User[] b = new User[length];
            System.arraycopy(a, shift, b, 0, length - shift);
            System.arraycopy(a, 0, b, length - shift, shift);
            return b;
        } else {
            return null;
        }
    }
}
