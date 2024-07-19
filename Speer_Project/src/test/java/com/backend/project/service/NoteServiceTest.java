package com.backend.project.service;

import com.backend.project.entity.Note;
import com.backend.project.entity.SharedNote;
import com.backend.project.entity.User;
import com.backend.project.enums.Messages;
import com.backend.project.exception.AccessDeniedException;
import com.backend.project.exception.NoteNotFoundException;
import com.backend.project.exception.UserNotFoundException;
import com.backend.project.model.NoteDetails;
import com.backend.project.model.NoteTitleContent;
import com.backend.project.repository.NoteRepository;
import com.backend.project.repository.SharedNoteRepository;
import com.backend.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotesServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private NoteRepository noteRepo;

    @Mock
    private SharedNoteRepository sharedNoteRepo;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private NotesService notesService;

    private User user;
    private Note note;
    private NoteDetails noteDetails;
    private NoteTitleContent noteTitleContent;
    private SharedNote sharedNote;

    @BeforeEach
    void setUp() {
        user = new User("testUser", "encodedPassword");
        note = new Note();
        note.setId(1L);
        note.setTitle("Test Title");
        note.setContent("Test Content");
        note.setUser(user);

        noteDetails = new NoteDetails(null, "Test Title", "Test Content");
        noteTitleContent = new NoteTitleContent("Test Title", "Test Content");

        sharedNote = new SharedNote();
        sharedNote.setNote(note);
        sharedNote.setSharedWith(user);
    }

    @Test
    @DisplayName("Test getUserFromJwts success")
    void getUserFromJwtsPositive() {
        when(jwtService.getSubject(anyString())).thenReturn("testUser");
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(user));

        Optional<User> result = notesService.getUserFromJwts("jwtToken");

        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
        verify(userRepo, times(1)).findByUsername(anyString());
    }

    @Test
    @DisplayName("Test getUserFromJwts user not found")
    void getUserFromJwtsNegative() {
        when(jwtService.getSubject(anyString())).thenReturn("testUser");
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> notesService.getUserFromJwts("jwtToken"));

        assertEquals(Messages.NO_USR_FND.toString(), exception.getMessage());
        verify(userRepo, times(1)).findByUsername(anyString());
    }

//    @Test
//    @DisplayName("Test getNotesByUser success")
//    void getNotesByUser() {
//        User user2 = new User("testUser2", "encodedPassword");
//        Note note2 = new Note();
//        note2.setId(2L);
//        note2.setTitle("Test Title");
//        note2.setContent("Test Content");
//        note2.setUser(user2);
//
////        noteDetails = new NoteDetails("Test Title", "Test Content");
//
//        sharedNote = new SharedNote();
//        sharedNote.setNote(note2);
//        sharedNote.setSharedWith(user2);
//        when(noteRepo.findByUser(any(User.class))).thenReturn(List.of(note));
//        when(sharedNoteRepo.findAllBySharedWith(any(User.class))).thenReturn(List.of(sharedNote));
//
//        List<Note> result = notesService.getNotesByUser(user);
//
//        assertEquals(2, result.size()); // One personal note + one shared note
//        verify(noteRepo, times(1)).findByUser(any(User.class));
//        verify(sharedNoteRepo, times(1)).findAllBySharedWith(any(User.class));
//    }

    @Test
    @DisplayName("Test getNoteByUserAndId success")
    void getNoteByUserAndIdPositive() {
        when(noteRepo.findById(anyLong())).thenReturn(Optional.of(note));

        Note result = notesService.getNoteByUserAndId(user, 1L);

        assertNotNull(result);
        assertEquals(note.getId(), result.getId());
        verify(noteRepo, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Test getNoteByUserAndId access denied")
    void getNoteByUserAndIdNegative() {
        User otherUser = new User("otherUser", "encodedPassword");
        note.setUser(otherUser);

        when(noteRepo.findById(anyLong())).thenReturn(Optional.of(note));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> notesService.getNoteByUserAndId(user, 1L));

        assertEquals(Messages.ACCES_DND.toString(), exception.getMessage());
        verify(noteRepo, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Test getNoteBySharedWithAndId success")
    void getNoteBySharedWithAndIdPositive() {
        when(noteRepo.findById(anyLong())).thenReturn(Optional.of(note));
        when(sharedNoteRepo.findBySharedWithAndNote(any(User.class), any(Note.class))).thenReturn(Optional.of(sharedNote));

        Note result = notesService.getNoteBySharedWithAndId(user, 1L);

        assertNotNull(result);
        assertEquals(note.getId(), result.getId());
        verify(noteRepo, times(1)).findById(anyLong());
        verify(sharedNoteRepo, times(1)).findBySharedWithAndNote(any(User.class), any(Note.class));
    }

    @Test
    @DisplayName("Test getNoteBySharedWithAndId note not found")
    void getNoteBySharedWithAndIdNegative() {
        when(noteRepo.findById(anyLong())).thenReturn(Optional.empty());

        NoteNotFoundException exception = assertThrows(NoteNotFoundException.class, () -> notesService.getNoteBySharedWithAndId(user, 1L));

        assertEquals(Messages.NO_NOTE_FND.toString(), exception.getMessage());
        verify(noteRepo, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Test saveNoteByUser success")
    void saveNoteByUser() {
        when(noteRepo.save(any(Note.class))).thenReturn(note);

        Long result = notesService.saveNoteByUser(user, noteTitleContent);

        assertNotNull(result);
        assertEquals(note.getId(), result);
        verify(noteRepo, times(1)).save(any(Note.class));
    }

    @Test
    @DisplayName("Test updateNoteByUserAndId success")
    void updateNoteByUserAndId() {
        when(noteRepo.findById(anyLong())).thenReturn(Optional.of(note));
        when(noteRepo.save(any(Note.class))).thenReturn(note);

        Long result = notesService.updateNoteByUserAndId(user, 1L, noteDetails);

        assertNotNull(result);
        assertEquals(note.getId(), result);
        verify(noteRepo, times(1)).findById(anyLong());
        verify(noteRepo, times(1)).save(any(Note.class));
    }

    @Test
    @DisplayName("Test deleteNoteByUserAndId success")
    void deleteNoteByUserAndIdPositive() {
        when(noteRepo.findById(anyLong())).thenReturn(Optional.of(note));

        String result = notesService.deleteNoteByUserAndId(user, 1L);

        assertEquals(Messages.SUCCESS.toString(), result);
        verify(noteRepo, times(1)).findById(anyLong());
        verify(noteRepo, times(1)).delete(any(Note.class));
    }

    @Test
    @DisplayName("Test deleteNoteByUserAndId access denied")
    void deleteNoteByUserAndIdNegative() {
        User otherUser = new User("otherUser", "encodedPassword");
        note.setUser(otherUser);

        when(noteRepo.findById(anyLong())).thenReturn(Optional.of(note));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> notesService.deleteNoteByUserAndId(user, 1L));

        assertEquals(Messages.ACCES_DND.toString(), exception.getMessage());
        verify(noteRepo, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Test shareNoteWithUser success")
    void shareNoteWithUser() {
        User sharedWithUser = new User("sharedWithUser", "encodedPassword");
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(sharedWithUser));
        when(noteRepo.findById(anyLong())).thenReturn(Optional.of(note));
        when(sharedNoteRepo.save(any(SharedNote.class))).thenReturn(sharedNote);

        String result = notesService.shareNoteWithUser(user, "sharedWithUser", 1L);

        assertEquals(Messages.SUCCESS.toString(), result);
        verify(userRepo, times(1)).findByUsername(anyString());
        verify(noteRepo, times(1)).findById(anyLong());
        verify(sharedNoteRepo, times(1)).save(any(SharedNote.class));
    }

    @Test
    @DisplayName("Test searchNotesByKeywords success")
    void searchNotesByKeywords() {
        user.setId(1L);
        when(noteRepo.searchByKeywords(any(), any())).thenReturn(Collections.singletonList(note));

        List<Note> result = notesService.searchNotesByKeywords(user, "test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(noteRepo, times(1)).searchByKeywords(anyLong(), anyString());
    }
}
