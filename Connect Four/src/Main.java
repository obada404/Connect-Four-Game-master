import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.LineBorder;

public class Main {

	static Board board = new Board();
	static JFrame frameMainWindow;
	static JFrame frameGameOver;
	static Component compMainWindowContents;

	static JPanel panelMain;
	static JPanel panelBoardChecker;
	static JLayeredPane layeredGameBoard;

	static final int DEFAULT_WIDTH = 570;
	static final int DEFAULT_HEIGHT = 515;

	static boolean firstGame = true;

	static int mousePosition;
	static int player1ScoreValue = 0;
	static int player2ScoreValue = 0;
	static JLabel player1Score, player2Score;
	static JLabel playingChecker;

	static GameParameters game_params = new GameParameters();
	static int gameMode = game_params.getGameMode();
	static String difficulty = game_params.getDifficulty();
	static String player1Name = game_params.getPlayer1Name();
	static String player2Name = game_params.getPlayer2Name();

	static AlphaBeta alphaBeta = new AlphaBeta(difficulty, Board.PLAYER1);
	public static JLabel checkerLabel = null;

	// for Undo operation
	public static int humanPlayerRowUndo;
	public static int humanPlayerColUndo;
	public static int humanPlayer;
	public static JLabel humanPlayerCheckerLabelUndo;
	public static JLabel imageBoardLabel;

	private static JMenuBar menuBar;
	private static JMenu file, help;
	private static JMenuItem newGame, undo, preferences, exit, howToPlay, about;

	public static Clip clip1, clip2;

	private static void AddMenus() {
		menuBar = new JMenuBar();

		file = new JMenu("File");
		newGame = new JMenuItem("New Game    Ctrl+N");
		undo = new JMenuItem("Undo    Ctrl+Z");
		undo.setEnabled(false);

		preferences = new JMenuItem("Preferences");
		exit = new JMenuItem("Exit");
		file.add(newGame);
		file.add(undo);
		file.add(preferences);
		file.add(exit);

		help = new JMenu("Help");
		howToPlay = new JMenuItem("How to Play");
		about = new JMenuItem("About");
		help.add(howToPlay);
		help.add(about);

		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player1ScoreValue = 0;
				player2ScoreValue = 0;
				createNewGame();
			}
		});

		undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (undo.isEnabled()) {
					undo();
					undo.setEnabled(false);
				}
			}
		});

		preferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PreferencesWindow prefs = new PreferencesWindow(game_params);
				prefs.setVisible(true);
				prefs.P1Name.setText(game_params.getPlayer1Name());

				prefs.P2Name.setText(game_params.getPlayer2Name());
				prefs.difficulty.setSelectedItem(game_params.getDifficulty());
				prefs.gameMode.setSelectedItem(game_params.getGameMode());

				preferences.removeActionListener(this);
			}
		});

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		howToPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(panelMain,
						"• Place the checker wherever you need using the left and right arrows.\n"
								+ "• Drop the checker down using the down arrow.\n"
								+ "• To win you must place 4 checkers in an row, horizontally, vertically or diagonally.",
						"How to Play", JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon(Main.class.getResource("arrowKeys.png")));
			}
		});

		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(panelMain, "Created by: Ashraf & Mohammad", "About",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		menuBar.add(file);
		menuBar.add(help);
		frameMainWindow.setJMenuBar(menuBar);
		frameMainWindow.setVisible(true);
	}

	// The Main Connect Four Board
	public static JLayeredPane createLayeredBoard() {
		layeredGameBoard = new JLayeredPane();
		layeredGameBoard.setPreferredSize(new Dimension(565, 510));
		layeredGameBoard.setOpaque(true);
		layeredGameBoard.setBackground(Color.WHITE);
		layeredGameBoard.setBorder(new LineBorder(new Color(214, 217, 223), 20));

		ImageIcon imageBoard = new ImageIcon(Main.class.getResource("Board.png"));
		imageBoardLabel = new JLabel(imageBoard);

		imageBoardLabel.setBounds(19, 16, imageBoard.getIconWidth(), imageBoard.getIconHeight());
		layeredGameBoard.add(imageBoardLabel, new Integer(0), 0);
		return layeredGameBoard;
	}

	public static KeyAdapter gameKeyAdapter = new KeyAdapter() {
		public void keyPressed(KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.VK_RIGHT && playingChecker.getX() < 475) {
				playingChecker.setLocation(playingChecker.getX() + 75, playingChecker.getY());
			} else if (event.getKeyCode() == KeyEvent.VK_LEFT && playingChecker.getX() > 25) {
				playingChecker.setLocation(playingChecker.getX() - 75, playingChecker.getY());
			} else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
				if (playingChecker.getX() == 25) {
					makeMove(0);
				} else if (playingChecker.getX() == 100) {
					makeMove(1);
				} else if (playingChecker.getX() == 175) {
					makeMove(2);
				} else if (playingChecker.getX() == 250) {
					makeMove(3);
				} else if (playingChecker.getX() == 325) {
					makeMove(4);
				} else if (playingChecker.getX() == 400) {
					makeMove(5);
				} else if (playingChecker.getX() == 475) {
					makeMove(6);
				}
				if (!board.isOverflowOccured()) {
					game();
					undo.setEnabled(true);
					saveUndoMove();
					if (gameMode == GameParameters.HumanVSComputer)
						computerMove();
					else {
						if (board.getLastPlayerPlayed() == Board.PLAYER1) {
							playingChecker.setIcon(new ImageIcon(Main.class.getResource("black.png")));
						} else {
							playingChecker.setIcon(new ImageIcon(Main.class.getResource("red.png")));
						}
					}
					layeredGameBoard.add(imageBoardLabel, new Integer(0), 0);
					if (game_params.getGameMode() == GameParameters.HumanVSComputer) {
						if (board.getLastPlayerPlayed() == Board.PLAYER2) {
							clip1.setFramePosition(0);
							clip1.start();
						}
					} else {
						if (board.getLastPlayerPlayed() == Board.PLAYER1) {
							clip2.setFramePosition(0);
							clip2.start();
						} else {
							clip1.setFramePosition(0);
							clip1.start();
						}
					}
				}
				frameMainWindow.requestFocusInWindow();
			} else if ((event.getKeyCode() == KeyEvent.VK_Z) && ((event.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
				if (undo.isEnabled()) {
					undo();
					undo.setEnabled(false);
				}
			} else if ((event.getKeyCode() == KeyEvent.VK_N) && ((event.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
				player1ScoreValue = 0;
				player2ScoreValue = 0;
				createNewGame();
			}
		}
	};

	public static void undo() {
		// undo implementation for Human VS Human mode
		if (!board.isGameOver() && game_params.getGameMode() == GameParameters.HumanVSHuman) {
			try {
				if (board.getLastPlayerPlayed() == Board.PLAYER2)
					playingChecker.setIcon(new ImageIcon(Main.class.getResource("red.png")));
				else
					playingChecker.setIcon(new ImageIcon(Main.class.getResource("black.png")));

				playingChecker.setVisible(true);
				if (frameMainWindow.getKeyListeners().length == 0) {
					frameMainWindow.addKeyListener(gameKeyAdapter);
				}
				board.undoMove(board.getLastMove().getRow(), board.getLastMove().getCol(), humanPlayer);
				layeredGameBoard.remove(checkerLabel);
				frameMainWindow.paint(frameMainWindow.getGraphics());
			} catch (ArrayIndexOutOfBoundsException ex) {
			}
		}

		// Undo implementation for Human VS Computer mode
		else if (!board.isGameOver() && game_params.getGameMode() == GameParameters.HumanVSComputer) {
			try {
				playingChecker.setVisible(true);
				if (frameMainWindow.getKeyListeners().length == 0) {
					frameMainWindow.addKeyListener(gameKeyAdapter);
				}
				board.undoMove(board.getLastMove().getRow(), board.getLastMove().getCol(), board.getLastPlayerPlayed());
				layeredGameBoard.remove(checkerLabel);
				board.undoMove(humanPlayerRowUndo, humanPlayerColUndo, humanPlayer);
				layeredGameBoard.remove(humanPlayerCheckerLabelUndo);
				frameMainWindow.paint(frameMainWindow.getGraphics());
			} catch (ArrayIndexOutOfBoundsException ex) {
			}
		}
	}

	public static void createNewGame() {
		board = new Board();

		gameMode = game_params.getGameMode();
		difficulty = game_params.getDifficulty();
		player1Name = game_params.getPlayer1Name();
		player2Name = game_params.getPlayer2Name();

		alphaBeta.setMaxDepth(difficulty);

		if (frameMainWindow != null)
			frameMainWindow.dispose();
		frameMainWindow = new JFrame("Connect Four Game -AI-");
		frameMainWindow.setLocation(DEFAULT_WIDTH / 2, DEFAULT_HEIGHT / 10);

		compMainWindowContents = createContentComponents();
		frameMainWindow.getContentPane().add(compMainWindowContents, BorderLayout.CENTER);

		if (player2ScoreValue > player1ScoreValue) {
			player2Score.setForeground(new Color(19, 137, 0));
			player1Score.setForeground(Color.RED);
		} else if (player2ScoreValue < player1ScoreValue) {
			player2Score.setForeground(Color.RED);
			player1Score.setForeground(new Color(19, 137, 0));
		}

		frameMainWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frameMainWindow.addKeyListener(gameKeyAdapter);
		frameMainWindow.setFocusable(true);
		frameMainWindow.pack();

		AddMenus();
	}

	// It finds which player plays next and makes a move on the board.
	public static void makeMove(int col) {
		board.setOverflowOccured(false);

		int previousRow = board.getLastMove().getRow();
		int previousCol = board.getLastMove().getCol();
		int previousPlayer = board.getLastPlayerPlayed();

		if (board.getLastPlayerPlayed() == Board.PLAYER2) {
			board.makeMove(col, Board.PLAYER1);
		} else {
			board.makeMove(col, Board.PLAYER2);
		}

		if (board.isOverflowOccured()) {
			board.getLastMove().setRow(previousRow);
			board.getLastMove().setCol(previousCol);
			board.setLastPlayerPlayed(previousPlayer);
		}
	}

	// It places a checker on the board.
	public static void placeChecker(String name, int row, int col) {
		int xOffset = 75 * col;
		int yOffset = 75 * row;
		ImageIcon checkerIcon;
		if (board.getLastPlayerPlayed() == Board.PLAYER1)
			checkerIcon = new ImageIcon(Main.class.getResource("red.png"));
		else
			checkerIcon = new ImageIcon(Main.class.getResource("black.png"));
		checkerLabel = new JLabel(checkerIcon);
		checkerLabel.setBounds(27 + xOffset, 27 + yOffset, checkerIcon.getIconWidth(), checkerIcon.getIconHeight());
		layeredGameBoard.add(checkerLabel, new Integer(0), 0);
		layeredGameBoard.add(imageBoardLabel, new Integer(0), 1);
	}

	public static void saveUndoMove() {
		humanPlayerRowUndo = board.getLastMove().getRow();
		humanPlayerColUndo = board.getLastMove().getCol();
		humanPlayer = board.getLastPlayerPlayed();
		humanPlayerCheckerLabelUndo = checkerLabel;
	}

	// Gets called after makeMove(col) is called.
	public static void game() {
		int row = board.getLastMove().getRow();
		int col = board.getLastMove().getCol();
		int currentPlayer = board.getLastPlayerPlayed();

		if (currentPlayer == Board.PLAYER1) {
			placeChecker(player1Name, row, col);
		}
		if (currentPlayer == Board.PLAYER2) {
			placeChecker(player2Name, row, col);
		}
		if (board.checkGameOver()) {
			gameOver();
		}
	}

	// Gets called after the human player makes a move. It makes an alphaBeta
	// algorithm move.
	public static void computerMove() {
		Move alpha = new Move(Integer.MIN_VALUE);
		Move beta = new Move(Integer.MAX_VALUE);

		if (!board.isGameOver()) {
			if (board.getLastPlayerPlayed() == Board.PLAYER1) {
				Move computerMove = alphaBeta.alphaBetaAlgorithm(board, 0, alpha, beta, true);
				board.makeMove(computerMove.getCol(), Board.PLAYER2);
				game();
			}
		}
	}

	public static Component createContentComponents() {
		JLabel player1 = new JLabel(game_params.getPlayer1Name());
		player1.setBounds(0, 100, 110, 20);
		player1.setHorizontalAlignment(SwingConstants.CENTER);
		player1.setFont(new Font("Times New Roman", Font.BOLD, 16));

		player1Score = new JLabel(String.valueOf(player1ScoreValue));
		player1Score.setBounds(0, 130, 110, 20);
		player1Score.setHorizontalAlignment(SwingConstants.CENTER);
		player1Score.setFont(new Font("Times New Roman", Font.BOLD, 30));

		JLabel player1Checker = new JLabel(new ImageIcon(Main.class.getResource("red.png")));
		player1Checker.setBounds(25, 10, 70, 70);

		JLabel player2;
		if (game_params.getGameMode() == GameParameters.HumanVSComputer) {
			player2 = new JLabel("Computer");
		} else {
			player2 = new JLabel(game_params.getPlayer2Name());
		}
		player2.setHorizontalAlignment(SwingConstants.CENTER);
		player2.setBounds(0, 400, 110, 20);
		player2.setFont(new Font("Times New Roman", Font.BOLD, 16));

		JLabel player2Checker = new JLabel(new ImageIcon(Main.class.getResource("black.png")));
		player2Checker.setBounds(25, 310, 70, 70);

		player2Score = new JLabel(String.valueOf(player2ScoreValue));
		player2Score.setBounds(0, 430, 110, 20);
		player2Score.setHorizontalAlignment(SwingConstants.CENTER);
		player2Score.setFont(new Font("Times New Roman", Font.BOLD, 30));

		JPanel playersPanel = new JPanel();
		playersPanel.setPreferredSize(new Dimension(120, 500));
		playersPanel.setLayout(null);
		playersPanel.add(player2);
		playersPanel.add(player2Score);
		playersPanel.add(player2Checker);
		playersPanel.add(player1Checker);
		playersPanel.add(player1);
		playersPanel.add(player1Score);

		JLabel slide = new JLabel(new ImageIcon(Main.class.getResource("slider.png")));
		slide.setBounds(17, 15, 538, 80);

		playingChecker = new JLabel(new ImageIcon(Main.class.getResource("red.png")));
		playingChecker.setBounds(250, 20, 70, 70);

		panelBoardChecker = new JPanel();
		panelBoardChecker.setLayout(null);
		panelBoardChecker.setPreferredSize(new Dimension(DEFAULT_WIDTH, 100));
		panelBoardChecker.add(playingChecker);
		panelBoardChecker.setLayout(null);
		panelBoardChecker.add(slide);
		playingChecker.setVisible(true);

		layeredGameBoard = createLayeredBoard();

		// Panel creation to store all the elements of the board.
		panelMain = new JPanel();
		panelMain.setLayout(new BorderLayout());
		panelMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// add BoardChecker and GameBoard to panelMain.
		panelMain.add(panelBoardChecker, BorderLayout.NORTH);
		panelMain.add(layeredGameBoard, BorderLayout.CENTER);
		panelMain.add(playersPanel, BorderLayout.EAST);

		frameMainWindow.setResizable(false);
		return panelMain;
	}

	public static void gameOver() {
		board.setGameOver(true);

		int choice = 0;
		board.checkWinState();

		if (board.getWinner() == Board.PLAYER1) {
			player1ScoreValue++;
			player1Score.setText(String.valueOf(player1ScoreValue));

			if (gameMode == GameParameters.HumanVSComputer) {
				choice = JOptionPane.showConfirmDialog(panelMain,
						"Congratulations, " + player1Name + ". You win! Continue Playing?", "GAME OVER",
						JOptionPane.YES_NO_OPTION, 0, new ImageIcon(Main.class.getResource("happy.png")));
			} else if (gameMode == GameParameters.HumanVSHuman) {
				choice = JOptionPane.showConfirmDialog(panelMain, player1Name + " wins! Continue Playing?", "GAME OVER",
						JOptionPane.YES_NO_OPTION, 0, new ImageIcon(Main.class.getResource("happy.png")));
			}
		} else if (board.getWinner() == Board.PLAYER2) {
			player2ScoreValue++;
			player2Score.setText(String.valueOf(player2ScoreValue));

			if (gameMode == GameParameters.HumanVSComputer)
				choice = JOptionPane.showConfirmDialog(panelMain, "Computer wins! Continue Playing?", "GAME OVER",
						JOptionPane.YES_NO_OPTION, 0, new ImageIcon(Main.class.getResource("sad.png")));
			else if (gameMode == GameParameters.HumanVSHuman)
				choice = JOptionPane.showConfirmDialog(panelMain, player2Name + " wins! Continue Playing?", "GAME OVER",
						JOptionPane.YES_NO_OPTION);
		} else {
			choice = JOptionPane.showConfirmDialog(panelMain, "It's a draw! Continue Playing?", "GAME OVER",
					JOptionPane.YES_NO_OPTION);
		}

		if (player1ScoreValue > player2ScoreValue) {
			player1Score.setForeground(new Color(19, 137, 0));
			player2Score.setForeground(Color.RED);
		} else if (player1ScoreValue < player2ScoreValue) {
			player1Score.setForeground(Color.RED);
			player2Score.setForeground(new Color(19, 137, 0));
		}

		if (choice == JOptionPane.YES_OPTION) {
			createNewGame();
		} else {
			undo.setEnabled(false);
			frameMainWindow.removeKeyListener(frameMainWindow.getKeyListeners()[0]);
		}
	}

	public static void main(String[] args) throws LineUnavailableException {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
			AudioInputStream audioInputStream1 = AudioSystem.getAudioInputStream(Main.class.getResource("clip1.wav"));
			clip1 = AudioSystem.getClip();
			clip1.open(audioInputStream1);

			AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(Main.class.getResource("clip2.wav"));
			clip2 = AudioSystem.getClip();
			clip2.open(audioInputStream2);

		} catch (Exception e) {
			e.printStackTrace();
		}
		PreferencesWindow prefWin = new PreferencesWindow(game_params);
		prefWin.setVisible(true);
	}
}
