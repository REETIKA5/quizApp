package com.quiz.Backend.controller;

import com.quiz.Backend.models.Score;
import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.models.User;
import com.quiz.Backend.services.ScoreService;
import com.quiz.Backend.services.TournamentService;
import com.quiz.Backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreService scoreService;
    private final UserService userService;
    private final TournamentService tournamentService;

    @Autowired
    public ScoreController(ScoreService scoreService, UserService userService, TournamentService tournamentService) {
        this.scoreService = scoreService;
        this.userService = userService;
        this.tournamentService = tournamentService;
    }

    @PostMapping("/{tournamentId}/{username}")
    public ResponseEntity<Score> saveScore(@PathVariable Long tournamentId,
                                           @PathVariable String username,
                                           @RequestParam int playerScore) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId).orElseThrow();
        User user = userService.findByUsername(username).orElseThrow();

        Score score = scoreService.savePlayerScore(tournament, user, playerScore);
        return new ResponseEntity<>(score, HttpStatus.CREATED);
    }
    @PostMapping("/submit/{tournamentId}/{userId}")
    public ResponseEntity<Score> submitQuiz(@PathVariable Long tournamentId,
                                            @PathVariable Long userId,
                                            @RequestParam List<String> submittedAnswers) {
        Score score = scoreService.submitQuiz(tournamentId, userId, submittedAnswers);
        return new ResponseEntity<>(score, HttpStatus.CREATED);
    }

    @GetMapping("/tournament/{id}")
    public List<Score> getScoresByTournament(@PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId).orElseThrow();
        return scoreService.getScoresByTournament(tournament);
    }

    @GetMapping("/player/{username}")
    public List<Score> getScoresByPlayer(@PathVariable String username) {
        User player = userService.findByUsername(username).orElseThrow();
        return scoreService.getScoresByPlayer(player);
    }
}
