package com.backend.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import static org.hibernate.Length.LONG32;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notes")
public class Note {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(length = LONG32)
    private String content;

//    @JsonManagedReference
//    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<SharedNote> sharedNotes;

    public Note(User user, String title, String content){
        this.user = user;
        this.title = title;
        this.content = content;
    }
}
