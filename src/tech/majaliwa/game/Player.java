package tech.majaliwa.game;

import tech.majaliwa.Face;

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

    private void checkInput() {
        try {
            var input = scanner.nextLine();
            if (input.equalsIgnoreCase("p")) {
                if (canPlayerPickACard()) {
                    pickCard(this);
                    playerPickCount++;
                } else {
                    if (Rules.isDamageCardOnPile())
                        System.out.println("Play a card to counter or type 'accept' to accept damage.");
                    else {
                        System.out.println("You can't pick a card yet. Play a card or pass your turn.");
                    }
                }
                playerActions();
                checkInput();
            } else if (input.equalsIgnoreCase("pass")) {
                if (canPlayerPassTurn()) {
                    System.out.println("You have passed your turn");
                    playerPickCount = 0;
                    PLAYER_TURN = false;
                } else {
                    System.out.println("You can't pass your turn yet");
                    playerActions();
                    checkInput();
                }
            } else {
                try {
                    var cardPlayed = playCard(Integer.parseInt(input));
                    if (cardPlayed != null) {
                        if (canFollowCard()) {
                            System.out.println("You can follow this card: " + cardPlayed);
                            playerPickCount = 0;
                            playerActions();
                            checkInput();
                        }
                        if (isAskingCardOnPile()) {
                            checkIfPlayerWon(this);
                            askForSuit(scanner);
                        }
                        checkIfPlayerWon(this);
                        playerPickCount = 0;
                        PLAYER_TURN = false;
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Wrong card position.");
                    playerActions();
                    checkInput();
                }
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid input");
            playerActions();
            checkInput();
        }
    }

    private Suit askingCountered(Card cardPlayed) {
        if (cardPlayed.face() == Face.ACE) {
            addToPile(cardPlayed);
            askForSuit(scanner);
            return askedSuit;
        }
        return null;
    }

    private void playerActions() {
        System.out.println("It's your turn " + this.getName());
        if (!pile.isEmpty()) {
            var topCard = Objects.requireNonNull(getTopCard());
            System.out.println("Top card: " + topCard);
            System.out.println("Damage card? " + (damageCardOnPile() ? "Yes" : "No"));

            System.out.println("Asked suit: " + getAskedSuit());
            System.out.println("Pick count: " + playerPickCount);
        }
        System.out.println("Your hand: ");
        this.getHand().forEach(
                card -> System.out.print(card + "(" + (this.getHand().indexOf(card) + 1) + ")" + " ")
        );
        System.out.println();
    }
}
