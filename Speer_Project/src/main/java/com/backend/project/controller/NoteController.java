package com.backend.project.controller;

import com.backend.project.entity.Note;
import com.backend.project.entity.User;
import com.backend.project.enums.Messages;
import com.backend.project.model.NoteDetails;
import com.backend.project.model.ShareWith;
import com.backend.project.service.NotesService;
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
public class NoteController {
    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    private final NotesService notesService;

    @Autowired
    public NoteController(NotesService notesService) {
        this.notesService = notesService;
    }

    @GetMapping({"","/", "/{id}"})
    public ResponseEntity<List<NoteDetails>> getNotes(@RequestHeader Map<String, String> headers, @PathVariable(required = false) Long id) {
        logger.info("Getting notes: {}", Messages.START);
        try {
            User user = getUserFromHeaders(headers).orElseThrow(() -> new RuntimeException(Messages.NO_USR_FND.toString()));

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
    public ResponseEntity<Long> createNote(@RequestHeader Map<String, String> headers, @RequestBody NoteDetails noteDetails) {
        logger.info("Creating note: {}", Messages.START);
        try {
            User user = getUserFromHeaders(headers).orElseThrow(() -> new RuntimeException(Messages.NO_USR_FND.toString()));
            Long noteId = notesService.saveNoteByUser(user, noteDetails);
            return new ResponseEntity<>(noteId, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while creating note: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        } finally {
            logger.info("Creating note: {}", Messages.END);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateNoteById(@RequestHeader Map<String, String> headers, @PathVariable Long id, @RequestBody NoteDetails noteDetails) {
        logger.info("Updating note with id {}: {}", id, Messages.START);
        try {
            User user = getUserFromHeaders(headers).orElseThrow(() -> new RuntimeException(Messages.NO_USR_FND.toString()));
            Long noteId = notesService.updateNoteByUserAndId(user, id, noteDetails);
            return new ResponseEntity<>(noteId, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while updating note with id {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        } finally {
            logger.info("Updating note with id {}: {}", id, Messages.END);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNoteById(@RequestHeader Map<String, String> headers, @PathVariable Long id) {
        logger.info("Deleting note with id {}: {}", id, Messages.START);
        try {
            User user = getUserFromHeaders(headers).orElseThrow(() -> new RuntimeException(Messages.NO_USR_FND.toString()));
            String message = notesService.deleteNoteByUserAndId(user, id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            logger.error("Error while deleting note with id {}: {}", id, e.getMessage());
            return new ResponseEntity<>(Messages.FAILED + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            logger.info("Deleting note with id {}: {}", id, Messages.END);
        }
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<String> shareNoteWithUser(@RequestHeader Map<String, String> headers, @PathVariable Long id, @RequestBody ShareWith shareWith) {
        logger.info("Sharing note with id {} to user {}: {}", id, shareWith.username(), Messages.START);
        try {
            User user = getUserFromHeaders(headers).orElseThrow(() -> new RuntimeException(Messages.NO_USR_FND.toString()));
            String message = notesService.shareNoteWithUser(user, shareWith.username(), id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            logger.error("Error while sharing note with id {} to user {}: {}", id, shareWith.username(), e.getMessage());
            return new ResponseEntity<>(Messages.FAILED + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            logger.info("Sharing note with id {} to user {}: {}", id, shareWith.username(), Messages.END);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Note>> searchByKeywords(@RequestHeader Map<String, String> headers, @RequestParam(value = "q") String q) {
        try {
            User user = getUserFromHeaders(headers).orElseThrow(() -> new RuntimeException(Messages.NO_USR_FND.toString()));
            List<Note> notes = notesService.searchNotesByKeywords(user, q);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private Optional<User> getUserFromHeaders(Map<String, String> headers) {
        return notesService.getUserFromJwts(headers.get("authorization").split(" ")[1]);
    }
}
