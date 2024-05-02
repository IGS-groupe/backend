package com.example.backend.entity;

import java.util.Date;

import jakarta.persistence.*;


import jakarta.persistence.TemporalType;
import lombok.Data;
@Data
@Entity
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "company", length = 100)
    private String company;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_complaint", nullable = false)
    private Date dateOfComplaint;

    @Column(name = "issue_description", nullable = false, length = 500)
    private String issueDescription;

    @Column(name = "contact_person", length = 100)
    private String contactPerson; // Who were you dealing with?
}
