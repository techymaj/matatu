package tech.majaliwa.game.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.majaliwa.game.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumingThat;
import static tech.majaliwa.game.Deck.createDeck;
import static tech.majaliwa.game.Game.*;
import static tech.majaliwa.game.Rules.*;
import static tech.majaliwa.game.User.addCardToPile;

class GameTest {
    private int cardPosition;
    private ArrayList<Card> hand;
    private ArrayList<Card> deck;

    @BeforeEach
    public void setUp() {
        pile = new ArrayList<>();
        deck = createDeck(true);
        List<Card> serve = deck.subList(0, 7);
        hand = new ArrayList<>(serve);
        Random randomCard = new Random();
        cardPosition = randomCard.nextInt(0, hand.size());
        deck.subList(0, 7).clear();
    }

    @AfterEach
    public void tearDown() {
        hand.clear();
        deck.clear();
    }

    @Test
    @DisplayName("If pile is empty, play any card")
    public void ifPileIsEmpty_PlayAnyCard() {
        var playCard = new Card(Face.THREE, Suit.HEARTS, 30);
        if (canPlayerPlayCard(playCard)) addCardToPile(playCard);
        assertTrue(pile.contains(playCard));
    }

    @Test
    @DisplayName("If pile is not empty, play card of same suit")
    public void ifPileIsNotEmpty_PlayCardOfSameSuit() {
        var firstCard = new Card(Face.FIVE, Suit.HEARTS, 15);
        addCardToPile(firstCard);
        var playSameSuitCard = new Card(Face.THREE, Suit.HEARTS, 3);
        if (canPlayerPlayCard(playSameSuitCard)) addCardToPile(playSameSuitCard);
        assertTrue(pile.contains(playSameSuitCard));
    }

    @Test
    @DisplayName("If pile is not empty, don't play card of different suit")
    public void ifPileIsNotEmpty_DontPlayCardOfDifferentSuit() {
        var firstCard = new Card(Face.TWO, Suit.HEARTS, 15);
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
        var jackOfHearts = new Card(Face.JACK, Suit.HEARTS, 15);
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
        var eightOfHearts = new Card(Face.EIGHT, Suit.HEARTS, 15);
        addCardToPile(eightOfHearts);
        var followWithCard = new Card(Face.THREE, Suit.HEARTS, 3);
        if (canPlayerPlayCard(followWithCard)) {
            if (canFollowCard()) addCardToPile(followWithCard);
        }
        assertTrue(pile.contains(followWithCard));
    }

    @Test
    @DisplayName("If card on top of pile is Two, damage card on pile")
    public void ifCardOnTopOfPileIsTwo_DamageCardOnPile() {
        var twoOfHearts = new Card(Face.TWO, Suit.HEARTS, 15);
        addCardToPile(twoOfHearts);
        assertTrue(dealDamageIfDamageCard());
    }

    @Test
    @DisplayName("If card on top of pile is Three and is Game.JOKER_MODE, damage card on pile")
    public void ifCardOnTopOfPileIsThree_DamageCardOnPile() {
        JOKER_MODE = true;
        var threeOfHearts = new Card(Face.THREE, Suit.HEARTS, 30);
        addCardToPile(threeOfHearts);
        assertTrue(dealDamageIfDamageCard());
    }

    @Test
    @DisplayName("If card on top of pile is Joker, damage card on pile")
    public void ifCardOnTopOfPileIsJoker_DamageCardOnPile() {
        JOKER_MODE = true;
        var jokerOfHearts = new Card(Face.JOKER, Suit.JOKER_F, 60);
        addCardToPile(jokerOfHearts);
        assertTrue(dealDamageIfDamageCard());
    }

    @Test
    @DisplayName("If card on top of pile is Three and is not JOKER_MODE, not a damage card")
    public void ifCardOnTopOfPileIsThreeAndNotJokerMode_DamageCardOnPile() {
        JOKER_MODE = false;
        var threeOfHearts = new Card(Face.THREE, Suit.HEARTS, 30);
        addCardToPile(threeOfHearts);
        assertFalse(dealDamageIfDamageCard());
    }

    @Test
    @DisplayName("If card on top of pile is Joker and is not Joker Mode, not a damage card")
    public void ifCardOnTopOfPileIsJokerAndIsNotJokerMode_DamageCardOnPile() {
        JOKER_MODE = false;
        var jokerOfHearts = new Card(Face.JOKER, Suit.JOKER_F, 60);
        addCardToPile(jokerOfHearts);
        assertFalse(dealDamageIfDamageCard());
    }

    @Test
    @DisplayName("If card on top is Two, player takes damage")
    public void ifCardOnTopIsTwo_PlayerTakesDamage() {
        Player player = new Player("Player");
        var twoOfHearts = new Card(Face.TWO, Suit.HEARTS, 15);
        addCardToPile(twoOfHearts);
        player.pickTwoCards(deck);
        var cardsPicked = player.getHand().size();
        assertEquals(2, cardsPicked);
    }

    @Test
    @DisplayName("If card on top is Two, and is not Joker Mode player takes damage")
    public void ifCardOnTopIsTwoAndIsJokerMode_PlayerTakesDamage() {
        JOKER_MODE = false;
        Player player = new Player("Player");
        var twoOfHearts = new Card(Face.TWO, Suit.HEARTS, 15);
        addCardToPile(twoOfHearts);
        player.pickTwoCards(deck);
        var cardsPicked = player.getHand().size();
        assertEquals(2, cardsPicked);
    }

    @Test
    @DisplayName("If card on top is Two, player can counter damage")
    public void ifCardOnTopIsTwo_PlayerCanCounterDamage() {
        Player player = new Player("Player");
        addCardToPile(new Card(Face.TWO, Suit.HEARTS, 15));
        var cardPlayedToCounter = new Card(Face.TWO, Suit.SPADES, 20);
        var damageCountered = player.damageCountered(player, cardPlayedToCounter);
        assertTrue(damageCountered);
    }

    @Test
    @DisplayName("If card on top is Two, Ace Of Spades can counter damage")
    public void ifCardOnTopIsTwo_AceOfSpadesCanCounterDamage() {
        Player player = new Player("Player");
        addCardToPile(new Card(Face.TWO, Suit.HEARTS, 15));
        var cardPlayedToCounter = new Card(Face.ACE, Suit.SPADES, 60);
        var damageCountered = player.damageCountered(player, cardPlayedToCounter);
        assertTrue(damageCountered);
    }

    @Test
    @DisplayName("If card on top is Two, only Ace Of Spades and Two can counter damage")
    public void ifCardOnTopIsTwo_OnlyAceOfSpadesCanCounterDamage() {
        Player player = new Player("Player");
        addCardToPile(new Card(Face.TWO, Suit.HEARTS, 20));
        assertAll("Only Ace Of Spades and Two can counter damage",
                () -> assertFalse(player.damageCountered(player,
                        new Card(Face.ACE, Suit.CLUBS, 15))),
                () -> assertFalse(player.damageCountered(player,
                        new Card(Face.ACE, Suit.DIAMONDS, 15))),
                () -> assertFalse(player.damageCountered(player,
                        new Card(Face.ACE, Suit.HEARTS, 15))),
                () -> assertTrue(player.damageCountered(player,
                        new Card(Face.TWO, Suit.SPADES, 20)))
        );
    }

    @Test
    @DisplayName("If card on top is Two and is Joker Mode, only Ace Of Spades, Two, Three and Joker can counter damage")
    public void ifCardOnTopIsTwoAndIJokerMode_OnlyAceOfSpadesCanCounterDamage() {
        JOKER_MODE = true;
        Player player = new Player("Player");
        addCardToPile(new Card(Face.TWO, Suit.HEARTS, 20));
        assertAll("Ace, Two, Three and Joker can counter damage",
                () -> assertFalse(player.damageCountered(player,
                        new Card(Face.ACE, Suit.CLUBS, 15))),
                () -> assertFalse(player.damageCountered(player,
                        new Card(Face.ACE, Suit.DIAMONDS, 15))),
                () -> assertFalse(player.damageCountered(player,
                        new Card(Face.ACE, Suit.HEARTS, 15))),
                () -> assertTrue(player.damageCountered(player,
                        new Card(Face.TWO, Suit.SPADES, 20))),
                () -> assertTrue(player.damageCountered(player,
                        new Card(Face.THREE, Suit.SPADES, 30))),
                () -> assertTrue(player.damageCountered(player,
                        new Card(Face.JOKER, Suit.JOKER_F, 50)))
        );
    }

    @Test
    @DisplayName("If card on top is Three and is Joker Mode, player takes damage")
    public void ifCardOnTopIsThree_PlayerTakesDamage() {
        JOKER_MODE = true;
        Player player = new Player("Player");
        var threeOfHearts = new Card(Face.THREE, Suit.HEARTS, 30);
        addCardToPile(threeOfHearts);
        if (JOKER_MODE) player.pickThreeCards(deck);
        var cardsPicked = player.getHand().size();
        assertEquals(3, cardsPicked);
    }

    @Test
    @DisplayName("If card on top is Three and is not Joker Mode, player doesn't take damage")
    public void ifCardOnTopIsThreeAndIsNotJokerMode_PlayerNeverTakesDamage() {
        JOKER_MODE = false;
        Player player = new Player("Player");
        var threeOfHearts = new Card(Face.THREE, Suit.HEARTS, 3);
        addCardToPile(threeOfHearts);
        if (JOKER_MODE) player.pickThreeCards(deck);
        var cardsPicked = player.getHand().size();
        assertEquals(0, cardsPicked);
    }

    @Test
    @DisplayName("If card on top is Three and is Joker Mode, player can counter damage")
    public void ifCardOnTopIsThree_PlayerCanCounterDamage() {
        JOKER_MODE = true;
        Player player = new Player("Player");
        addCardToPile(new Card(Face.THREE, Suit.HEARTS, 15));
        var cardPlayedToCounter = new Card(Face.THREE, Suit.SPADES, 15);
        var damageCountered = JOKER_MODE && player.damageCountered(player, cardPlayedToCounter);
        assertTrue(damageCountered);
    }

    @Test
    @DisplayName("If card on top is Three and is Joker Mode, Ace Of Spades can counter damage")
    public void ifCardOnTopIsThree_AceOfSpadesCanCounterDamage() {
        JOKER_MODE = true;
        Player player = new Player("Player");
        addCardToPile(new Card(Face.THREE, Suit.HEARTS, 15));
        var cardPlayedToCounter = new Card(Face.ACE, Suit.SPADES, 60);
        var damageCountered = JOKER_MODE && player.damageCountered(player, cardPlayedToCounter);
        assertTrue(damageCountered);
    }

    @Test
    @DisplayName("If card on top is Three and is Joker Mode, only Ace Of Spades, Two, Three and Joker can counter damage")
    public void ifCardOnTopIsThree_OnlyAceOfSpadesCanCounterDamage() {
        JOKER_MODE = true;
        Player player = new Player("Player");
        assertAll("Ace, Two, Three and Joker can counter damage",
                () -> assertFalse(player.damageCountered(player,
                        new Card(Face.ACE, Suit.CLUBS, 15))),
                () -> assertFalse(player.damageCountered(player,
                        new Card(Face.ACE, Suit.DIAMONDS, 15))),
                () -> assertFalse(player.damageCountered(player,
                        new Card(Face.ACE, Suit.HEARTS, 15))),
                () -> assertTrue(player.damageCountered(player,
                        new Card(Face.TWO, Suit.SPADES, 20))),
                () -> assertTrue(player.damageCountered(player,
                        new Card(Face.THREE, Suit.SPADES, 30))),
                () -> assertTrue(player.damageCountered(player,
                        new Card(Face.JOKER, Suit.JOKER_F, 50)))
        );
    }

    @Test
    @DisplayName("If card on top is Joker and is Joker Mode, player takes damage")
    public void ifCardOnTopIsJoker_PlayerTakesDamage() {
        JOKER_MODE = true;
        Player player = new Player("Player");
        var joker = new Card(Face.JOKER, Suit.JOKER_M, 50);
        addCardToPile(joker);
        if (JOKER_MODE) player.pickFiveCards(deck);
        var cardsPicked = player.getHand().size();
        assertEquals(5, cardsPicked);
    }

    @Test
    @DisplayName("If card on top is Joker and is not Joker Mode, player doesn't take damage")
    public void ifCardOnTopIsJokerAndIsNotJokerMode_PlayerNeverTakesDamage() {
        JOKER_MODE = false;
        Player player = new Player("Player");
        var joker = new Card(Face.THREE, Suit.HEARTS, 3);
        addCardToPile(joker);
        if (JOKER_MODE) player.pickFiveCards(deck);
        var cardsPicked = player.getHand().size();
        assertEquals(0, cardsPicked);
    }

    @Test
    @DisplayName("If card on top is Joker and is Joker Mode, player can counter damage")
    public void ifCardOnTopIsJoker_PlayerCanCounterDamage() {
        JOKER_MODE = true;
        Player player = new Player("Player");
        addCardToPile(new Card(Face.JOKER, Suit.JOKER_M, 50));
        var cardPlayedToCounter = new Card(Face.JOKER, Suit.JOKER_M, 50);
        var damageCountered = JOKER_MODE && player.damageCountered(player, cardPlayedToCounter);
        assertTrue(damageCountered);
    }

    @Test
    @DisplayName("If card on top is Joker and is Joker Mode, Ace Of Spades can counter damage")
    public void ifCardOnTopIsJoker_AceOfSpadesCanCounterDamage() {
        JOKER_MODE = true;
        Player player = new Player("Player");
        addCardToPile(new Card(Face.THREE, Suit.HEARTS, 15));
        var cardPlayedToCounter = new Card(Face.ACE, Suit.SPADES, 60);
        var damageCountered = JOKER_MODE && player.damageCountered(player, cardPlayedToCounter);
        assertTrue(damageCountered);
    }

    @Test
    @DisplayName("If card on top is Joker and is Joker Mode, only Ace Of Spades, Two, Three and Joker can counter damage")
    public void ifCardOnTopIsJoker_OnlyAceOfSpadesCanCounterDamage() {
        JOKER_MODE = true;
        Player player = new Player("Player");
        assertAll("Ace, Two, Three and Joker can counter damage",

                () -> assertFalse(player.damageCountered(player,
                        new Card(Face.ACE, Suit.CLUBS, 15))),
                () -> assertFalse(player.damageCountered(player,
                        new Card(Face.ACE, Suit.DIAMONDS, 15))),
                () -> assertFalse(player.damageCountered(player,
                        new Card(Face.ACE, Suit.HEARTS, 15))),
                () -> assertTrue(player.damageCountered(player,
                        new Card(Face.TWO, Suit.SPADES, 20))),
                () -> assertTrue(player.damageCountered(player,
                        new Card(Face.THREE, Suit.SPADES, 30))),
                () -> assertTrue(player.damageCountered(player,
                        new Card(Face.JOKER, Suit.JOKER_F, 50)))
        );
    }

    @Test
    @DisplayName("Play The Asked Suit")
    void playTheAskedSuit() {
        addCardToPile(new Card(Face.ACE, Suit.CLUBS, 60));
        askedSuit = Suit.HEARTS;
        assumingThat(isAskingCardOnPile(),
                () -> {
                    var cardPlayed = new Card(Face.FIVE, Suit.HEARTS, 5);
                    var cardCanBePlayed = canPlayerPlayCard(cardPlayed);
                    if (cardCanBePlayed) {
                        addCardToPile(cardPlayed);
                    }
                    assertTrue(pile.contains(cardPlayed));
                }
        );
    }

    @Test
    @DisplayName("Ask can be countered")
    void askCanBeCountered() {
        var cardPlayed = new Card(Face.ACE, Suit.HEARTS, 20);
        addCardToPile(cardPlayed);
        askedSuit = Suit.HEARTS;
        assumingThat(isAskingCardOnPile(),
                () -> {
                    var counter = new Card(Face.ACE, Suit.CLUBS, 20);
                    var countered = canPlayerPlayCard(counter);
                    assertTrue(countered);
                }
        );
    }

    @Test
    @DisplayName("If the cutting card is played_ENDGAME")
    void ifTheCuttingCardIsPlayedEndgame() {
        var cardPlayed = new Card(Face.SEVEN, Suit.HEARTS, 7);
        addCardToPile(cardPlayed);
        User.isCuttingCard(cardPlayed);
    }
}