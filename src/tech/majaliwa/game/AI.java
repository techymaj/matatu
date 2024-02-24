package tech.majaliwa.game;

import java.util.Comparator;
import java.util.NoSuchElementException;

import static tech.majaliwa.game.Game.*;
import static tech.majaliwa.game.Rules.*;

public class AI extends User {

    public AI(String name) {
        super(name);
    }

    void aiTurn() {
        System.out.println("AI's turn");
        System.out.println(getHand()); // TODO: remove in production
        playerPickCount = 0;

        if (damageCardOnPile()) {
            var cardPlayed = aiPlaysCard();
            if (cardPlayed == null) {
                takeDamage(this, pile.getLast().face());
                setDamageCardOnPile(false); // allow play to continue normally
            }
            return;
        }

        ifAIDoesnTakesDamage();
    }

    private void ifAIDoesnTakesDamage() {
        var cardPlayed = aiPlaysCard();
        if (cardPlayed == null) {
            return;
        }

        var aiCanFollowThisCard = canFollowCard();
        if (aiCanFollowThisCard) {
            aiTurn();
        }

        var aiCanAskSuit = isAskingCardOnPile();
        if (aiCanAskSuit) {
            var hand = getHand();
            hand.sort(Comparator.comparing(Card::suit));
            if (!hand.isEmpty()) {
                var card = hand.getFirst();
                var suit = card.suit();
                setAskedSuit(suit);
                System.out.println("AI asked for: " + suit);
            }
        }
    }

    public Card aiPlaysCard() {
        var iterator = getHand().listIterator();
        while (iterator.hasNext()) {
            var card = iterator.next();
            var aiCanPlayThisCard = canPlayerPlayCard(card);
            if (aiCanPlayThisCard) {
                addCardToPile(card);
                setDamageCardOnPile(false); // allow play to continue normally
                dealDamageIfDamageCard();
                iterator.remove();
                System.out.println("AI played: " + card);
                AI_CAN_PICK_CARD_FROM_DECK = true;
                setAskedSuit(null);  // restrict follow with wrong card e.g. (8♦, 8♠, 9♦) with askedSuit ♦ is wrong
                checkIfPlayerWon(this);
                return card;
            }
            if (damageCardOnPile()) return null; // AI can't play any card
        }

        // No valid card to play was found in hand
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
        try {
            var cardPicked = deck.getFirst();
            System.out.println("AI picked a card from the deck: " + cardPicked); // TODO: remove in production
            getHand().add(cardPicked);
            deck.removeFirst();
        } catch (NoSuchElementException nse) {
            reshuffleDeckAndContinuePlaying();
            aiPicksFromDeck();
        }
    }
}