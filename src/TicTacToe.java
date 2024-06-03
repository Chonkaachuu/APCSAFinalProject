import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
// danny is not cooking with this project
public class TicTacToe extends JFrame implements ActionListener {
    private JButton[][] button = new JButton[3][3];
    private JLabel statusLabel;
    private boolean p1turn = true;
    private int p1score = 0;
    private int p2score = 0;
    private JLabel scoreLabel;
    private boolean ai = false;
    private ImageIcon xIcon;
    private ImageIcon oIcon;
    private JPanel boardPanel;
    private ImageIcon hiddenImage;
    private JLabel hiddenImageLabel;
    private JButton showButton;

    public TicTacToe() {
        // Load custom images for X and O
        xIcon = new ImageIcon(getClass().getResource("/images/x1.png"));
        oIcon = new ImageIcon(getClass().getResource("/images/o1.png"));

        SoundUtils.loopSound("/sounds/bbl.wav");

        setTitle("Tic Tac Toe");
        setSize(750, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem playWithAI = new JMenuItem("Play with AI");
        JMenuItem playWithHuman = new JMenuItem("Play with Human");
        JMenu colorMenu = new JMenu("Change Color");
        JMenuItem defaultColor = new JMenuItem("Default");
        JMenuItem blueColor = new JMenuItem("Blue");
        JMenuItem greenColor = new JMenuItem("Green");
        JMenuItem redColor = new JMenuItem("Red");
        JMenuItem pinkColor = new JMenuItem("Pink");

        JMenuItem quit = new JMenuItem("Quit");

        // Load the hidden image
        hiddenImage = new ImageIcon(getClass().getResource("/images/hidden.png"));
        hiddenImageLabel = new JLabel(hiddenImage);
        hiddenImageLabel.setVisible(false);

        showButton = new JButton("Show/Hide Hidden Item");
        showButton.addActionListener(new ActionListener() {
            private boolean isPlayingHiddenSound = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isVisible = hiddenImageLabel.isVisible();
                hiddenImageLabel.setVisible(!isVisible);
                setSize(750, 750);

                if (!isPlayingHiddenSound) {
                    SoundUtils.pauseSound();
                    SoundUtils.playHiddenSound("/sounds/meetthegrahams.wav");
                } else {
                    SoundUtils.stopHiddenSound();
                    SoundUtils.resumeSound();
                }
                isPlayingHiddenSound = !isPlayingHiddenSound;
            }
        });

        playWithAI.addActionListener(e -> {
            ai = true;
            resetGame();
        });

        playWithHuman.addActionListener(e -> {
            ai = false;
            resetGame();
        });

        defaultColor.addActionListener(e -> changeBoardColor(Color.LIGHT_GRAY));
        blueColor.addActionListener(e -> changeBoardColor(Color.BLUE));
        greenColor.addActionListener(e -> changeBoardColor(Color.GREEN));
        redColor.addActionListener(e -> changeBoardColor(Color.RED));
        pinkColor.addActionListener(e -> changeBoardColor(Color.PINK));


        quit.addActionListener(e -> System.exit(0));

        colorMenu.add(defaultColor);
        colorMenu.add(blueColor);
        colorMenu.add(greenColor);
        colorMenu.add(redColor);
        colorMenu.add(pinkColor);



        menu.add(showButton);
        menu.add(playWithAI);
        menu.add(playWithHuman);
        menu.add(colorMenu);
        menu.add(quit);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3));
        initializeButtons(boardPanel);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());

        statusLabel = new JLabel("Player 1's turn (X)");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        scoreLabel = new JLabel("Score - Player 1: 0 | Player 2: 0");
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        statusPanel.add(scoreLabel, BorderLayout.SOUTH);

        add(boardPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        add(hiddenImageLabel, BorderLayout.NORTH);  // Add the hidden image label to the frame

        setVisible(true);
    }

    private void initializeButtons(JPanel panel) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                button[i][j] = new JButton();
                button[i][j].setFont(new Font("Arial", Font.PLAIN, 60));
                button[i][j].setFocusPainted(false);
                // button[i][j].setContentAreaFilled(false);  // Removed this line
                button[i][j].setOpaque(true);
                button[i][j].addActionListener(this);
                panel.add(button[i][j]);
            }
        }
    }


    private void changeBoardColor(Color color) {
        boardPanel.setBackground(color);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                button[i][j].setBackground(color);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton buttonClicked = (JButton) e.getSource();
        SoundUtils.pauseSound();

        if (p1turn) {
            buttonClicked.setIcon(xIcon);
            statusLabel.setText("Player 2's turn (O)");
        } else {
            buttonClicked.setIcon(oIcon);
            statusLabel.setText("Player 1's turn (X)");
        }

        buttonClicked.setEnabled(false);
        p1turn = !p1turn;

        // Play click sound
        String clickSound = p1turn ? "/sounds/baby.wav" : "/sounds/wop.wav";
        int delay = p1turn ? 1500 : 650;  // Delay for resume sound

        SoundUtils.playSound(clickSound);
        new Timer(delay, evt -> {
            SoundUtils.resumeSound();
            ((Timer) evt.getSource()).stop();
        }).start();

        checkForWinner();

        if (ai && !p1turn) {
            // Delay AI move to let sounds play
            new Timer(2000, aiMoveEvent -> {
                aiMove();
                ((Timer) aiMoveEvent.getSource()).stop();
            }).start();
        }
    }

    private void aiMove() {
        Random rand = new Random();
        int row, col;
        do {
            row = rand.nextInt(3);
            col = rand.nextInt(3);
        } while (button[row][col].getIcon() != null);

        button[row][col].doClick();
    }


    private void checkForWinner() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (button[i][0].getIcon() == button[i][1].getIcon() &&
                    button[i][1].getIcon() == button[i][2].getIcon() &&
                    button[i][0].getIcon() != null) {
                announceWinner(button[i][0].getIcon());
                SoundUtils.pauseSound();
                return;
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (button[0][i].getIcon() == button[1][i].getIcon() &&
                    button[1][i].getIcon() == button[2][i].getIcon() &&
                    button[0][i].getIcon() != null) {
                announceWinner(button[0][i].getIcon());
                SoundUtils.pauseSound();
                return;
            }
        }

        // Check diagonals
        if (button[0][0].getIcon() == button[1][1].getIcon() &&
                button[1][1].getIcon() == button[2][2].getIcon() &&
                button[0][0].getIcon() != null) {
            announceWinner(button[0][0].getIcon());
            SoundUtils.pauseSound();
            return;
        }

        if (button[0][2].getIcon() == button[1][1].getIcon() &&
                button[1][1].getIcon() == button[2][0].getIcon() &&
                button[0][2].getIcon() != null) {
            announceWinner(button[0][2].getIcon());
            SoundUtils.pauseSound();
            return;
        }

        // Check for draw
        boolean draw = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (button[i][j].isEnabled()) {
                    draw = false;
                    break;
                }
            }
        }

        if (draw) {
            SoundUtils.playSound("/sounds/draw.wav");
            JOptionPane.showMessageDialog(this, "It's a draw!");
            resetBoard();
        }
    }

    private void announceWinner(Icon winnerIcon) {
        SoundUtils.playSound("/sounds/win.wav");
        String winnerMessage = "Player " + (winnerIcon == xIcon ? "1" : "2") + " wins!";
        JOptionPane.showMessageDialog(this, winnerMessage);

        if (winnerIcon == xIcon) {
            p1score++;
        } else {
            p2score++;
        }

        updateScore();
        resetBoard();
    }

    private void updateScore() {
        scoreLabel.setText("Score - Player 1: " + p1score + " | Player 2: " + p2score);
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                button[i][j].setIcon(null);
                button[i][j].setEnabled(true);
            }
        }
        p1turn = true;
        statusLabel.setText("Player 1's turn (X)");
    }

    private void resetGame() {
        resetBoard();
        p1score = 0;
        p2score = 0;
        updateScore();
    }

    public static void main(String[] args) {
        new TicTacToe();
    }
}
