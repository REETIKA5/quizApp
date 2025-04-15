package com.quiz.Backend.services;

import com.quiz.Backend.models.Score;
import com.quiz.Backend.models.User;
import com.quiz.Backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    private EmailService emailService;  // Email service to send emails

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }



    public User registerUser(User user) {
        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String username, String password) {
        return userRepository.findByUsername(username).filter(user -> user.getPassword().equals(password));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> updateUserByUsername(String username, User updatedUser) {
        Optional<User> existingUserOpt = userRepository.findByUsername(username);

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setAge(updatedUser.getAge());

            // Check if the password is being changed
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                // Check if it's already encoded (starts with $2a$)
                if (!updatedUser.getPassword().startsWith("$2a$")) {
                    String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
                    existingUser.setPassword(encodedPassword);
                } else {
                    existingUser.setPassword(updatedUser.getPassword()); // fallback
                }
            }

            userRepository.save(existingUser);
            return Optional.of(existingUser);
        }

        return Optional.empty();
    }





    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public boolean requestPasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Simple reset email without token generation (no link)
            String resetMessage = "Hi " + user.getFirstName() + ",\n\n" +
                    "We received a request to reset your password. " +
                    "If you requested a password reset, please follow these instructions: \n\n" +
                    "1. Go to the following link to reset your password: \n" +
                    "   http://localhost:8080/reset-password\n" +
                    "2. Enter your new password.\n\n" +
                    "If you did not request a password reset, please ignore this email.\n\n" +
                    "Best regards,\nYour Team";


            emailService.sendEmail(
                    user.getEmail(),
                    "Password Reset Request",
                    resetMessage
            );

            return true;
        }

        return false;
    }


    public void save(User user) {
        userRepository.save(user);
    }



}
