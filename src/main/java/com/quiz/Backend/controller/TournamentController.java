package com.quiz.Backend.controller;

import com.quiz.Backend.models.Question;
import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.models.TournamentStatus;
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

    // Method to allow user to participate in the tournament
    @PostMapping("/{id}/participate/{username}")
    public ResponseEntity<String> participateInTournament(@PathVariable Long id, @PathVariable String username) {
        // Check if the tournament is ongoing
        if (!tournamentService.canParticipate(id)) {
            return ResponseEntity.status(403).body("Tournament is not ongoing. You cannot participate.");
        }

        // Add the user to the tournament participation list
        tournamentService.participateInTournament(id, username);

        return ResponseEntity.ok("Successfully joined the tournament!");
    }


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

    // Like/Unlike Tournament
    @PostMapping("/{id}/like/{username}")
    public ResponseEntity<Map<String, Object>> likeTournament(@PathVariable Long id, @PathVariable String username) {
        tournamentService.incrementLikes(id, username);

        Map<String, Object> response = new HashMap<>();
        response.put("tournamentId", id);
        response.put("likedBy", username);
        response.put("message", "Tournament liked successfully!");

        return ResponseEntity.ok(response);
    }

    // Get all questions for a specific tournament
    @GetMapping("/{id}/questions")
    public List<Question> getQuestions(@PathVariable Long id) {
        Tournament tournament = tournamentService.getTournamentById(id).orElseThrow();
        return tournament.getQuestions();
    }

    // Get tournaments by status (upcoming, ongoing, past, participated)
    @GetMapping("/status/{status}")
    public List<Tournament> getTournamentsByStatus(@PathVariable TournamentStatus status) {
        return tournamentService.getTournamentsByStatus(status);
    }

    // Get ongoing tournaments
    @GetMapping("/ongoing")
    public List<Tournament> getOngoingTournaments() {
        return tournamentService.getOngoingTournaments();
    }


}
