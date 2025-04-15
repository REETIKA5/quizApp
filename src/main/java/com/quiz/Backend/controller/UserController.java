package com.quiz.Backend.controller;

import com.quiz.Backend.models.User;
import com.quiz.Backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.Optional;


@RestController
@RequestMapping("/api/users")

public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        user.setRole(User.Role.PLAYER); // default role unless admin
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(user));
    }
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String username, @RequestParam String password) {
        Optional<User> user = userService.loginUser(username, password);
        if (user.isPresent()) {
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

        @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out");
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/update/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody @Valid User updatedUser) {
        Optional<User> user = userService.updateUserByUsername(username, updatedUser);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.status(404).body(null);
    }


    @PostMapping("/request-reset-password")
    public ResponseEntity<String> requestResetPassword(@RequestParam String email) {
        boolean result = userService.requestPasswordReset(email);
        if (result) {
            return ResponseEntity.ok("Reset instructions sent to email.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword)); // Encrypting password before saving
            userService.save(user); // Saving the user with the updated password
            return ResponseEntity.ok("Password reset successfully.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid email.");
    }

}
