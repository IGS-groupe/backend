package com.example.backend.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.config.JwtService;
import com.example.backend.dto.DemandeUserDTO;
import com.example.backend.dto.LoginDto;
import com.example.backend.dto.NewsDTO;
import com.example.backend.dto.ResetPasswordDTO;
import com.example.backend.dto.SignUpDto;
import com.example.backend.dto.User2DTO;
import com.example.backend.dto.UserDTO;
import com.example.backend.entity.AnalysisStatus;
import com.example.backend.entity.Contact;
import com.example.backend.entity.Demande;
import com.example.backend.entity.News;
import com.example.backend.entity.ProjectCard;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.DemandeRepository;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.services.ContactService;
import com.example.backend.services.MailService;
import com.example.backend.services.NewsService;
import com.example.backend.services.ProjectCardService;
import com.example.backend.services.TokenBlacklistService;

import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.web.bind.annotation.*;

import java.time.Duration;

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
    @Autowired
    private NewsService newsService;

    @Autowired
    private DemandeRepository demandeRepository;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ProjectCardService service;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    // private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
       @PostMapping("/signin")
        public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto) {
            String usernameOrEmail = loginDto.getUsernameOrEmail();

            // 1) Load user
            User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "User not found."));
            }

            // 2) Check credentials
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(usernameOrEmail, loginDto.getPassword())
                );
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("message", "Invalid credentials"));
            }

            // 3) Check account status
            if (!user.isActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("message", "Account is disabled."));
            }

            // 4) Generate JWT and set as HttpOnly cookie
            String jwtToken = jwtService.generateToken(user);

            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwtToken)
                    .httpOnly(true)
                    // ⚠️ Set to true in production (HTTPS). Keep false for local dev without HTTPS.
                    .secure(false)
                    .path("/")
                    // Lax is fine if frontend and backend share the same site. 
                    // If you host them on different top-level domains, use "None" AND secure(true).
                    .sameSite("Lax")
                    .maxAge(Duration.ofDays(1))
                    .build();

            // 5) Build response payload WITHOUT token
            Map<String, Object> payload = new HashMap<>();
            payload.put("message", "User authenticated successfully");
            payload.put("userId", user.getId());
            payload.put("firstName", user.getFirstName());
            payload.put("lastName", user.getLastName());
            payload.put("roles", user.getRoles()
                    .stream()
                    .map(r -> r.getName())
                    .collect(Collectors.toSet()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(payload);
        }

    

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (userRepository.existsByUsernameIgnoreCase(signUpDto.getUsername())) {
            errors.put("username", "Username is already taken.");
        }
        if (userRepository.existsByEmailIgnoreCase(signUpDto.getEmail())) {
            errors.put("email", "Email is already taken.");
        }
        if (userRepository.existsByPhoneNumber(signUpDto.getPhoneNumber())) {
            errors.put("phoneNumber", "Phone number is already in use.");
        }

        if (!errors.isEmpty()) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("message", "Duplicate values.");
            body.put("errors", errors);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        }

        // Create new user's account
        User user = new User();
        user.setFirstName(signUpDto.getFirstName());
        user.setLastName(signUpDto.getLastName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setPhoneNumber(signUpDto.getPhoneNumber());
        user.setGenre(signUpDto.getGenre());

        Role role = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRoles(Collections.singleton(role));
        user.setActivationToken(UUID.randomUUID().toString());
        userRepository.save(user);

        // Send activation email
        try {
            mailService.sendActivationEmail(
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                user.getActivationToken()
            );
            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully and activation email sent.",
                "activationRequired", true
            ));
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "User registered but failed to send activation email"));
        }
    }

    

    @PostMapping("/signupSuperAdmin")
    public ResponseEntity<?> registerSuperAdmin(@RequestBody SignUpDto signUpDto){
        
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
        user.setPhoneNumber(signUpDto.getPhoneNumber());
        user.setGenre(signUpDto.getGenre());
        
        Role roles = roleRepository.findByName("ROLE_SUPER_ADMIN").orElseThrow(() -> new RuntimeException("Error: Role is not found."));
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
            return ResponseEntity.badRequest().body("{\"error\": \"User with this email does not exist.\"}");
        }
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);
        try {
            mailService.sendResetPasswordEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), resetToken);
            return ResponseEntity.ok("{\"message\": \"Reset password link sent to your email.\"}");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to send reset password email\"}");
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO request) {
        User user = userRepository.findByResetToken(request.getToken()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid or expired reset token."));
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null); // Clear the token after use
        userRepository.save(user);

        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset successfully."));
    }

   @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie clear = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) // true in prod (HTTPS)
                .path("/")
                .sameSite("Lax")
                .maxAge(0) // delete now
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clear.toString())
                .body(Collections.singletonMap("message", "Logged out"));
    }

    @PostMapping("/contacts")
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        Contact savedContact = contactService.saveContact(contact);
        
        // Send acknowledgment email in English
        try {
            mailService.sendContactFormAcknowledgment(
                contact.getEmail(), 
                contact.getName()
            );
        } catch (MessagingException e) {
            // Log the error but don't fail the contact creation
            System.err.println("Failed to send contact acknowledgment email: " + e.getMessage());
        }
        
        return ResponseEntity.ok(savedContact);
    }
    @GetMapping("/news")
    public List<NewsDTO> getAllNews() {
        return newsService.getAllNews();
    }
    @GetMapping("/news/{id}")
    public NewsDTO getById(@PathVariable Long id) {
        return newsService.getNewsByIdDTO(id); // ✅ correct name
    }
    @GetMapping("/users/count-role-user")
    public ResponseEntity<Map<String, Long>> countUsersWithRoleUser() {
        long count = userRepository.countByRoles_Name("ROLE_USER");
        Map<String, Long> response = new HashMap<>();
        response.put("userCount", count);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/demandes/complete-results")
        public ResponseEntity<List<DemandeUserDTO>> getDemandesWithCompleteResults() {
            List<Demande> demandes = demandeRepository.findAllWithClientsByEtat(AnalysisStatus.COMPLETE_RESULTS);

            List<DemandeUserDTO> response = demandes.stream()
                .map(d -> new DemandeUserDTO(
                        d.getDemandeId(),
                        d.getClients().stream()
                            .map(u -> new User2DTO(u.getUsername(), u.getImageUrl()))
                            .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        }

   @GetMapping("/users/by-role")
    public ResponseEntity<List<Map<String, Object>>> getUsersByRole(@RequestParam String role) {
        List<User> users = userRepository.findByRoles_Name(role);

        List<Map<String, Object>> userResponses = users.stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("username", user.getUsername());
            userMap.put("imageUrl", user.getImageUrl());
            return userMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(userResponses);
    }
    @GetMapping("/project-cards")
    public List<ProjectCard> list() {
        return service.list();
    }

}
