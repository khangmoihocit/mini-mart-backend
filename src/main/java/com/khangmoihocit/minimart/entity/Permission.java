package com.khangmoihocit.minimart.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    String name;

    @Column(name = "description")
    String description;

    @ManyToMany(mappedBy = "permissions")
    Set<Role> roles = new HashSet<>();
}
