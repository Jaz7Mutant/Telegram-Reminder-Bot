package com.jaz7.reminder;

import com.jaz7.bot.BotOptions;
import com.jaz7.inputOutput.UserIO;
import com.jaz7.user.User;

import java.util.List;
import java.util.logging.Logger;

public class NoteKeeper {
    public List<Note> userNotes;
    public NoteAdder noteAdder;
    public Note offeredNote;
    private UserIO userIO;
    private String chatId;
    private User user;
    public Reminder reminder;
    private NoteSerializer noteSerializer;
    private static final Logger LOGGER = Logger.getLogger(NoteKeeper.class.getSimpleName());

    public NoteKeeper(String chatId, UserIO userIO, User user, Reminder reminder, NoteSerializer noteSerializer) {
        this.reminder = reminder;
        this.chatId = chatId;
        this.userIO = userIO;
        this.user = user;
        this.noteSerializer = noteSerializer;
        this.noteAdder = new NoteAdder(userIO, noteSerializer, reminder, chatId, user);
        LOGGER.info(String.format("%s: NoteKeeper has been created", chatId));
    }

    public UserState joinMeeting(String userMessage) {
        for (Note currNote : reminder.notes) {
            if (currNote.getToken() == null) {
                continue;
            }
            if (currNote.getToken().equals(userMessage)) {
                reminder.notes.add(currNote.copy(chatId));
                reminder.notePrinter.run();
                int stringLimit = 20;
                if (currNote.getText().length() < 20) {
                    stringLimit = currNote.getText().length();
                }
                userIO.showMessage(
                        String.format(
                                "%s%s%s%s",
                                BotOptions.botAnswers.get("NewNote"),
                                currNote.getText().substring(0, stringLimit),
                                BotOptions.botAnswers.get("WithRemind"),
                                currNote.getRemindDate().format(NotePrinter.dateTimeFormatter)),
                        chatId);
                noteSerializer.serializeNotes(reminder.notes);
                LOGGER.info(String.format("%s: Joined meeting", chatId));
                return UserState.IDLE;
            }
        }
        userIO.showMessage("Wrong token", chatId);
        return UserState.IDLE;
    }

    public UserState removeNote(String userMessage) { // TODO Если владелец удаляет заметку, удалять все заметки с таким же токеном, без слова MEET в начале
        LOGGER.info(String.format("%s: Removing note...", chatId));
        userNotes = reminder.getUserNotes(chatId);
        int respond = RespondParser.parseRemoveNoteRespond(userMessage, userNotes, chatId);
        if (respond == -1) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return UserState.IDLE;
        }
        reminder.notes.remove(userNotes.get(respond - 1));
        userIO.showMessage(BotOptions.botAnswers.get("Removed"), chatId);
        noteSerializer.serializeNotes(reminder.notes); // TODO Можно убрать, если не хватает производительности
        LOGGER.info(String.format("%s: Note has been removed", chatId));
        return UserState.IDLE;
    }

    public UserState respondToOffer(String userMessage) {
        LOGGER.info(String.format("%s: Responding to offer", chatId));
        boolean respond;
        try {
            respond = RespondParser.parseRespondToOfferRespond(userMessage, chatId);
        } catch (IllegalArgumentException e) {
            userIO.showMessage(BotOptions.botAnswers.get("WrongFormat"), chatId);
            return user.currentState;
        }
        if (respond) {
            reminder.notes.add(offeredNote);
            userIO.showMessage(BotOptions.botAnswers.get("OfferAccept"), chatId);
            userIO.showMessage(BotOptions.botAnswers.get("CompanionAcceptYourOffer"), user.companionChatId);
        } else {
            offeredNote = null;
            userIO.showMessage(BotOptions.botAnswers.get("OfferDecline"), chatId);
            userIO.showMessage(BotOptions.botAnswers.get("CompanionDeclineYourOffer"), user.companionChatId);
        }
        return UserState.IDLE;
    }
}