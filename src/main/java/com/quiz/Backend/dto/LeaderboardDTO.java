package com.quiz.Backend.dto;

import java.time.LocalDateTime;

public class LeaderboardDTO {

    private String playerName;
    private int score;
    private LocalDateTime completionDate;

    public LeaderboardDTO(String playerName, int score, LocalDateTime completionDate) {
        this.playerName = playerName;
        this.score = score;
        this.completionDate = completionDate;
    }


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }
}
