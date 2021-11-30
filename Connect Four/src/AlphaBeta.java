import java.util.ArrayList;

public class AlphaBeta {
	private int maxDepth;
	private int player;
	private Move move, lastMove;

	public AlphaBeta() {
		maxDepth = 1;
		player = Board.PLAYER1;
	}

	public AlphaBeta(String difficulty, int player) {
		if (difficulty.equals("Easy"))
			maxDepth = 1;
		else if (difficulty.equals("Medium"))
			maxDepth = 4;
		else if (difficulty.equals("Hard"))
			maxDepth = 8;
		this.player = player;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(String difficulty) {
		if (difficulty.equals("Easy"))
			maxDepth = 1;
		else if (difficulty.equals("Medium"))
			maxDepth = 4;
		else if (difficulty.equals("Hard"))
			maxDepth = 8;
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public Move alphaBetaAlgorithm(Board board, int depth, Move alpha, Move beta, boolean player2Turn) {
		if ((board.checkGameOver()) || (depth == maxDepth)) {
			lastMove = new Move(board.getLastMove().getRow(), board.getLastMove().getCol(), board.evaluate());
			return lastMove;
		}
		if (player2Turn) {
			ArrayList<Board> children = new ArrayList<Board>(board.getChildren(Board.PLAYER1));
			//alpha = new Move(Integer.MIN_VALUE);
			for (Board child : children) {
				move = alphaBetaAlgorithm(child, depth + 1, alpha, beta, false);

				// Maximizing
				if (move.getValue() > alpha.getValue()) {
					alpha = child.getLastMove();
					alpha.setValue(move.getValue());

				}
				// Beta Cut-Off
				if (alpha.getValue() > beta.getValue())
					break;
			}
			return alpha;
		} else {
			ArrayList<Board> children = new ArrayList<Board>(board.getChildren(Board.PLAYER2));
			//beta = new Move(Integer.MAX_VALUE);
			for (Board child : children) {
				move = alphaBetaAlgorithm(child, depth + 1, alpha, beta, true);

				// Minimizing
				if (move.getValue() < beta.getValue()) {
					beta = child.getLastMove();
					beta.setValue(move.getValue());
				}
				// Alpha Cut-Off
				if (alpha.getValue() > beta.getValue())
					break;
			}
			return beta;
		}
	}
}
