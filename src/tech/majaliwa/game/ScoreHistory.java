package tech.majaliwa.game;

public class ScoreHistory {
    private User user;
    private int score;
    private int round;

    public ScoreHistory(User user, int score, int round) {
        this.user = user;
        this.score = score;
        this.round = round;
    }

    @Override
    public String toString() {
        return ("""
                In round #%d:
                %s has %d points
                """).formatted(round, user.getName(), score);

    }
}
