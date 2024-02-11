package tech.majaliwa.game;

import static tech.majaliwa.game.Game.getTopCard;

public class AI extends User {
    public AI(String name) {
        super(name);
    }

    public void aiTurn() {
        System.out.println("It's " + this.getName() + "'s turn");
    }
}
