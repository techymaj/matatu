package tech.majaliwa.game;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static tech.majaliwa.game.Game.*;

final class MatatuWindow extends JFrame {
    private static final Color INK = new Color(20, 29, 34);
    private static final Color MUTED = new Color(145, 165, 165);
    private static final Color GOLD = new Color(238, 190, 96);
    private static final Color PANEL = new Color(29, 43, 48);

    private final PipedOutputStream gameInput = new PipedOutputStream();
    private final PrintStream originalOut = System.out;
    private final JTextArea activity = new JTextArea();
    private final JPanel setupPanel = new JPanel(new GridBagLayout());
    private final JPanel tablePanel = new JPanel(new BorderLayout(18, 16));
    private final JPanel centerPanel = new JPanel(new GridBagLayout());
    private final JPanel handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    private final JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
    private final JPanel suitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
    private final JPanel replayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
    private final JLabel statusLabel = new JLabel("Your table is ready", SwingConstants.CENTER);
    private final JLabel opponentLabel = new JLabel("AI  ·  waiting", SwingConstants.CENTER);
    private final JLabel deckLabel = new JLabel("DRAW PILE", SwingConstants.CENTER);
    private final JLabel discardLabel = new JLabel("Discard pile", SwingConstants.CENTER);
    private final JLabel cuttingLabel = new JLabel("CUTTING SUIT  —", SwingConstants.CENTER);
    private final JLabel pileCountLabel = label("PILE  ·  0", Color.WHITE, 12, true);
    private final JLabel scoreLabel = new JLabel("ROUND 01     ·     CLASSIC", SwingConstants.CENTER);
    private final CardFace discardCard = new CardFace(null);
    private final JTextField nameField = new JTextField("Player", 20);
    private final JButton modeButton = new JButton("CLASSIC  ·  52 CARDS");
    private final JButton startButton = new JButton("DEAL THE CARDS  →");
    private final JButton drawButton = new JButton("Draw a card");
    private final JButton passButton = new JButton("Pass turn");
    private final JButton acceptButton = new JButton("Take damage");
    private final JButton yesButton = new JButton("Play another round");
    private final JButton noButton = new JButton("Leave table");
    private final Timer refreshTimer;
    private boolean jokerMode;
    private boolean gameStarted;
    private boolean playerReady;
    private boolean suitPrompt;
    private boolean replayPrompt;
    private boolean inputConnected;

    MatatuWindow() {
        super("Matatu! — The Ugandan Card Table");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(990, 680));
        setSize(1220, 820);
        setLocationRelativeTo(null);

        configureTheme();
        buildSetup();
        buildTable();
        setContentPane(setupPanel);
        configureInput();

        refreshTimer = new Timer(250, event -> refreshTable());
        refreshTimer.start();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                refreshTimer.stop();
                System.setOut(originalOut);
                try {
                    gameInput.close();
                } catch (IOException ignored) {
                    // Closing the window also closes the game input pipe.
                }
            }
        });
    }

    private void configureTheme() {
        getContentPane().setBackground(INK);
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 13));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 16));
        UIManager.put("TextField.background", new Color(238, 235, 222));
        UIManager.put("TextField.foreground", INK);
    }

    private void buildSetup() {
        setupPanel.setBackground(INK);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 30, 10, 30);

        JLabel eyebrow = label("UGANDA'S MUCH-LOVED CARD GAME", GOLD, 12, true);
        eyebrow.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 0;
        setupPanel.add(eyebrow, c);

        JLabel title = label("MATATU!", Color.WHITE, 62, true);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 1;
        c.insets = new Insets(0, 30, 4, 30);
        setupPanel.add(title, c);

        JLabel subtitle = label("A little luck. A little strategy. One card at a time.", MUTED, 16, false);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 2;
        c.insets = new Insets(0, 30, 26, 30);
        setupPanel.add(subtitle, c);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(PANEL);
        form.setBorder(new EmptyBorder(25, 30, 25, 30));
        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 0;
        f.fill = GridBagConstraints.HORIZONTAL;
        f.insets = new Insets(7, 0, 7, 0);

        f.gridy = 0;
        form.add(label("YOUR NAME", GOLD, 11, true), f);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(72, 92, 92), 1),
                new EmptyBorder(10, 12, 10, 12)));
        f.gridy = 1;
        form.add(nameField, f);

        f.gridy = 2;
        f.insets = new Insets(19, 0, 7, 0);
        form.add(label("CHOOSE YOUR DECK", GOLD, 11, true), f);
        modeButton.setForeground(Color.WHITE);
        modeButton.setBackground(new Color(49, 68, 68));
        modeButton.setFocusPainted(false);
        modeButton.setBorder(new EmptyBorder(13, 16, 13, 16));
        modeButton.addActionListener(event -> {
            jokerMode = !jokerMode;
            modeButton.setText(jokerMode ? "JOKER MODE  ·  54 CARDS" : "CLASSIC  ·  52 CARDS");
        });
        f.gridy = 3;
        f.insets = new Insets(7, 0, 15, 0);
        form.add(modeButton, f);

        startButton.setForeground(INK);
        startButton.setBackground(GOLD);
        startButton.setFocusPainted(false);
        startButton.setBorder(new EmptyBorder(14, 16, 14, 16));
        startButton.addActionListener(event -> startGame());
        f.gridy = 4;
        f.insets = new Insets(7, 0, 0, 0);
        form.add(startButton, f);

        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(10, 30, 10, 30);
        setupPanel.add(form, c);

        JLabel rules = label("Match the suit or rank  ·  Jacks and eights let you follow  ·  Sevens cut the round",
                MUTED, 12, false);
        rules.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 4;
        c.insets = new Insets(18, 30, 10, 30);
        setupPanel.add(rules, c);
    }

    private void buildTable() {
        tablePanel.setBackground(INK);
        tablePanel.setBorder(new EmptyBorder(18, 20, 16, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel logo = label("MATATU!", GOLD, 22, true);
        scoreLabel.setForeground(MUTED);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.add(logo, BorderLayout.WEST);
        header.add(scoreLabel, BorderLayout.CENTER);

        JPanel opponent = new JPanel(new BorderLayout(8, 0));
        opponent.setOpaque(false);
        JLabel avatar = label("♟", GOLD, 24, true);
        opponentLabel.setForeground(Color.WHITE);
        opponentLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        opponent.add(avatar, BorderLayout.WEST);
        opponent.add(opponentLabel, BorderLayout.CENTER);
        header.add(opponent, BorderLayout.EAST);
        tablePanel.add(header, BorderLayout.NORTH);

        JPanel playSurface = new FeltPanel();
        playSurface.setLayout(new BorderLayout(10, 10));
        playSurface.setBorder(new EmptyBorder(22, 24, 22, 24));

        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        playSurface.add(statusLabel, BorderLayout.NORTH);

        centerPanel.setOpaque(false);
        GridBagConstraints centerC = new GridBagConstraints();
        centerC.insets = new Insets(8, 24, 8, 24);
        centerC.gridy = 0;
        JLabel deck = label("🂠", new Color(246, 241, 220), 74, false);
        deck.setHorizontalAlignment(SwingConstants.CENTER);
        deckLabel.setForeground(new Color(221, 231, 219));
        deckLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        JPanel deckStack = new JPanel(new BorderLayout(0, 3));
        deckStack.setOpaque(false);
        deckStack.add(deck, BorderLayout.CENTER);
        deckStack.add(deckLabel, BorderLayout.SOUTH);
        centerC.gridx = 0;
        centerPanel.add(deckStack, centerC);

        JLabel separator = label("· · ·", new Color(181, 207, 183), 20, false);
        centerC.gridx = 1;
        centerPanel.add(separator, centerC);

        JPanel discardStack = new JPanel(new BorderLayout(0, 5));
        discardStack.setOpaque(false);
        discardLabel.setForeground(new Color(221, 231, 219));
        discardLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        discardStack.add(discardLabel, BorderLayout.NORTH);
        discardStack.add(discardCard, BorderLayout.CENTER);
        centerC.gridx = 2;
        centerPanel.add(discardStack, centerC);

        JPanel pileStats = new JPanel(new GridLayout(2, 1, 0, 5));
        pileStats.setOpaque(false);
        cuttingLabel.setForeground(new Color(236, 218, 172));
        cuttingLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        pileStats.add(pileCountLabel);
        pileStats.add(cuttingLabel);
        centerC.gridx = 3;
        centerPanel.add(pileStats, centerC);
        playSurface.add(centerPanel, BorderLayout.CENTER);

        JPanel lower = new JPanel(new BorderLayout(0, 7));
        lower.setOpaque(false);
        JPanel controls = new JPanel(new BorderLayout(0, 2));
        controls.setOpaque(false);
        controls.add(actionPanel, BorderLayout.CENTER);
        controls.add(suitPanel, BorderLayout.SOUTH);
        controls.add(replayPanel, BorderLayout.NORTH);
        lower.add(controls, BorderLayout.NORTH);
        playSurface.add(lower, BorderLayout.SOUTH);

        JPanel boardWrap = new JPanel(new BorderLayout());
        boardWrap.setOpaque(false);
        boardWrap.add(playSurface, BorderLayout.CENTER);
        tablePanel.add(boardWrap, BorderLayout.CENTER);

        JPanel sidebar = new JPanel(new BorderLayout(0, 10));
        sidebar.setBackground(PANEL);
        sidebar.setBorder(new EmptyBorder(14, 14, 14, 14));
        JLabel logTitle = label("TABLE TALK", GOLD, 11, true);
        sidebar.add(logTitle, BorderLayout.NORTH);
        activity.setEditable(false);
        activity.setLineWrap(true);
        activity.setWrapStyleWord(true);
        activity.setBackground(PANEL);
        activity.setForeground(new Color(222, 230, 221));
        activity.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane logScroll = new JScrollPane(activity);
        logScroll.setBorder(null);
        logScroll.getViewport().setBackground(PANEL);
        sidebar.add(logScroll, BorderLayout.CENTER);
        sidebar.setPreferredSize(new Dimension(265, 100));
        tablePanel.add(sidebar, BorderLayout.EAST);

        handPanel.setOpaque(false);
        handPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(211, 221, 207, 80)),
                new EmptyBorder(4, 4, 4, 4)));
        JScrollPane handScroll = new JScrollPane(handPanel);
        handScroll.setPreferredSize(new Dimension(500, 155));
        handScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(211, 221, 207, 80)),
                " YOUR HAND  ·  click a card to play ", 0, 0,
                new Font("SansSerif", Font.BOLD, 10), Color.WHITE));
        handScroll.getViewport().setOpaque(false);
        handScroll.setOpaque(false);
        lower.add(handScroll, BorderLayout.CENTER);

        configureActionButton(drawButton, new Color(235, 190, 100), INK);
        configureActionButton(passButton, new Color(220, 227, 216), INK);
        configureActionButton(acceptButton, new Color(208, 112, 82), Color.WHITE);
        drawButton.addActionListener(event -> submit("p"));
        passButton.addActionListener(event -> submit("pass"));
        acceptButton.addActionListener(event -> submit("accept"));
        actionPanel.add(drawButton);
        actionPanel.add(passButton);
        actionPanel.add(acceptButton);
        actionPanel.setOpaque(false);

        for (Suit suit : new Suit[]{Suit.HEARTS, Suit.SPADES, Suit.CLUBS, Suit.DIAMONDS}) {
            JButton chooseSuit = new JButton(suitGlyph(suit) + "  " + suit.name());
            configureActionButton(chooseSuit, new Color(52, 73, 66), Color.WHITE);
            chooseSuit.addActionListener(event -> submit(switch (suit) {
                case HEARTS -> "h";
                case SPADES -> "s";
                case CLUBS -> "c";
                case DIAMONDS -> "d";
                default -> throw new IllegalStateException("Unsupported suit");
            }));
            suitPanel.add(chooseSuit);
        }
        suitPanel.setOpaque(false);

        yesButton.addActionListener(event -> submit(jokerMode ? "y" : "n"));
        noButton.addActionListener(event -> submit("n"));
        configureActionButton(yesButton, GOLD, INK);
        configureActionButton(noButton, new Color(52, 73, 66), Color.WHITE);
        replayPanel.add(yesButton);
        replayPanel.add(noButton);
        replayPanel.setOpaque(false);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(INK);
        outer.add(tablePanel, BorderLayout.CENTER);
        setContentPane(outer);
    }

    private void configureInput() {
        try {
            PipedInputStream input = new PipedInputStream(8192);
            input.connect(gameInput);
            Game.scanner = new java.util.Scanner(input, StandardCharsets.UTF_8);
            inputConnected = true;
            System.setOut(new PrintStream(new ActivityOutputStream(originalOut), true, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize the game input.", e);
        }
    }

    private void startGame() {
        String playerName = nameField.getText().trim();
        if (playerName.isEmpty()) {
            statusLabel.setText("Please enter a name to join the table.");
            nameField.requestFocusInWindow();
            return;
        }
        gameStarted = true;
        setContentPane(tablePanel);
        revalidate();
        repaint();
        sendLine(jokerMode ? "y" : "n");
        sendLine(playerName);
        Thread gameThread = new Thread(() -> {
            try {
                new Game().startGame();
            } catch (RuntimeException exception) {
                originalOut.println("The game stopped unexpectedly: " + exception.getMessage());
                SwingUtilities.invokeLater(() -> statusLabel.setText("The game stopped unexpectedly."));
            }
        }, "matatu-game");
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private void refreshTable() {
        if (!gameStarted) return;

        User player = !users.isEmpty() ? users.get(0) : null;
        User ai = users.size() > 1 ? users.get(1) : null;
        ArrayList<Card> hand = player == null ? new ArrayList<>() : new ArrayList<>(player.getHand());

        opponentLabel.setText(ai == null ? "AI  ·  waiting" : "AI  ·  " + ai.getHand().size() + " cards");
        scoreLabel.setText(String.format("ROUND %02d     ·     %s", Math.max(round, 1),
                jokerMode ? "JOKER MODE" : "CLASSIC"));
        deckLabel.setText("DRAW PILE  ·  " + deck.size());
        cuttingLabel.setText("CUTTING SUIT  ·  " + (cuttingSuit == null ? "—" : suitGlyph(cuttingSuit)));

        pileCountLabel.setText("PILE  ·  " + pile.size());
        Card topCard = getTopCard();
        updateDiscard(topCard);

        boolean turnAvailable = playerReady && PLAYER_TURN && !GAME_OVER;
        boolean damage = damageCardOnPile();
        drawButton.setEnabled(turnAvailable && Rules.canPlayerPickACard());
        passButton.setEnabled(turnAvailable && Rules.canPlayerPassTurn());
        acceptButton.setEnabled(turnAvailable && damage);
        setActionText(damage ? "Take damage" : "Draw a card");

        for (java.awt.Component component : suitPanel.getComponents()) {
            component.setVisible(suitPrompt);
        }
        for (java.awt.Component component : replayPanel.getComponents()) {
            component.setVisible(replayPrompt);
        }

        handPanel.removeAll();
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            boolean playable = Rules.canPlayerPlayCard(card);
            CardFace cardFace = new CardFace(card);
            cardFace.setEnabled(turnAvailable && playable && !suitPrompt && !replayPrompt);
            cardFace.setPlayable(playable);
            int cardIndex = i + 1;
            cardFace.addActionListener(event -> submit(Integer.toString(cardIndex)));
            handPanel.add(cardFace);
        }
        if (hand.isEmpty() && player != null && !GAME_OVER) {
            JLabel empty = label("Your hand is clear — you're winning!", GOLD, 13, true);
            handPanel.add(empty);
        }
        handPanel.revalidate();
        handPanel.repaint();

        if (replayPrompt) statusLabel.setText("Round complete. Deal another round?");
        else if (suitPrompt) statusLabel.setText("Choose the suit the next player must follow.");
        else if (damage) statusLabel.setText("Damage is on the pile — counter it or take the cards.");
        else if (turnAvailable) statusLabel.setText("Your turn — choose a playable card or an action.");
        else if (GAME_OVER) statusLabel.setText("Round complete — check the table talk for the result.");
        else statusLabel.setText(ai == null ? "Shuffling the deck…" : "The AI is making its move…");
    }

    private void updateDiscard(Card topCard) {
        discardCard.setCard(topCard);
    }

    private void submit(String input) {
        if (!inputConnected) return;
        if (!input.equals("y") && !input.equals("n") && !input.matches("[hscd]")) {
            playerReady = false;
        }
        if (input.equals("y") || input.equals("n")) replayPrompt = false;
        if (input.matches("[hscd]")) suitPrompt = false;
        sendLine(input);
        refreshTable();
    }

    private void sendLine(String input) {
        try {
            gameInput.write((input + "\n").getBytes(StandardCharsets.UTF_8));
            gameInput.flush();
        } catch (IOException e) {
            statusLabel.setText("Could not send your move to the game.");
            originalOut.println("Unable to send game input: " + e.getMessage());
        }
    }

    private void setActionText(String text) {
        drawButton.setText(text);
    }

    private static void configureActionButton(JButton button, Color background, Color foreground) {
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 14, 10, 14));
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    private static JLabel label(String text, Color color, int size, boolean bold) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        return label;
    }

    private static String suitGlyph(Suit suit) {
        return switch (suit) {
            case HEARTS -> "♥";
            case SPADES -> "♠";
            case CLUBS -> "♣";
            case DIAMONDS -> "♦";
            case JOKER_F -> "♀";
            case JOKER_M -> "♂";
        };
    }

    private final class ActivityOutputStream extends OutputStream {
        private final OutputStream console;
        private final ByteArrayOutputStream line = new ByteArrayOutputStream();

        private ActivityOutputStream(OutputStream console) {
            this.console = console;
        }

        @Override
        public synchronized void write(int value) throws IOException {
            console.write(value);
            if (value == '\n') {
                String completed = line.toString(StandardCharsets.UTF_8)
                        .replaceAll("\u001B\\[[;\\d]*m", "");
                line.reset();
                SwingUtilities.invokeLater(() -> {
                    if (!completed.isBlank()) {
                        activity.append(completed + "\n");
                        activity.setCaretPosition(activity.getDocument().getLength());
                    }
                    if (completed.startsWith("It's your turn,")) playerReady = true;
                    if (completed.startsWith("Choose a suit:")) suitPrompt = true;
                    if (completed.contains("Do you want to play again?")) replayPrompt = true;
                });
            } else if (value != '\r') {
                line.write(value);
            }
        }

        @Override
        public synchronized void flush() throws IOException {
            console.flush();
        }

        @Override
        public synchronized void close() throws IOException {
            flush();
        }
    }

    private static final class FeltPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, new Color(22, 105, 78),
                    getWidth(), getHeight(), new Color(12, 73, 60)));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 26, 26);
            g.setColor(new Color(232, 210, 156, 55));
            g.drawRoundRect(12, 12, getWidth() - 25, getHeight() - 25, 20, 20);
            g.dispose();
        }

        @Override
        public boolean isOpaque() {
            return false;
        }
    }

    private static final class CardFace extends JButton {
        private Card card;
        private boolean playable;

        private CardFace(Card card) {
            this.card = card;
            setPreferredSize(new Dimension(76, 110));
            setMinimumSize(new Dimension(76, 110));
            setMaximumSize(new Dimension(76, 110));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setToolTipText(card == null ? "No card on the pile" : card.toString());
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }

        private void setPlayable(boolean playable) {
            this.playable = playable;
        }

        private void setCard(Card card) {
            this.card = card;
            setToolTipText(card == null ? "No card on the pile" : card.toString());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth() - 3;
            int h = getHeight() - 3;
            Color base = isEnabled() && playable ? new Color(255, 254, 245) : new Color(218, 220, 211);
            g.setColor(base);
            g.fillRoundRect(2, 2, w, h, 12, 12);
            g.setColor(isEnabled() && playable ? GOLD : new Color(170, 179, 168));
            g.setStroke(new java.awt.BasicStroke(isEnabled() && playable ? 2.4f : 1f));
            g.drawRoundRect(2, 2, w, h, 12, 12);
            if (card == null) {
                g.setColor(new Color(59, 104, 89));
                g.fillRoundRect(9, 9, w - 14, h - 14, 9, 9);
                g.setColor(new Color(226, 214, 174));
                g.drawRoundRect(14, 14, w - 24, h - 24, 7, 7);
                g.setFont(new Font("Serif", Font.BOLD, 37));
                g.drawString("M", w / 2 - 12, h / 2 + 13);
            } else {
                Color ink = switch (card.suit()) {
                    case HEARTS, DIAMONDS -> new Color(183, 54, 59);
                    case JOKER_F, JOKER_M -> new Color(116, 73, 138);
                    default -> new Color(34, 43, 42);
                };
                String face = card.face().getFace();
                String suit = suitGlyph(card.suit());
                g.setColor(ink);
                g.setFont(new Font("SansSerif", Font.BOLD, face.length() > 2 ? 11 : 17));
                g.drawString(face, 9, 23);
                g.setFont(new Font("SansSerif", Font.BOLD, 14));
                g.drawString(suit, 9, 39);
                g.setFont(new Font("Serif", Font.BOLD, card.face() == Face.JOKER ? 27 : 37));
                Font metricsFont = g.getFont();
                int symbolWidth = g.getFontMetrics(metricsFont).stringWidth(suit);
                g.drawString(suit, (w - symbolWidth) / 2 + 1, h / 2 + 15);
                g.setFont(new Font("SansSerif", Font.BOLD, 11));
                String value = face + suit;
                int valueWidth = g.getFontMetrics().stringWidth(value);
                g.drawString(value, w - valueWidth - 8, h - 10);
            }
            if (!isEnabled() && card != null) {
                g.setColor(new Color(30, 40, 35, 38));
                g.fillRoundRect(2, 2, w, h, 12, 12);
            }
            g.dispose();
        }
    }
}
