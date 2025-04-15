package com.quiz.Backend.services;

import com.quiz.Backend.dto.QuestionDTO;
import com.quiz.Backend.models.Question;
import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.models.TournamentStatus;
import com.quiz.Backend.models.User;
import com.quiz.Backend.repositories.TournamentRepository;
import com.quiz.Backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final QuestionService questionService;

    @Autowired
    private EmailService emailService;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository, QuestionService questionService) {
        this.tournamentRepository = tournamentRepository;
        this.questionService = questionService;
    }
    public List<Tournament> getTournamentsByCategoryAndStatus(String category, TournamentStatus status) {
        return tournamentRepository.findByCategoryAndStatus(category, status);
    }



    public Optional<Tournament> startTournament(Long tournamentId) {
        Optional<Tournament> tournament = tournamentRepository.findById(tournamentId);
        if (tournament.isPresent()) {
            tournament.get().setStatus(TournamentStatus.ONGOING);
            return Optional.of(tournamentRepository.save(tournament.get()));
        }
        return Optional.empty();
    }


    public Optional<Tournament> endTournament(Long tournamentId) {
        Optional<Tournament> tournament = tournamentRepository.findById(tournamentId);
        if (tournament.isPresent()) {
            tournament.get().setStatus(TournamentStatus.PAST);
            return Optional.of(tournamentRepository.save(tournament.get()));
        }
        return Optional.empty();
    }

    public Optional<Tournament> addParticipant(Long tournamentId, Long userId) {
        Optional<Tournament> tournament = tournamentRepository.findById(tournamentId);
        Optional<User> user = userRepository.findById(userId);  // Fetch the User by ID
        if (tournament.isPresent() && user.isPresent()) {
            tournament.get().getParticipants().add(user.get());  // Add the User object, not the userId
            return Optional.of(tournamentRepository.save(tournament.get()));
        }
        return Optional.empty();
    }

    public Optional<Tournament> likeTournament(Long tournamentId, Long userId) {
        Optional<Tournament> tournament = tournamentRepository.findById(tournamentId);
        Optional<User> user = userRepository.findById(userId);  // Fetch the User by ID
        if (tournament.isPresent() && user.isPresent()) {
            tournament.get().getLikedByUsers().add(user.get());  // Add the User object, not the userId
            tournament.get().setLikes(tournament.get().getLikedByUsers().size());
            return Optional.of(tournamentRepository.save(tournament.get()));
        }
        return Optional.empty();
    }


    public boolean canParticipate(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        return tournament.getStatus() == TournamentStatus.ONGOING;
    }


    @Transactional
    public List<QuestionDTO> startQuiz(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        if (tournament.getStatus() != TournamentStatus.ONGOING) {
            throw new RuntimeException("Tournament is not ongoing. Cannot start quiz.");
        }

        List<Question> questions = questionService.getQuestionsByTournament(tournament);

        return questions.stream().map(question -> {
            // Combine and shuffle options
            List<String> allOptions = new ArrayList<>(question.getIncorrectAnswers());
            allOptions.add(question.getCorrectAnswer());
            Collections.shuffle(allOptions);

            // Return only sanitized data via DTO
            return new QuestionDTO(
                    question.getId(),
                    question.getQuestionText(),
                    allOptions
            );
        }).collect(Collectors.toList());
    }



    public void participateInTournament(Long tournamentId, String username) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (!tournament.getParticipants().contains(user)) {
            tournament.getParticipants().add(user);
            tournamentRepository.save(tournament);  // Save the updated tournament
        } else {
            throw new RuntimeException("User has already participated in this tournament.");
        }
    }
    public boolean isUserParticipating(Long tournamentId, String username) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        return tournament.getParticipants().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }



    public List<Tournament> getTournamentsByStatus(TournamentStatus status) {
        return tournamentRepository.findByStatus(status);
    }


    public List<Tournament> getOngoingTournaments() {
        return tournamentRepository.findByStatus(TournamentStatus.ONGOING);
    }

    @Autowired
    private UserRepository userRepository;


    public Tournament createTournament(Tournament tournament) {
        Tournament savedTournament = tournamentRepository.save(tournament);


        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getEmail() != null && !user.getRole().name().equalsIgnoreCase("ADMIN")) {
                try {
                    emailService.sendEmail(
                            user.getEmail(),
                            "🎉 New Tournament: " + savedTournament.getName(),
                            "Hi " + user.getFirstName() + ",\n\n" +
                                    "A new quiz tournament has been created just for you!\n\n" +
                                    "🏆 Tournament: " + savedTournament.getName() + "\n" +
                                    "📚 Category: " + savedTournament.getCategory() + "\n" +
                                    "🎯 Difficulty: " + savedTournament.getDifficulty() + "\n" +
                                    "🕒 Starts: " + savedTournament.getStartDate() + "\n" +
                                    "⏳ Ends: " + savedTournament.getEndDate() + "\n\n" +
                                    "Log in now and be the champion!\n\n" +
                                    "Best of luck,\nQuiz Team 🧠"
                    );
                } catch (Exception e) {
                    System.out.println("❌ Failed to send email to " + user.getEmail() + ": " + e.getMessage());
                }
            }
        }

        return savedTournament;
    }


    public Optional<Tournament> getTournamentById(Long id) {
        return tournamentRepository.findById(id);
    }



    public Tournament updateTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }


    public void deleteTournament(Long id) {
        tournamentRepository.deleteById(id);
    }
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll(); // This should return flat List<Tournament>
    }


    public List<Tournament> getTournamentsByStartDateAfter(LocalDate currentDate) {
        return tournamentRepository.findByStartDateAfter(currentDate);
    }
    public List<Tournament> getTournamentsByEndDateBefore(LocalDate currentDate) {
        return tournamentRepository.findByEndDateBefore(currentDate);
    }
    public List<Tournament> getParticipatedTournamentsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return tournamentRepository.findByParticipants(user);
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



}
