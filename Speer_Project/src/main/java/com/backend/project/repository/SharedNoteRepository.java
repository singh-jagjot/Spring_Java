package com.backend.project.repository;

import com.backend.project.entity.Note;
import com.backend.project.entity.SharedNote;
import com.backend.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharedNoteRepository extends JpaRepository<SharedNote, Long> {
    List<SharedNote> findAllBySharedWith(User sharedWith);
    List<SharedNote> findBySharedByAndSharedWith(User sharedBy, User sharedWith);
    List<SharedNote> findBySharedByAndNote(User sharedBy, Note note);
    Optional<SharedNote> findBySharedWithAndNote(User sharedWith, Note note);

}
