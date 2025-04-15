package com.quiz.Backend.repositories;

import com.quiz.Backend.models.Score;
import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findByPlayer(User player);
    List<Score> findByTournament(Tournament tournament);
    List<Score> findByTournamentId(Long tournamentId);
    int countByTournamentId(Long tournamentId);
    List<Score> findByTournamentIdOrderByPlayerScoreDesc(Long tournamentId);

}
