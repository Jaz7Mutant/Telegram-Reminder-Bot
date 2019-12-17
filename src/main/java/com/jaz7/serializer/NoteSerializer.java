package com.jaz7.serializer;

import com.jaz7.reminder.Note;

import java.util.SortedSet;

public interface NoteSerializer {
    void serializeNotes(SortedSet<Note> notes);

    SortedSet<Note> deserializeNotes();
}
