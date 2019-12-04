package com.jaz7.reminder;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseNoteSerializer extends AbstractNoteSerializer {
    private static Connection connection;
    private static final Logger LOGGER = Logger.getLogger("DBSerializer");

    @Override
    //более тупой варик, просто удаляю всю таблицу и с 0 заполняю заново
    public void serializeNotes(SortedSet<Note> notes) {
        try {
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
        SortedSet<Note> notes = new TreeSet<>(Comparator.comparing(Note::getRemindDate).
                thenComparing(Note::getText).
                thenComparing(Note::getChatId).
                thenComparing(Note::hashCode));
        try {
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

    //public void serializeNotes1(SortedSet<Note> notes) {
    //try {
    //LOGGER.info("Serializing notes...");
    //for (Note note : notes) {
    //int rowsUpdated = connection.createStatement().executeUpdate(
    //я не знаю что именно может измениться, поэтому обновляю все
    //можешь удалить отсюда те поля, которые не изменяются
    //предположил что token и chatId и не меняются
    //String.format(
    //"UPDATE Notes SET Text = '%s', EventDate = '%s', " +
    //"RemindDate = '%s', RemindPeriod = %d " +
    //"WHERE Token = '%s' AND ChatId = '%s'",
    //note.getText(), note.getEventDate().format(formatter),
    //note.getRemindDate().format(formatter), note.getRemindPeriod(),
    //note.getToken(), note.getChatId()));
    //if (rowsUpdated == 0)
    //connection.createStatement().executeUpdate(
    //String.format("INSERT Notes(Token, ChatId, Text, EventDate, RemindDate, RemindPeriod)" +
    //"VALUES (%s)", note.toStringValue()));
    //}
    //LOGGER.info("Notes has been serialized");
    //} catch (SQLException e) {
    //LOGGER.log(Level.WARNING, "Notes serializing error:" + e.getMessage(), e);
    //}
    //}

    public static void connectToDataBase() throws SQLException {
        String url = "jdbc:mysql://remotemysql.com/N8QPpqMaSc?serverTimezone=Europe/Moscow&autoReconnect=True";
        String username = "N8QPpqMaSc";
        String password = "KPt1jX9vmH";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException |
                NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = DriverManager.getConnection(url, username, password);
    }


}
