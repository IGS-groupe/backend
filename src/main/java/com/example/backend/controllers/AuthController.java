package com.example.backend.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.config.JwtService;
import com.example.backend.dto.LoginDto;
import com.example.backend.dto.ResetPasswordDTO;
import com.example.backend.dto.SignUpDto;
import com.example.backend.entity.Contact;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.services.ContactService;
import com.example.backend.services.MailService;
import java.util.UUID;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private ContactService contactService;
    
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto) {
        String usernameOrEmail = loginDto.getUsernameOrEmail();
        // Find user by username or email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);
        // Authenticate the user using the provided credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), loginDto.getPassword()));
        if (user == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "User not found."));
            }
        // Generate JWT token for the authenticated user
        String jwtToken = jwtService.generateToken(user);
        Long userId = user.getId();
        // Return the authentication response with a custom message based on the user's role
        Map<String, Object> response = new HashMap<>();
            response.put("message","User registered successfully");
            response.put("userId",userId);
            response.put("token",jwtToken);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

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
        
        Role roles = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Error: Role is not found."));
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
    @PostMapping("/signupAdmin")
    public ResponseEntity<?> registerAdmin(@RequestBody SignUpDto signUpDto){
        
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
        
        Role roles = roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("Error: Role is not found."));
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
                HttpHeaders headers = new HttpHeaders();
                headers.add("Location", "http://localhost:4200/account/login");
                return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
            } else {
                return ResponseEntity.badRequest().body("Account is already activated");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid activation link");
        }
    }

    @GetMapping("/request-reset-password")
    public ResponseEntity<?> requestResetPassword(@RequestParam("email") String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User with this email does not exist.");
        }
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);
        try {
            mailService.sendResetPasswordEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), resetToken);
            return ResponseEntity.ok("Reset password link sent to your email.");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send reset password email");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO request) {
        User user = userRepository.findByResetToken(request.getToken()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid or expired reset token.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null); // Clear the token
        userRepository.save(user);
        
        return ResponseEntity.ok("Password reset successfully.");
    }

    @PostMapping("/contacts")
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        Contact savedContact = contactService.saveContact(contact);
        return ResponseEntity.ok(savedContact);
    }
}