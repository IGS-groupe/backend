package com.example.backend.entity;

import java.util.Set;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name="users" , uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"}),
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nom;
    private String prenom;
    private String email;
    private String motdepasse;
    private String genre;
   
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;
}
