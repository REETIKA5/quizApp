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


    public List<String> getCorrectAnswers(Long tournamentId) {
        // Fetch the tournament by ID
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));


        List<Question> questions = questionRepository.findByTournament(tournament);

        // Extract the correct answers and return them as a list of strings
        return questions.stream()
                .map(Question::getCorrectAnswer) // Extract the correct answer from each question
                .collect(Collectors.toList());
    }

    public Score submitQuiz(Long tournamentId, Long userId, List<String> submittedAnswers) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Question> questions = questionRepository.findByTournament(tournament);
        int correctCount = 0;

        for (int i = 0; i < questions.size(); i++) {
            String correct = questions.get(i).getCorrectAnswer();
            String submitted = submittedAnswers.get(i);
            if (correct != null && correct.equals(submitted)) {
                correctCount++;
            }
        }

        Score score = new Score();
        score.setPlayer(user); // 👈 USERNAME indirectly saved via User entity
        score.setTournament(tournament); // 👈 Saves tournament ID
        score.setCorrectAnswers(correctCount);
        score.setPlayerScore(correctCount); // Or correctCount * 10
        score.setCompletedDate(LocalDateTime.now());
        score.setAnswers(submittedAnswers); // Optional but nice

        return scoreRepository.save(score); // 👈 THIS SAVES TO DATABASE
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

    public List<Score> getLeaderboard(Long tournamentId) {
        return scoreRepository.findByTournamentIdOrderByPlayerScoreDesc(tournamentId);
    }



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
