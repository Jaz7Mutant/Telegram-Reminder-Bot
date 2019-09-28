import java.util.SortedSet;

public interface NoteSerializer {
    public void serializeNotes(SortedSet notes);

    public SortedSet<Note> deserializeNotes();
}
