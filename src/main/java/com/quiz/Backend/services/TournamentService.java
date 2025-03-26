package com.quiz.Backend.services;

import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.models.TournamentStatus;
import com.quiz.Backend.models.User;
import com.quiz.Backend.repositories.TournamentRepository;
import com.quiz.Backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }
    // Check if the tournament is ongoing (can participate only if it's ongoing)
    public boolean canParticipate(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Only allow participation if the status is ongoing
        return tournament.getStatus() == TournamentStatus.ONGOING;
    }

    // Allow a user to participate in a tournament
    public void participateInTournament(Long tournamentId, String username) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent duplicate participation
        if (!tournament.getParticipants().contains(user)) {
            tournament.getParticipants().add(user);
            tournamentRepository.save(tournament);  // Save the updated tournament
        } else {
            throw new RuntimeException("User has already participated in this tournament.");
        }
    }

    // Get tournaments by status (e.g., upcoming, ongoing, past)
    public List<Tournament> getTournamentsByStatus(TournamentStatus status) {
        return tournamentRepository.findByStatus(status);
    }

    // Get ongoing tournaments
    public List<Tournament> getOngoingTournaments() {
        return tournamentRepository.findByStatus(TournamentStatus.ONGOING);
    }

    @Autowired
    private UserRepository userRepository;

    // Create a new tournament
    public Tournament createTournament(Tournament tournament) {
        Tournament savedTournament = tournamentRepository.save(tournament);

        // Send email to all non-admin users
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.getRole().name().equals("ADMIN")) {
                emailService.sendEmail(
                        user.getEmail(),
                        "🎉 New Tournament: " + savedTournament.getName(),
                        "Hi " + user.getFirstName() + ",\n\nA new quiz tournament has been created!\n" +
                                "Tournament: " + savedTournament.getName() + "\nCategory: " + savedTournament.getCategory() +
                                "\nDifficulty: " + savedTournament.getDifficulty() +
                                "\n\nLog in now to participate and win!\n\nCheers,\nQuiz Team"
                );
            }
        }

        return savedTournament;
    }

    // Get tournament by ID
    public Optional<Tournament> getTournamentById(Long id) {
        return tournamentRepository.findById(id);
    }

    // Get all tournaments
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    // Update tournament
    public Tournament updateTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    // Delete tournament
    public void deleteTournament(Long id) {
        tournamentRepository.deleteById(id);
    }


    // Increment likes for a tournament (like/unlike toggle)
    @Transactional  // Ensures database transaction
    public void incrementLikes(Long id, String username) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found with ID: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Prevent duplicate likes
        if (!tournament.getLikedByUsers().contains(user)) {
            tournament.getLikedByUsers().add(user);
            tournament.setLikes(tournament.getLikes() + 1);

            tournamentRepository.save(tournament);  // Explicitly save tournament
            userRepository.save(user);  // Explicitly save user update
        }
    }

    // Decrement likes for a tournament (like/unlike toggle)
    public void decrementLikes(Tournament tournament) {
        if (tournament.getLikes() > 0) {
            tournament.setLikes(tournament.getLikes() - 1);
            tournamentRepository.save(tournament);
        }
    }

    // Toggle Like for the tournament
    public void toggleLike(Long tournamentId, Long userId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        if (tournament.getLikedByUsers().contains(user)) {
            // If user has already liked, unlike it
            tournament.getLikedByUsers().remove(user);
        } else {
            // Otherwise, like it
            tournament.getLikedByUsers().add(user);
        }

        tournamentRepository.save(tournament);
    }


}
