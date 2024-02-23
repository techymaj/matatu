package tech.majaliwa.game;

import java.util.Objects;

import static tech.majaliwa.game.Game.*;

public class Rules {

    public static boolean canPlayerPickACard() {
        // start of game
        if (pile.isEmpty()) return false;

        // if it's not your turn
        if (!PLAYER_TURN) return false;

        // can't pick twice without playing or passing
        if (playerPickCount == 1) return false;

        // if attacked by a damage card
        return !damageCardOnPile();
    }

    public static boolean dealDamageIfDamageCard() {
        if (pile.isEmpty()) return false;

        var cardOnTop = pile.getLast();
        var currentFace = cardOnTop.face();

        switch (currentFace) {
            case TWO -> {
                setDamageCardOnPile(true);
                return true;
            }
            case THREE, JOKER -> {
                if (JOKER_MODE) {
                    setDamageCardOnPile(true);
                    return true;
                }
                return false;
            }
        }

        return false;
    }

    public static boolean isAskingCardOnPile() {
        if (pile.isEmpty()) return false;

        var cardOnTop = pile.getLast();
        var currentFace = cardOnTop.face();

        return Objects.requireNonNull(currentFace) == Face.ACE;
    }

    public static boolean canPlayerPlayCard(Card card) {
        return isValidCard(card);
    }

    public static boolean canPlayerPassTurn() {
        if (pile.isEmpty()) return false;
        var damageCardOnPile = damageCardOnPile();
        if (damageCardOnPile) return false;
        return playerPickCount == 1;
    }

    public static boolean canFollowCard() {
        if (pile.isEmpty()) return false;
        var cardOnTopOfPile = pile.getLast();
        var currentFace = cardOnTopOfPile.face();
        switch (currentFace) {
            case JACK, EIGHT -> {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidCard(Card card) {
        if (card == null) {
            return true; // pass turn
        }
        if (pile.isEmpty()) {
            return true; // Any card can be played if the pile is empty
        } else {
            var previousFace = pile.getLast().face();
            var currentFace = card.face();

            var previousSuit = pile.getLast().suit();
            var currentSuit = card.suit();

            var previousCardIsJoker_F = pile.getLast().suit().equals(Suit.JOKER_F);
            var previousCardIsJoker_M = pile.getLast().suit().equals(Suit.JOKER_M);

            var currentCardIsJoker_F = card.suit().equals(Suit.JOKER_F);
            var currentCardIsJoker_M = card.suit().equals(Suit.JOKER_M);
            var currentFaceIsJoker = card.face().equals(Face.JOKER);

            var canPlayOnTopOfJoker_F = currentSuit.equals(Suit.HEARTS) || currentSuit.equals(Suit.DIAMONDS);
            var canPlayOnTopOfJoker_M = currentSuit.equals(Suit.SPADES) || currentSuit.equals(Suit.CLUBS);

            var canPlayJoker_F = previousSuit.equals(Suit.HEARTS) || previousSuit.equals(Suit.DIAMONDS);
            var canPlayJoker_M = previousSuit.equals(Suit.SPADES) || previousSuit.equals(Suit.CLUBS);

            var previousFaceIsAce = previousFace.equals(Face.ACE);
            var previousSuitIsSpades = previousSuit.equals(Suit.SPADES);
            var previousCardIsAceOfSpades = previousFaceIsAce && previousSuitIsSpades;

            var currentFaceIsAce = currentFace.equals(Face.ACE);
            var currentFaceIsAceOfSpades = currentFaceIsAce && currentSuit.equals(Suit.SPADES);

            var damageCardTwo = previousFace.equals(Face.TWO);
            var damageCardThree = previousFace.equals(Face.THREE) && JOKER_MODE;
            var damageCardJoker = previousFace.equals(Face.JOKER) && JOKER_MODE;
            var damageCard = damageCardOnPile();

            if ((damageCardTwo || damageCardThree || damageCardJoker) && damageCard) {
                return currentFace.equals(previousFace) || currentFaceIsAceOfSpades;
            }

            if (askedSuit != null) {
                return currentSuit.equals(askedSuit) || currentFaceIsAce || currentFaceIsJoker; // play if suit matches or ask countered
            }

            if (currentFaceIsAce) {
                return true; // play ace of hearts, diamonds, spades or clubs
            }

            if (previousCardIsAceOfSpades && (canPlayOnTopOfJoker_F || canPlayOnTopOfJoker_M)) {
                return true; // play hearts, diamonds, spades or clubs on top of ace of spades
            }

            if (previousCardIsJoker_F && canPlayOnTopOfJoker_F) {
                return true; // play hearts or diamonds on top of joker F
            }

            if (previousCardIsJoker_M && canPlayOnTopOfJoker_M) {
                return true; // play spades or clubs on top of joker M
            }

            if (currentCardIsJoker_F && canPlayJoker_F) {
                return true; // play joker F on top of hearts or diamonds
            }

            if (currentCardIsJoker_M && canPlayJoker_M) {
                return true; // play joker M on top of spades or clubs
            }

            return currentFace.equals(previousFace) || currentSuit.equals(previousSuit); // play if face or suit matches
        }
    }
}
