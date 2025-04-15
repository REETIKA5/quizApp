package com.quiz.Backend.controller;


import com.quiz.Backend.dto.LeaderboardDTO;

import com.quiz.Backend.services.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/leaderboard")
public class LeaderBoardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("/tournament/{tournamentId}")
    public List<LeaderboardDTO> getLeaderboard(@PathVariable Long tournamentId) {
        return leaderboardService.getLeaderboard(tournamentId);
    }

    @GetMapping("/total-players/{tournamentId}")
    public int getTotalPlayers(@PathVariable Long tournamentId) {
        return leaderboardService.getTotalPlayers(tournamentId);
    }

    @GetMapping("/average-score/{tournamentId}")
    public double getAverageScore(@PathVariable Long tournamentId) {
        return leaderboardService.getAverageScore(tournamentId);
    }
}
