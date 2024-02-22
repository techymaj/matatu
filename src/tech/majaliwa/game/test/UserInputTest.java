package tech.majaliwa.game.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tech.majaliwa.game.Card;
import tech.majaliwa.game.Face;
import tech.majaliwa.game.Suit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;
import static tech.majaliwa.game.Deck.createDeck;
import static tech.majaliwa.game.Game.*;
import static tech.majaliwa.game.Game.setDamageCardOnPile;
import static tech.majaliwa.game.Rules.canPlayerPickACard;
import static tech.majaliwa.game.User.addCardToPile;

public class UserInputTest {

    private int cardPosition;
    private ArrayList<Card> hand;
    private ArrayList<Card> deck;

    @BeforeEach
    public void setUp() {
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
    @Nested
    @DisplayName("Checking user input")
    class checkUserInput {
        @ParameterizedTest(name = "If input is: \"{0}\" and is player's turn")
        @DisplayName("A player can pick")
        @ValueSource(strings = {"p"})
        void checkIfAPlayerCanPick(String input) {
            assumingThat(input.equalsIgnoreCase("p"),
                    () -> {
                        PLAYER_TURN = true;
                        playerPickCount = 0;
                        setDamageCardOnPile(false);
                        addCardToPile(new Card(Face.TWO, Suit.HEARTS, 20));
                        var aPlayerCanPickACard = canPlayerPickACard();
                        assertTrue(aPlayerCanPickACard);
                    }
            );
        }

        @ParameterizedTest(name = "If input is: \"{0}\" and player already picked")
        @DisplayName("A player can't pick twice")
        @ValueSource(strings = {"p"})
        void checkIfAPlayerCanPickTwice(String input) {
            assumingThat(input.equalsIgnoreCase("p"),
                    () -> {
                        addCardToPile(new Card(Face.TWO, Suit.HEARTS, 20));
                        playerPickCount = 1;
                        var aPlayerCanPickACard = canPlayerPickACard();
                        assertFalse(aPlayerCanPickACard);
                    }
            );
        }

        @ParameterizedTest(name = "If input is: \"{0}\" and pile is empty")
        @DisplayName("A player can't pick")
        @ValueSource(strings = {"p"})
        void checkIfAPlayerCantPick(String input) {
            assumingThat(input.equalsIgnoreCase("p"),
                    () -> {
                        var aPlayerCanPickACard = canPlayerPickACard();
                        assertFalse(aPlayerCanPickACard);
                    }
            );
        }

        @ParameterizedTest(name = "If input is \"{0}\" and not player's turn")
        @DisplayName("A player can't pick")
        @ValueSource(strings = {"p"})
        void playerCantPickIfNotPlayersTurn(String input) {
            assumingThat(input.equalsIgnoreCase("p"),
                    () -> {
                        PLAYER_TURN = false;
                        var aPlayerCanPickACard = canPlayerPickACard();
                        assertFalse(aPlayerCanPickACard);
                    }
            );
        }

        @ParameterizedTest(name = "If input is \"{0}\" and damage card on top")
        @DisplayName("A player can't pick")
        @ValueSource(strings = {"p"})
        void playerCantPickIfDamageCardOnTop(String input) {
            assumingThat(input.equalsIgnoreCase("p"),
                    () -> {
                        setDamageCardOnPile(true);
                        var aPlayerCanPickACard = canPlayerPickACard();
                        assertFalse(aPlayerCanPickACard);
                    }
            );
        }
    }
}
