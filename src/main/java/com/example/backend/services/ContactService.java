package com.example.backend.services;

import java.util.List;
import java.util.Optional;

import com.example.backend.entity.Contact;

public interface ContactService {
    Contact saveContact(Contact contact);
    List<Contact> getAllContacts();
    Optional<Contact> getContactById(Long id);
    Contact updateContact(Contact contact);
    void deleteContact(Long id);
}
