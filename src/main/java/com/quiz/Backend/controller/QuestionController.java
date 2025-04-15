package com.quiz.Backend.controller;

import com.quiz.Backend.models.Question;
import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.services.QuestionService;
import com.quiz.Backend.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")

public class QuestionController {

    private final QuestionService questionService;
    private final TournamentService tournamentService;

    @Autowired
    public QuestionController(QuestionService questionService, TournamentService tournamentService) {
        this.questionService = questionService;
        this.tournamentService = tournamentService;
    }

    @PostMapping("/fetch/{tournamentId}")
    public ResponseEntity<List<Question>> fetchQuestions(
            @PathVariable Long tournamentId,
            @RequestParam String category,
            @RequestParam String difficulty) {

        // Get the tournament object from the database
        Tournament tournament = tournamentService.getTournamentById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Fetch or create questions based on the tournament ID, category, and difficulty
        List<Question> questions = questionService.fetchOrCreateQuestions(tournament, category, difficulty);

        // Return the questions as the response
        return ResponseEntity.ok(questions);

    }

    @GetMapping("/tournament/{tournamentId}")
    public List<Question> getQuestionsByTournament(@PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        return questionService.getQuestionsByTournament(tournament);
    }
    @DeleteMapping("/byTournament/{tournamentId}")
    public ResponseEntity<String> deleteQuestionsByTournament(@PathVariable Long tournamentId) {
        questionService.deleteQuestionsByTournament(tournamentId);
        return ResponseEntity.ok("✅ All questions for tournament " + tournamentId + " deleted.");
    }

}