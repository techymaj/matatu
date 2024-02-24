package tech.majaliwa.game;

import static tech.majaliwa.game.GameColors.*;

public enum Suit {
    HEARTS("♥"),
    SPADES("♠"),
    CLUBS("♣"),
    DIAMONDS("♦"),
    JOKER_F("\uD83D\uDC83"),
    JOKER_M("\uD83D\uDD7A"); //

    private final String unicode;

    private Suit(String unicode) {
        switch (unicode) {
            case "♥", "♦" -> {
                this.unicode = RED + unicode + RESET;
            }
            default -> {
                this.unicode = WHITE + unicode + RESET;
            }
        }
    }

    public String getUnicode() {
        return this.unicode;
    }
}
