package com.backend.project.controller;

import com.backend.project.entity.Note;
import com.backend.project.entity.User;
import com.backend.project.enums.Messages;
import com.backend.project.exception.AccessDeniedException;
import com.backend.project.exception.NoteNotFoundException;
import com.backend.project.exception.UserNotFoundException;
import com.backend.project.model.NoteDetails;
import com.backend.project.model.NoteTitleContent;
import com.backend.project.model.ShareWith;
import com.backend.project.service.NotesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "Operations pertaining to notes")
public class NoteController {
    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    private final NotesService notesService;

    @Autowired
    public NoteController(NotesService notesService) {
        this.notesService = notesService;
    }

    @GetMapping({"","/", "/{id}"})
    @Operation(security = { @SecurityRequirement(name = "bearer-key") }, summary = "Get all the notes(created and shared) for the user, if 'id' is provided then only the note with that 'id'", description = "Gets notes for the user")
    public ResponseEntity<List<NoteDetails>> getNotes(@RequestHeader Map<String, String> headers, @PathVariable(required = false) Long id) {
        logger.info("Getting notes: {}", Messages.START);
        try {
            User user = getUserFromHeaders(headers);

            if (id != null) {
                Note note = notesService.getNoteByUserAndId(user, id);
                if (note == null) {
                    note = notesService.getNoteBySharedWithAndId(user, id);
                }
                if (note != null) {
                    logger.info("Getting note for user: {}", user.getUsername());
                    NoteDetails noteDetails = new NoteDetails(note.getId(), note.getTitle(), note.getContent());
                    return new ResponseEntity<>(List.of(noteDetails), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(List.of(), HttpStatus.OK);
                }
            } else {
                logger.info("Getting notes for user: {}", user.getUsername());
                List<NoteDetails> noteDetailsList = notesService.getNotesByUser(user).stream()
                        .map(note -> new NoteDetails(note.getId(), note.getTitle(), note.getContent()))
                        .toList();
                return new ResponseEntity<>(noteDetailsList, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("Error while retrieving notes: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        } finally {
            logger.info("Getting notes: {}", Messages.END);
        }
    }

    @PostMapping
    @Operation(security = { @SecurityRequirement(name = "bearer-key") }, summary = "Creating note with provided title and content for the user'", description = "Create note for the user")
    public ResponseEntity<Long> createNote(@RequestHeader Map<String, String> headers, @RequestBody NoteTitleContent noteTitleContent) {
        logger.info("Creating note: {}", Messages.START);
        try {
            User user = getUserFromHeaders(headers);
            Long noteId = notesService.saveNoteByUser(user, noteTitleContent);
            return new ResponseEntity<>(noteId, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while creating note: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        } finally {
            logger.info("Creating note: {}", Messages.END);
        }
    }

    @PutMapping("/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") }, summary = "Update note of 'id' with the updated tile and content for the user if user have access ", description = "Update note for the user")
    public ResponseEntity<Long> updateNoteById(@RequestHeader Map<String, String> headers, @PathVariable Long id, @RequestBody NoteDetails noteDetails) {
        logger.info("Updating note with id {}: {}", id, Messages.START);
        try {
            User user = getUserFromHeaders(headers);
            Long noteId = notesService.updateNoteByUserAndId(user, id, noteDetails);
            return new ResponseEntity<>(noteId, HttpStatus.OK);
        } catch (NoteNotFoundException e){
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        } finally {
            logger.info("Updating note with id {}: {}", id, Messages.END);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") }, summary = "Delete note of 'id' for the user if user have access ", description = "Delete note for the user")
    public ResponseEntity<String> deleteNoteById(@RequestHeader Map<String, String> headers, @PathVariable Long id) {
        logger.info("Deleting note with id {}: {}", id, Messages.START);
        try {
            User user = getUserFromHeaders(headers);
            String message = notesService.deleteNoteByUserAndId(user, id);
            return ResponseEntity.ok(message);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(Messages.FAILED + ": " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(Messages.FAILED + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            logger.info("Deleting note with id {}: {}", id, Messages.END);
        }
    }

    @PostMapping("/{id}/share")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") }, summary = "Sharing note of 'id' with another user with username provided in the body. Works only if user have access to that note", description = "Share note with other user")
    public ResponseEntity<String> shareNoteWithUser(@RequestHeader Map<String, String> headers, @PathVariable Long id, @RequestBody ShareWith shareWith) {
        logger.info("Sharing note with id {} to user {}: {}", id, shareWith.username(), Messages.START);
        try {
            User user = getUserFromHeaders(headers);
            String message = notesService.shareNoteWithUser(user, shareWith.username(), id);
            return ResponseEntity.ok(message);
        } catch (UserNotFoundException | AccessDeniedException e){
            return new ResponseEntity<>(Messages.FAILED + ": " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(Messages.FAILED + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            logger.info("Sharing note with id {} to user {}: {}", id, shareWith.username(), Messages.END);
        }
    }

    @GetMapping("/search")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") }, summary = "Searching notes user have access to with the provided keywords.", description = "Search notes for the user")
    public ResponseEntity<List<Note>> searchByKeywords(@RequestHeader Map<String, String> headers, @RequestParam(value = "q") String q) {
        try {
            User user = getUserFromHeaders(headers);
            List<Note> notes = notesService.searchNotesByKeywords(user, q);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private User getUserFromHeaders(Map<String, String> headers) {
        return notesService.getUserFromJwts(headers.get("authorization").split(" ")[1]).orElseThrow();
    }
}
