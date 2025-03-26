package com.quiz.Backend.services;

import com.quiz.Backend.models.Question;
import com.quiz.Backend.models.Score;
import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.models.User;
import com.quiz.Backend.repositories.QuestionRepository;
import com.quiz.Backend.repositories.ScoreRepository;
import com.quiz.Backend.repositories.TournamentRepository;
import com.quiz.Backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final TournamentRepository tournamentRepository; // Inject TournamentRepository
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

   @Autowired
    public ScoreService(ScoreRepository scoreRepository, TournamentRepository tournamentRepository, UserRepository userRepository, QuestionRepository questionRepository) {
        this.scoreRepository = scoreRepository;
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    // Method to get correct answers for a tournament
    public List<String> getCorrectAnswers(Long tournamentId) {
        // Fetch the tournament by ID
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Fetch all questions associated with the tournament
        List<Question> questions = questionRepository.findByTournament(tournament);

        // Extract the correct answers and return them as a list of strings
        return questions.stream()
                .map(Question::getCorrectAnswer) // Extract the correct answer from each question
                .collect(Collectors.toList());
    }

    // Submit quiz and calculate score
    public Score submitQuiz(Long tournamentId, Long userId, List<String> submittedAnswers) {
        // Fetch the tournament and user
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new RuntimeException("Tournament not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Get the correct answers for the tournament
        List<String> correctAnswers = getCorrectAnswers(tournamentId);

        // Calculate the number of correct answers
        int correctCount = 0;
        for (int i = 0; i < correctAnswers.size(); i++) {
            if (submittedAnswers.get(i).equals(correctAnswers.get(i))) {
                correctCount++;
            }
        }

        // Calculate score (assuming 10 points per correct answer)
        int score = (correctCount * 10) / correctAnswers.size();

        // Create and save the Score record
        Score scoreRecord = new Score();
        scoreRecord.setTournament(tournament);
        scoreRecord.setPlayer(user);
        scoreRecord.setPlayerScore(score);
        scoreRecord.setCorrectAnswers(correctCount);
        scoreRecord.setAnswers(submittedAnswers);
        scoreRecord.setCompletedDate(LocalDateTime.now());

        scoreRepository.save(scoreRecord);  // Save the score record

        return scoreRecord;
    }

    public Score savePlayerScore(Tournament tournament, User player, int playerScore) {
        Score score = new Score(tournament, player, playerScore, LocalDateTime.now());
        return scoreRepository.save(score);
    }

    public List<Score> getScoresByTournament(Tournament tournament) {
        return scoreRepository.findByTournament(tournament);
    }

    public List<Score> getScoresByPlayer(User player) {
        return scoreRepository.findByPlayer(player);
    }



    // Provide feedback (unchanged)
    public List<String> provideFeedback(List<String> submittedAnswers, List<String> correctAnswers) {
        List<String> feedback = new ArrayList<>();
        for (int i = 0; i < correctAnswers.size(); i++) {
            if (submittedAnswers.get(i).equals(correctAnswers.get(i))) {
                feedback.add("Question " + (i + 1) + ": Correct");
            } else {
                feedback.add("Question " + (i + 1) + ": Incorrect. Correct answer: " + correctAnswers.get(i));
            }
        }
        return feedback;
    }
}
