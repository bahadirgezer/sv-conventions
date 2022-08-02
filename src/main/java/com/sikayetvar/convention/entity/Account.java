package com.sikayetvar.convention.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Table(name = "account")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
//@Where(clause = "deleted = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, insertable = false, updatable = false)
    Long id;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "username", nullable = false, unique = true)
    String username;

    @JsonIgnore
    @OneToMany(mappedBy = "owner")
    Set<Comment> comments = new HashSet<Comment>();

    //@Formula("")
    @Column(name = "comment_count", nullable = false)
    Long commentCount;

    @Column(name = "deleted", nullable = false)
    Boolean deleted;
}
