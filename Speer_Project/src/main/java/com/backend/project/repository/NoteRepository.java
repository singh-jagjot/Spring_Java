package com.backend.project.repository;

import com.backend.project.entity.Note;
import com.backend.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUser(User user);

    @Query(value = "(SELECT n.* FROM notes n WHERE n.user_id = :userId " +
            "AND to_tsvector('english', n.title || ' ' || n.content) @@ plainto_tsquery('english', :keywords)) " +
            "UNION " +
            "(SELECT n.* FROM notes n " +
            "JOIN shared s ON n.id = s.note_id WHERE s.shared_with_id = :userId " +
            "AND to_tsvector('english', n.title || ' ' || n.content) @@ plainto_tsquery('english', :keywords))",
            nativeQuery = true)
    List<Note> searchByKeywords(@Param("userId") Long userId, @Param("keywords") String keywords);
}
