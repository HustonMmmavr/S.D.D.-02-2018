package com.colorit.backend.game.session;

public class GameResults {
    private boolean isWinner;
    private int rating;

    public GameResults(boolean isWinner, int rating) {
        this.isWinner = isWinner;
        this.rating = rating;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public int getRating() {
        return rating;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
