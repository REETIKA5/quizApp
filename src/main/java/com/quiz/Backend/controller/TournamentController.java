package com.quiz.Backend.controller;

import com.quiz.Backend.models.Question;
import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.services.TournamentService;
import com.quiz.Backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private UserService userService;

    @PostMapping
    public Tournament createTournament(@RequestBody Tournament tournament) {
        return tournamentService.createTournament(tournament);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournament(@PathVariable Long id) {
        return tournamentService.getTournamentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Tournament> getAllTournaments() {
        return tournamentService.getAllTournaments();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable Long id, @RequestBody Tournament tournament) {
        tournament.setId(id);
        return ResponseEntity.ok(tournamentService.updateTournament(tournament));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/like/{username}")
    public ResponseEntity<Map<String, Object>> likeTournament(@PathVariable Long id, @PathVariable String username) {
        tournamentService.incrementLikes(id, username);

        Map<String, Object> response = new HashMap<>();
        response.put("tournamentId", id);
        response.put("likedBy", username);
        response.put("message", "Tournament liked successfully!");

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}/questions")
    public List<Question> getQuestions(@PathVariable Long id) {
        Tournament tournament = tournamentService.getTournamentById(id).orElseThrow();
        return tournament.getQuestions();
    }
}
