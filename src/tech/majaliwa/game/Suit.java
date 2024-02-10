package tech.majaliwa.game;

public enum Suit {
    HEARTS("♥"),
    SPADES("♠"),
    CLUBS("♣"),
    DIAMONDS("♦"),
    JOKER_F("\uD83D\uDC83"),
    JOKER_M("\uD83D\uDD7A"); //

    private final String unicode;

    private Suit(String unicode) {
        this.unicode = unicode;
    }

    public String getUnicode() {
        return this.unicode;
    }
}
