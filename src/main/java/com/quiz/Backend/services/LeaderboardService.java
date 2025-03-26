package com.quiz.Backend.services;

import com.quiz.Backend.dto.LeaderboardDTO;
import com.quiz.Backend.models.Score;
import com.quiz.Backend.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    @Autowired
    private ScoreRepository scoreRepository;

    // Get leaderboard sorted by score for a tournament
    public List<LeaderboardDTO> getLeaderboard(Long tournamentId) {
        List<Score> scores = scoreRepository.findByTournamentId(tournamentId);

        // Calculate total players
        int totalPlayers = scores.size();

        // Calculate average score
        double averageScore = scores.stream().mapToInt(Score::getPlayerScore).average().orElse(0);

        // Build leaderboard (sorted by score)
        List<LeaderboardDTO> leaderboard = scores.stream()
                .map(score -> new LeaderboardDTO(score.getPlayer().getUsername(), score.getPlayerScore(), score.getCompletedDate()))
                .sorted(Comparator.comparingInt(LeaderboardDTO::getScore).reversed())  // Sort by score descending
                .collect(Collectors.toList());

        return leaderboard;
    }

    // Get total players in a tournament
    public int getTotalPlayers(Long tournamentId) {
        return scoreRepository.countByTournamentId(tournamentId);
    }

    // Get average score for a tournament
    public double getAverageScore(Long tournamentId) {
        List<Score> scores = scoreRepository.findByTournamentId(tournamentId);
        return scores.stream().mapToInt(Score::getPlayerScore).average().orElse(0);
    }
}
