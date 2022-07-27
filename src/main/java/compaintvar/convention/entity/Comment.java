package compaintvar.convention.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Table(name = "comment")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true, insertable = false, updatable = false)
    Long id;

    @Column(nullable = false)
    String content;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="owner_id", referencedColumnName = "id")
    Account owner;

    @OneToOne(mappedBy = "next", fetch = FetchType.EAGER)
    Comment previous;

    @OneToOne(mappedBy = "previous")
    Comment next;
}
