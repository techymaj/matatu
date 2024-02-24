package tech.majaliwa.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static tech.majaliwa.game.Deck.printDeck;
import static tech.majaliwa.game.User.checkWinner;

public class Game {

    public static ArrayList<Card> pile = new ArrayList<>();
    public static ArrayList<Card> deck = new ArrayList<>();
    public static Scanner scanner = new Scanner(System.in);
    public static boolean PLAYER_TURN = true;
    public static int playerPickCount;
    public static boolean JOKER_MODE;
    public static boolean GAME_OVER;
    public static Suit askedSuit;
    public static boolean DAMAGE_CARD_ON_PILE;
    public static boolean AI_CAN_PICK_CARD_FROM_DECK;
    public static List<User> users = new ArrayList<>();
    public static Suit cuttingSuit;
    public static boolean CUTTING_CARD_PLAYED;
    public static int round;
    public static String PLAYER_WHO_WON;
    public static ArrayList<ScoreHistory> scores = new ArrayList<>();

    static {
        playerPickCount = 0;
        DAMAGE_CARD_ON_PILE = false;
        AI_CAN_PICK_CARD_FROM_DECK = true;
        cuttingSuit = null;
        CUTTING_CARD_PLAYED = false;
        round = 1;
        PLAYER_WHO_WON = "";
    }

    public static void main(String[] args) {
        System.out.println(Intro.WELCOME_MESSAGE);
        System.out.println();

        var scanner = setGameMode();
        var playerName = getPlayerName(scanner);

        var player = new Player(playerName);
        var AI = new AI("AI");
        users.add(player);
        users.add(AI);

        gameUsers(player, AI);

        gameInSession(player, AI);
    }

    private static String getPlayerName(Scanner scanner) {
        System.out.println("Enter your player name. Enter 'e' to exit the game");

        String playerName = scanner.nextLine();
        switch (playerName.toLowerCase()) {
            case "e", "exit" -> {
                System.out.println("Have a good day!");
                System.exit(0);
            }
            case "" -> {
                System.out.println("Invalid input");
                return getPlayerName(scanner);
            }
        }
        return playerName;
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

    public static void restartGame() {
        System.out.println();
        System.out.println("Do you want to play again? (y/n)");
        var player = users.get(0);
        var ai = users.get(1);
        var input = scanner.nextLine();

        checkInputToRestartOrNot(input, player, ai);
    }

    private static void checkInputToRestartOrNot(String input, User player, User ai) {
        switch (input.toLowerCase()) {
            case "y", "yes" -> {
                System.out.println("Restarting game...");
                pile.clear();
                deck.clear();
                player.getHand().clear();
                ai.getHand().clear();
                GAME_OVER = false;
                round++;
                setGameMode();
                gameUsers(player, ai);
                gameInSession(player, ai);
            }
            case "n", "no" -> {
                System.out.println("Thanks for playing!");
                scanner.close();
                System.exit(0);
            }
            default -> {
                System.out.println("Invalid input");
                restartGame();
            }
        }
    }

    private static Scanner setGameMode() {
        System.out.println("Do you want to play in Joker mode? (y/n). Enter 'e' to exit the game");
        String answer = scanner.nextLine();

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
        setCuttingCard();
        System.out.println("Size of deck: " + deck.size());
    }

    private static Card setCuttingCard() {
        var cuttingCard = deck.getFirst();
        if (cuttingCard.face().equals(Face.SEVEN)) {
            Collections.shuffle(deck);
            return setCuttingCard();
        }
        setCuttingSuit(cuttingCard.suit());
        deck.remove(cuttingCard);
        return cuttingCard;
    }

    private static <T extends  User> void gameInSession(T player, T ai) {

        do {
            playTheGame(player, ai);
        } while (!GAME_OVER);
        if (CUTTING_CARD_PLAYED) checkWinner(player, (AI) ai);
        System.out.println("-".repeat(25));
        System.out.println("Your hand: " + player.getHand());
        var playerCardValue = player.getHand().stream().mapToInt(Card::cardValue).sum();
        System.out.println("Your total card value: " + playerCardValue);

        System.out.println("-".repeat(25));
        System.out.println("AI's hand: " + ai.getHand());
        var aiCardValue = ai.getHand().stream().mapToInt(Card::cardValue).sum();
        System.out.println("AI's total card value: " + aiCardValue);
        System.out.println("-".repeat(25));

        scores.add(new ScoreHistory(player, playerCardValue, round));
        scores.add(new ScoreHistory(ai, aiCardValue, round));
        System.out.println("Score History \nRound: #" + round + " goes to " + PLAYER_WHO_WON);
        System.out.println("-".repeat(25));
        scores.forEach(System.out::println);
        System.out.println("-".repeat(25));

        restartGame();
    }

    private static <T extends User> void playTheGame(T player, T ai) {
        getPile();
        if (PLAYER_TURN) {
            switchTo(player);
        } else {
            switchTo(ai);
        }
    }

    private static <T extends User> void switchTo(T user) {
        if (user instanceof AI turn) {
            turn.aiTurn();
            PLAYER_TURN = true;
        }
        if (user instanceof Player turn) {
            turn.playerTurn();
            PLAYER_TURN = false;
        }
    }

    public static void getPile() {
        Deck.printDeck("Current pile", pile, 4);
        System.out.println("Pile size --> " + pile.size());
        System.out.println("Deck size --> " + deck.size());
        System.out.println("-".repeat(25));
        System.out.println("Cutting suit: " + getCuttingSuit() + " - " + getCuttingSuit().getUnicode());
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

    public static boolean damageCardOnPile() {
        return DAMAGE_CARD_ON_PILE;
    }

    public static void setDamageCardOnPile(boolean damageCardOnPile) {
        DAMAGE_CARD_ON_PILE = damageCardOnPile;
    }

    public static Suit getCuttingSuit() {
        return cuttingSuit;
    }

    public static void setCuttingSuit(Suit cuttingSuit) {
        Game.cuttingSuit = cuttingSuit;
    }
}
