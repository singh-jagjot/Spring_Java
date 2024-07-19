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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotesService {
    private final Logger logger = LoggerFactory.getLogger(NotesService.class);

    private final UserRepository userRepo;
    private final NoteRepository noteRepo;
    private final SharedNoteRepository sharedNoteRepo;
    private final JwtService jwtService;

    @Autowired
    NotesService(UserRepository userRepo, NoteRepository noteRepo, SharedNoteRepository sharedNoteRepo, JwtService jwtService) {
        this.userRepo = userRepo;
        this.noteRepo = noteRepo;
        this.sharedNoteRepo = sharedNoteRepo;
        this.jwtService = jwtService;
    }

    public Optional<User> getUserFromJwts(String jwts) {
        logger.info("Retrieving user from JWT: {}", Messages.START);
        try {
            Optional<User> user = userRepo.findByUsername(jwtService.getSubject(jwts));
            if (user.isEmpty()) {
                throw new UserNotFoundException(Messages.NO_USR_FND.toString());
            }
            return user;
        } catch (UserNotFoundException e) {
            logger.error("User not found for JWT: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while retrieving user from JWT: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.info("Retrieving user from JWT: {}", Messages.END);
        }
    }

    public List<Note> getNotesByUser(User user) {
        logger.info("Retrieving notes for user '{}': {}", user.getUsername(), Messages.START);
        try {
            List<Note> userNotes = noteRepo.findByUser(user);
            List<Note> sharedNotes = sharedNoteRepo.findAllBySharedWith(user)
                    .stream()
                    .map(SharedNote::getNote)
                    .toList();
            userNotes.addAll(sharedNotes);
            return userNotes;
        } catch (Exception e) {
            logger.error("Error while retrieving notes for user '{}': {}", user.getUsername(), e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.info("Retrieving notes for user '{}': {}", user.getUsername(), Messages.END);
        }
    }

    public Note getNoteByUserAndId(User user, Long noteId) {
        logger.info("Retrieving note with ID '{}' for user '{}': {}", noteId, user.getUsername(), Messages.START);
        try {
            Optional<Note> note = noteRepo.findById(noteId);
            if (note.isEmpty() || !user.equals(note.get().getUser())) {
                throw new AccessDeniedException(Messages.ACCES_DND.toString());
            }
            return note.get();
        } catch (AccessDeniedException e) {
            logger.error("Access denied for note with ID '{}' for user '{}': {}", noteId, user.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while retrieving note with ID '{}' for user '{}': {}", noteId, user.getUsername(), e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.info("Retrieving note with ID '{}' for user '{}': {}", noteId, user.getUsername(), Messages.END);
        }
    }

    public Note getNoteBySharedWithAndId(User sharedWith, Long noteId) {
        logger.info("Retrieving shared note with ID '{}' for user '{}': {}", noteId, sharedWith.getUsername(), Messages.START);
        try {
            Optional<Note> note = noteRepo.findById(noteId);
            if (note.isEmpty()) {
                throw new NoteNotFoundException(Messages.NO_NOTE_FND.toString());
            }
            Optional<SharedNote> sharedNote = sharedNoteRepo.findBySharedWithAndNote(sharedWith, note.get());
            if (sharedNote.isEmpty()) {
                throw new AccessDeniedException(Messages.ACCES_DND.toString());
            }
            return sharedNote.get().getNote();
        } catch (NoteNotFoundException | AccessDeniedException e) {
            logger.error("Error while retrieving shared note with ID '{}' for user '{}': {}", noteId, sharedWith.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while retrieving shared note with ID '{}' for user '{}': {}", noteId, sharedWith.getUsername(), e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.info("Retrieving shared note with ID '{}' for user '{}': {}", noteId, sharedWith.getUsername(), Messages.END);
        }
    }

    public Long saveNoteByUser(User user, NoteTitleContent noteDetails) {
        logger.info("Saving note for user '{}': {}", user.getUsername(), Messages.START);
        try {
            Note note = new Note(user, noteDetails.title(), noteDetails.content());
            return noteRepo.save(note).getId();
        } catch (Exception e) {
            logger.error("Error while saving note for user '{}': {}", user.getUsername(), e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.info("Saving note for user '{}': {}", user.getUsername(), Messages.END);
        }
    }

    public Long updateNoteByUserAndId(User user, Long id, NoteDetails noteDetails) {
        logger.info("Updating note with ID '{}' for user '{}': {}", id, user.getUsername(), Messages.START);
        try {
            Note note = getNoteByUserAndId(user, id);
            note.setTitle(noteDetails.title());
            note.setContent(noteDetails.content());
            return noteRepo.save(note).getId();
        } catch (NoteNotFoundException e) {
            logger.error("Note not found with ID '{}' for user '{}': {}", id, user.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating note with ID '{}' for user '{}': {}", id, user.getUsername(), e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.info("Updating note with ID '{}' for user '{}': {}", id, user.getUsername(), Messages.END);
        }
    }

    public String deleteNoteByUserAndId(User user, Long id) {
        logger.info("Deleting note with ID '{}' for user '{}': {}", id, user.getUsername(), Messages.START);
        try {
            Note note = getNoteByUserAndId(user, id);
            if (note == null) {
                throw new AccessDeniedException(Messages.ACCES_DND.toString());
            }
            noteRepo.delete(note);
            return Messages.SUCCESS.toString();
        } catch (AccessDeniedException e) {
            logger.error("Access denied for deleting note with ID '{}' for user '{}': {}", id, user.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while deleting note with ID '{}' for user '{}': {}", id, user.getUsername(), e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.info("Deleting note with ID '{}' for user '{}': {}", id, user.getUsername(), Messages.END);
        }
    }

    public String shareNoteWithUser(User sharedBy, String username, Long noteId) {
        logger.info("Sharing note with ID '{}' by user '{}' with user '{}': {}", noteId, sharedBy.getUsername(), username, Messages.START);
        try {
            Optional<User> sharedWith = userRepo.findByUsername(username);
            if (sharedWith.isEmpty()) {
                throw new UserNotFoundException(Messages.NO_USR_FND.toString());
            }
            Optional<Note> note = noteRepo.findById(noteId);
            if (note.isEmpty() || !note.get().getUser().equals(sharedBy)) {
                throw new AccessDeniedException(Messages.ACCES_DND.toString());
            }
            sharedNoteRepo.save(new SharedNote(sharedBy, sharedWith.get(), note.get()));
            return Messages.SUCCESS.toString();
        } catch (UserNotFoundException | AccessDeniedException e) {
            logger.error("Error while sharing note with ID '{}' by user '{}' with user '{}': {}", noteId, sharedBy.getUsername(), username, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while sharing note with ID '{}' by user '{}' with user '{}': {}", noteId, sharedBy.getUsername(), username, e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.info("Sharing note with ID '{}' by user '{}' with user '{}': {}", noteId, sharedBy.getUsername(), username, Messages.END);
        }
    }

    public List<Note> searchNotesByKeywords(User user, String keywords) {
        logger.info("Searching notes for user '{}' by keywords '{}': {}", user.getUsername(), keywords, Messages.START);
        try {
            return noteRepo.searchByKeywords(user.getId(), keywords);
        } catch (Exception e){
            logger.error("Error while searching notes for user '{}' by keywords '{}': {}", user.getUsername(), keywords, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        finally {
            logger.info("Searching notes for user '{}' by keywords '{}': {}", user.getUsername(), keywords, Messages.END);
        }
    }
}
