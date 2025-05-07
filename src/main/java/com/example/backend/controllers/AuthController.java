package com.example.backend.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.config.JwtService;
import com.example.backend.dto.LoginDto;
import com.example.backend.dto.ResetPasswordDTO;
import com.example.backend.dto.SignUpDto;
import com.example.backend.entity.Contact;
import com.example.backend.entity.News;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.services.ContactService;
import com.example.backend.services.MailService;
import com.example.backend.services.NewsService;
import com.example.backend.services.TokenBlacklistService;

import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    // private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto) {
        String usernameOrEmail = loginDto.getUsernameOrEmail();
        // Find user by username or email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "User not found."));
        }

        // Authenticate the user using the provided credentials
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, loginDto.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Invalid credentials"));
        }
        
        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Account is disabled."));
        }
        
        // Generate JWT token for the authenticated user
        String jwtToken = jwtService.generateToken(user);
        Long userId = user.getId();
        String firstName = user.getFirstName(); // Corrected typo from 'fisrtName' to 'firstName'
        String lastName = user.getLastName();
        Set<String> roles = user.getRoles().stream()
                                .map(role -> role.getName())
                                .collect(Collectors.toSet());
        
        // Return the authentication response with user details and roles
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User authenticated successfully");
        response.put("userId", userId);
        response.put("firstName", firstName); // Corrected key from 'fisrtName'
        response.put("lastName", lastName);
        response.put("roles", roles); // Changed from 'role' to 'roles' to reflect multiple roles may exist
        response.put("token", jwtToken);
        
        return ResponseEntity.ok(response); // Changed from CREATED to OK for a standard login operation
    }
    

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto) {
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            return ResponseEntity.badRequest().body("{\"error\": \"Username is already taken!\"}");
        }
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return ResponseEntity.badRequest().body("{\"error\": \"Email is already taken!\"}");
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
    
        Role roles = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRoles(Collections.singleton(roles));
        user.setActivationToken(UUID.randomUUID().toString());
        userRepository.save(user);
    
        // Send activation email
        try {
            mailService.sendActivationEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), user.getActivationToken());
            return ResponseEntity.ok("{\"message\": \"User registered successfully and activation email sent.\"}");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"User registered but failed to send activation email\"}");
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
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid Authorization header.");
        }

        String jwt = authHeader.substring(7); // Extract JWT from header
        tokenBlacklistService.blacklistToken(jwt);
        return ResponseEntity.ok("Logout successful.");
    }

    @PostMapping("/contacts")
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        Contact savedContact = contactService.saveContact(contact);
        return ResponseEntity.ok(savedContact);
    }
    @GetMapping("/news")
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }
}