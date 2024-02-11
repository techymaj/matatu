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
                    System.out.println("You can't pick a card yet");
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
                    PLAYER_TURN = true;
                }
            } else {
                var cardToPlay = playCard(Integer.parseInt(input));
                addToPile(cardToPlay);
                if (canFollowCard()) {
                    System.out.println("You can follow this card");
                    playerActions();
                    checkInput();
                }
                playerPickCount = 0;
                PLAYER_TURN = false;
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid input");
        }
    }

    private Suit askingCountered(Player player, Card cardPlayed) {
        if (cardPlayed.face() == Face.ACE) {
            addToPile(cardPlayed);
            askForSuit(scanner);
            return askedSuit;
        }
        return null;
    }

    private void playerActions() {
        System.out.println("It's your turn " + this.getName());
        System.out.println("Your hand: ");
        this.getHand().forEach(
                card -> System.out.print(card + "(" + (this.getHand().indexOf(card) + 1) + ")" + " ")
        );
        System.out.println();
        if (!pile.isEmpty()) {
            var topCard = Objects.requireNonNull(getTopCard());
            System.out.println("Top card: " + topCard);
        }
    }
}
