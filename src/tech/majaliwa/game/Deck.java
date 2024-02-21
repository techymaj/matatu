package tech.majaliwa.game;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private final List<Card> cards;

    private Deck(boolean isCLASSIC) {
        if (isCLASSIC) {
            this.cards = new ArrayList<>(54);
        } else {
            this.cards = new ArrayList<>(52);
        }
    }

    public List<Card> getCards() {
        return this.cards;
    }

    public static ArrayList<Card> createDeck(boolean isJokerMode) {
        var deck = new Deck(isJokerMode);
        ArrayList<Card> deckOfCards = new ArrayList<>(deck.getCards());
        int[] cardValues = {20, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15};

        for (int i = 0; i <= 12; i++) {
            if (face3AndIsJokerMode(isJokerMode, i, deckOfCards)) continue;
            if (specialCaseForAceOfSpades(i, deckOfCards, cardValues)) break;
            createClassicDeck(deckOfCards, i, cardValues);
        }

        createJokerDeckIf(isJokerMode, deckOfCards);

        return deckOfCards;
    }

    private static void createJokerDeckIf(boolean isJokerMode, ArrayList<Card> deckOfCards) {
        if (isJokerMode) {
            deckOfCards.add(new Card(Face.getFace(13),Suit.JOKER_F, 50));
            deckOfCards.add(new Card(Face.getFace(13),Suit.JOKER_M, 50));
        }
    }

    private static void createClassicDeck(ArrayList<Card> deckOfCards, int i, int[] cardValues) {
        deckOfCards.add(new Card(Face.getFace(i), Suit.SPADES, cardValues[i]));
        deckOfCards.add(new Card(Face.getFace(i), Suit.HEARTS, cardValues[i]));
        deckOfCards.add(new Card(Face.getFace(i), Suit.CLUBS, cardValues[i]));
        deckOfCards.add(new Card(Face.getFace(i), Suit.DIAMONDS, cardValues[i]));
    }

    private static boolean specialCaseForAceOfSpades(int i, ArrayList<Card> deckOfCards, int[] cardValues) {
        if (i == 12) {
            deckOfCards.add(new Card(Face.getFace(i), Suit.SPADES, 60));
            deckOfCards.add(new Card(Face.getFace(i), Suit.HEARTS, cardValues[i]));
            deckOfCards.add(new Card(Face.getFace(i), Suit.CLUBS, cardValues[i]));
            deckOfCards.add(new Card(Face.getFace(i), Suit.DIAMONDS, cardValues[i]));
            return true;
        }
        return false;
    }

    private static boolean face3AndIsJokerMode(boolean isJokerMode, int i, ArrayList<Card> deckOfCards) {
        if (isJokerMode && i == 1) {
            deckOfCards.add(new Card(Face.getFace(i), Suit.HEARTS, 30));
            deckOfCards.add(new Card(Face.getFace(i), Suit.SPADES, 30));
            deckOfCards.add(new Card(Face.getFace(i), Suit.CLUBS, 30));
            deckOfCards.add(new Card(Face.getFace(i), Suit.DIAMONDS, 30));
            return true;
        }
        return false;
    }

    public static void printDeck(String description, List<Card> deckOfCards, int rowCount) {
        System.out.println();
        System.out.println(description);

        if (rowCount < 1) {
            System.out.println("Enter a valid number of rows (number of rows > 0)");
            return;
        }

        // rows needed
        var rowsToPrint = deckOfCards.size() / rowCount;
        var set = 0;

        for (var card : deckOfCards) {
            System.out.printf("%-8s", card);
            set++;
            if (set == rowsToPrint) {
                System.out.println();
                set = 0;
            }
        }
        System.out.println();
    }
}
