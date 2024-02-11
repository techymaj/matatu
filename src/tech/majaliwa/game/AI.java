package tech.majaliwa.game;

import static tech.majaliwa.game.Game.playerPickCount;

public class AI extends User {

    public AI(String name) {
        super(name);
    }

    void aiTurn(AI ai) {
        System.out.println("AI's turn");
        playerPickCount = 0;
    }
}