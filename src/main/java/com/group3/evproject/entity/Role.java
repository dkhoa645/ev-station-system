package com.group3.evproject.entity;

import com.group3.evproject.Enum.RoleName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, unique = true)
    RoleName name;

    @ManyToMany(mappedBy = "roles")
    Set<User> users = new HashSet<>();
}
