import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

 class HangmanGUI extends JFrame {
    private String[] words = {"java", "python", "hangman", "programming", "openai"};
    private String word;
    private char[] guessedWord;
    private int attempts = 6;
    private boolean won = false;
    private HashSet<Character> guessedLetters = new HashSet<>();
    private JLabel wordLabel, attemptsLabel, messageLabel, timerLabel;
    private JTextField inputField;
    private JTextArea incorrectGuessesArea;
    private JButton guessButton, restartButton;
    private Timer timer;
    private int timeLeft = 60; // 60 seconds timer

    public HangmanGUI() {
        setTitle("Hangman Game");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeGame();

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 1));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        wordLabel = new JLabel("Word: " + new String(guessedWord));
        wordLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(wordLabel);

        attemptsLabel = new JLabel("Attempts remaining: " + attempts);
        attemptsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(attemptsLabel);

        timerLabel = new JLabel("Time remaining: " + timeLeft + " seconds");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(timerLabel);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        messageLabel = new JLabel("Enter a letter or guess the word:");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        centerPanel.add(messageLabel);

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 18));
        centerPanel.add(inputField);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        guessButton = new JButton("Guess");
        guessButton.setFont(new Font("Arial", Font.PLAIN, 18));
        guessButton.addActionListener(new GuessButtonListener());
        buttonPanel.add(guessButton);

        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.PLAIN, 18));
        restartButton.addActionListener(e -> restartGame());
        buttonPanel.add(restartButton);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        incorrectGuessesArea = new JTextArea("Incorrect guesses: ");
        incorrectGuessesArea.setEditable(false);
        incorrectGuessesArea.setFont(new Font("Arial", Font.PLAIN, 16));
        bottomPanel.add(new JScrollPane(incorrectGuessesArea), BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        startTimer();
    }

    private void initializeGame() {
        word = words[(int) (Math.random() * words.length)];
        guessedWord = new char[word.length()];
        for (int i = 0; i < guessedWord.length; i++) {
            guessedWord[i] = '_';
        }
        guessedLetters.clear();
        attempts = 6;
        timeLeft = 60;
        won = false;
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                timeLeft--;
                timerLabel.setText("Time remaining: " + timeLeft + " seconds");
                if (timeLeft <= 0) {
                    timer.cancel();
                    gameOver();
                }
            }
        }, 1000, 1000);
    }

    private void gameOver() {
        messageLabel.setText("Time's up! Game over! The word was: " + word);
        guessButton.setEnabled(false);
    }

    private void restartGame() {
        initializeGame();
        wordLabel.setText("Word: " + new String(guessedWord));
        attemptsLabel.setText("Attempts remaining: " + attempts);
        timerLabel.setText("Time remaining: " + timeLeft + " seconds");
        messageLabel.setText("Enter a letter or guess the word:");
        incorrectGuessesArea.setText("Incorrect guesses: ");
        guessButton.setEnabled(true);
        startTimer();
    }

    private class GuessButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = inputField.getText();
            inputField.setText("");
            inputField.requestFocus();

            if (input.length() == 1) {
                char guess = input.charAt(0);

                if (guessedLetters.contains(guess)) {
                    messageLabel.setText("You have already guessed that letter. Try again.");
                    return;
                }

                guessedLetters.add(guess);
                boolean correctGuess = false;
                for (int i = 0; i < word.length(); i++) {
                    if (word.charAt(i) == guess) {
                        guessedWord[i] = guess;
                        correctGuess = true;
                    }
                }

                if (!correctGuess) {
                    attempts--;
                    incorrectGuessesArea.append(guess + " ");
                    attemptsLabel.setText("Attempts remaining: " + attempts);
                    if (attempts == 0) {
                        messageLabel.setText("Game over! The word was: " + word);
                        guessButton.setEnabled(false);
                    } else {
                        messageLabel.setText("Incorrect guess. Try again.");
                    }
                } else {
                    wordLabel.setText("Word: " + new String(guessedWord));
                    if (new String(guessedWord).equals(word)) {
                        won = true;
                        messageLabel.setText("Congratulations! You guessed the word: " + word);
                        guessButton.setEnabled(false);
                    } else {
                        messageLabel.setText("Correct guess. Keep going!");
                    }
                }
            } else if (input.equalsIgnoreCase(word)) {
                won = true;
                wordLabel.setText("Word: " + word);
                messageLabel.setText("Congratulations! You guessed the word: " + word);
                guessButton.setEnabled(false);
            } else {
                attempts--;
                attemptsLabel.setText("Attempts remaining: " + attempts);
                if (attempts == 0) {
                    messageLabel.setText("Game over! The word was: " + word);
                    guessButton.setEnabled(false);
                } else {
                    messageLabel.setText("Incorrect guess. Try again.");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            HangmanGUI game = new HangmanGUI();
            game.setVisible(true);
        });
    }
}
