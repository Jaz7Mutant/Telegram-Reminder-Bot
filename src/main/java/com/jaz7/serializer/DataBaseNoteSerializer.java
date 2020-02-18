package com.jaz7.serializer;

import com.jaz7.reminder.Note;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseNoteSerializer extends AbstractNoteSerializer {
    private static Connection connection;
    private static final Logger LOGGER = Logger.getLogger("DBSerializer");

    @Override
    public void serializeNotes(SortedSet<Note> notes){
        try {
            reconnectToDataBase();
            LOGGER.info("Serializing notes...");
            connection.createStatement().executeUpdate("TRUNCATE TABLE Notes");
            for (Note note : notes) {
                connection.createStatement().executeUpdate(
                        String.format("INSERT Notes(Token, ChatId, Text, EventDate, RemindDate, RemindPeriod)" +
                                "VALUES (%s)", note.toStringValue()));
            }
            LOGGER.info("Notes has been serialized");
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Notes serializing error: " + e.getMessage(), e);
        }
    }

    @Override
    public SortedSet<Note> deserializeNotes() {
        LOGGER.info("Deserializing notes...");
        SortedSet<Note> notes = new ConcurrentSkipListSet<>(Comparator.comparing(Note::getRemindDate).
                thenComparing(Note::getText).
                thenComparing(Note::getChatId).
                thenComparing(Note::hashCode));
        try {
            reconnectToDataBase();
            ResultSet result = connection.createStatement().executeQuery("SELECT * FROM Notes");
            while (result.next()) {
                notes.add(new Note(result.getString("ChatId"),
                        result.getString("Text"),
                        LocalDateTime.parse(result.getString("EventDate"), formatter),
                        LocalDateTime.parse(result.getString("RemindDate"), formatter),
                        result.getLong("RemindPeriod"),
                        result.getString("Token")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Notes deserializing error:" + e.getMessage(), e);
        }
        LOGGER.info("Notes has been deserialized");
        return notes;
    }


    private static void reconnectToDataBase() throws SQLException {
        while (!connection.isValid(610))
            connectToDataBase();
    }

    public static void connectToDataBase() throws SQLException {
        String url = null;
        String username = null;
        String password = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException |
                NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = DriverManager.getConnection(url, username, password);
    }


}
