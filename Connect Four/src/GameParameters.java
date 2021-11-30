public class GameParameters {

	private int gameMode;
	private String difficutly;
	private String player1Name;
	private String player2Name;

	public static final int HumanVSComputer = 1;
	public static final int HumanVSHuman = 2;

	public GameParameters() {
		this.difficutly = "Easy";
		this.gameMode = HumanVSComputer;
	}

	public String getDifficulty() {
		return this.difficutly;
	}

	public void setDifficulty(String difficulty) {
		this.difficutly = difficulty;
	}

	public String getPlayer1Name() {
		return player1Name;
	}

	public void setPlayer1Name(String player1Name) {
		this.player1Name = player1Name;
	}

	public String getPlayer2Name() {
		return player2Name;
	}

	public void setPlayer2Name(String player2Name) {
		this.player2Name = player2Name;
	}

	public int getGameMode() {
		return gameMode;
	}

	public void setGameMode(int gameMode) {
		this.gameMode = gameMode;
	}
}
