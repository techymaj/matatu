package tech.majaliwa.game;

import tech.majaliwa.Face;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import static tech.majaliwa.game.Game.*;
import static tech.majaliwa.game.Rules.canPlayerPlayCard;

public class User {
    private static boolean AI_TAKES_DAMAGE;
    private final String name;
    private ArrayList<Card> hand;

    public User(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public void setInitialHand(ArrayList<Card> deck) {
        var initialHand = deck.subList(0, 7);
        this.hand.addAll(initialHand);
        deck.subList(0, 7).clear();
    }

    public static boolean isAiTakesDamage() {
        return AI_TAKES_DAMAGE;
    }

    public static void setAiTakesDamage(boolean aiTakesDamage) {
        AI_TAKES_DAMAGE = aiTakesDamage;
    }

    public Card playCard(int position) {
        var iterator = this.hand.listIterator();

        var cardToPlay = this.hand.get(position - 1);
        while (iterator.hasNext()) {
            var card = iterator.next();
            if (card.equals(cardToPlay)) {
                if (canPlayerPlayCard(card)) {
                    iterator.remove();
                    addToPile(card);
                    return card;
                }
                System.out.println("Invalid card. Try again");
                var topCard = Objects.requireNonNull(getTopCard());
                System.out.println("Top card: " + topCard);
                System.out.println("Your hand: ");
                this.getHand().forEach(
                        c -> System.out.print(c + "(" + (this.getHand().indexOf(c) + 1) + ")" + " ")
                );
                System.out.println();
                return playCard(scanner.nextInt());
            }
        }

        return null;
    }

    public static void pickCard(User user) {
        if (deck.isEmpty()) {
            reshuffleDeckAndContinuePlaying();
        }
        var cardToPick = deck.getFirst();
        deck.removeFirst();

        user.getHand().add(cardToPick);

        if (user instanceof Player) {
            System.out.println("You picked: " + cardToPick);
        } else {
            System.out.println(user.name + " picked: " + cardToPick);
        }
    }

    static void takeDamage(User user, Face currentFace) {
        switch (currentFace) {
            case TWO -> {
                var deckIsEmpty = user.pickTwoCards(deck);
                if (deckIsEmpty == -1) {
                    reshuffleDeckAndContinuePlaying();
                    user.pickTwoCards(deck);
                }
            }
            case THREE -> {
                var deckIsEmpty = user.pickThreeCards(deck);
                if (deckIsEmpty == -1) {
                    reshuffleDeckAndContinuePlaying();
                    user.pickThreeCards(deck);
                }
            }
            case JOKER -> {
                var deckIsEmpty = user.pickFiveCards(deck);
                if (deckIsEmpty == -1) {
                    reshuffleDeckAndContinuePlaying();
                    user.pickFiveCards(deck);
                }
            }
        }
    }

    public int pickTwoCards(ArrayList<Card> deck) {
        if (deck.isEmpty()) {
            System.out.println("There is nothing left in the deck");
            return -1;
        }

        try {
            var picked = deck.subList(0, 2);
            ArrayList<Card> arrayList = new ArrayList<>(picked);

            this.hand.addAll(arrayList);

            if (this instanceof Player) {
                System.out.println("Picked 2");
            } else {
                System.out.println(this.name + " picked 2");
            }
            System.out.println();
            picked.clear();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Not enough cards in the deck");
            return -1;
        }

        return 1;
    }

    public int pickThreeCards(ArrayList<Card> deck) {
        if (deck.isEmpty()) {
            System.out.println("There is nothing left in the deck");
            return -1;
        }
        try {
            var picked = deck.subList(0, 3);
            ArrayList<Card> arrayList = new ArrayList<>(picked);

            this.hand.addAll(arrayList);

            if (this instanceof Player) {
                System.out.println("Picked 3");
            } else {
                System.out.println(this.name + " picked 3");
            }
            System.out.println();
            picked.clear();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Not enough cards in the deck");
            return -1;
        }

        return 1;
    }

    public int pickFiveCards(ArrayList<Card> deck) {
        if (deck.isEmpty()) {
            System.out.println("There is nothing left in the deck");
            return -1;
        }

        try {
            var picked = deck.subList(0, 5);
            ArrayList<Card> arrayList = new ArrayList<>(picked);

            this.hand.addAll(arrayList);

            if (this instanceof Player) {
                System.out.println("Picked 5");
            } else {
                System.out.println(this.name + " picked 5");
            }
            System.out.println();
            picked.clear();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Not enough cards in the deck");
            return -1;
        }

        return 1;
    }

    public boolean damageCountered(User user, Card cardPlayed) {
        if (cardPlayed.face().equals(Face.TWO) ||
                cardPlayed.face().equals(Face.THREE) ||
                cardPlayed.face().equals(Face.JOKER) ||
                (cardPlayed.face().equals(Face.ACE) && cardPlayed.suit().equals(Suit.SPADES))
        ) {
            setAiTakesDamage(true);
            addToPile(cardPlayed);
            checkIfPlayerWon(user);
            return true;
        }

        return false;
    }

    public static <T extends User> void checkIfPlayerWon(T user) {

        if (user.getHand().isEmpty()) {
            if (user instanceof Player) {
                System.out.println("Congratulations " + user.getName() + " you won!");
            } else {
                System.out.println(user.getName() + " won!");
            }
            GAME_OVER = true;
            restartGame(new Scanner(System.in));
        }
    }

    public static void addToPile(Card card) {
        pile.add(card);
    }

    public void askForSuit(Scanner scanner) {
        System.out.println("Choose a suit: (H)earts, (S)pades, (C)lubs, (D)iamonds");
        var suit = scanner.nextLine().toUpperCase();

        switch (suit) {
            case "H" -> setAskedSuit(Suit.HEARTS);
            case "S" -> setAskedSuit(Suit.SPADES);
            case "C" -> setAskedSuit(Suit.CLUBS);
            case "D" -> setAskedSuit(Suit.DIAMONDS);
            default -> {
                System.out.println("Invalid input");
                askForSuit(scanner);
            }
        }
    }
}
