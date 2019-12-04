package com.jaz7.reminder;

import java.time.format.DateTimeFormatter;
import java.util.SortedSet;

public abstract class AbstractNoteSerializer implements NoteSerializer {
    protected static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public abstract void serializeNotes(SortedSet<Note> notes);

    @Override
    public  abstract SortedSet<Note> deserializeNotes();
}
