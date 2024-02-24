package tech.majaliwa.game;

import static tech.majaliwa.game.Game.scanner;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.startGame();
        scanner.close();
    }
}
