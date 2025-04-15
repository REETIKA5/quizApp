package com.quiz.Backend.services;

import com.quiz.Backend.dto.OpenTdbResponse;
import com.quiz.Backend.dto.OpenTdbResult;
import com.quiz.Backend.models.Question;
import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.repositories.QuestionRepository;
import com.quiz.Backend.repositories.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final RestTemplate restTemplate;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, RestTemplate restTemplate) {
        this.questionRepository = questionRepository;
        this.restTemplate = restTemplate;
    }
    @Transactional
    public List<Question> fetchOrCreateQuestions(Tournament tournament, String category, String difficulty) {
        // Check if questions already exist for this tournament
        List<Question> existingQuestions = questionRepository.findByTournament(tournament);

        if (!existingQuestions.isEmpty()) {
            // If questions already exist, return them
            return existingQuestions;
        }

        // If no questions exist, fetch from OpenTDB
        return fetchQuestionsFromOpenTdb(category, difficulty, tournament);
    }

    @Transactional
    public List<Question> fetchQuestionsFromOpenTdb(String category, String difficulty, Tournament tournament) {
        // Construct the OpenTDB API URL
        String url = String.format(
                "https://opentdb.com/api.php?amount=10&category=%s&difficulty=%s&type=multiple",
                category, difficulty
        );

        // Fetch the questions from OpenTDB
        ResponseEntity<OpenTdbResponse> response = restTemplate.getForEntity(url, OpenTdbResponse.class);
        List<Question> questionList = new ArrayList<>();

        if (response.getBody() != null && response.getBody().getResults() != null) {
            // Map OpenTDB results to Question entities
            for (OpenTdbResult result : response.getBody().getResults()) {
                Question question = new Question(
                        result.getQuestion(),             // Question text
                        result.getCorrect_answer(),       // Correct answer
                        result.getIncorrect_answers(),    // Incorrect answers
                        "multiple-choice",                // Question type
                        tournament                        // Tournament reference
                );

                // Save the question to the database
                questionRepository.save(question);
                questionList.add(question);
            }
        } else {
            throw new RuntimeException("No questions found for the specified category and difficulty.");
        }

        // Return the list of questions
        return questionList;
    }

    public void deleteQuestionsByTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        List<Question> questions = questionRepository.findByTournament(tournament);
        questionRepository.deleteAll(questions);
    }


    public List<Question> getQuestionsByTournament(Tournament tournament) {
        // Fetch questions based on the tournament they belong to
        return questionRepository.findByTournament(tournament);
    }
}