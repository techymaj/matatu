package tech.majaliwa.game;

import java.util.Objects;

import static tech.majaliwa.game.Game.*;
import static tech.majaliwa.game.Rules.*;

public class Player extends User {
    public Player(String name) {
        super(name);
    }

    public void playerTurn() {
        playerActions();

        // Incoming so react

//        if (isDamageCardOnPile()) {
//            String choice = scanner.nextLine();
//            System.out.println("You have been hit with a damage card");
//            var cardPlayed = playCard(Integer.parseInt(choice));
//            var countered = damageCountered(this, cardPlayed);
//            if (countered) {
//                PLAYER_TURN = false;
//                return;
//            }
//        }
//        if (isAskingCardOnPile()) {
//            String choice = scanner.nextLine();
//            var askedSuit = getAskedSuit();
//            System.out.println("Asked suit is: " + askedSuit);
//            var cardPlayed = playCard(Integer.parseInt(choice));
//            var countered = askingCountered(this, cardPlayed);
//            if (countered != null) {
//                PLAYER_TURN = false;
//                return;
//            }
//        }

        checkInput();
    }

    public void checkInput() {
        try {
            tryThisUserInput();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid input");
            playerActions();
            checkInput();
        }
    }

    private void tryThisUserInput() {
        var input = scanner.nextLine();
        switch (input.toLowerCase()) {
            case "p" -> userInputIsP();
            case "pass" -> userInputIsPass();
            default -> playACard(input);
        }
    }

    private void playACard(String input) {
        try {
            tryPlayingACardPosition(input);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Wrong card position.");
            playerActions();
            checkInput();
        }
    }

    private void userInputIsP() {
        var playerCanPickACard = canPlayerPickACard();
        if (playerCanPickACard) {
            pickCard(this);
            playerPickCount++;
        } else {
            playerCantPickACard();
        }
        playerActions();
        checkInput();
    }

    private static void playerCantPickACard() {
        if (Rules.isDamageCardOnPile())
            System.out.println("Play a card to counter or type 'accept' to accept damage.");
        else {
            System.out.println("You can't pick a card yet. Play a card or pass your turn.");
        }
    }

    private void userInputIsPass() {
        if (canPlayerPassTurn()) {
            System.out.println("You have passed your turn");
            playerPickCount = 0;
            PLAYER_TURN = false;
            AI_CAN_PICK_CARD_FROM_DECK = true;
        } else {
            System.out.println("You can't pass your turn yet");
            playerActions();
            checkInput();
        }
    }

    private void tryPlayingACardPosition(String input) {
        var cardPlayed = playCard(Integer.parseInt(input));
        if (cardPlayed != null) {
            playThisCardIfCardPlayedIsNotNull(cardPlayed);
        }
    }

    private void playThisCardIfCardPlayedIsNotNull(Card cardPlayed) {
        var theCardPlayedCanBeFollowed = canFollowCard();
        if (theCardPlayedCanBeFollowed) {
            System.out.println("You can follow this card: " + cardPlayed);
            playerPickCount = 0;
            playerActions();
            checkInput();
        }
        var theCardPlayedIsAnAskingCard = isAskingCardOnPile();
        if (theCardPlayedIsAnAskingCard) {
            checkIfPlayerWon(this);
            askForSuit(scanner);
        }
        checkIfPlayerWon(this);
        playerPickCount = 0;
        PLAYER_TURN = false;
    }

    private Suit askingCountered(Card cardPlayed) {
        if (cardPlayed.face() == Face.ACE) {
            addCardToPile(cardPlayed);
            askForSuit(scanner);
            return askedSuit;
        }
        return null;
    }

    public void playerActions() {
        System.out.println("It's your turn " + this.getName());
        if (!pile.isEmpty()) {
            printActionsIfPileIsNotEmpty();
        }
        System.out.println("Your hand: ");
        this.getHand().forEach(
                card -> System.out.print(card + "(" + (this.getHand().indexOf(card) + 1) + ")" + " ")
        );
        System.out.println();
    }

    private static void printActionsIfPileIsNotEmpty() {
        var topCard = Objects.requireNonNull(getTopCard());
        System.out.println("Top card: " + topCard);
        System.out.println("Damage card? " + (damageCardOnPile() ? "Yes" : "No"));

        if (getAskedSuit() != null) {
            System.out.println("Asked suit: " + getAskedSuit() + " - " + getAskedSuit().getUnicode());
        }
        System.out.println("Pick count: " + playerPickCount);
    }
}
