package com.backend.project.controller;

import com.backend.project.config.ProjectConfig;
import com.backend.project.entity.Note;
import com.backend.project.entity.User;
import com.backend.project.enums.Messages;
import com.backend.project.exception.AccessDeniedException;
import com.backend.project.exception.NoteNotFoundException;
import com.backend.project.model.NoteDetails;
import com.backend.project.model.NoteTitleContent;
import com.backend.project.model.ShareWith;
import com.backend.project.service.JwtService;
import com.backend.project.service.NotesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import({ProjectConfig.class})
@WebMvcTest(NoteController.class)
public class NoteControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private NotesService notesService;

    @MockBean
    private JwtService jwtService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        when(jwtService.verifyJwts(any())).thenReturn(Messages.TKN_VALD.toString());
    }

    @Test
    @DisplayName("Test 'getNotes': Positive")
    public void getNotesPositive1() throws Exception {
        User user = new User("user1", "pass1");
        Note note = new Note(user, "title1", "content1");
        note.setId(1L);

        when(notesService.getUserFromJwts(any())).thenReturn(Optional.of(user));
        when(notesService.getNotesByUser(any())).thenReturn(List.of(note));

        this.mvc.perform(get("/api/notes")
                        .header("authorization", "Bearer dummy"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(note))));
    }

    @Test
    @DisplayName("Test 'getNotes/{id}': Positive")
    public void getNotesPositive2() throws Exception {
        User user = new User("user1", "pass1");
        Note note = new Note(user, "title1", "content1");
        note.setId(1L);

        when(notesService.getUserFromJwts(any())).thenReturn(Optional.of(user));
//        when(notesService.getNotesByUser(any())).thenReturn(List.of(note));
        when(notesService.getNoteByUserAndId(user, 1L)).thenReturn(note);

        this.mvc.perform(get("/api/notes/1")
                        .header("authorization", "Bearer dummy"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(List.of(note))));
    }

    @Test
    @DisplayName("Test 'getNotes': Negative")
    public void getNotesNegative() throws Exception {
        User user = new User("user1", "pass1");

        when(notesService.getUserFromJwts(any())).thenReturn(Optional.of(user));
        when(notesService.getNotesByUser(any())).thenThrow(new RuntimeException("Test"));

        this.mvc.perform(get("/api/notes")
                        .header("authorization", "Bearer dummy"))
                .andExpect(status().isInternalServerError());
    }


    @Test
    @DisplayName("Test createNote: Positive")
    void createNotePositive() throws Exception {
        User user = new User("user1", "pass1");
        NoteDetails noteDetails = new NoteDetails(null, "Test Title", "Test Content");

        when(notesService.getUserFromJwts(any())).thenReturn(Optional.of(user));
        when(notesService.saveNoteByUser(any(User.class), any(NoteTitleContent.class))).thenReturn(1L);

        mvc.perform(post("/api/notes")
                        .header("authorization", "Bearer dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Test createNote: Negative")
    void createNoteNegative() throws Exception {
        NoteDetails noteDetails = new NoteDetails(null, "Test Title", "Test Content");
        when(notesService.getUserFromJwts(any())).thenThrow(new RuntimeException("Test"));

        mvc.perform(post("/api/notes")
                        .header("authorization", "Bearer dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteDetails)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Test update note: Positive")
    void updateNotePositive() throws Exception {
        User user = new User();

        NoteDetails noteDetails = new NoteDetails(1L,"Updated Title", "Updated Content");

        when(notesService.getUserFromJwts(any())).thenReturn(Optional.of(user));
        when(notesService.updateNoteByUserAndId(any(User.class), any(Long.class), any(NoteDetails.class))).thenReturn(noteDetails.id());

        mvc.perform(put("/api/notes/1")
                        .header("authorization", "Bearer dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(noteDetails.id())));
    }

    @Test
    @DisplayName("Test update note: Negative")
    void updateNoteNegative() throws Exception {
        User user = new User();

        NoteDetails noteDetails = new NoteDetails(1L,"Updated Title", "Updated Content");

        when(notesService.getUserFromJwts(any())).thenReturn(Optional.of(user));
        when(notesService.updateNoteByUserAndId(any(User.class), any(Long.class), any(NoteDetails.class)))
                .thenThrow(new NoteNotFoundException("Test"));

        mvc.perform(put("/api/notes/1")
                        .header("authorization", "Bearer dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteDetails)))
                .andExpect(status().isBadRequest());

        when(notesService.updateNoteByUserAndId(any(User.class), any(Long.class), any(NoteDetails.class)))
                .thenThrow(new RuntimeException("Test"));

        mvc.perform(put("/api/notes/1")
                        .header("authorization", "Bearer dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteDetails)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Test delete note: Positive")
    void deleteNotePositive() throws Exception {
        User user = new User();
        when(notesService.getUserFromJwts(any())).thenReturn(Optional.of(user));
        when(notesService.deleteNoteByUserAndId(any(User.class), any(Long.class))).thenReturn(Messages.SUCCESS.toString());

        mvc.perform(delete("/api/notes/1")
                        .header("authorization", "Bearer dummy"))
                .andExpect(status().isOk())
                .andExpect(content().string(Messages.SUCCESS.toString()));
    }

    @Test
    @DisplayName("Test delete note: Negative")
    void deleteNoteNegative() throws Exception {
        User user = new User();
        when(notesService.getUserFromJwts(any())).thenReturn(Optional.of(user));
        when(notesService.deleteNoteByUserAndId(any(User.class), any(Long.class))).thenThrow(new AccessDeniedException("Test"));

        mvc.perform(delete("/api/notes/1")
                        .header("authorization", "Bearer dummy"))
                .andExpect(status().isUnauthorized());

        when(notesService.deleteNoteByUserAndId(any(User.class), any(Long.class))).thenThrow(new RuntimeException("Test"));

        mvc.perform(delete("/api/notes/1")
                        .header("authorization", "Bearer dummy"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Test share note: Positive")
    void shareNotePositive() throws Exception {
        User user = new User();
        user.setUsername("sharedBy");

        ShareWith shareWith = new ShareWith("sharedWith");

        when(notesService.getUserFromJwts(any())).thenReturn(Optional.of(user));
        when(notesService.shareNoteWithUser(any(User.class), any(String.class), any(Long.class))).thenReturn(Messages.SUCCESS.toString());

        mvc.perform(post("/api/notes/1/share")
                        .header("authorization", "Bearer dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shareWith)))
                .andExpect(status().isOk())
                .andExpect(content().string(Messages.SUCCESS.toString()));
    }

    @Test
    @DisplayName("Test share note: Negative")
    void shareNoteNegative() throws Exception {
        User user = new User();
        user.setUsername("sharedBy");

        ShareWith shareWith = new ShareWith("sharedWith");

        when(notesService.getUserFromJwts(any())).thenReturn(Optional.of(user));
        when(notesService.shareNoteWithUser(any(User.class), any(String.class), any(Long.class))).thenThrow(new AccessDeniedException("Test"));

        mvc.perform(post("/api/notes/1/share")
                        .header("authorization", "Bearer dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shareWith)))
                .andExpect(status().isUnauthorized());

        when(notesService.shareNoteWithUser(any(User.class), any(String.class), any(Long.class))).thenThrow(new RuntimeException("Test"));

        mvc.perform(post("/api/notes/1/share")
                        .header("authorization", "Bearer dummy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shareWith)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Test search notes: Positive")
    void searchNotesPositive() throws Exception {
        User user = new User();
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Test Title");
        note.setContent("Test Content");

        when(notesService.getUserFromJwts(any())).thenReturn(Optional.of(user));
        when(notesService.searchNotesByKeywords(any(User.class), any(String.class))).thenReturn(List.of(note));

        mvc.perform(get("/api/notes/search")
                        .header("authorization", "Bearer dummy")
                        .param("q", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"));
    }
}
