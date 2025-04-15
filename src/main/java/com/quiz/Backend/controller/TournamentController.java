package com.quiz.Backend.controller;

import com.quiz.Backend.dto.QuestionDTO;
import com.quiz.Backend.dto.TournamentDTO;
import com.quiz.Backend.models.Question;
import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.models.TournamentStatus;
import com.quiz.Backend.services.TournamentService;
import com.quiz.Backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tournaments")

public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private UserService userService;


    @PostMapping("/create")
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament tournament) {
        return ResponseEntity.ok(tournamentService.createTournament(tournament));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Map<String, Object>>> getTournamentsByStatus(@PathVariable String status) {
        try {
            TournamentStatus tournamentStatus = TournamentStatus.valueOf(status.toUpperCase());

            List<Tournament> tournaments = tournamentService.getTournamentsByStatus(tournamentStatus);

            // Manually filter only the required fields
            List<Map<String, Object>> response = tournaments.stream().map(t -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", t.getId());
                map.put("name", t.getName());
                map.put("category", t.getCategory());
                map.put("difficulty", t.getDifficulty());
                map.put("startDate", t.getStartDate().toString());
                map.put("endDate", t.getEndDate().toString());
                map.put("status", t.getStatus().toString());
                return map;
            }).toList();

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/category/{category}/status/{status}")
    public ResponseEntity<List<Tournament>> getTournamentsByCategoryAndStatus(
            @PathVariable String category, @PathVariable String status) {
        try {
            TournamentStatus tournamentStatus = TournamentStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(tournamentService.getTournamentsByCategoryAndStatus(category, tournamentStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(null);  // Return 400 Bad Request if status is invalid
        }
    }


    @PostMapping("/start/{id}")
    public ResponseEntity<String> startTournament(@PathVariable Long id) {
        Optional<Tournament> tournament = tournamentService.startTournament(id);
        if (tournament.isPresent()) {
            return ResponseEntity.ok("Tournament started");
        }
        return ResponseEntity.status(404).body("Tournament not found");
    }


    @PostMapping("/end/{id}")
    public ResponseEntity<String> endTournament(@PathVariable Long id) {
        Optional<Tournament> tournament = tournamentService.endTournament(id);
        if (tournament.isPresent()) {
            return ResponseEntity.ok("Tournament ended");
        }
        return ResponseEntity.status(404).body("Tournament not found");
    }

    @PostMapping("/add-participant/{tournamentId}/{userId}")
    public ResponseEntity<String> addParticipant(@PathVariable Long tournamentId, @PathVariable Long userId) {
        Optional<Tournament> tournament = tournamentService.addParticipant(tournamentId, userId);
        if (tournament.isPresent()) {
            return ResponseEntity.ok("User added as participant");
        }
        return ResponseEntity.status(404).body("Tournament not found");
    }


    @PostMapping("/like/{id}/{userId}")
    public ResponseEntity<String> likeTournament(@PathVariable Long id, @PathVariable Long userId) {
        Optional<Tournament> tournament = tournamentService.likeTournament(id, userId);
        if (tournament.isPresent()) {
            return ResponseEntity.ok("Tournament liked");
        }
        return ResponseEntity.status(404).body("Tournament not found");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable Long id, @RequestBody Tournament tournament) {
        tournament.setId(id);
        return ResponseEntity.ok(tournamentService.updateTournament(tournament));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournament(@PathVariable Long id) {
        return tournamentService.getTournamentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }

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




    @PostMapping("/{id}/start")
    public ResponseEntity<List<QuestionDTO>> startQuiz(@PathVariable Long id) {
        List<QuestionDTO> questions = tournamentService.startQuiz(id);
        return ResponseEntity.ok(questions);
    }


    @PostMapping("/{id}/submit-answers/{username}")
    public ResponseEntity<Map<String, Object>> submitQuizAnswers(
            @PathVariable Long id,
            @PathVariable String username,
            @RequestBody Map<String, String> userAnswers) {

        // 1. Check if user is participating
        if (!tournamentService.isUserParticipating(id, username)) {
            return ResponseEntity.status(403).body(Map.of("message", "User is not part of the tournament"));
        }

        // 2. Get the tournament and its questions
        Tournament tournament = tournamentService.getTournamentById(id).orElseThrow();
        List<Question> questions = tournament.getQuestions();

        // 3. Score calculation
        int score = 0;
        for (Map.Entry<String, String> entry : userAnswers.entrySet()) {
            String questionId = entry.getKey();
            String userAnswer = entry.getValue();

            Optional<Question> questionOpt = questions.stream()
                    .filter(q -> q.getId().toString().equals(questionId))
                    .findFirst();

            if (questionOpt.isPresent()) {
                Question question = questionOpt.get();
                if (question.getCorrectAnswer().equals(userAnswer)) {
                    score++;
                }
            }
        }

        // ✅ 4. Include question-by-question feedback
        List<Map<String, Object>> questionResults = new ArrayList<>();
        for (Question question : questions) {
            String questionId = question.getId().toString();
            String userAnswer = userAnswers.getOrDefault(questionId, null);

            Map<String, Object> qResult = new HashMap<>();
            qResult.put("questionId", question.getId());
            qResult.put("questionText", question.getQuestionText());
            qResult.put("correctAnswer", question.getCorrectAnswer());
            qResult.put("userAnswer", userAnswer);

            questionResults.add(qResult);
        }

        // ✅ 5. Prepare full response
        Map<String, Object> response = new HashMap<>();
        response.put("score", score);
        response.put("questionResults", questionResults);

        return ResponseEntity.ok(response);
    }




    @GetMapping("/{id}/questions")
    public List<Question> getQuestions(@PathVariable Long id) {
        Tournament tournament = tournamentService.getTournamentById(id).orElseThrow();
        return tournament.getQuestions();
    }
    @GetMapping("")
    public ResponseEntity<List<TournamentDTO>> getAllTournaments() {
        List<Tournament> tournaments = tournamentService.getAllTournaments();

        List<TournamentDTO> tournamentDTOs = tournaments.stream().map(t -> {
            TournamentDTO dto = new TournamentDTO();
            dto.setId(t.getId());
            dto.setName(t.getName());
            dto.setCategory(t.getCategory());
            dto.setDifficulty(t.getDifficulty());
            dto.setStartDate(t.getStartDate().toString());
            dto.setEndDate(t.getEndDate().toString());
            dto.setLikes(t.getLikes());
            dto.setQuestionCount(t.getQuestions() != null ? t.getQuestions().size() : 0); // ✅ NEW
            return dto;
        }).toList();

        return ResponseEntity.ok(tournamentDTOs);
    }

    @GetMapping("/participated/{username}")
    public List<Tournament> getParticipatedTournaments(@PathVariable String username) {
        return tournamentService.getParticipatedTournamentsByUsername(username);
    }
    @GetMapping("/{id}/correct-answers")
    public ResponseEntity<List<Map<String, Object>>> getCorrectAnswers(@PathVariable Long id) {
        Optional<Tournament> optionalTournament = tournamentService.getTournamentById(id);
        if (optionalTournament.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Question> questions = optionalTournament.get().getQuestions();

        List<Map<String, Object>> correctAnswers = questions.stream().map(q -> {
            Map<String, Object> map = new HashMap<>();
            map.put("questionId", q.getId());
            map.put("questionText", q.getQuestionText());
            map.put("correctAnswer", q.getCorrectAnswer());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(correctAnswers);
    }

}
