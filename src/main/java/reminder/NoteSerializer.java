package reminder;

import java.util.SortedSet;

public interface NoteSerializer {
    public void serializeNotes(SortedSet<Note> notes);

    public SortedSet<Note> deserializeNotes();
}
