package com.example.backend.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.LoginDto;
import com.example.backend.dto.SignUpDto;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.services.MailService;
import java.util.UUID;
import jakarta.mail.MessagingException;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String usernameOrEmail = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "User not found."));
        }
        Long userId = user.getId();
        return ResponseEntity.ok(Collections.singletonMap("userId", userId));
    }
    

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){
        
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        // Create new user's account
        User user = new User();
        user.setFirstName(signUpDto.getFirstName());
        user.setLastName(signUpDto.getLastName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setGenre(signUpDto.getGenre());
        
        Role roles = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRoles(Collections.singleton(roles));
        user.setActivationToken(UUID.randomUUID().toString());
        userRepository.save(user);
        
        // Send activation email
        try {
            mailService.sendActivationEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), user.getActivationToken());
            return ResponseEntity.ok("User registered successfully and activation email sent.");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registered but failed to send activation email");
        }
    }
    @GetMapping("/activate")
    public ResponseEntity<String> activateUser(@RequestParam("token") String token) {
        Optional<User> userOptional = userRepository.findByActivationToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isActive()) {
                user.setActive(true);
                user.setActivationToken(null); // clear the token after activation
                userRepository.save(user);
                return ResponseEntity.ok("Account activated successfully");
            } else {
                return ResponseEntity.badRequest().body("Account is already activated");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid activation link");
        }
    }
}