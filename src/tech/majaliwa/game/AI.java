package tech.majaliwa.game;

import static tech.majaliwa.game.Game.*;
import static tech.majaliwa.game.Rules.*;

public class AI extends User {

    public AI(String name) {
        super(name);
    }

    void aiTurn() {
        System.out.println("AI's turn");
        System.out.println(getHand());
        playerPickCount = 0;
        setDamageCardOnPile(false);

        var cardPlayed = aiPlaysCard();

        if (cardPlayed == null) {
           return;
        }

        if (canFollowCard()) {
            aiTurn();
        }
    }

    public Card aiPlaysCard() {
        var iterator = getHand().listIterator();
        while (iterator.hasNext()) {
            var card = iterator.next();
            if (canPlayerPlayCard(card)) {
                iterator.remove();
                addCardToPile(card);
                System.out.println("AI played: " + card);
                AI_CAN_PICK_CARD_FROM_DECK = true;
                setAskedSuit(null);  // restrict follow with wrong card e.g. (8♦, 8♠, 9♦) with askedSuit ♦ is wrong
                return card;
            }
        }

        // No card was found in hand
        if (AI_CAN_PICK_CARD_FROM_DECK) {
            aiPicksFromDeck();
            return aiPlaysCard();
        } else {
            System.out.println("AI passed turn");
        }

        return null; // pass turn
    }

    private void aiPicksFromDeck() {
        AI_CAN_PICK_CARD_FROM_DECK = false;
        var cardPicked = deck.getFirst();
        System.out.println("AI picked a card from the deck: " + cardPicked);
        getHand().add(cardPicked);
        deck.removeFirst();
    }
}