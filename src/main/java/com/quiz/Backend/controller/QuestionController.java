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

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/fetch/{tournamentId}")
    public ResponseEntity<List<Question>> fetchQuestions(
            @PathVariable Long tournamentId,
            @RequestParam String category,
            @RequestParam String difficulty) {

        Tournament tournament = tournamentService.getTournamentById(tournamentId).orElseThrow();
        List<Question> questions = questionService.fetchQuestionsFromOpenTdb(category, difficulty, tournament);
        return ResponseEntity.ok(questions);
    }


    @GetMapping("/tournament/{tournamentId}")
    public List<Question> getQuestionsByTournament(@PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId).orElseThrow();
        return questionService.getQuestionsByTournament(tournament);
    }
}