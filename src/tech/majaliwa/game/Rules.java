package tech.majaliwa.game;

public class Rules {
    public static String WELCOME_MESSAGE = """
        Welcome to Matatu!,
        This is a card game that is played in Uganda.,
        The goal of the game is to be the first player to get rid of all your cards.
        You can play a card if it matches the rank or suit of the top card on the pile.,
        If you don't have a card to play, you must pick up a card from the deck.,
        If you pick up a card that you can play, you can play it.,
        If you pick up a card that you can't play, you must keep it and let the next player play.
        The game is played in rounds, and the first player to get rid of all their cards wins the round.
        """;

    public static String JOKER = """
                    ------------------------------------------------------------
                    You are now playing in classic mode.
                    In classic mode, the deck is made up of 54 cards.
                    The extra cards are two jokers valued at 50 each.
                    The Ace of spades holds the highest value in the deck (60).
                    ------------------------------------------------------------""";

    public static String CLASSIC = """
                    ------------------------------------------------------------
                    You are now playing in normal mode.
                    In normal mode, the deck is made up of 52 cards.
                    The deck does not have jokers.
                    The Ace of spades holds the highest value in the deck (60).
                    ------------------------------------------------------------""";
}
