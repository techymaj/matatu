package tech.majaliwa.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static tech.majaliwa.game.Deck.printDeck;

public class Game {

    public static ArrayList<Card> pile = new ArrayList<>();
    public static ArrayList<Card> deck = new ArrayList<>();
    public static Scanner scanner = new Scanner(System.in);
    public static boolean PLAYER_TURN = true;
    public static int playerPickCount;
    public static boolean JOKER_MODE;
    public static boolean GAME_OVER;
    public static Suit askedSuit;

    public Rules rules;
    public static List<User> users = new ArrayList<>();

    public Game() {
        this.rules = new Rules();
    }

    public static void main(String[] args) {
        System.out.println(Intro.WELCOME_MESSAGE);
        System.out.println();

        var scanner = setGameMode();
        System.out.println("Enter your player name");
        String playerName = scanner.nextLine();

        var player = new Player(playerName);
        var AI = new AI("AI");
        users.add(player);
        users.add(AI);

        gameUsers(player, AI);

        gameInSession(player, AI);
    }

    public void startGame() {
        main(new String[]{});
    }

    public static void reshuffleDeckAndContinuePlaying() {
        var getLastCard = pile.getLast();
        System.out.println("There is nothing left in the deck");
        System.out.println("Shuffling the pile...");
        Collections.shuffle(pile);
        pile.remove(getLastCard);
        deck.addAll(pile);
        pile.clear();
        System.out.println("Pile shuffled and added to the deck");
        pile.add(getLastCard);
        printDeck("Current pile", pile, 1);
    }

    public static void restartGame(Scanner scanner) {
        System.out.println();
        System.out.println("Do you want to play again? (y/n)");
        var player = users.get(0);
        var ai = users.get(1);
        var input = scanner.nextLine();

        if (input.equalsIgnoreCase("y")) {
            System.out.println("Restarting game...");
            pile.clear();
            deck.clear();
            GAME_OVER = false;
            setGameMode();
            gameUsers(player, ai);
            player.setInitialHand(deck);
            ai.setInitialHand(deck);
            gameInSession(player, ai);
        } else if (input.equalsIgnoreCase("n")) {
            System.out.println("Thanks for playing!");
            System.exit(0);
        } else {
            System.out.println("Invalid input");
            restartGame(scanner);
        }
    }

    private static Scanner setGameMode() {
        System.out.println("Do you want to play in Joker mode? (y/n). Enter 'e' to exit the game");
        String answer = scanner.nextLine();
        System.out.println();

        switch (answer.toLowerCase()) {
            case "y", "yes" -> {
                JOKER_MODE = true;
                System.out.println(Intro.JOKER);
            }
            case "e", "exit" -> {
                System.out.println("Have a good day!");
                System.exit(0);
            }
            case "n", "no" -> {
                JOKER_MODE = false;
                System.out.println(Intro.CLASSIC);
            }
            default -> {
                System.out.println("Invalid input. Enter 'e' to exit or 'y' or 'n' to continue");
                return setGameMode();
            }
        }

        return scanner;
    }

    private static <T extends User> void gameUsers(T player, T ai) {
        deck = Deck.createDeck(Game.JOKER_MODE);
        printDeck("Deck of Cards", deck, 4);
        System.out.println("Size of deck: " + deck.size());

        System.out.println();
        System.out.println("---------- Game in session ----------");
        System.out.println("You are now playing with the A.I");

        System.out.println("Shuffling deck...");
        Collections.shuffle(deck);

        System.out.println("Dealing cards...");
        player.setInitialHand(deck);
        ai.setInitialHand(deck);
        System.out.println("Size of deck: " + deck.size());
    }

    private static <T extends  User> void gameInSession(T player, T ai) {

        do {
            getPile();
            if (PLAYER_TURN) {
                if (player instanceof Player turn) {
                    turn.playerTurn();
//                    PLAYER_TURN = false;
                }
            } else {
                if (ai instanceof AI turn) {
                    turn.aiTurn();
                    PLAYER_TURN = true;
                }
            }
        } while (!GAME_OVER);

        restartGame(scanner);
        scanner.close();
    }

    public static void getPile() {
        Deck.printDeck("Current pile", pile, 4);
        System.out.println("Pile size --> " + pile.size());
        System.out.println("Deck size --> " + deck.size());
        System.out.println("-".repeat(25));
        if (!pile.isEmpty()) {
            System.out.println("Top of the pile: " + pile.getLast());
        } else {
            System.out.println("Pile is empty");
        }
        System.out.println("-".repeat(25));
    }

    public static Card getTopCard() {
        if (pile.isEmpty()) {
            return null;
        }
        return pile.getLast();
    }

    public static Suit getAskedSuit() {
        return askedSuit;
    }

    public static void setAskedSuit(Suit askedSuit) {
        Game.askedSuit = askedSuit;
    }
}
