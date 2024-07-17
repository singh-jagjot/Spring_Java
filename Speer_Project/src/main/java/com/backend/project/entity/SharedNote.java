package com.backend.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Check;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@Check(name = "check_columns_not_equal", constraints = "shared_by_id != shared_with_id")
@Table(name = "shared", uniqueConstraints={
        @UniqueConstraint(name = "unique_column_combination1", columnNames = {"shared_with_id", "note_id"}),
        @UniqueConstraint(name = "unique_column_combination2", columnNames={"shared_by_id", "shared_with_id", "note_id"})
})
public class SharedNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_by_id", nullable = false)
    private User sharedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_with_id", nullable = false)
    private User sharedWith;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    public SharedNote(User sharedBy, User sharedWith, Note note){
        this.sharedBy = sharedBy;
        this.sharedWith = sharedWith;
        this.note = note;
    }
}
