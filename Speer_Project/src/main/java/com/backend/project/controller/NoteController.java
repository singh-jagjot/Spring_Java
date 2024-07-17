package com.backend.project.controller;

import com.backend.project.entity.Note;
import com.backend.project.entity.User;
import com.backend.project.model.NoteDetails;
import com.backend.project.model.ShareWith;
import com.backend.project.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private NotesService notesService;

    @Autowired
    NoteController(NotesService notesService) {
        this.notesService = notesService;
    }

//    @GetMapping
//    public ResponseEntity<List<NoteDetails>> getNotes(@RequestHeader Map<String, String> headers){
//        User user = notesService.getUserFromJwts(headers.get("authorization").split(" ")[1]);
//        List<NoteDetails> noteDetailsList = notesService.getNotesByUser(user).stream().map(note -> new NoteDetails(note.getId(), note.getTitle(), note.getContent())).toList();
//        return new ResponseEntity<>(noteDetailsList, HttpStatus.OK);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<List<NoteDetails>> getNote(@RequestHeader Map<String, String> headers, @PathVariable(required = false) Long id){
//        User user = notesService.getUserFromJwts(headers.get("authorization").split(" ")[1]);
//        if(id != null){
//            NoteDetails noteDetails;
//            Note note = notesService.getNoteByUserAndId(user, id);
//            if(note != null){
//                noteDetails = new NoteDetails(note.getId(), note.getTitle(), note.getContent());
//                return new ResponseEntity<>(List.of(noteDetails), HttpStatus.OK);
//            } else {
//                note = notesService.getNoteBySharedWithAndId(user, id);
//                noteDetails = new NoteDetails(note.getId(), note.getTitle(), note.getContent());
//            }
//        }
//        return new ResponseEntity<>(List.of(), HttpStatus.OK);
//    }

    @GetMapping({"","/", "/{id}"})
    public ResponseEntity<List<NoteDetails>> getNotes(@RequestHeader Map<String, String> headers, @PathVariable(required = false) Long id) {
        User user = getUserFromHeaders(headers);
        if (id != null) {
            Note note = notesService.getNoteByUserAndId(user, id);
            if (note == null) {
                note = notesService.getNoteBySharedWithAndId(user, id);
            }
            if (note != null) {
                NoteDetails noteDetails = new NoteDetails(note.getId(), note.getTitle(), note.getContent());
                return new ResponseEntity<>(List.of(noteDetails), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(List.of(), HttpStatus.OK);
            }
        } else {
            List<NoteDetails> noteDetailsList = notesService.getNotesByUser(user).stream().map(note -> new NoteDetails(note.getId(), note.getTitle(), note.getContent())).toList();
            return new ResponseEntity<>(noteDetailsList, HttpStatus.OK);
        }
    }

    @PostMapping
    public ResponseEntity<Long> createNote(@RequestHeader Map<String, String> headers, @RequestBody NoteDetails note) {
        User user = getUserFromHeaders(headers);
        Long noteId = notesService.saveNoteByUser(user, note);
        return new ResponseEntity<>(noteId, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateNoteById(@RequestHeader Map<String, String> headers, @PathVariable(required = true, value = "id") Long id, @RequestBody NoteDetails note) {
        User user = getUserFromHeaders(headers);
        Long noteId = notesService.updateNoteByUserAndId(user, id, note);
        return new ResponseEntity<>(noteId, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNoteById(@RequestHeader Map<String, String> headers, @PathVariable(required = true, value = "id") Long id) {
        User user = getUserFromHeaders(headers);
        return new ResponseEntity<>(notesService.deleteNoteByUserAndId(user, id), HttpStatus.OK);
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<String> shareNoteWithUser(@RequestHeader Map<String, String> headers, @PathVariable(required = true, value = "id") Long noteId, @RequestBody ShareWith shareWith) {
        User user = getUserFromHeaders(headers);
        String msg = notesService.shareNoteWithUser(user, shareWith.username(), noteId);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Note> searchByKeywords(@RequestHeader Map<String, String> headers, @RequestParam(required = true, value = "q") String q) {
        User user = getUserFromHeaders(headers);

        return null;
    }

    private User getUserFromHeaders(Map<String, String> headers) {
        return notesService.getUserFromJwts(headers.get("authorization").split(" ")[1]);
    }
}
