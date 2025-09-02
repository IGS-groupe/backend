package com.example.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.example.backend.config.FileUploadUtil;
import com.example.backend.config.JwtService;
import com.example.backend.dto.ChangePasswordDTO;
import com.example.backend.dto.SignUpDto;
import com.example.backend.dto.UserDTO;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.services.ContactService;
import com.example.backend.services.MailService;
import com.example.backend.services.UserService;
import lombok.extern.slf4j.Slf4j;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Value("${app.base-url}")
    private String baseUrl;

    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          MailService mailService,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/signupAdmin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> registerAdmin(@RequestBody SignUpDto signUpDto){
        
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setFirstName(signUpDto.getFirstName());
        user.setLastName(signUpDto.getLastName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setGenre(signUpDto.getGenre());
        user.setPhoneNumber(signUpDto.getPhoneNumber());
        
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

    @GetMapping("{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long userId){
        User user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/role/user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public List<User> getUsersWithUserRole() {
        return userService.getUsersByRole("ROLE_USER");
    }

    @GetMapping("/role/admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<User> getUsersWithAdminRole() {
        return userService.getUsersByRole("ROLE_ADMIN");
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<?> getMe(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Not authenticated"));
        }

        // Find full User entity from DB using username/email from token
        User user = userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "User not found"));
        }

        // Build response DTO with safe fields (no password)
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRoles(user.getRoles().stream().map(Role::getName).toList());
        dto.setActive(user.isActive());

        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "{id}", consumes = "application/json")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<User> updateUserJson(
            @PathVariable Long id,
            @RequestBody User user) {

        User existingUser = userService.getUserById(id);
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setEmail(user.getEmail());

        User updatedUser = userService.updateUser(existingUser);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping(value = "{id}/with-file", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<User> updateUserWithFile(
            @PathVariable Long id,
            @RequestPart("firstName") String firstName,
            @RequestPart("lastName") String lastName,
            @RequestPart("phoneNumber") String phoneNumber,
            @RequestPart("email") String email,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {

        User existingUser = userService.getUserById(id);
        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser.setPhoneNumber(phoneNumber);
        existingUser.setEmail(email);

        if (logo != null && !logo.isEmpty()) {
            String relativePath = FileUploadUtil.saveUserImage(id, logo);
            String imagePath = baseUrl + relativePath;
            existingUser.setImageUrl(imagePath);
        }

        User updatedUser = userService.updateUser(existingUser);
        return ResponseEntity.ok(updatedUser);
    }


    @PutMapping("/disable/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> disableUser(@PathVariable("id") Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        if(user.isActive()){
            user.setActive(false);
        }
        else{
            user.setActive(true);
        }
        userRepository.save(user);
        return ResponseEntity.ok("User account disabled successfully.");
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDto) {
        try {
            String usernameOrEmail = changePasswordDto.getUsernameOrEmail();
            User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "User not found."));
            }
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), changePasswordDto.getPassword()));
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            userRepository.save(user);
            return ResponseEntity.badRequest().body(Collections.singletonMap("message","Password updated successfully."));

        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message","Incorrect old password."));
        }
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long userId){
        userService.deleteUser(userId);
        return new ResponseEntity<>("User successfully deleted!", HttpStatus.OK);
    }
}
