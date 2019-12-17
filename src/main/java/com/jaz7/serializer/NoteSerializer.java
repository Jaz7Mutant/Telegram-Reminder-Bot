package com.jaz7.reminder;

import java.util.SortedSet;

public interface NoteSerializer {
    void serializeNotes(SortedSet<Note> notes);

    SortedSet<Note> deserializeNotes();
}
