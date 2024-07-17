package com.backend.project.service;

import com.backend.project.entity.Note;
import com.backend.project.entity.SharedNote;
import com.backend.project.entity.User;
import com.backend.project.enums.Messages;
import com.backend.project.model.NoteDetails;
import com.backend.project.repository.NoteRepository;
import com.backend.project.repository.SharedNoteRepository;
import com.backend.project.repository.UserRepository;
import com.backend.project.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotesService {
    private UserRepository urepo;
    private NoteRepository nrepo;
    private SharedNoteRepository srepo;
    private JwtUtil util;

    @Autowired
    NotesService(UserRepository urepo, NoteRepository nrepo, SharedNoteRepository srepo, JwtUtil util){
        this.urepo = urepo;
        this.nrepo = nrepo;
        this.srepo = srepo;
        this.util = util;
    }

    public User getUserFromJwts(String jwts) {
        return urepo.findByUsername(util.getSubject(jwts));
    }

    public List<Note> getNotesByUser(User user){
        try {
            List<Note> userNotes =  nrepo.findByUser(user);
            List<Note> sharedNotes = srepo.findAllBySharedWith(user).stream().map(SharedNote::getNote).toList();
            userNotes.addAll(sharedNotes);
            return userNotes;
        } catch (Exception e){
            throw new RuntimeException(Messages.FAILED + ": " + e.getMessage());
        }
    }

    public Note getNoteByUserAndId(User user, Long noteId){
        Note note = nrepo.getReferenceById(noteId);
        if (user.equals(note.getUser())) return note;
        return null;
    }

    public Note getNoteBySharedWithAndId(User sharedWith, Long noteId){
        Note note = nrepo.getReferenceById(noteId);
        SharedNote sharedNote = srepo.findBySharedWithAndNote(sharedWith, note);
        return sharedNote != null ? sharedNote.getNote() : null ;
    }
    public Long saveNoteByUser(User user, NoteDetails noteDetails){
        return nrepo.save(new Note(user, noteDetails.title(), noteDetails.content())).getId();
    }

    public Long updateNoteByUserAndId(User user, Long id, NoteDetails noteDetails){
        Note note = getNoteByUserAndId(user, id);
        note.setTitle(noteDetails.title());
        note.setContent(noteDetails.content());
        return nrepo.save(note).getId();
    }

    public String deleteNoteByUserAndId(User user, Long id){
        Note note = getNoteByUserAndId(user, id);
        if(note == null) return Messages.ACCES_DND.toString();
        nrepo.delete(note);
        return Messages.SUCCESS.toString();
    }

    public String shareNoteWithUser(User sharedBy, String username, Long nodeId){
        try {
            User sharedWith = urepo.findByUsername(username);
            if(sharedWith == null) return Messages.NO_USR_FND.toString();
            Note note = nrepo.getReferenceById(nodeId);
            if(!note.getUser().equals(sharedBy)) return Messages.ACCES_DND.toString();
            srepo.save(new SharedNote(sharedBy, sharedWith, note));
        } catch (Exception e){
            return Messages.FAILED + ": " + e.getMessage();
        }
        return Messages.SUCCESS.toString();
    }
}
