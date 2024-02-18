package tech.majaliwa.game.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.majaliwa.Face;
import tech.majaliwa.game.Card;
import tech.majaliwa.game.Game;
import tech.majaliwa.game.Suit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.majaliwa.game.Deck.createDeck;
import static tech.majaliwa.game.Rules.canFollowCard;
import static tech.majaliwa.game.Rules.canPlayerPlayCard;
import static tech.majaliwa.game.User.addCardToPile;

class GameTest {

    private ArrayList<Card> pile;
    private int cardPosition;
    private ArrayList<Card> hand;

    @BeforeEach
    public void setUp() {
        ArrayList<Card> deck = createDeck(true);
        pile = Game.pile;
        List<Card> serve = deck.subList(0, 7);
        hand = new ArrayList<>(serve);
        Random randomCard = new Random();
        cardPosition = randomCard.nextInt(0, hand.size());
        deck.subList(0, 7).clear();
    }

    @Test
    @DisplayName("If pile is empty, play any card")
    public void ifPileIsEmpty_PlayAnyCard() {
        var playRandomCard = hand.get(cardPosition);
        if (canPlayerPlayCard(playRandomCard)) addCardToPile(playRandomCard);
        assertTrue(pile.contains(playRandomCard));
    }

    @Test
    @DisplayName("If pile is not empty, play card of same suit")
    public void ifPileIsNotEmpty_PlayCardOfSameSuit() {
        var firstCard = new Card(Face.TWO, Suit.HEARTS, 20);
        addCardToPile(firstCard);
        var playSameSuitCard = new Card(Face.THREE, Suit.HEARTS, 3);
        if (canPlayerPlayCard(playSameSuitCard)) addCardToPile(playSameSuitCard);
        assertTrue(pile.contains(playSameSuitCard));
    }

    @Test
    @DisplayName("If pile is not empty, don't play card of different suit")
    public void ifPileIsNotEmpty_DontPlayCardOfDifferentSuit() {
        var firstCard = new Card(Face.TWO, Suit.HEARTS, 20);
        addCardToPile(firstCard);
        var dontPlayDifferentSuit = new Card(Face.THREE, Suit.CLUBS, 3);
        if (canPlayerPlayCard(dontPlayDifferentSuit)) addCardToPile(dontPlayDifferentSuit);
        assertFalse(pile.contains(dontPlayDifferentSuit));
    }

    @Test
    @DisplayName("If card on top of pile is Ace of spades, play any card")
    public void ifCardOnTopOfPileIsAceOfSpades_PlayAnyCard() {
        var aceOfSpades = new Card(Face.ACE, Suit.SPADES, 60);
        addCardToPile(aceOfSpades);
        var playAnyCard = hand.get(cardPosition);
        if (canPlayerPlayCard(playAnyCard)) addCardToPile(playAnyCard);
        assertTrue(pile.contains(playAnyCard));
    }

    @Test
    @DisplayName("If card on top of pile is Jack, follow it with a valid card")
    public void ifCardOnTopOfPileIsJackOrEight_FollowItWithACard() {
        var jackOfHearts = new Card(Face.JACK, Suit.HEARTS, 20);
        addCardToPile(jackOfHearts);
        var followWithCard = new Card(Face.THREE, Suit.HEARTS, 3);
        if (canPlayerPlayCard(followWithCard)) {
            if (canFollowCard()) addCardToPile(followWithCard);
        }
        assertTrue(pile.contains(followWithCard));
    }

    @Test
    @DisplayName("If card on top of pile is Eight, follow it with a valid card")
    public void ifCardOnTopOfPileIsEight_FollowItWithACard() {
        var eightOfHearts = new Card(Face.EIGHT, Suit.HEARTS, 20);
        addCardToPile(eightOfHearts);
        var followWithCard = new Card(Face.THREE, Suit.HEARTS, 3);
        if (canPlayerPlayCard(followWithCard)) {
            if (canFollowCard()) addCardToPile(followWithCard);
        }
        assertTrue(pile.contains(followWithCard));
    }
}