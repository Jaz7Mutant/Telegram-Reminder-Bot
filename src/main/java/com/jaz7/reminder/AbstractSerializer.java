package com.jaz7.reminder;

import java.time.format.DateTimeFormatter;
import java.util.SortedSet;
import java.util.logging.Logger;

public abstract class AbstractSerializer implements NoteSerializer {
    protected static final Logger LOGGER = Logger.getLogger(JsonNoteSerializer.class.getSimpleName());
    protected static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public abstract void serializeNotes(SortedSet<Note> notes);

    @Override
    public  abstract SortedSet<Note> deserializeNotes();
}
