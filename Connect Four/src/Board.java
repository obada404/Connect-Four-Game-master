import java.util.ArrayList;

public class Board {
	public static final int PLAYER1 = 1;
	public static final int PLAYER2 = -1;
	public static final int EMPTY = 0;

	private Move lastMove;
	private int lastPlayerPlayed;

	private int winner;
	private int[][] gameBoard;
	private boolean overflowOccured = false;
	private boolean isGameOver;

	public Board() {
		lastMove = new Move();
		lastPlayerPlayed = PLAYER2;
		winner = EMPTY;
		gameBoard = new int[6][7];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				gameBoard[i][j] = EMPTY;
			}
		}
	}

	public Board(Board board) {
		lastMove = board.lastMove;
		lastPlayerPlayed = board.lastPlayerPlayed;
		winner = board.winner;
		gameBoard = new int[6][7];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				gameBoard[i][j] = board.gameBoard[i][j];
			}
		}
	}

	public Move getLastMove() {
		return lastMove;
	}

	public void setLastMove(Move lastMove) {
		this.lastMove.setRow(lastMove.getRow());
		this.lastMove.setCol(lastMove.getCol());
		this.lastMove.setValue(lastMove.getValue());
	}

	public int getLastPlayerPlayed() {
		return lastPlayerPlayed;
	}

	public void setLastPlayerPlayed(int lastLetterPlayed) {
		this.lastPlayerPlayed = lastLetterPlayed;
	}

	public int[][] getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(int[][] gameBoard) {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				this.gameBoard[i][j] = gameBoard[i][j];
			}
		}
	}

	public int getWinner() {
		return winner;
	}

	public void setWinner(int winner) {
		this.winner = winner;
	}

	public boolean isGameOver() {
		return isGameOver;
	}

	public void setGameOver(boolean isGameOver) {
		this.isGameOver = isGameOver;
	}

	public boolean isOverflowOccured() {
		return overflowOccured;
	}

	public void setOverflowOccured(boolean overflowOccured) {
		this.overflowOccured = overflowOccured;
	}

	// It finds automatically in which row the checker should be inserted.
	public void makeMove(int col, int player) {
		try {
			int row = getRowPosition(col);
			this.lastMove = new Move(row, col);
			this.lastPlayerPlayed = player;
			this.gameBoard[row][col] = player;
		} catch (ArrayIndexOutOfBoundsException e) {
			setOverflowOccured(true);
		}
	}

	// It returns the position of the last empty row in a column.
	public int getRowPosition(int col) {
		int rowPosition = -1;
		for (int row = 0; row < 6; row++) {
			if (gameBoard[row][col] == EMPTY) {
				rowPosition = row;
			}
		}
		return rowPosition;
	}

	// Makes the specified cell in the border empty.
	public void undoMove(int row, int col, int player) {
		this.gameBoard[row][col] = 0;
		if (player == PLAYER2) {
			this.lastPlayerPlayed = PLAYER1;
		} else if (player == PLAYER1) {
			this.lastPlayerPlayed = PLAYER2;
		}
	}

	public boolean canMove(int row, int col) {
		if ((row <= -1) || (col <= -1) || (row > 5) || (col > 6)) {
			return false;
		}
		return true;
	}

	public boolean checkFullColumn(int col) {
		if (gameBoard[0][col] == EMPTY)
			return false;
		return true;
	}

	// The max number of the children is 7, because we have 7 columns.
	public ArrayList<Board> getChildren(int player) {
		ArrayList<Board> children = new ArrayList<Board>();
		for (int col = 0; col < 7; col++) {
			if (!checkFullColumn(col)) {
				Board child = new Board(this);
				child.makeMove(col, player);
				children.add(child);
			}
		}
		return children;
	}

	public int evaluate() {
		// 100 for winning. -100 for losing.
		// 10 for 3 checkers in a row for player1. -10 for 3 checkers in a row for player2.
		// 1 for 2 checkers in a row for player1.  -1 for 2 checkers in a row for player2.
		int player1Lines = 0;
		int player2Lines = 0;

		if (checkWinState()) {
			if (getWinner() == PLAYER1) {
				player1Lines = player1Lines + 100;
			} else if (getWinner() == PLAYER2) {
				player2Lines = player2Lines + 100;
			}
		}
		player1Lines = player1Lines + check3InARow(PLAYER1) * 10 + check2InARow(PLAYER1);
		player2Lines = player2Lines + check3InARow(PLAYER2) * 10 + check2InARow(PLAYER2);

		return player1Lines - player2Lines;
	}

	public boolean checkWinState() {
		// Check for 4 consecutive checkers in a row, horizontally.
		for (int i = 5; i >= 0; i--) {
			for (int j = 0; j < 4; j++) {
				if (gameBoard[i][j] == gameBoard[i][j + 1] && gameBoard[i][j] == gameBoard[i][j + 2]
						&& gameBoard[i][j] == gameBoard[i][j + 3] && gameBoard[i][j] != EMPTY) {
					setWinner(gameBoard[i][j]);
					return true;
				}
			}
		}

		// Check for 4 consecutive checkers in a row, vertically.
		for (int i = 5; i >= 3; i--) {
			for (int j = 0; j < 7; j++) {
				if (gameBoard[i][j] == gameBoard[i - 1][j] && gameBoard[i][j] == gameBoard[i - 2][j]
						&& gameBoard[i][j] == gameBoard[i - 3][j] && gameBoard[i][j] != EMPTY) {
					setWinner(gameBoard[i][j]);
					return true;
				}
			}
		}

		// Check for 4 consecutive checkers in a row, in descending diagonals.
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				if (gameBoard[i][j] == gameBoard[i + 1][j + 1] && gameBoard[i][j] == gameBoard[i + 2][j + 2]
						&& gameBoard[i][j] == gameBoard[i + 3][j + 3] && gameBoard[i][j] != EMPTY) {
					setWinner(gameBoard[i][j]);
					return true;
				}
			}
		}

		// Check for 4 consecutive checkers in a row, in ascending diagonals.
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				if (canMove(i - 3, j + 3)) {
					if (gameBoard[i][j] == gameBoard[i - 1][j + 1] && gameBoard[i][j] == gameBoard[i - 2][j + 2]
							&& gameBoard[i][j] == gameBoard[i - 3][j + 3] && gameBoard[i][j] != EMPTY) {
						setWinner(gameBoard[i][j]);
						return true;
					}
				}
			}
		}
		setWinner(0); // nobody wins.
		return false;
	}

	public boolean checkGameOver() {
		if (checkWinState()) {
			return true;
		}

		// Check if it is a draw.
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				if (gameBoard[row][col] == EMPTY) {
					return false;
				}
			}
		}
		return true;
	}

	public int check3InARow(int player) {
		int times = 0;
		// Check for 3 consecutive checkers in a row, horizontally.
		for (int i = 5; i >= 0; i--) {
			for (int j = 0; j < 7; j++) {
				if (canMove(i, j + 2)) {
					if (gameBoard[i][j] == gameBoard[i][j + 1] && gameBoard[i][j] == gameBoard[i][j + 2]
							&& gameBoard[i][j] == player) {
						times++;
					}
				}
			}
		}

		// Check for 3 consecutive checkers in a row, vertically.
		for (int i = 5; i >= 0; i--) {
			for (int j = 0; j < 7; j++) {
				if (canMove(i - 2, j)) {
					if (gameBoard[i][j] == gameBoard[i - 1][j] && gameBoard[i][j] == gameBoard[i - 2][j]
							&& gameBoard[i][j] == player) {
						times++;
					}
				}
			}
		}

		// Check for 3 consecutive checkers in a row, in descending diagonal.
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				if (canMove(i + 2, j + 2)) {
					if (gameBoard[i][j] == gameBoard[i + 1][j + 1] && gameBoard[i][j] == gameBoard[i + 2][j + 2]
							&& gameBoard[i][j] == player) {
						times++;
					}
				}
			}
		}

		// Check for 3 consecutive checkers in a row, in ascending diagonal.
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				if (canMove(i - 2, j + 2)) {
					if (gameBoard[i][j] == gameBoard[i - 1][j + 1] && gameBoard[i][j] == gameBoard[i - 2][j + 2]
							&& gameBoard[i][j] == player) {
						times++;
					}
				}
			}
		}
		return times;
	}

	public int check2InARow(int player) {
		int times = 0;
		// Check for 2 consecutive checkers in a row, horizontally.
		for (int i = 5; i >= 0; i--) {
			for (int j = 0; j < 7; j++) {
				if (canMove(i, j + 1)) {
					if (gameBoard[i][j] == gameBoard[i][j + 1] && gameBoard[i][j] == player) {
						times++;
					}
				}
			}
		}

		// Check for 2 consecutive checkers in a row, vertically.
		for (int i = 5; i >= 0; i--) {
			for (int j = 0; j < 7; j++) {
				if (canMove(i - 1, j)) {
					if (gameBoard[i][j] == gameBoard[i - 1][j] && gameBoard[i][j] == player) {
						times++;
					}
				}
			}
		}

		// Check for 2 consecutive checkers in a row, in descending diagonal.
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				if (canMove(i + 1, j + 1)) {
					if (gameBoard[i][j] == gameBoard[i + 1][j + 1] && gameBoard[i][j] == player) {
						times++;
					}
				}
			}
		}

		// Check for 2 consecutive checkers in a row, in ascending diagonal.
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				if (canMove(i - 1, j + 1)) {
					if (gameBoard[i][j] == gameBoard[i - 1][j + 1] && gameBoard[i][j] == player) {
						times++;
					}
				}
			}
		}
		return times;
	}
}
