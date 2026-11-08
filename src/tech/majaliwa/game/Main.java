package tech.majaliwa.game;

import java.awt.GraphicsEnvironment;

public class Main {
    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            Game game = new Game();
            game.startGame();
            Game.scanner.close();
            return;
        }

        javax.swing.SwingUtilities.invokeLater(() -> new MatatuWindow().setVisible(true));
    }
}
