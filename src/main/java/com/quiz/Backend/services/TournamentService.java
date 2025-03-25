package com.quiz.Backend.services;

import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.models.User;
import com.quiz.Backend.repositories.TournamentRepository;
import com.quiz.Backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private UserRepository userRepository;
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

    public Optional<Tournament> getTournamentById(Long id) {
        return tournamentRepository.findById(id);
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    public Tournament updateTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public void deleteTournament(Long id) {
        tournamentRepository.deleteById(id);
    }


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



    public void decrementLikes(Tournament tournament) {
        if (tournament.getLikes() > 0) {
            tournament.setLikes(tournament.getLikes() - 1);
            tournamentRepository.save(tournament);
        }
    }
}