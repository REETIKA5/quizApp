package com.quiz.Backend.services;

import com.quiz.Backend.models.Score;
import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.models.User;
import com.quiz.Backend.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;

    @Autowired
    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
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
}


